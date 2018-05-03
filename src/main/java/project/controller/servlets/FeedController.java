package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.pojo.Post;
import project.model.pojo.User;
import project.model.pojo.UserFeed;
import project.model.pojo.wrappers.TagFeedWrapper;

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


    @RequestMapping(value = "/deletePost")
    public void deletePost(@RequestParam int postID) throws SQLException {
        postManager.deletePost(postID);
    }

    //TODO
    @RequestMapping(value = "/friends")
    public List<Post> getFriendsFeed(HttpSession session) throws SQLException {
        User user = (User)session.getAttribute("user");
        return postManager.getFriendsFeed(user.getId());
    }

    //TODO
    @RequestMapping(value = "/trending")
    public List<Post>getTrendingFeed() throws  SQLException {
        return postManager.getTrendingFeed();
    }
    //TODO
    @RequestMapping(value = "/user")
    public UserFeed getUserFeed(
            @RequestParam(value = "id", required = false) Integer id,
            HttpSession session) throws SQLException {
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


    @RequestMapping(value = "/tag")
    public TagFeedWrapper getTagFeed(@RequestParam int id) throws SQLException {
        return postManager.getTagFeed(id);
    }
}
