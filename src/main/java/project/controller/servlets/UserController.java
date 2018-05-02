package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
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
import java.util.ArrayList;

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

    public void editUserData(
                               @RequestParam String username,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               HttpSession session) throws LoggingManager.RegistrationException, UserManager.UserManagerException {


            User user = (User) session.getAttribute("user");
            if (oldPassword.equals(user.getPassword())) {
                //if there is new password entered
                if (newPassword != "" && !newPassword.isEmpty() && newPassword != null) {
                    if (newPassword.equals(confirmPassword)) {
                        userManager.updateProfileInfo(user, newPassword, firstName, lastName, email);

                    } else {
                        throw new UserManager.UserManagerException("Passwords don't match.");
                    }
                    //use the old password
                } else {
                    userManager.updateProfileInfo(user, oldPassword, firstName, lastName, email);
                }

            } else {
                throw new UserManager.UserManagerException("You have entered wrong origin password.");
            }

    }



    @ResponseBody
    @RequestMapping(value = "/getCurrent", method = RequestMethod.POST)
    public User getUserData(HttpSession session) {
        return (User)session.getAttribute("user");
    }

    @GetMapping(value = "/get")
    public User getUser(@RequestParam("id") int userID) throws UserManager.UserManagerException {
        User user = userManager.getUser(userID);
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public void subscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException {
            User subscriber = (User)session.getAttribute("user");
        User subscribedTo = null;
        try {
            subscribedTo = userDao.getUserByID(subscribedToID);
        } catch (SQLException e) {
            throw new  UserManager.UserManagerException("Problem during subscribing.");
        }
        userManager.subscribe(subscriber,subscribedTo );
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public void unsubscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException {
        User subscriber = (User)session.getAttribute("user");
        User subscribedTo = null;
        try {
            subscribedTo = userDao.getUserByID(subscribedToID);
        } catch (SQLException e) {
            throw new  UserManager.UserManagerException("Problem during unsubscribing.");
        }
        userManager.removeSubscription(subscriber,subscribedTo );
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

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) throws LoggingManager.LoggingException {
        User user = loggingManager.login(username, password);
        System.out.println("CONTROLLER:"+user);
        request.getSession().setAttribute("user", user);
    }

    @ResponseBody
    @RequestMapping(value = "/addLike", method = RequestMethod.POST)
    public String addLike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {

        User user = (User)session.getAttribute("user");
        String text;
        text= postManager.likePost(postDao.getPost(postID), user);
        System.out.println("USERCONTROLLER: "+text);
        return text;

    }

    @ResponseBody
    @RequestMapping(value = "/addDislike", method = RequestMethod.POST)
    public String addDislike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {
        User user = (User)session.getAttribute("user");
        return postManager.dislikePost(postDao.getPost(postID),user);

    }

    @ResponseBody
    @RequestMapping(value = "/getSubscriptions", method = RequestMethod.POST)
    public ArrayList<SubscriberUserPojo> getSubscriptions(HttpSession session) throws UserManager.UserManagerException {

        User user = (User)session.getAttribute("user");
        ArrayList<SubscriberUserPojo> subscriptions=null;
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
