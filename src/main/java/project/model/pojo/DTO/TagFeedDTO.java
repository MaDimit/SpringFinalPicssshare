package project.model.pojo.DTO;

import project.model.pojo.Post;

import java.util.List;

public class TagFeedDTO {

    private List<Post> posts;
    private String tagname;

    public TagFeedDTO(List<Post> posts, String tagname) {
        this.posts = posts;
        this.tagname = tagname;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public String getTagname() {
        return tagname;
    }
}
