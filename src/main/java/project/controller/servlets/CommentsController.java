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

@RestController
public class CommentsController {

    @Autowired
    CommentManager commentManager;
    @Autowired
    PostDao postDao;
    @Autowired
    UserDao userDao;



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