package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.LoggingManager;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.Comment;
import project.model.pojo.SubscriberUserPojo;
import project.model.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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


    @ResponseBody
    @RequestMapping(value = "/editUserData", method = RequestMethod.POST)

    public String editUserData(
                               @RequestParam String username,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               HttpSession session) {
        String message = "success";
        System.out.println(username);

        try {
            User user = (User) session.getAttribute("user");
            if (oldPassword.equals(user.getPassword())) {
                //if there is new password entered
                if (newPassword != "" && !newPassword.isEmpty() && newPassword != null) {
                    if (newPassword.equals(confirmPassword)) {
                        userManager.updateProfileInfo(user, newPassword, firstName, lastName, email);

                    } else {
                        message = "New passwords don't match.";
                    }
                    //use the old password
                } else {
                    userManager.updateProfileInfo(user, oldPassword, firstName, lastName, email);
                }

            } else {
                message = "You have entered wrong origin password.";
                return message;
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }



    @ResponseBody
    @RequestMapping(value = "/getCurrent", method = RequestMethod.POST)
    public User getUserData(HttpSession session) {
        return (User)session.getAttribute("user");
    }

    @GetMapping(value = "/get")
    public User getUser(@RequestParam("id") int userID){
        User user = null;
        try {
            user = userManager.getUser(userID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public String subscribe(@RequestParam int subscribedToID, HttpSession session){
        String message = "success";

        try {

            User subscriber = (User)session.getAttribute("user");
            User subscribedTo =  userDao.getUserByID(subscribedToID);
            userManager.subscribe(subscriber,subscribedTo );

        } catch (SQLException e) {
            message=e.getMessage();
        } catch (UserManager.UserManagerException e) {
            message=e.getMessage();
        }

        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public String unsubscribe(@RequestParam int subscribedToID, HttpSession session){
        String message = "success";
        try {
            User subscriber = (User)session.getAttribute("user");
            User subscribedTo =  userDao.getUserByID(subscribedToID);
            userManager.removeSubscription(subscriber,subscribedTo );
        } catch (SQLException e) {
            message=e.getMessage();
        } catch (UserManager.UserManagerException e) {
            message=e.getMessage();
        }

        return message;
    }




    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam String username,
                           @RequestParam String password1,
                           @RequestParam String password2,
                           @RequestParam String email,
                           HttpSession session) {
        String message = "success";
        try {
            if (!password1.equals(password2)) {
                message = "passNotMatch";
                return message;
            } else {
                User user = loggingManager.register(username, password1, email);
                session.setAttribute("user", user);
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        String message = "success";
        try {
            User user = loggingManager.login(username, password);
            request.getSession().setAttribute("user", user);
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/addLike", method = RequestMethod.POST)
    public String addLike(@RequestParam int postID, HttpSession session) {
        String message = "";
        User user = (User)session.getAttribute("user");
        try {
            message = postManager.likePost(postDao.getPost(postID), user);
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/addDislike", method = RequestMethod.POST)
    public String addDislike(@RequestParam int postID, HttpSession session) {
        String message = "";
        User user = (User)session.getAttribute("user");
        try {
            message = postManager.dislikePost(postDao.getPost(postID),user);
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/getSubscriptions", method = RequestMethod.POST)
    public ArrayList<SubscriberUserPojo> getSubscriptions(HttpSession session) {

        User user = (User)session.getAttribute("user");
        ArrayList<SubscriberUserPojo> subscriptions=null;
        try {
            subscriptions = userManager.getAllSubscriptions(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscriptions;
    }



}
