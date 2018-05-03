package project.model.pojo.wrappers;

import project.model.pojo.Post;

public class PostDTO {

    private String base64Image;
    private Post post;

    public PostDTO(Post post, String base64Image) {
        this.post = post;
        this.base64Image = base64Image;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public Post getPost() {
        return post;
    }

}
