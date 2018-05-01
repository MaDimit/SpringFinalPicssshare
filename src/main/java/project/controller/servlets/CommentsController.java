package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.CommentManager;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.Comment;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class CommentsController {

    @Autowired
    CommentManager commentManager;
    @Autowired
    PostDao postDao;
    @Autowired
    UserDao userDao;

    @ResponseBody
    @RequestMapping(value = "/getCommentLikes", method = RequestMethod.POST)
    public ArrayList<User> getCommentLikes(@RequestParam int commentID){
        ArrayList<User> commentLikers=null;
        try {
            commentLikers = (ArrayList<User>) commentManager.getCommentLikers(commentID);
        } catch (SQLException e) {

        }
        return commentLikers;
    }

    @ResponseBody
    @RequestMapping(value = "/addCommentLike", method = RequestMethod.POST)
    public String addCommentLike(@RequestParam int commentID, HttpSession session){

        User user = (User)session.getAttribute("user");
        String message ="success";
        try {

            commentManager.likeComment(commentID, user.getId());


        } catch (SQLException e) {
            if(e.getMessage().startsWith("Duplicate ")){
                message = "You have already liked this comment.";
                return message;
            }
           message = e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
        }

        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/addComment", method = RequestMethod.POST)
    public Comment addComment(@RequestParam int postID, @RequestParam String commentText, HttpSession session){
        Comment comment=null;
        User user = (User)session.getAttribute("user");
        try {
            comment = new Comment(postDao.getPost(postID), user, commentText);
            commentManager.addComment(comment);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comment;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
    public String deleteComment(@RequestParam int commentID){

        String message = "success";
        try {
            commentManager.deleteComment(commentID);

        } catch (SQLException e) {
            message=e.getMessage();
        }
        return message;
    }


}
