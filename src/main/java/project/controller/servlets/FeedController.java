package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.UserDao;
import project.model.pojo.Post;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    PostManager postManager;

    @RequestMapping(value = "/friends")
    public List<Post> getFriendsFeed(@RequestParam int uid) throws SQLException, PostManager.PostException {
        System.out.println(uid);
        ArrayList<Post> userPosts = (ArrayList<Post>) postManager.getFriendsFeed(uid);
        System.out.println(userPosts);
        return userPosts;
    }
}
