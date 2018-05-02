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


    public void updateProfileInfo(User user, String password, String first_name, String last_name, String email) throws LoggingManager.RegistrationException, UserManagerException {
        //TODO add not null validation here instead of UserDao
        try {
            userDao.executeProfileUpdate(user,password,first_name,last_name,email);
        } catch (SQLException e) {
            throw new UserManagerException("Problem during updating user info.");
        }
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