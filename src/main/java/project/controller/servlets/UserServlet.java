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
    private UserManager userManager;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session){
        String message = "success";
        try {
            User user = loggingManager.login(username, password);
            session.setAttribute("user", user);
        } catch (Exception e) {
    message = e.getMessage();
}
        return message;
                }

    @ResponseBody
    @RequestMapping(value = "/addLike", method = RequestMethod.POST)
    public String addLike(@RequestParam int likerID, @RequestParam int postID){
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
    public String addDislike(@RequestParam int dislikerID, @RequestParam int postID){
        String message = "";
        try {
            message = postManager.dislikePost(postDao.getPost(postID), userDao.getUserByID(dislikerID));
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

}
