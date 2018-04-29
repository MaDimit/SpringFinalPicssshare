package project.model.pojo.wrappers;

import project.model.pojo.Post;

public class PostWrapper {

    private String base64Image;
    private Post post;

    public PostWrapper(Post post, String base64Image) {
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
