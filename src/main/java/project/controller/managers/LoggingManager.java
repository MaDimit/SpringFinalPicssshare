package project.controller.managers;


import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.UserDao;
import project.model.pojo.User;

import java.sql.SQLException;

@Component
public class LoggingManager {

    public static class RegistrationException extends InfoException {
        public RegistrationException(String msg) {
            super(msg);
        }
    }

    public static class LoggingException extends InfoException {
        public LoggingException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private UserDao userDao;


    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingManager.class);

    //========================REGISTER PART===================================//

    public User register(String username, String password, String email) throws RegistrationException, SQLException {

        //Username validating
        validateUsername(username);

        if (!validatePassword(password)) {
            throw new RegistrationException("Weak password.");
        }
        if (!validateEmailAddress(email)) {
            throw new RegistrationException("Email is not valid.");
        }

        //if data is valid user obj is created
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, password, email);
        user.setProfilePicUrl("defaultAvatar.png");
        //adding to DB
        String code;
        try {
            code = SendMailSSL.randomStringGenerator.generateString();
            userDao.registerUser(user, code);
        } catch (SQLException e) {
           LOGGER.error("Data base exception occurred in register() for user {}, id:{} . {}", user.getUsername(), user.getId(), e.getMessage());
           throw e;
        }

        //send register mail in another thread in order not to delay registration proccess
        new Thread(){
            @Override
            public void run() {
                SendMailSSL.sendMail(user.getUsername(), user.getEmail(), code);
            }

        }.start();
        LOGGER.info("User {}, id:{} registered", user.getUsername(), user.getId());
        return user;
    }

    //========================VALIDATIONS===================================//
    // validate username
    public boolean validateUsername(String username) throws RegistrationException, SQLException {
        if (username == null || username.isEmpty()) {
            throw new RegistrationException("Empty username.");
        }
        if (!username.matches("^[a-zA-Z0-9]+([_ -]?[a-zA-Z0-9])*$")) {
            throw new RegistrationException("Username consists non valid characters.");
        }
        try {
            if(userDao.checkIfUsernameIsTaken(username)){
                throw new RegistrationException("Username is taken.");
            }
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in validateUsername() for username {}. {}",username, e.getMessage());
            throw e;
        }
        return true;
    }

    /*
     * This regex will enforce these rules for password:
     *
     * At least one upper case English letter, (?=.*?[A-Z])
     * At least one lower case English letter, (?=.*?[a-z])
     * At least one digit, (?=.*?[0-9])
     * Minimum eight in length .{8,} (with the anchors)
     */
    public boolean validatePassword(String password) {
        return (password != null && !password.isEmpty()
                && password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$"));
    }

    // validate email address
    public boolean validateEmailAddress(String email) throws RegistrationException, SQLException {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);

        try {
            if (userDao.checkIfEmailIsTaken(email)) {
                throw new RegistrationException("Email is already taken.");
            }
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in validateEmailAddress(). {}", e.getMessage());
            throw e;
        }
        return m.matches();
    }



    //=========================LOGGING=======================================//

    //Logging by username and password ----> //
    public User login(String username, String password) throws LoggingException, SQLException {
        User user = null;
        try {
            user = userDao.login(username);
            if (user == null) {
                throw new LoggingException("Wrong username.");
            }
            if (!BCrypt.checkpw(password,user.getPassword())) {
                throw new LoggingException("Wrong password.");
            }
        } catch (SQLException e) {
           LOGGER.error("Data base exception occurred in login(). {}", e.getMessage());
           throw e;
        }
        LOGGER.info("User {}, id:{} logged in", user.getUsername(),user.getId());
        return user;
    }


}
