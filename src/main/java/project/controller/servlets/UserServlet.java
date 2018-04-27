package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.LoggingManager;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserServlet {

    @Autowired
    private LoggingManager loggingManager;
    @Autowired
    PostManager postManager;
    @Autowired
    UserDao userDao;
    @Autowired
    PostDao postDao;
    @Autowired
    UserManager userManager;

    @ResponseBody
    @RequestMapping(value = "/editUserData", method = RequestMethod.POST)

    public String editUserData(@RequestParam int userID,
                               @RequestParam String username,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam String profilePicURL) {
        String message = "success";
        System.out.println(username);

        try {
            User user = userDao.getUserByID(userID);
            if (oldPassword.equals(user.getPassword())) {
                //if there is new password entered
                if (newPassword != "" && !newPassword.isEmpty() && newPassword != null) {
                    if (newPassword.equals(confirmPassword)) {
                        userManager.updateProfileInfo(user, newPassword, firstName, lastName, email, profilePicURL);

                    } else {
                        message = "New passwords don't match.";
                    }
                    //use the old password
                } else {
                    userManager.updateProfileInfo(user, oldPassword, firstName, lastName, email, profilePicURL);
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
    @RequestMapping(value = "/getUserData", method = RequestMethod.POST)
    public User getUserData(@RequestParam int userID) {
        User user = null;
        try {
            user = userDao.getUserByID(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
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
    public String addLike(@RequestParam int likerID, @RequestParam int postID) {
        String message = "";
        try {
            message = postManager.likePost(postDao.getPost(postID), userDao.getUserByID(likerID));
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/addDislike", method = RequestMethod.POST)
    public String addDislike(@RequestParam int dislikerID, @RequestParam int postID) {
        String message = "";
        try {
            message = postManager.dislikePost(postDao.getPost(postID), userDao.getUserByID(dislikerID));
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

}
