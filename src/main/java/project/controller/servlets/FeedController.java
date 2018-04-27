package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.UserDao;
import project.model.pojo.Post;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    PostManager postManager;

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
        System.out.println(user);
        ArrayList<Post> userPosts = (ArrayList<Post>) postManager.getFriendsFeed(user.getId());
        System.out.println(userPosts);
        return userPosts;
    }
}
