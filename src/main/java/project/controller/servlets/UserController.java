package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.LoggingManager;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.SubscriberUserPojo;
import project.model.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private LoggingManager loggingManager;
    @Autowired
    PostManager postManager;
    @Autowired
    PostDao postDao;
    @Autowired
    UserManager userManager;
    @Autowired
    UserDao userDao;


    @PostMapping(value = "/edit")
    public void editProfile(@RequestParam String oldPassword,
                            @RequestParam String newPassword,
                            @RequestParam String confirmPassword,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email,
                            @RequestParam String confirmation,
                            HttpSession session) throws UserManager.UserManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        userManager.updateProfileInfo(user, oldPassword, newPassword, confirmPassword, firstName, lastName, email, confirmation);
    }

    @ResponseBody
    @RequestMapping(value = "/getCurrent", method = RequestMethod.POST)
    public User getUserData(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping(value = "/get")
    public User getUser(@RequestParam("id") int userID) throws SQLException {
        User user = userManager.getUser(userID);
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public void subscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException, SQLException {
        User subscriber = (User) session.getAttribute("user");
        User subscribedTo = userDao.getUserByID(subscribedToID);
        userManager.subscribe(subscriber, subscribedTo);
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public void unsubscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException, SQLException {
        User subscriber = (User) session.getAttribute("user");
        User subscribedTo = null;
        subscribedTo = userDao.getUserByID(subscribedToID);
        userManager.removeSubscription(subscriber, subscribedTo);
    }


    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam String username,
                           @RequestParam String password1,
                           @RequestParam String password2,
                           @RequestParam String email,
                           HttpSession session) throws LoggingManager.RegistrationException, SQLException {
        String message = "success";
        if (!password1.equals(password2)) {
            message = "passNotMatch";
            return message;
        } else {
            User user = loggingManager.register(username, password1, email);
            session.setAttribute("user", user);
        }

        return message;
    }



    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) throws LoggingManager.LoggingException, SQLException {
        User user = loggingManager.login(username, password);
        request.getSession().setAttribute("user", user);
    }

    @RequestMapping(value = "/addLike", method = RequestMethod.POST)
    public String addLike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {

        User user = (User) session.getAttribute("user");
        String text;
        text = postManager.likePost(postDao.getPost(postID), user);
        System.out.println("USERCONTROLLER: " + text);
        return text;

    }

    @RequestMapping(value = "/addDislike", method = RequestMethod.POST)
    public String addDislike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        return postManager.dislikePost(postDao.getPost(postID), user);

    }

    @RequestMapping(value = "/getSubscriptions", method = RequestMethod.POST)
    public List<SubscriberUserPojo> getSubscriptions(HttpSession session) throws SQLException {

        User user = (User) session.getAttribute("user");
        List<SubscriberUserPojo> subscriptions = null;
        subscriptions = userManager.getAllSubscriptions(user.getId());
        return subscriptions;
    }

    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "success";
    }


}
