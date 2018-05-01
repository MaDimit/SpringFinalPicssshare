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
    public static class AlbumManagerException extends Exception{
        private AlbumManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private AlbumDao albumDao;

    public HashMap<Integer, String> getAllAlbumsNames(int userID) throws AlbumManagerException{
        try {
            return albumDao.getAllAlbumsNames(userID);
        } catch (SQLException e) {
            //add log here
            throw new AlbumManagerException("Problem with getting albums for this user by name.");
        }
    }

    public List<Album> getAllAlbumsForUser(int userID) throws AlbumManagerException{
        try {
            return albumDao.getAllAlbumsForUser(userID);
        } catch (SQLException e) {
            //add log here
            throw new AlbumManagerException("Problem with getting albums for this user.");
        }
    }

    public ArrayList<Post> getAlbumByID(int albumID)throws AlbumManagerException {
        try {
            return albumDao.getAllPostsForAlbum(albumID);
        } catch (SQLException e) {
            throw new AlbumManagerException("Problem with getting album. Maybe wrong ID or no such album found?");
        }
    }

    public void createAlbum(User u, String name) throws AlbumManagerException {
        //create album object
        Album album = new Album(u, name);
        //add in db
        try {
            albumDao.addAlbumInDB(album);
        } catch (SQLException e) {
            throw new AlbumManagerException("Problem with creating new album.");
        }
    }

    public void addPostInAlbum(int postID, int albumID)throws AlbumManagerException {
        try {
            albumDao.addPostInAlbumInDB(postID, albumID);
        }catch (SQLException e){
            if(e.getMessage().startsWith("Duplicate entry")){
                throw new AlbumManagerException("This post already in album");
            }
            else {
                throw new AlbumManagerException("Problem with adding post to album.");
            }
        }
    }
    public void removePostFromAlbum(int postID, int albumID) throws AlbumManagerException {
        try {
            albumDao.removePostFromAlbumInDB(postID, albumID);
        } catch (SQLException e) {
            throw new AlbumManagerException("Problem with removing post from album.");
        }
    }

    public void deleteAlbum(int albumID) throws AlbumManagerException {
        try {
            albumDao.deleteAlbum(albumID);
        } catch (SQLException e) {
            throw new AlbumManagerException("Problem with deleting album.");
        }
    }

}