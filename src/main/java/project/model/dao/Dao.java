package project.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

 class Dao {
     @Autowired
    protected DbManager dbManager;

    protected Connection conn;


}