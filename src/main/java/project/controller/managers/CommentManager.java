package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.CommentDao;
import project.model.dao.UserDao;
import project.model.pojo.Comment;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.List;

@Component
public class CommentManager {
    public static class CommentManagerException extends Exception{
        private CommentManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    public List<User> getCommentLikers(int commentID) throws CommentManagerException {
        try {
            return commentDao.getCommentLikers(commentID);
        } catch (SQLException e) {
            throw new CommentManagerException("Problem with getting likers for a comment.");
        }
    }
    

    public void likeComment(int commentID, int likerID) throws CommentManagerException {
        if(commentID>-1 && likerID>-1) {
            try {
                commentDao.addLike(commentDao.getCommentByID(commentID), userDao.getUserByID(likerID));
            } catch (SQLException e) {
                if(e.getMessage().startsWith("Duplicate ")){
                    throw new CommentManagerException("You have already liked this comment.");
                }
                throw new CommentManagerException("Problem with liking a comment.");
            }
        }
        else {
            throw new CommentManagerException("Error during user liking a comment.");
        }
    }

    public void addComment(Comment c) throws CommentManagerException {
        if(c!=null) {
            try {
                commentDao.addComment(c);
            } catch (SQLException e) {
                throw new CommentManagerException("Error during adding a comment.");
            }
        }
    }

    public void deleteComment(int commentID) throws CommentManagerException {
        if(commentID>-1) {
            try {
                commentDao.deleteComment(commentID);
            } catch (SQLException e) {
                throw new CommentManagerException("Error with deleting a comment.");
            }
        }
    }


}
