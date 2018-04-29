package project.model.pojo;

public class SubscriberUserPojo {
    private int id;
    private String username;
    private String profilePicUrl;

    public SubscriberUserPojo(int id, String username, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
    }

    public int getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public String getProfilePicUrl() {
        return profilePicUrl;
    }

}
