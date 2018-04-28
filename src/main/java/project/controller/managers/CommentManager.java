package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.CommentDao;
import project.model.dao.UserDao;
import project.model.pojo.Comment;
import project.model.pojo.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CommentManager {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    public List<User> getCommentLikers(int commentID) throws SQLException {
        return commentDao.getCommentLikers(commentID);
    }

    public void editComment(int oldCommentID, String editContent) throws PostManager.PostException, SQLException {
        Comment comment = commentDao.getCommentByID(oldCommentID);
        comment.setContent(editContent);
        comment.setDate(LocalDateTime.now());
        commentDao.editComment(comment);
    }

    public void likeComment(int commentID, int likerID) throws Exception {
        if(commentID>-1 && likerID>-1) {
            commentDao.addLike(commentDao.getCommentByID(commentID), userDao.getUserByID(likerID));
        }
        else {
            throw new Exception("Error during user liking a comment.");
        }
    }

    public void addComment(Comment c) throws SQLException {
        if(c!=null) {
            commentDao.addComment(c);
        }
    }

    public void deleteComment(int commentID) throws SQLException {
        if(commentID>-1) {
            commentDao.deleteComment(commentID);
        }
    }


}
