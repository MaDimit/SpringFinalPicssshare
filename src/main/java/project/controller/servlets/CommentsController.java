package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.CommentManager;
import project.model.dao.PostDao;
import project.model.pojo.Comment;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentsController {

    @Autowired
    CommentManager commentManager;
    @Autowired
    PostDao postDao;

    @GetMapping("/likes")
    public List<User> getCommentLikes(@RequestParam int commentID) throws SQLException {
        return commentManager.getCommentLikers(commentID);
    }

    @PostMapping("/like")
    public void addCommentLike(@RequestParam int commentID, HttpSession session) throws CommentManager.CommentManagerException, SQLException {
        User user = (User)session.getAttribute("user");
        commentManager.likeComment(commentID, user.getId());
    }

    @PostMapping()
    public Comment addComment(@RequestParam int postID, @RequestParam String commentText, HttpSession session) throws CommentManager.CommentManagerException, SQLException {
        User user = (User)session.getAttribute("user");
        Comment comment = new Comment(postDao.getPost(postID), user, commentText);
        commentManager.addComment(comment);
        return comment;
    }

    @PostMapping("/delete")
    public void deleteComment(@RequestParam int commentID, HttpSession session) throws CommentManager.CommentManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        commentManager.deleteComment(commentID, user);
    }


}
