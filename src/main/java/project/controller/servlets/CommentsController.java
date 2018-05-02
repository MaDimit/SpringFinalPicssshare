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
    public ArrayList<User> getCommentLikes(@RequestParam int commentID) throws CommentManager.CommentManagerException {
        ArrayList<User> commentLikers=null;
        commentLikers = (ArrayList<User>) commentManager.getCommentLikers(commentID);

        return commentLikers;
    }

    @ResponseBody
    @RequestMapping(value = "/addCommentLike", method = RequestMethod.POST)
    public void addCommentLike(@RequestParam int commentID, HttpSession session) throws CommentManager.CommentManagerException {
        User user = (User)session.getAttribute("user");
        commentManager.likeComment(commentID, user.getId());

    }

    @ResponseBody
    @RequestMapping(value = "/addComment", method = RequestMethod.POST)
    public Comment addComment(@RequestParam int postID, @RequestParam String commentText, HttpSession session) throws CommentManager.CommentManagerException, SQLException {
        Comment comment=null;
        User user = (User)session.getAttribute("user");
        //TODO
        comment = new Comment(postDao.getPost(postID), user, commentText);
        commentManager.addComment(comment);
        return comment;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
    public void deleteComment(@RequestParam int commentID, HttpSession session) throws CommentManager.CommentManagerException {
        User user = (User) session.getAttribute("user");
        commentManager.deleteComment(commentID, user);

    }


}
