package project.controller.servlets;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FeedServlet {

    @RequestMapping(value = "/hi")
    public String getHi(){
        return "hi";
    }
}
