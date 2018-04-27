package project.controller.servlets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.controller.managers.PostManager;
import project.controller.managers.UserManager;
import project.model.pojo.Post;
import project.model.pojo.User;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

@RestController
@RequestMapping("/img")
public class FileController {

    @Autowired
    private PostManager postManager;

    @Autowired
    private UserManager userManager;

    @Value("${upload.path}")
    private String UPLOAD_PATH;

    @PostMapping("upload")
    public String uploadImage(@RequestParam("file") MultipartFile uploadfile, HttpSession session) throws IOException, PostManager.PostException {
        User user = (User)session.getAttribute("user");
        String postUrl = createUri(user.getUsername());
        String path = UPLOAD_PATH + postUrl;
        File serverFile = new File(path);
        serverFile.getParentFile().mkdirs();
        Files.copy(uploadfile.getInputStream(),serverFile.toPath());

        postManager.addPost(new Post(user,postUrl));
        return Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
    }

    //Get image by url from post
    @GetMapping(value = "/get")
    public String getImage(@RequestParam("url") String url) throws IOException{
        String path = UPLOAD_PATH + url;
        return Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
    }

    @PostMapping("/uploadProfilePic")
    public String uploadProfileImage(@RequestParam("file") MultipartFile uploadfile, HttpSession session) throws IOException, SQLException {
        User user = (User)session.getAttribute("user");
        String profilePicUrl = user.getUsername() + File.separator + "avatar";
        String path = UPLOAD_PATH + profilePicUrl;
        File serverFile = new File(path);
        serverFile.getParentFile().mkdirs();
        Files.copy(uploadfile.getInputStream(),serverFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        userManager.changeProfilePic(user, profilePicUrl);
        return Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
    }

    private String createUri(String username){
        return username + File.separator + System.currentTimeMillis();
    }

}
