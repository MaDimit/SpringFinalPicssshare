package project.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.pojo.DTO.SearchDTO;
import project.model.pojo.DTO.SubscriberUserDTO;
import project.model.pojo.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDao {

    @Autowired
    private DataSource dataSource;

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
    public User getUserByEmail(String email) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, username, password, first_name, last_name, email, profile_picture_url FROM users WHERE users.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return createUser(resultSet);
            }
            return null;
        }
    }

    public void changeUserPassword(String newPassword, int userID) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            stmt.close();
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

    public User getUserByUsername(String username) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, username, password, first_name, last_name, email, profile_picture_url FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
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

    public List<SubscriberUserDTO> getAllSubscriptions(int subscriber_id) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ArrayList<SubscriberUserDTO> subscriptions = new ArrayList<>();
            String sql = "SELECT subscribedto_id FROM subscriber_subscribed WHERE subscriber_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subscriber_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int subscribedToID = rs.getInt("subscribedto_id");
                String subscribedToUsername = getUserByID(subscribedToID).getUsername();
                String subscriberToProfilePic = getUserByID(subscribedToID).getProfilePicUrl();
                SubscriberUserDTO user = new SubscriberUserDTO(subscribedToID, subscribedToUsername, subscriberToProfilePic);
                subscriptions.add(user);
            }
            return subscriptions;
        }
    }

    public List<SearchDTO.SearchedUser> searchUsers(String input) throws SQLException{
        try (Connection conn = dataSource.getConnection()){
            List<SearchDTO.SearchedUser> users = new ArrayList<>();
            String sql = "SELECT username, id FROM users WHERE username LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, input);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                users.add(new SearchDTO.SearchedUser(rs.getString("username"), rs.getInt("id")));
            }
            return users;
        }
    }

    public void updateProfile(User user) throws SQLException{
        try(Connection conn = dataSource.getConnection()){
            String sql = "UPDATE users SET password = ?, first_name = ?, last_name = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
        }
    }

    public String getConfirmationCode(int userID) throws SQLException {
        String confirmationCode=null;
        try(Connection conn = dataSource.getConnection()){
            String sql = "SELECT activation_code FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                confirmationCode=rs.getString("activation_code");
            }
        }
        return confirmationCode;
    }

    public void confirmRegistration(int userID) throws SQLException {
        try(Connection conn = dataSource.getConnection()){
            String sql = "UPDATE users SET activation_code=null WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
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
            String sql = "SELECT id, username, password,first_name,last_name,email,profile_picture_url, activation_code FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            boolean activated=true;
            if (!rs.next()) {
                return null;

            }
            else{
                String activationCode = rs.getString("activation_code");
                if(activationCode!=null){
                    activated = false;
                }
            }
            User user = createUser(rs);
            //if it is activated - set it
            if(activated){
                user.setActivated();
            }
            stmt.close();
            return user;
        }
    }


    public void registerUser(User user, String codeGenerated) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            // Inserting into DB
            String sql = "INSERT INTO users (username, password, email, profile_picture_url, activation_code) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getProfilePicUrl());
            stmt.setString(5, codeGenerated);
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