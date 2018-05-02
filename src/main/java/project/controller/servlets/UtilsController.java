package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.PostManager;
import project.controller.managers.UtilManager;
import project.model.pojo.wrappers.SearchWrapper;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/util")
public class UtilsController {

    @Autowired
    private UtilManager utilManager;
    @Autowired
    private PostManager postManager;

    @GetMapping("/search")
    public SearchWrapper search(@RequestParam("input") String input) throws SQLException, UtilManager.UtilManagerException {
        SearchWrapper sr = utilManager.search(input);
        return sr;
    }


    @ResponseBody
    @RequestMapping(value = "/addTag", method = RequestMethod.POST)
    public List<String> addTags(@RequestParam String input, @RequestParam int postID) throws PostManager.PostManagerException, SQLException {
        return postManager.addTags(input, postID);

    }
}
