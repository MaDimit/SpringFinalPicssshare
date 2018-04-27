package project.model.pojo;

import java.util.List;

public class UserFeed {

    private User user;
    private List<Post> posts;
    private boolean owner;

    public UserFeed(User user, List<Post> posts, boolean owner) {
        this.user = user;
        this.posts = posts;
        this.owner = owner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}
