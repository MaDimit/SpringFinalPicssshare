package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.LoggingManager;
import project.controller.managers.UserManager;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private LoggingManager loggingManager;
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

}
