package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.controller.managers.UtilManager;
import project.model.pojo.SearchResult;

import java.sql.SQLException;

@RestController
@RequestMapping("/util")
public class UtilsController {

    @Autowired
    private UtilManager utilManager;

    @GetMapping("/search")
    public SearchResult search(@RequestParam("input") String input) throws SQLException {
        SearchResult sr = utilManager.search(input);
        return sr;
    }

}
