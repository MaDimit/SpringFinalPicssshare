package project.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.LoggingManager;
import project.model.pojo.SubscriberUserPojo;
import project.model.pojo.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class UserDao {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private LoggingManager loggingManager;

    public UserDao() throws SQLException {
    }

    //================== User Interface ==================//

    //used for search
    public ArrayList<User> getAllUsersByPattern(String pattern) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ArrayList<User> matchingUsers = new ArrayList<>();
            String sql = "SELECT id FROM users WHERE UPPER(username) LIKE UPPER('%" + pattern + "%') ORDER BY username;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            //  stmt.setString(1,pattern);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("id");
                matchingUsers.add(getUserByID(userID));
            }
            return matchingUsers;
        }
    }

    public User getUserByID(int id) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, username, password, first_name, last_name, email, profile_picture_url FROM users WHERE users.id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return createUser(resultSet);
            }
            return null;
        }
    }

    public void addSubscription(User subscriber, User subscribedTo) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO subscriber_subscribed (subscriber_id, subscribedto_id) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subscriber.getId());
            stmt.setInt(2, subscribedTo.getId());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public void removeSubscription(User subscriber, User subscribedTo) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM subscriber_subscribed WHERE subscriber_id = ? AND subscribedto_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subscriber.getId());
            stmt.setInt(2, subscribedTo.getId());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public void deleteUser(User user) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public ArrayList<SubscriberUserPojo> getAllSubscriptions(int subscriber_id) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ArrayList<SubscriberUserPojo> subscriptions = new ArrayList<>();
            String sql = "SELECT subscribedto_id FROM subscriber_subscribed WHERE subscriber_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subscriber_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int subscribedToID = rs.getInt("subscribedto_id");
                String subscribedToUsername = getUserByID(subscribedToID).getUsername();
                String subscriberToProfilePic = getUserByID(subscribedToID).getProfilePicUrl();
                SubscriberUserPojo user = new SubscriberUserPojo(subscribedToID, subscribedToUsername, subscriberToProfilePic);
                subscriptions.add(user);
            }
            return subscriptions;
        }
    }

    public List<String> searchUsers(String input) throws SQLException{
        try (Connection conn = dataSource.getConnection()){
            List<String> users = new ArrayList<>();
            String sql = "SELECT username FROM users WHERE username LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, input);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                users.add(rs.getString("username"));
            }
            return users;
        }
    }

    // username and id should not be modified
    public void executeProfileUpdate(User u, String password, String first_name, String last_name, String email)
            throws SQLException, LoggingManager.RegistrationException {

        try (Connection conn = dataSource.getConnection()) {

            // TODO Exctract notnull validation to UserManager
            // Store in collection not null values, because the user could choose to change
            // different number of
            // information for his profile

            HashMap<String, String> notNullValues = new HashMap<>();

            if (loggingManager.validatePassword(password)) {
                notNullValues.put("password", password);
                u.setPassword(password);
            }
            else throw new LoggingManager.RegistrationException("Invalid format for the new password.");

            if (loggingManager.validateFirstName(first_name)) {
                notNullValues.put("first_name", first_name);
                u.setFirstName(first_name);

            }
            else throw new LoggingManager.RegistrationException("Error. Maybe you have used illegal characters for first name?");


            if (loggingManager.validateLastName(last_name)) {
                notNullValues.put("last_name", last_name);
                u.setLastName(last_name);

            }
            else throw new LoggingManager.RegistrationException("Error. Maybe you have used illegal characters for last name?");

            // check the existing ones and if there is not such an email - set it
            if (email.equals(u.getEmail())) {
                notNullValues.put("email", email);
            } else {
                //if email is different
                if (loggingManager.validateEmailAddress(email)) {
                    //if new email is not taken
                    if (!checkIfEmailIsTaken(email)) {
                        notNullValues.put("email", email);
                        u.setEmail(email);
                    } else
                        throw new LoggingManager.RegistrationException("This email is already taken. Try with other...");

                } else throw new LoggingManager.RegistrationException("Invalid format for the new email address.");


                System.out.println("EMAIL SET");
            }


            StringBuilder sb = new StringBuilder();
            // comma count is used for placing commas between set statements
            int commaCounter = 0;
            for (Map.Entry<String, String> entry : notNullValues.entrySet()) {
                commaCounter++;
                sb.append(entry.getKey());
                sb.append("='");
                sb.append(entry.getValue());
                sb.append("'");
                if (commaCounter > 0 && commaCounter < notNullValues.size()) {
                    sb.append(", ");
                }

            }
            String sql = "UPDATE users SET " + sb.toString() + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, u.getId());
            System.out.println(sql);
            stmt.executeUpdate();
            System.out.println("EXECUTED");
            stmt.close();
        }
    }

    public void changeProfilePic(int userID, String url) throws SQLException{
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE users SET profile_picture_url = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, url);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            stmt.close();
        }
    }

    //================== Logging and Registration ==================//

    public boolean checkIfUsernameIsTaken(String username) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT users.username FROM users WHERE users.username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            //return true if exists and false if not
            return resultSet.next();
        }
    }

    public boolean checkIfEmailIsTaken(String email) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT users.username FROM users WHERE users.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            //return true if exists and false if not
            return resultSet.next();
        }
    }

    public User login(String username) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, username, password,first_name,last_name,email,profile_picture_url FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            User user = createUser(rs);
            stmt.close();
            return user;
        }
    }


    public void registerUser(User user) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            // Inserting into DB
            String sql = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();

            // Getting id for registered user
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            stmt.close();
        }
    }

    //================== User Creation ==================//

    User createUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstname = rs.getString("first_name");
        String lastname = rs.getString("last_name");
        String email = rs.getString("email");
        String profilePicUrl = rs.getString("profile_picture_url");
        return new User(id, username, password, firstname, lastname, email, profilePicUrl);
    }

}