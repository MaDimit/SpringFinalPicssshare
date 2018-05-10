package project.controller.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.PostDao;
import project.model.dao.UserDao;
import project.model.pojo.DTO.SearchDTO;

import java.sql.SQLException;
import java.util.List;

@Component
public class UtilManager {
    public static class UtilManagerException extends InfoException {
        public UtilManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;

    private final static Logger LOGGER = LoggerFactory.getLogger(UtilManager.class);

    private final String TAG_TEMPLATE = "#%s%%";
    private final String USER_TEMPLATE = "%s%%";

    public SearchDTO search(String input) throws SQLException, UtilManagerException {
        //Prevent inserting script for password in login form
        String filteredSearch = XSSPreventer.escapeHtml(input);
        input = filteredSearch;
        if(input == null || input.isEmpty()){
            throw new UtilManagerException("You can't search with empty input.");
        }
        List<SearchDTO.SearchedUser> users = null;
        List<SearchDTO.SearchedTag> tags = null;
        try {
            users = userDao.searchUsers(String.format(USER_TEMPLATE,input));
            tags = postDao.searchTags(String.format(TAG_TEMPLATE,input));
        } catch (SQLException e) {
            LOGGER.error("Database problem occurred in search() for input {}. {}", input, e.getMessage());
            throw e;
        }
        return new SearchDTO(users,tags);
    }

}
