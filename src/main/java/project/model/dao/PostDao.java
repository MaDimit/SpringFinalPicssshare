package project.model.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.pojo.Post;
import project.model.pojo.User;
import project.model.pojo.wrappers.SearchWrapper;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
@Component
public class PostDao {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private DataSource dataSource;


    private final int TRENDING_DAYS = 1;

    //================== Posts Interface ==================//

    //------------------ post manipulations ------------------//

    public void addPost(Post post) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO posts (date, poster_id, url) VALUES (?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setObject(1, Timestamp.valueOf(post.getDate()));
            stmt.setInt(2, post.getPoster().getId());
            stmt.setString(3, post.getUrl());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int id;
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                post.setId(id);
            } else {
                throw new SQLException("Creating post failed, no ID obtained.");
            }
            stmt.close();
        }
    }

    public Post getPost(int id) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, date, poster_id, url FROM posts WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            Post post = rs.next() ? createPost(rs) : null;
            stmt.close();
            return post;
        }
    }

    public void deletePost(int id) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            //delete from db
            String sql = "DELETE FROM posts WHERE id=(?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public List<SearchWrapper.SearchedTag> searchTags(String input) throws SQLException{
        try (Connection conn = dataSource.getConnection()){
            List<SearchWrapper.SearchedTag> tags = new ArrayList<>();
            String sql = "SELECT tag_name, id FROM tags WHERE tag_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, input );
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tags.add(new SearchWrapper.SearchedTag(rs.getString("tag_name"), rs.getInt("id")));
            }
            return tags;
        }
    }

    //------------------ liking/disliking ------------------//

    public String addLike(Post post, User user) throws SQLException {
        synchronized (post) {
            int affectedRows = addLikeDislike(post, user, 1);
            if (affectedRows == 1) {
                return post.addLiker(user) == 1 ? "success" : "You have already liked this post.";

            } else if (affectedRows == 2) {
                if(post.addLiker(user)==1) {
                    post.removeDisliker(user);
                    return "removedDislikeAddLike";
                }
                else return "You have already liked this post.";
            }
            return "null";
        }
    }

    public String addDislike(Post post, User user) throws SQLException {
        synchronized (post) {
            int affectedRows = addLikeDislike(post, user, -1);
            if (affectedRows == 1) {
                return post.addDisliker(user) == 1 ? "success" : "You have already disliked this post.";

            } else if (affectedRows == 2) {
                if(post.addDisliker(user)==1) {
                    post.removeLiker(user);
                    return "removedLikeAddDislike";
                }
                else return "You have already disliked this post.";
            }
            return "null";
        }
    }

    public HashSet<String> getAllLikers() throws SQLException{
        return getAllLikesDislikes(1);
    }

    public HashSet<String> getAllDislikers() throws SQLException{
        return getAllLikesDislikes(-1);
    }

    private HashSet<String> getAllLikesDislikes(int status) throws SQLException{

        try(Connection conn = dataSource.getConnection()) {
            HashSet<String> users = new HashSet<>();
            String sql = "SELECT liker_id, likedpost_id FROM liker_post WHERE status = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getInt("liker_id") + "" + rs.getInt("likedpost_id"));
            }
            return users;
        }
    }

    //------------------ feed creation ------------------//

    public List<Post> getUserFeed(int userID) throws SQLException{

        try(Connection conn = dataSource.getConnection()) {
            List<Post> posts = new ArrayList<>();
            String sql = "SELECT id, date, poster_id, url FROM posts WHERE poster_id = ? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(createPost(rs));
            }
            stmt.close();
            return posts;
        }
    }

    public List<Post> getFriendsFeed(int userID) throws SQLException{

        try(Connection conn = dataSource.getConnection()) {
            List<Post> posts = new ArrayList<>();
            String sql = "SELECT id, date, poster_id, url FROM posts p\n" +
                    "JOIN subscriber_subscribed s ON  s.subscriber_id = ?\n" +
                    "WHERE poster_id = subscribedto_id ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(createPost(rs));
            }
            stmt.close();
            return posts;
        }
    }

    public List<Post> getTrendingFeed()throws SQLException{

        try(Connection conn = dataSource.getConnection()) {
            List<Post> posts = new ArrayList<>();
            String sql = "SELECT id, date, poster_id, url FROM posts WHERE date >= NOW() - INTERVAL ? DAY;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, TRENDING_DAYS);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(createPost(rs));
            }
            stmt.close();
            return posts;
        }
    }

    //================== Post likes/dislikes ==================//

    private int addLikeDislike(Post post, User user, int status) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            //Inserting or updating liker_post table
            String sql = "INSERT INTO liker_post (liker_id, likedpost_id, status) VALUES (?, ?, ?)\n" +
                    "  ON DUPLICATE KEY UPDATE status = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            stmt.setInt(2, post.getId());
            stmt.setInt(3, status);
            stmt.setInt(4, status);
            //Update, row already existed with a status - 1 row affected
            //Insert, newly created row - 1 row affected
            //Update, status changed to opposite - 2 rows affected
            int affectedRows = stmt.executeUpdate();
            stmt.close();
            return affectedRows;
        }

    }

    //================== Post creation ==================//

    // Used in post creation for getPost, and feed. Should be added to while loop or used once with rs.next;
    Post createPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        User user = userDao.getUserByID(rs.getInt("poster_id"));
        String url = rs.getString("url");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        //add all the info for the post
        Post post = new Post(id, user, url, date);
        post.setTags(getAllTagsForPost(post));
        post.setComments(commentDao.getAllComments(post));
        post.setLikers(getPostLikers(post));
        post.setDislikers(getPostDislikers(post));
        return post;
    }

    //get all the tags related to this post
    private List<String> getAllTagsForPost(Post post) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            List<String> tags = new ArrayList<>();

            //Fetching tags from DB
            String sql = "SELECT tags.tag_name FROM tags \n" +
                    "                JOIN post_tag ON post_tag.tag_id=tags.id\n" +
                    "                \n" +
                    "                JOIN posts ON post_tag.post_id=posts.id\n" +
                    "                WHERE posts.id = ? \n" +
                    "                ORDER BY tags.tag_name;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, post.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tag = rs.getString("tag_name");
                tags.add(tag);
            }
            stmt.close();
            return tags;
        }
    }

    private ArrayList<User> getPostLikers(Post post) throws SQLException {
        return getLikersDislikers(post.getId(), 1);
    }

    private ArrayList<User> getPostDislikers(Post post) throws SQLException {
        return getLikersDislikers(post.getId(), -1);
    }

    private ArrayList<User> getLikersDislikers(int postID, int status) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            ArrayList<User> users = new ArrayList<>();
            //Fetching likers from DB
            String sql = "SELECT users.id FROM users\n" +
                    "JOIN liker_post ON liker_post.liker_id=users.id\n" +
                    "JOIN posts ON liker_post.likedpost_id=posts.id\n" +
                    "WHERE posts.id= ? AND status = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postID);
            stmt.setInt(2, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int likerId = rs.getInt(1);
                users.add(userDao.getUserByID(likerId));
            }
            stmt.close();
            return users;
        }
    }

    public String getTagByID(int tagID)throws SQLException{

        try(Connection conn = dataSource.getConnection()){
            String sql = "SELECT tag_name FROM tags WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,tagID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("tag_name");
        }
    }

    //hashset used for putting unique posts only in result collection
    public HashSet<Post> getPostsByTags(List<String> tags) throws SQLException {

        try(Connection conn = dataSource.getConnection()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                sb.append("UPPER('%" + tags.get(i) + "%') ");
                if (i != tags.size() - 1) {
                    sb.append("OR tag_name LIKE ");
                }
            }
            HashSet<Post> matchingPosts = new HashSet<>();
            String sql = "SELECT post_id FROM post_tag WHERE tag_id IN ( SELECT id FROM tags WHERE UPPER(tag_name)" +
                    " LIKE " + sb.toString() + ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int postID = resultSet.getInt("post_id");
                matchingPosts.add(getPost(postID));
            }
            return matchingPosts;
        }
    }

    public List<Post> getPostsByTags(String tagname) throws SQLException {
        List<String> tag = new ArrayList<>();
        tag.add(tagname);
        return new ArrayList<Post>(getPostsByTags(tag));
    }

    public void addTags(List<String> tags, int postID) throws SQLException{
        Connection conn = dataSource.getConnection();
        System.out.println(tags);
        System.out.println("Post id: " + postID);
        try{
            conn.setAutoCommit(false);
            List<Integer> tagsID;
            String sql = "INSERT IGNORE INTO tags (tag_name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for(String tag : tags){
                stmt.setString(1, tag);
                stmt.addBatch();
            }
            stmt.executeBatch();

            sql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, (SELECT id FROM tags WHERE tag_name = ?))";
            stmt = conn.prepareStatement(sql);
            for(String tag : tags){
                stmt.setInt(1, postID);
                stmt.setString(2, tag);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

}
