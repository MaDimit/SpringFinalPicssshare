package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.AlbumDao;
import project.model.pojo.Album;
import project.model.pojo.Post;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class AlbumManager {

    @Autowired
    private AlbumDao albumDao;

    public HashMap<Integer, String> getAllAlbumsNames(int userID) throws SQLException {
        return albumDao.getAllAlbumsNames(userID);
    }

    public List<Album> getAllAlbumsForUser(int userID) throws SQLException {
        return albumDao.getAllAlbumsForUser(userID);
    }

    public ArrayList<Post> getAlbumByID(int albumID) throws SQLException {
        return albumDao.getAllPostsForAlbum(albumID);
    }

    public void createAlbum(User u, String name) throws SQLException {
        //create album object
        Album album = new Album(u, name);
        //add in db
       albumDao.addAlbumInDB(album);
    }

    public void addPostInAlbum(int postID, int albumID)throws Exception {
        try {
            albumDao.addPostInAlbumInDB(postID, albumID);
        }catch (SQLException e){
            System.out.println(e);
            if(e.getMessage().startsWith("Duplicate entry")){
                throw new Exception("This post already in album");
            }
        }
    }
    public  void removePostFromAlbum(int postID, int albumID) throws SQLException {
        albumDao.removePostFromAlbumInDB(postID, albumID);
    }

    public void deleteAlbum(int albumID) throws SQLException {
        albumDao.deleteAlbum(albumID);
    }

}