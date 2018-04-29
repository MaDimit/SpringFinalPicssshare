package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.UserDao;
import project.model.pojo.User;

import java.sql.SQLException;

@Component
public class UserManager {

    public static class UserManagerException extends Exception{
        private UserManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private UserDao userDao;


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

    public boolean removeSubscription(User subscriber, User subscribedTo) throws UserManagerException{
        validatesubscribtion(subscriber,subscribedTo);

        try {
            userDao.removeSubscription(subscriber, subscribedTo);
        }catch (SQLException e){
            throw new UserManagerException("Problem during unsubscribing");
        }
        return true;
    }

    private void validatesubscribtion(User subscriber, User subscribedTo) throws UserManagerException{
        if(subscribedTo == null || subscriber == null){
            throw new UserManagerException("User you trying to subscribe to does not exist");
        }
    }


    public void updateProfileInfo(User user, String password, String first_name, String last_name, String email) throws SQLException, LoggingManager.RegistrationException {
        //TODO add not null validation here instead of UserDao
        userDao.executeProfileUpdate(user,password,first_name,last_name,email);
    }

    public void changeProfilePic(User user, String url)throws SQLException{
        userDao.changeProfilePic(user.getId(), url);
    }

    public void deleteUser(User user) throws UserManagerException{
        try {
            userDao.deleteUser(user);
        }catch (SQLException e){
            throw new UserManagerException("Problem during account deletion.");
        }
    }

    public User getUser(int userID) throws SQLException{
        return userDao.getUserByID(userID);
    }

    public User getUser(String username) throws SQLException{
        return userDao.getUserByUsername(username);
    }

}