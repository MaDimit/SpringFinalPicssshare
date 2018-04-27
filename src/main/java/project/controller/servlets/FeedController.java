package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.UserDao;
import project.model.pojo.Post;
import project.model.pojo.User;
import project.model.pojo.UserFeed;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

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
    public UserFeed getUserFeed(@RequestParam(required = false) Integer id, HttpSession session) throws SQLException, PostManager.PostException {
        User currentUser = (User)session.getAttribute("user");
        boolean owner = true;
        User user = currentUser;
        List<Post> posts;
        if(id != null && id != currentUser.getId()){
            owner = false;
            user = userManager.getUserByID(id);
        }
        posts = postManager.getUserFeed(user.getId());
        return new UserFeed(user, posts, owner);
    }
}
