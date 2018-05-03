package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.AlbumManager;
import project.model.pojo.Album;
import project.model.pojo.Post;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/album")
public class AlbumsContoller {

    @Autowired
    AlbumManager albumManager;

    @GetMapping()
    public ArrayList<Album> getAlbumFeed(HttpSession session) throws SQLException {
        ArrayList<Album> albums = null;
        User currentUser = (User) session.getAttribute("user");
        albums = (ArrayList<Album>) albumManager.getAllAlbumsForUser(currentUser.getId());

        return albums;
    }

    @PostMapping()
    public void addAlbum(@RequestParam String albumName, HttpSession session) throws SQLException, AlbumManager.AlbumManagerException {
        String message = "success";
        User u = (User) session.getAttribute("user");
        albumManager.createAlbum(u,albumName);

    }

    @PostMapping("/delete")
    public void deleteAlbum(@RequestParam int albumID) throws SQLException {

        albumManager.deleteAlbum(albumID);

    }

    @GetMapping("/names")
    public Map<Integer, String> getAlbumsNames(HttpSession session) throws SQLException {
        Map<Integer, String> albumsNames=null;
        User currentUser = (User) session.getAttribute("user");
        albumsNames = albumManager.getAllAlbumsNames(currentUser.getId());
        return albumsNames;
    }

    @RequestMapping("/posts")
    public ArrayList<Post> getAlbumPictures(@RequestParam int albumID) throws SQLException {
        ArrayList<Post> posts = null;
        posts = albumManager.getAlbumByID(albumID);

        return posts;
    }

    @PostMapping("/add/post")
    public void addToAlbum (@RequestParam int postID, @RequestParam int albumID) throws AlbumManager.AlbumManagerException, SQLException {
        albumManager.addPostInAlbum(postID, albumID);
    }

    @PostMapping("/delete/post")
    public void deleteAlbumPost(@RequestParam int postID, @RequestParam int albumID) throws SQLException {
        albumManager.removePostFromAlbum(postID, albumID);
    }
}
