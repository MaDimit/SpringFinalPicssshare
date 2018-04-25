package project.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.pojo.Comment;
import project.model.pojo.Post;
import project.model.pojo.User;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentDao {
    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DataSource dataSource;


    //================== Comments Interface ==================//

    public void addComment(Comment comment) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "INSERT INTO comments (poster_id, date, content, post_id) VALUES (?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, comment.getUser().getId());
        stmt.setTimestamp(2, Timestamp.valueOf(comment.getDate()));
        stmt.setString(3, comment.getContent());
        stmt.setInt(4, comment.getPost().getId());
        stmt.executeUpdate();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            comment.setId(generatedKeys.getInt(1));
            comment.getPost().addComment(comment);
        } else {
            throw new SQLException("Creating comment failed, no ID obtained.");
        }
        stmt.close();
    }

    public void deleteComment(int commentID) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "DELETE FROM comments WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, commentID);
        stmt.executeUpdate();
        stmt.close();
    }

    public void addLike(Comment comment, User liker) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "INSERT INTO liker_comment (comment_liker_id, liked_comment_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, liker.getId());
        stmt.setInt(2, comment.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeLike(Comment comment) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "DELETE FROM liker_comment WHERE comment_liker_id = ? AND liked_comment_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, comment.getUser().getId());
        stmt.setInt(2, comment.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    //================== Comments creation ==================//

    public Comment getCommentByID(int id) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE comments.id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,id);
        ResultSet resultSet = stmt.executeQuery();
        if(resultSet.next()) {
            int commentID = resultSet.getInt("id");
            int posterID = resultSet.getInt("poster_id");
            LocalDateTime time = resultSet.getTimestamp("date").toLocalDateTime();
            String content = resultSet.getString("content");
            int postID = resultSet.getInt("post_id");
            Comment c = new Comment(commentID, postDao.getPost(postID), userDao.getUserByID(posterID), time, content);
            c.setLikers(getCommentLikers(c.getId()));
            return c;
        }
        return null;
    }


    List<Comment> getAllComments(Post post) throws SQLException {
        Connection conn = dataSource.getConnection();
        ArrayList<Comment> comments = new ArrayList<>();
        //Fetching users from DB
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE post_id=? ORDER BY date DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, post.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            comments.add(createComment(rs, post));
        }
        stmt.close();
        return comments;
    }

    Comment createComment(ResultSet rs, Post post) throws SQLException {
        int commentId = rs.getInt("id");
        int posterId = rs.getInt("poster_id");
        LocalDateTime postDate = rs.getTimestamp("date").toLocalDateTime();
        String content = rs.getString("content");
        Comment c = new Comment(commentId, post, userDao.getUserByID(posterId), postDate, content);
        c.setLikers(this.getCommentLikers(c.getId()));
        return c;
    }

    private  List<User> getCommentLikers(int commentID) throws SQLException {
        Connection conn = dataSource.getConnection();
        List<User> likers = new ArrayList<>();
        String sql = "SELECT id, username, password, first_name, last_name,email, profile_picture_url FROM users\n" +
                "JOIN liker_comment ON id = comment_liker_id\n" +
                "WHERE liked_comment_id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, commentID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            likers.add(userDao.createUser(rs));
        }
        return likers;
    }

    //param is the new comment
    public void editComment(Comment comment) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "UPDATE comments SET comments.date = ?, comments.content = ? WHERE comments.id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setObject(1, comment.getDate());
        stmt.setString(2, comment.getContent());
        stmt.setInt(3, comment.getId());
        stmt.executeUpdate();
        stmt.close();
    }

}

