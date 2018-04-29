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

    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;

    private final String TAG_TEMPLATE = "#%s%%";
    private final String USER_TEMPLATE = "%s%%";

    public SearchWrapper search(String input) throws SQLException {
        List<SearchWrapper.SearchedUser> users = userDao.searchUsers(String.format(USER_TEMPLATE,input));
        List<SearchWrapper.SearchedTag> tags = postDao.searchTags(String.format(TAG_TEMPLATE,input));
        return new SearchWrapper(users,tags);
    }

}
