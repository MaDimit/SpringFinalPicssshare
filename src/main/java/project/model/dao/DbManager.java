package project.model.dao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//@Component
//class DbManager {
//
//    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
//
//    // Database credentials
//    private final String DB_URL;
//    private final String USER;
//    private final String PASS;
//    private final String SCHEMA;
//
//    private Connection connection;
//
//    private DbManager() {
//        this.DB_URL = "jdbc:mysql://127.0.0.1:30249/picssshare?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//        this.USER = "root";
//        this.PASS = "Mdu526143978";
//        this.SCHEMA = "picssshare";
//
//        try {
//            Class.forName(JDBC_DRIVER);
//            connection = DriverManager.getConnection(DB_URL,USER,PASS);
//
//        } catch (ClassNotFoundException e) {
//            System.err.println("Driver loading error.");
//        } catch(SQLException se) {
//            System.err.println("Connection creating error: " + se.getMessage());
//        }
//
//        useStatment(this.SCHEMA);
//    }
//
//    public Connection getConnection() {
//        return connection;
//    }
//
//    private void useStatment(String schema) {
//        String sql = "USE " + schema;
//        PreparedStatement stmt;
//
//        try {
//            stmt = connection.prepareStatement(sql);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("Problem during USE statment: " + e.getMessage());
//        }
//    }
//
//}