package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.wrappers.SearchWrapper;

import java.sql.SQLException;
import java.util.List;

@Component
public class UtilManager {
    public static class UtilManagerException extends Exception {
        public UtilManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;

    private final String TAG_TEMPLATE = "#%s%%";
    private final String USER_TEMPLATE = "%s%%";

    public SearchWrapper search(String input) throws UtilManagerException {
        List<SearchWrapper.SearchedUser> users = null;
        List<SearchWrapper.SearchedTag> tags = null;
        try {
            users = userDao.searchUsers(String.format(USER_TEMPLATE,input));
            tags = postDao.searchTags(String.format(TAG_TEMPLATE,input));
        } catch (SQLException e) {
            throw new UtilManagerException("Problem during searching users and tags.");
        }
        return new SearchWrapper(users,tags);
    }

}
