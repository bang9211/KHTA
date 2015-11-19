/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import infra.Direction;
import infra.InfraDatas;
import infra.type.RoadType;
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
public final class DataFetcher {

    private final ArrayList<HashMap<InfraDatas, Object>> corridors = new ArrayList();
    private final ArrayList<HashMap<InfraDatas, Object>> stations = new ArrayList();

    final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_NAME;
    String DB_URL;
    String USER_ID;
    String PASSWORD;
    
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

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
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setup() {
        connectDatabase();
        
        System.out.println("Setting up the Corridors");
        setCorridor();
        System.out.println("Corridor Count : " + corridors.size());
        setStation();
        System.out.println("Station Count : " + stations.size());
        
        disconnectDatabase();
    }

    private void setCorridor() {
        //Corridor로 부터 읽어서
        //해쉬맵에 저장

        String sql;

        try {
            sql = "SELECT * FROM its.corridor";
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                //Fetch a row
                String corID = resultSet.getString("ID");
                String name = resultSet.getString("name");
                RoadType roadType = RoadType.get(resultSet.getString("roadType"));
                Direction direction = Direction.get(resultSet.getString("direction"));

                //Store a row
                HashMap<InfraDatas, Object> corridor = new HashMap();
                corridor.put(InfraDatas.ID, corID);
                corridor.put(InfraDatas.NAME, name);
                corridor.put(InfraDatas.ROADTYPE, roadType);
                corridor.put(InfraDatas.DIRECTION, direction);
                corridors.add(corridor);
                /*
                System.out.println("id : " + corID + "\tCor length : " + corridors.size());
                for (HashMap<InfraDatas, Object> cor : corridors) {
                    System.out.println("CID : " + (String) cor.get(InfraDatas.ID)
                            + ", CNAME : " + (String) cor.get(InfraDatas.NAME)
                            + ", Direction : " + ((Direction) cor.get(InfraDatas.DIRECTION)));
                
                }
                */
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setStation() {
        //int 
        int slength = 1000;
        for (HashMap<InfraDatas, Object> cor : corridors) {
            String corID = (String) cor.get(InfraDatas.ID);
            Direction dr = (Direction) cor.get(InfraDatas.DIRECTION);

            String sql;

            try {
                sql = "SELECT * FROM its.station";
                resultSet = statement.executeQuery(sql);

                while (resultSet.next()) {
                    //Fetch a row
                    String staID = resultSet.getString("ID");
                    String name = resultSet.getString("name");
                    double location = resultSet.getDouble("location");
                    double startLocation = resultSet.getDouble("startLocation");
                    double endLocation = resultSet.getDouble("endLocation");
                    int type = resultSet.getInt("type");
                    int length = resultSet.getInt("length");
                    int order = resultSet.getInt("order");
                    RoadType roadType = RoadType.get(resultSet.getString("roadType"));
                    int lane = resultSet.getInt("lane");
                    float speedLimit = resultSet.getFloat("speedLimit");
                    String cor_ID = resultSet.getString("cor_ID");
                    String sec_ID = resultSet.getString("sec_ID");  //unused
                    

                    //Store a row
                    HashMap<InfraDatas, Object> station = new HashMap();
                    station.put(InfraDatas.ID, staID);
                    station.put(InfraDatas.NAME, name);
                    station.put(InfraDatas.LOCATION, location);
                    station.put(InfraDatas.START_LOC, startLocation);
                    station.put(InfraDatas.END_LOC, endLocation);
                    station.put(InfraDatas.TYPE_CODE, type);
                    station.put(InfraDatas.LENGTH, length);
                    station.put(InfraDatas.ORDER, order);
                    station.put(InfraDatas.ROADTYPE, roadType);
                    station.put(InfraDatas.LANE, lane);
                    station.put(InfraDatas.SPEED_LIMIT, speedLimit);
                    station.put(InfraDatas.COR_ID, cor_ID);
                    stations.add(station);

                    /*
                    System.out.println("id : " + corID + "\tCor length : " + corridors.size());
                    for (HashMap<InfraDatas, Object> cr : corridors) {
                        System.out.println("CID : " + (String) cr.get(InfraDatas.ID)
                                + ", CNAME : " + (String) cr.get(InfraDatas.NAME)
                                + ", Direction : " + ((Direction) cr.get(InfraDatas.DIRECTION)).toString());
                    }
                    */
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public ArrayList<HashMap<InfraDatas, Object>> getCorridors() {
        return corridors;
    }

    public ArrayList<HashMap<InfraDatas, Object>> getStations(String Corid) {
        ArrayList<HashMap<InfraDatas, Object>> selStations = new ArrayList();
        for (HashMap<InfraDatas, Object> ss : stations) {
            String scid = (String) ss.get(InfraDatas.COR_ID);
            if (scid.equals(Corid)) {
                selStations.add(ss);
            }
        }
        return selStations;
    }
}
