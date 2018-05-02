package project.controller.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.AlbumDao;
import project.model.pojo.Album;
import project.model.pojo.Post;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AlbumManager {
    public static class AlbumManagerException extends InfoException {
        private AlbumManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private AlbumDao albumDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumManager.class);

    public Map<Integer, String> getAllAlbumsNames(int userID) throws SQLException{
        try{
            return albumDao.getAllAlbumsNames(userID);
        }catch (SQLException e){
            LOGGER.error("Database exception occurred in getAllAlbums() for user id:{}. {}",userID, e.getMessage());
            throw e;
        }
    }

    public List<Album> getAllAlbumsForUser(int userID) throws SQLException{
        try {
            return albumDao.getAllAlbumsForUser(userID);
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in getAllAlbumsForUser() for user id:{}. {}",userID, e.getMessage());
            throw e;
        }
    }

    public ArrayList<Post> getAlbumByID(int albumID)throws SQLException {
        try {
            return albumDao.getAllPostsForAlbum(albumID);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in getAlbumByID() for album id:{}. {}",albumID, e.getMessage());
            throw e;
        }
    }

    public void createAlbum(User u, String name) throws SQLException {
        Album album = new Album(u, name);
        try {
            albumDao.addAlbumInDB(album);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in createAlbum() for user {}, id:{}. {}",u.getUsername(), u.getId(), e.getMessage());
            throw e;
        }
        LOGGER.info("User {}, id:{} created album {}", u.getUsername(), u.getId(), name);
    }

    public void addPostInAlbum(int postID, int albumID)throws AlbumManagerException, SQLException {
        try {
            albumDao.addPostInAlbumInDB(postID, albumID);
        }catch (SQLException e){
            if(e.getMessage().startsWith("Duplicate entry")){
                throw new AlbumManagerException("This post already in album");
            }
            LOGGER.error("Database exception occurred in addPostInAlbum() for postID:{}, albumID:{}. {}",postID, albumID, e.getMessage());
            throw e;
        }
    }
    public void removePostFromAlbum(int postID, int albumID) throws SQLException {
        try {
            albumDao.removePostFromAlbumInDB(postID, albumID);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in removePostFromAlbum() for postID:{}, albumID:{}. {}",postID, albumID, e.getMessage());
            throw e;
        }
    }

    public void deleteAlbum(int albumID) throws SQLException {
        try {
            albumDao.deleteAlbum(albumID);
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in deleteAlbum() for albumID:{}. {}", albumID, e.getMessage());
            throw e;
        }
    }

}