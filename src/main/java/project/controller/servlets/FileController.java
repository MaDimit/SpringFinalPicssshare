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
import project.model.pojo.wrappers.PostWrapper;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public PostWrapper uploadImage(@RequestParam("file") MultipartFile uploadfile, HttpSession session) throws Exception{
        if(!checkFileType(uploadfile)){
            throw new Exception("file type not supported");
        }
        User user = (User)session.getAttribute("user");
        String postUrl = createUri(user.getUsername());
        String path = UPLOAD_PATH + postUrl;
        File serverFile = new File(path);
        serverFile.getParentFile().mkdirs();
        Files.copy(uploadfile.getInputStream(),serverFile.toPath());

        Post post = new Post(user,postUrl);
        postManager.addPost(post);
        String base64Image = Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
        return new PostWrapper(post, base64Image);
    }

    //Get image by url from post
    @GetMapping(value = "/get")
    public String getImage(@RequestParam("url") String url) throws IOException {
        String path = UPLOAD_PATH + url;
        return Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
    }

    @PostMapping("/uploadProfilePic")
    public String uploadProfileImage(@RequestParam("file") MultipartFile uploadfile, HttpSession session) throws  Exception {
        if(!checkFileType(uploadfile)){
            throw new Exception("file type not supported");
        }
        User user = (User)session.getAttribute("user");
        String profilePicUrl = user.getUsername() + File.separator + "avatar";
        String path = UPLOAD_PATH + profilePicUrl;
        File serverFile = new File(path);
        serverFile.getParentFile().mkdirs();
        Files.copy(uploadfile.getInputStream(),serverFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        userManager.changeProfilePic(user, profilePicUrl);
        return Base64Utils.encodeToString(IOUtils.toByteArray(new FileInputStream(path)));
    }

    @PostMapping("/deleteUploaded")
    public void deleteUploadedPost(@RequestParam("postID") int postID)throws SQLException, PostManager.PostManagerException {
        postManager.deletePost(postID);
    }

    private String createUri(String username){
        return username + File.separator + System.currentTimeMillis();
    }

    private boolean checkFileType(MultipartFile file) throws IOException{
        if(file != null && !file.isEmpty()) {
            if(file.getContentType().startsWith("image")){
                return true;
            }
        }
        return false;
    }
}
