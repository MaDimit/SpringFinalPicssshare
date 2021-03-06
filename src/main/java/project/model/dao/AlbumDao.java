package project.model.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.pojo.Album;
import project.model.pojo.Post;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class AlbumDao {

    @Autowired
    private UserDao userDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private DataSource dataSource;


    public void addPostInAlbumInDB(int postID, int albumID) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO albums_has_posts (album_id, post_id) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, albumID);
            stmt.setInt(2, postID);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public void removePostFromAlbumInDB(int postID, int albumID) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM albums_has_posts WHERE album_id = ? AND post_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, albumID);
            stmt.setInt(2, postID);
            stmt.executeUpdate();
            stmt.close();
        }
    }


    public void addAlbumInDB(Album album) throws SQLException {
        int albumID;
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO albums (name, belonger_id) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, album.getName());
            stmt.setInt(2, album.getUserID());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                albumID = generatedKeys.getInt(1);
                album.setId(albumID);
            } else {
                throw new SQLException("Creating post failed, no ID obtained.");
            }
            stmt.close();
        }
    }

    public void deleteAlbum(int albumID) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM albums WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, albumID);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public List<Album> getAllAlbumsForUser(int userID) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            List<Album> albums = new ArrayList<>();
            String sql = "SELECT id, name, belonger_id FROM albums WHERE belonger_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int albumID = rs.getInt("id");
                String albumName = rs.getString("name");
                int belongerID = rs.getInt("belonger_id");
                //create album
                Album a = new Album(userDao.getUserByID(belongerID), albumName, albumID);
                albums.add(a);
            }
            return albums;
        }
    }

    public Album getAlbumByID(int desiredAlbumID) throws SQLException {
        Album a = null;
        try (Connection conn = dataSource.getConnection()) {

            String sql = "SELECT id, name, belonger_id FROM albums WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, desiredAlbumID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int albumID = rs.getInt("id");
                String albumName = rs.getString("name");
                int belongerID = rs.getInt("belonger_id");
                //create album
                a = new Album(userDao.getUserByID(belongerID), albumName, albumID);
            }
        }
        return a;

    }

    public ArrayList<Post> getAllPostsForAlbum(int albumID) throws SQLException {
        ArrayList<Post> posts =null;
        try (Connection conn = dataSource.getConnection()) {
            posts = new ArrayList<>();
            String query = "SELECT post_id FROM albums_has_posts WHERE album_id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, albumID);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int postID = results.getInt(1);
                posts.add(postDao.getPost(postID));
            }

        }
        return posts;
    }

    public HashMap<Integer, String> getAllAlbumsNames(int userID) throws SQLException {
        HashMap<Integer, String> albumsNames =null;
        try (Connection conn = dataSource.getConnection()) {
            albumsNames = new HashMap<>();
            String query = "SELECT id,name FROM albums WHERE belonger_id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userID);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int albumID = results.getInt("id");
                String albumName = results.getString("name");
                albumsNames.put(albumID, albumName);
            }

        }
        return albumsNames;
    }
}
