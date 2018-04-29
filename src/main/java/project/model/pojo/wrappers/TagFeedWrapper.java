package project.model.pojo.wrappers;

import project.model.pojo.Post;

import java.util.List;

public class TagFeedWrapper {

    private List<Post> posts;
    private String tagname;

    public TagFeedWrapper(List<Post> posts, String tagname) {
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
