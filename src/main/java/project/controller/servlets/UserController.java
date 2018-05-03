package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.LoggingManager;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.DTO.SubscriberUserDTO;
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

    @PostMapping("/edit")
    public void editProfile(@RequestParam String oldPassword,
                            @RequestParam String newPassword,
                            @RequestParam String confirmPassword,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email,
                            @RequestParam(required = false) String confirmation,
                            HttpSession session) throws UserManager.UserManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        userManager.updateProfileInfo(user, oldPassword, newPassword, confirmPassword, firstName, lastName, email, confirmation);
    }

    @GetMapping("/current")
    public User getUserData(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping()
    public User getUser(@RequestParam("id") int userID) throws SQLException {
        return userManager.getUser(userID);
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException, SQLException {
        User subscriber = (User) session.getAttribute("user");
        User subscribedTo = userDao.getUserByID(subscribedToID);
        userManager.subscribe(subscriber, subscribedTo);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestParam int subscribedToID, HttpSession session) throws UserManager.UserManagerException, SQLException {
        User subscriber = (User) session.getAttribute("user");
        User subscribedTo = userDao.getUserByID(subscribedToID);
        userManager.removeSubscription(subscriber, subscribedTo);
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password1,
                           @RequestParam String password2,
                           @RequestParam String email,
                           HttpSession session) throws LoggingManager.RegistrationException, SQLException {
        String message = "success";
        System.out.println("User is in register");
        if (!password1.equals(password2)) {
            message = "passNotMatch";
            return message;
        } else {
            User user = loggingManager.register(username, password1, email);
            session.setAttribute("user", user);
        }
        System.out.println("msg: " + message);
        return message;
    }

    @PostMapping("/login")
    public void login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) throws LoggingManager.LoggingException, SQLException {
        User user = loggingManager.login(username, password);
        request.getSession().setAttribute("user", user);
    }

    @PostMapping("/post/like")
    public String addLike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        String text;
        text = postManager.likePost(postDao.getPost(postID), user);
        System.out.println("USERCONTROLLER: " + text);
        return text;

    }

    @PostMapping("/post/dislike")
    public String addDislike(@RequestParam int postID, HttpSession session) throws PostManager.PostManagerException, SQLException {
        User user = (User) session.getAttribute("user");
        return postManager.dislikePost(postDao.getPost(postID), user);
    }

    @GetMapping(value = "/subscriptions")
    public List<SubscriberUserDTO> getSubscriptions(HttpSession session) throws SQLException {
        User user = (User) session.getAttribute("user");
        return userManager.getAllSubscriptions(user.getId());
    }

    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "success";
    }


}
