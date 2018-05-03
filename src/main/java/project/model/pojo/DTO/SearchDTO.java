package project.model.pojo.DTO;

import java.util.List;

public class SearchDTO {

    public static class SearchedUser{
        private String username;
        private int id;

        public SearchedUser(String username, int id) {
            this.username = username;
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public int getId() {
            return id;
        }
    }

    public static class SearchedTag{
        private String tag;
        private int id;

        public SearchedTag(String tag, int id) {
            this.tag = tag;
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public int getId() {
            return id;
        }
    }

    private List<SearchedUser> users;
    private List<SearchedTag> tags;

    public SearchDTO(List<SearchedUser> users, List<SearchedTag> tags) {
        this.users = users;
        this.tags = tags;
    }

    public List<SearchedUser> getUsers() {
        return users;
    }

    public List<SearchedTag> getTags() {
        return tags;
    }
}
