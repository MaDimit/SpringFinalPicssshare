package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.UserDao;
import project.model.pojo.SubscriberUserPojo;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class UserManager {

    public static class UserManagerException extends Exception{
        public UserManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private UserDao userDao;
    @Autowired
    private LoggingManager loggingManager;


    public ArrayList<SubscriberUserPojo> getAllSubscriptions(int subscriberID) throws UserManagerException {
        try {
            return userDao.getAllSubscriptions(subscriberID);
        } catch (SQLException e) {
            throw new UserManagerException("Problem during getting subscription.");
        }
    }

    public boolean subscribe(User subscriber, User subscribedTo)throws  UserManagerException{
        validatesubscribtion(subscriber,subscribedTo);

        try {
            userDao.addSubscription(subscriber, subscribedTo);
        }catch (SQLException e){
            if(e.getMessage().startsWith("Duplicate entry")){
                throw new UserManagerException("You have already subscribed to this user.");
            }
            throw new UserManagerException("Problem during subscription.");
        }
        return true;
    }


    public boolean removeSubscription(User subscriber, User subscribedTo)throws  UserManagerException{
        validatesubscribtion(subscriber,subscribedTo);

        try {
            userDao.removeSubscription(subscriber, subscribedTo);
        }catch (SQLException e){
            if(e.getMessage().startsWith("Duplicate entry")){
                throw new UserManagerException("You have already unsubscribed to this user.");
            }
            throw new UserManagerException("Problem during unsubscription.");
        }
        return true;
    }

    private void validatesubscribtion(User subscriber, User subscribedTo) throws UserManagerException{
        if(subscribedTo == null || subscriber == null){
            throw new UserManagerException("User you trying to subscribe to does not exist");
        }
    }

    public void updateProfileInfo(User user, String oldPassword, String newPassword, String confirmPassword, String firstName, String lastName, String email) throws UserManagerException, SQLException{
        String editedPassword = oldPassword;
        String editedFirstName = user.getFirstName();
        String editedLastName = user.getLastName();
        String editedEmail = user.getEmail();

        //Checking old password
        if(!user.getPassword().equals(oldPassword)){
            throw new UserManagerException("You have entered wrong origin password.");
        }

        //Checking new Password
        if(!oldPassword.isEmpty() && !newPassword.isEmpty()){
            if(!newPassword.equals(confirmPassword)){
                throw new UserManagerException("Passwords don't match.");
            }
            if(!loggingManager.validatePassword(newPassword)){
                throw new UserManagerException("Weak password.");
            }
            editedPassword = newPassword;
        }

        //Email validation
        if(!user.getEmail().equals(email)){
            try{
                if(!loggingManager.validateEmailAddress(email)){
                    throw new UserManagerException("Email is not valid.");
                }
            }catch (LoggingManager.RegistrationException e){
                throw new UserManagerException(e.getMessage());
            }
            editedEmail = email;
        }
        editedFirstName = validateName(user.getFirstName(), firstName);
        editedLastName = validateName(user.getLastName(), lastName);

        user.setPassword(editedPassword);
        user.setFirstName(editedFirstName);
        user.setLastName(editedLastName);
        user.setEmail(editedEmail);

        userDao.updateProfile(user);
    }

    private String validateName(String oldName, String name) throws UserManagerException{
        String editedName = oldName;
        boolean nameChange = false;
        if(oldName != null){
            if(!oldName.equals(name)){
                nameChange = true;
            }
        }else{
            if(!name.isEmpty()){
                nameChange = true;
            }
        }
        if(nameChange){
            if(!(name.matches( "[a-zA-Z]*" ) && name.length() > 0 && name.length() < 20)){
                throw new UserManagerException("Non valid first or last name.");
            }
            editedName = name;
        }
        return editedName;
    }

    public void changeProfilePic(User user, String url) throws UserManagerException {
        try {
            userDao.changeProfilePic(user.getId(), url);
        } catch (SQLException e) {
            throw new UserManagerException("Problem during changing profile picture.");
        }
    }

    public void deleteUser(User user) throws UserManagerException{
        try {
            userDao.deleteUser(user);
        }catch (SQLException e){
            throw new UserManagerException("Problem during account deletion.");
        }
    }

    public User getUser(int userID) throws UserManagerException {
        try {
            return userDao.getUserByID(userID);
        } catch (SQLException e) {
            throw new UserManagerException("Problem during getting user by ID.");
        }
    }

    public User getUser(String username) throws UserManagerException {
        try {
            return userDao.getUserByUsername(username);
        } catch (SQLException e) {
            throw new UserManagerException("Problem during getting user by username.");
        }
    }

}