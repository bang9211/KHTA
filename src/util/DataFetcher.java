/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import infra.Direction;
import infra.InfraDatas;
import infra.Period;
import infra.infraobject.Detector;
import infra.type.RoadType;
import infra.type.StationType;
import infra.type.TrafficType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HanYoungTak
 */
public class DataFetcher {

    final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_NAME;
    String DB_URL;
    String USER_ID;
    String PASSWORD;
    
    protected Connection connection = null;
    protected Statement statement = null;
    protected ResultSet resultSet = null;

    public DataFetcher(String dbName, String dbURL, String userID, String password){
        DB_NAME = dbName;
        DB_URL = dbURL;
        USER_ID = userID;
        PASSWORD = password;
    }
    
    public void connectDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER_ID, PASSWORD);
            System.out.println("MySQL Connection");
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnectDatabase() {
        try {
            if(resultSet != null) resultSet.close();
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
