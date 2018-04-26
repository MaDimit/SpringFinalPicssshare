package project.controller.servlets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.controller.managers.CommentManager;

@RestController
public class CommentsController {

    @Autowired
    CommentManager commentManager;

    @ResponseBody
    @RequestMapping(value = "/addComment", method = RequestMethod.POST)
    public void addComment(@RequestParam int posterID, @RequestParam int postID, @RequestParam String commentContext){
        System.out.println(posterID);
        System.out.println(postID);
        System.out.println(commentContext);

    }


}
