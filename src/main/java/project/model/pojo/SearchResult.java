package project.model.pojo;

import java.util.List;

public class SearchResult {

    private List<String> users;
    private List<String> tags;

    public SearchResult(List<String> users, List<String> tags) {
        this.users = users;
        this.tags = tags;
    }

    public List<String> getUsers() {
        return users;
    }

    public List<String> getTags() {
        return tags;
    }
}
