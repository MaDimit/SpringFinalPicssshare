package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    //TEMP
    @Value("${userid.session}")
    private int USER_ID;

    @RequestMapping(value = "/friends")
    public List<Post> getFriendsFeed(HttpSession session) throws SQLException, PostManager.PostException {
        //TEMP adding user to session on index page without login for testing
        session.setAttribute("user", userDao.getUserByID(USER_ID));
        //TEMP
        User user = (User)session.getAttribute("user");

        return postManager.getFriendsFeed(user.getId());
    }

    @RequestMapping(value = "/trending")
    public List<Post>getTrendingFeed(HttpSession session) throws SQLException, PostManager.PostException {
        //TEMP adding user to session on index page without login for testing
        session.setAttribute("user", userDao.getUserByID(USER_ID));
        //TEMP

        return postManager.getTrendingFeed();
    }

    @RequestMapping(value = "/user")
    public UserFeed getUserFeed(
            @RequestParam(value = "id", required = false) Integer id,
            HttpSession session) throws SQLException, PostManager.PostException {
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
    public ArrayList<Album> getAlbumFeed(HttpSession session)  {
        ArrayList<Album> albums = null;
        User currentUser = (User) session.getAttribute("user");
        try {
            albums = (ArrayList<Album>) albumManager.getAllAlbumsForUser(currentUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albums;
    }

    @RequestMapping(value = "/addAlbum")
    public String addAlbum(@RequestParam String albumName, HttpSession session)  {
        String message = "success";
        User u = (User) session.getAttribute("user");
        try {
           albumManager.createAlbum(u,albumName);

        } catch (SQLException e) {
            message=e.getMessage();
        }
        return message;
    }

    @RequestMapping(value = "/deleteAlbum")
    public String deleteAlbum(@RequestParam int albumID)  {
        String message = "success";
        try {
            albumManager.deleteAlbum(albumID);
        } catch (SQLException e) {
            message =  e.getMessage();
        }
        return message;
    }

    @RequestMapping(value = "/albumNames")
    public HashMap<Integer, String> getAlbumsNames(HttpSession session)  {
        HashMap<Integer, String> albumsNames=null;
        User currentUser = (User) session.getAttribute("user");
        try {
            albumsNames = albumManager.getAllAlbumsNames(currentUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albumsNames;
    }

    @RequestMapping(value = "/album")
    public ArrayList<Post> getAlbumPictures(@RequestParam int albumID)  {
        ArrayList<Post> posts = null;
        try {
           posts = albumManager.getAlbumByID(albumID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @RequestMapping(value = "/tag")
    public TagFeedWrapper getTagFeed(@RequestParam("id") int tagID)  throws SQLException {
        return postManager.getTagFeed(tagID);
    }
}
