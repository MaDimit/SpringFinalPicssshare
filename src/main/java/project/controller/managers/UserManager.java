package project.controller.managers;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.UserDao;
import project.model.pojo.DTO.SubscriberUserDTO;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.List;


@Component
public class UserManager {

    public void changeUserPassword(User user) throws UserManagerException {
        String newPassword = SendMailSSL.randomStringGenerator.generateString();
        SendMailSSL.sendResetPasswordEmail(user.getUsername(), user.getEmail(), newPassword);
        newPassword = BCrypt.hashpw(newPassword,BCrypt.gensalt());
        try {
            userDao.changeUserPassword(newPassword, user.getId());
        } catch (SQLException e) {
            throw new UserManagerException("Problem with changing password.");
        }
    }

    public static class UserManagerException extends InfoException {
        public UserManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private UserDao userDao;
    @Autowired
    private LoggingManager loggingManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);

    public List<SubscriberUserDTO> getAllSubscriptions(int subscriberID) throws SQLException {
        List<SubscriberUserDTO> subscriptions = null;
        try {
            subscriptions = userDao.getAllSubscriptions(subscriberID);
        } catch (SQLException e) {
            LOGGER.error("Database exception occurred in getAllSubscriptions for user id:{}. {}", subscriberID, e.getMessage());
            throw e;
        }
        return subscriptions;
    }

    public boolean subscribe(User subscriber, User subscribedTo) throws UserManagerException, SQLException {
        validatesubscribtion(subscriber, subscribedTo);
        try {
            userDao.addSubscription(subscriber, subscribedTo);
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry")) {
                throw new UserManagerException("You have already subscribed to this user.");
            }
            LOGGER.error("Database exception occurred in subscribe(). {}", e.getMessage());
            throw e;
        }
        LOGGER.info("{}, id:{} subscribed to {}, id:{}", subscriber.getUsername(), subscriber.getId(), subscribedTo.getUsername(), subscribedTo.getId());
        return true;
    }


    public boolean removeSubscription(User subscriber, User subscribedTo) throws UserManagerException, SQLException {
        validatesubscribtion(subscriber, subscribedTo);
        try {
            userDao.removeSubscription(subscriber, subscribedTo);
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry")) {
                throw new UserManagerException("You have already unsubscribed to this user.");
            }
            LOGGER.error("Database exception occurred in removeSubscription(). {}", e.getMessage());
            throw e;
        }
        LOGGER.info("{}, id:{} unsubscribed from {}, id:{}", subscriber.getUsername(), subscriber.getId(), subscribedTo.getUsername(), subscribedTo.getId());
        return true;
    }

    private void validatesubscribtion(User subscriber, User subscribedTo) throws UserManagerException {
        if (subscribedTo == null || subscriber == null) {
            throw new UserManagerException("Probably user you trying to subscribe to deleted his profile.");
        }
    }

    public void updateProfileInfo(User user, String oldPassword, String newPassword, String confirmPassword, String firstName, String lastName, String email, String confirmation) throws UserManagerException, SQLException {
        String editedPassword = user.getPassword();
        String editedEmail = user.getEmail();
        System.out.println(confirmation);

        //Checking old password
        if (!BCrypt.checkpw(oldPassword,user.getPassword())) {
            throw new UserManagerException("You have entered wrong origin password.");
        }

        //Checking new Password
        if (!newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new UserManagerException("Passwords don't match.");
            }
            if (!loggingManager.validatePassword(newPassword)) {
                throw new UserManagerException("Weak password.");
            }
            editedPassword = BCrypt.hashpw(newPassword,BCrypt.gensalt());
        }

        //Email validation
        if (!user.getEmail().equals(email)) {
            try {
                if (!loggingManager.validateEmailAddress(email)) {
                    throw new UserManagerException("Email is not valid.");
                }
            } catch (LoggingManager.RegistrationException e) {
                throw new UserManagerException(e.getMessage());
            }
            editedEmail = email;
        }

        user.setFirstName(validateName(user.getFirstName(), firstName));
        user.setLastName(validateName(user.getLastName(), lastName));
        user.setPassword(editedPassword);
        user.setEmail(editedEmail);

        try {

            //get the generated confirmation code
            if(confirmation!=null && confirmation!=""){
                String confirmationCode = userDao.getConfirmationCode(user.getId());
                if(confirmationCode.equalsIgnoreCase(confirmation)){
                    userDao.confirmRegistration(user.getId());
                    user.setActivated();
                }
            }
            userDao.updateProfile(user);
        } catch (SQLException e) {
            LOGGER.error("Database problem occurred in updateProfileInfo(). {}", e.getMessage());
        }
        LOGGER.info("User {}, id:{} updated his profile", user.getUsername(), user.getId());
    }

    private String validateName(String oldName, String name) throws UserManagerException {
        String editedName = oldName;
        boolean nameChange = false;
        if (oldName != null) {
            if (!oldName.equals(name)) {
                nameChange = true;
            }
        } else {
            if (!name.isEmpty()) {
                nameChange = true;
            }
        }
        if (nameChange) {
            if (!(name.matches("[a-zA-Z]*") && name.length() > 0 && name.length() < 20)) {
                throw new UserManagerException("Non valid first or last name.");
            }
            editedName = name;
        }
        return editedName;
    }

    public void changeProfilePic(User user, String url) throws SQLException {
        try {
            userDao.changeProfilePic(user.getId(), url);
        } catch (SQLException e) {
            LOGGER.error("Database problem occurred in changeProfilePic() for user {}, id:{}, image url: {}. {}",user.getUsername(), user.getId(), url, e.getMessage());
            throw e;
        }
        LOGGER.info("User {}, id:{} changed profile picture.", user.getUsername(), user.getId());
    }

    public void deleteUser(User user) throws SQLException {
        try {
            userDao.deleteUser(user);
        } catch (SQLException e) {
            LOGGER.error("Database problem occurred in deleteUser() for user {}, id:{}. {}",user.getUsername(), user.getId(), e.getMessage());
            throw e;
        }
        LOGGER.info("User {}, id:{} deleted his profile.", user.getUsername(), user.getId());
    }

    public User getUser(int userID) throws SQLException {
        User user = null;
        try {
            user = userDao.getUserByID(userID);
        }catch (SQLException e){
            LOGGER.error("Database problem occurred in getUser() userID:{}. {}", userID, e.getMessage());
            throw e;
        }
        return user;
    }

    public User getUser(String username) throws SQLException {
        User user = null;
        try {
            user = userDao.getUserByUsername(username);
        }catch (SQLException e){
            LOGGER.error("Database problem occurred in getUser() username:{}. {}", username, e.getMessage());
            throw e;
        }
        return user;
    }

    public User getUserByEmail(String email) throws SQLException {
        User user = null;
        try {
            user = userDao.getUserByEmail(email);
        }catch (SQLException e){
            LOGGER.error("Database problem occurred in getUser() userID:{}. {}", user.getId(), e.getMessage());
            throw e;
        }
        return user;
    }

}