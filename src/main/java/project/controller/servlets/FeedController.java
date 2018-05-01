package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.AlbumManager;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.UserDao;
import project.model.pojo.Album;
import project.model.pojo.Post;
import project.model.pojo.User;
import project.model.pojo.UserFeed;
import project.model.pojo.wrappers.TagFeedWrapper;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    AlbumManager albumManager;

    @Autowired
    PostManager postManager;
    @Autowired
    UserManager userManager;

    //TEMP
    @Autowired
    UserDao userDao;

    @RequestMapping(value = "/deletePost")
    public void deletePost(@RequestParam int postID) throws PostManager.PostManagerException {
        postManager.deletePost(postID);
    }

    //TODO
    @RequestMapping(value = "/friends")
    public List<Post> getFriendsFeed(HttpSession session) throws SQLException, PostManager.PostManagerException {
        User user = (User)session.getAttribute("user");
        return postManager.getFriendsFeed(user.getId());
    }

    //TODO
    @RequestMapping(value = "/trending")
    public List<Post>getTrendingFeed(HttpSession session) throws PostManager.PostManagerException, SQLException {

        return postManager.getTrendingFeed();
    }
    //TODO
    @RequestMapping(value = "/user")
    public UserFeed getUserFeed(
            @RequestParam(value = "id", required = false) Integer id,
            HttpSession session) throws PostManager.PostManagerException, UserManager.UserManagerException {
        User currentUser = (User)session.getAttribute("user");
        boolean owner = true;
        User user = currentUser;
        List<Post> posts;
        if(id != null && id != currentUser.getId()){
            owner = false;
            user = userManager.getUser(id);
        }
        posts = postManager.getUserFeed(user.getId());
        return new UserFeed(user, posts, owner);
    }

    @RequestMapping(value = "/albums")
    public ArrayList<Album> getAlbumFeed(HttpSession session) throws AlbumManager.AlbumManagerException {
        ArrayList<Album> albums = null;
        User currentUser = (User) session.getAttribute("user");
        albums = (ArrayList<Album>) albumManager.getAllAlbumsForUser(currentUser.getId());

        return albums;
    }

    @RequestMapping(value = "/addAlbum")
    public void addAlbum(@RequestParam String albumName, HttpSession session) throws AlbumManager.AlbumManagerException {
        String message = "success";
        User u = (User) session.getAttribute("user");
        albumManager.createAlbum(u,albumName);

    }

    @RequestMapping(value = "/deleteAlbum")
    public void deleteAlbum(@RequestParam int albumID) throws AlbumManager.AlbumManagerException {

        albumManager.deleteAlbum(albumID);

    }

    @RequestMapping(value = "/albumNames")
    public HashMap<Integer, String> getAlbumsNames(HttpSession session) throws AlbumManager.AlbumManagerException {
        HashMap<Integer, String> albumsNames=null;
        User currentUser = (User) session.getAttribute("user");
        albumsNames = albumManager.getAllAlbumsNames(currentUser.getId());


        return albumsNames;
    }

    @RequestMapping(value = "/album")
    public ArrayList<Post> getAlbumPictures(@RequestParam int albumID) throws AlbumManager.AlbumManagerException {
        ArrayList<Post> posts = null;
        posts = albumManager.getAlbumByID(albumID);

        return posts;
    }

    @PostMapping("/addToAlbum")
    public void addToAlbum (@RequestParam int postID, @RequestParam int albumID) throws AlbumManager.AlbumManagerException {
        albumManager.addPostInAlbum(postID, albumID);
    }

    @PostMapping("/deleteAlbumPost")
    public void deleteAlbumPost(@RequestParam int postID, @RequestParam int albumID) throws AlbumManager.AlbumManagerException {
        albumManager.removePostFromAlbum(postID, albumID);
    }

    @RequestMapping(value = "/tag")
    public TagFeedWrapper getTagFeed(@RequestParam int id) throws PostManager.PostManagerException {
        return postManager.getTagFeed(id);
    }
}
