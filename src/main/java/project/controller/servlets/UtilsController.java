package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.controller.managers.UtilManager;
import project.model.pojo.DTO.SearchDTO;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/util")
public class UtilsController {

    @Autowired
    private UtilManager utilManager;
    @Autowired
    private PostManager postManager;
    @Autowired
    private UserManager userManager;

    @GetMapping("/search")
    public SearchDTO search(@RequestParam("input") String input) throws SQLException, UtilManager.UtilManagerException {
        return utilManager.search(input);
    }

    @PostMapping("/recovery/password")
    public void forgotPassword(@RequestParam String email) throws SQLException, UserManager.UserManagerException {
        User user = userManager.getUserByEmail(email);
        userManager.changeUserPassword(user);
    }

    @PostMapping("/addTag")
    public List<String> addTags(@RequestParam String input, @RequestParam int postID) throws PostManager.PostManagerException, SQLException {
        return postManager.addTags(input, postID);

    }
}
