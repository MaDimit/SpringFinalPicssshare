package project.controller.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.CommentDao;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.Comment;
import project.model.pojo.Post;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.List;

@Component
public class CommentManager {
    public static class CommentManagerException extends InfoException {
        private CommentManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private PostDao postDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentManager.class);

    public List<User> getCommentLikers(int commentID) throws SQLException {
        try {
            return commentDao.getCommentLikers(commentID);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in getCommentLikers() for commentID:{}. {}", commentID, e.getMessage());
            throw e;
        }
    }


    public void likeComment(int commentID, int likerID) throws CommentManagerException, SQLException {
        try {
            commentDao.addLike(commentDao.getCommentByID(commentID), userDao.getUserByID(likerID));
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate ")) {
                throw new CommentManagerException("You have already liked this comment.");
            }
            LOGGER.error("Database exception occurred in likeComment() for commentID:{}, likerID:{} . {}", commentID, likerID, e.getMessage());
            throw e;
        }
        LOGGER.info("User with id:{} liked comment with id:{}", likerID, commentID);
    }

    public void addComment(Comment c) throws CommentManagerException, SQLException {
        if (c.getContent().length() < 1 || c.getContent().length() > 1000) {
            throw new CommentManagerException("Invalid comment size.");
        }
        if (c.getPost() == null) {
            throw new CommentManagerException("Probably post you trying to add comment to already deleted. Refresh page and try again.");
        }
        try {
            commentDao.addComment(c);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in addComment() for userID:{}, commentID:{}, postID:{} . {}", c.getUser().getId(), c.getId(), c.getPost().getId(), e.getMessage());
            throw e;
        }
        LOGGER.info("User {}, id:{} added comment id:{} to post id:{}", c.getUser().getUsername(), c.getUser().getId(), c.getId(), c.getPost().getId());
    }

    public void deleteComment(int commentID, User userInSession) throws CommentManagerException, SQLException {
        //FOR SECURITY --> If a person changes html to display the delete button, here we compare the user in
        //session with the owner of the post. One can delete comments only from his posts.
        Comment comment = commentDao.getCommentByID(commentID);
        Post commentPost = comment.getPost();
        User postOwner = commentPost.getPoster();
        if (postOwner.getId() == userInSession.getId()) {
            try {
                commentDao.deleteComment(commentID);
            } catch (SQLException e) {
                LOGGER.error("Database exception occurred in deleteComment() for commentID:{}, userID:{} . {}", commentID,userInSession.getId(), e.getMessage());
                throw e;
            }
        } else {
            throw new CommentManagerException("You are not owner of this post.");
        }
        LOGGER.info("User {}, id:{} deleted comment id:{}", userInSession.getUsername(), userInSession.getId(), commentID);
    }


}
