/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import infra.Direction;
import infra.InfraDatas;
import infra.Period;
import infra.infraobject.Detector;
import infra.type.RoadType;
import infra.type.StationType;
import infra.type.TrafficType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class InfraDataFetcher extends DataFetcher{
    
    private final ArrayList<HashMap<InfraDatas, Object>> corridors = new ArrayList();
    private final ArrayList<HashMap<InfraDatas, Object>> stations = new ArrayList();
    
    public InfraDataFetcher() {
        super(KHTAParam.DB_NAME, KHTAParam.DB_URL, KHTAParam.USER_ID, KHTAParam.PASSWORD);
    }
    
    public void setup() {
        System.out.println("Connecting Database...");
        connectDatabase();
        System.out.println("Setting up the Corridors");
        setCorridor();
        System.out.println("Corridor Count : " + corridors.size());
        System.out.println("Setting up the Stations");
        setStation();
        System.out.println("Station Count : " + stations.size());
        
        disconnectDatabase();
    }
    
    private void setCorridor() {
        //Corridor로 부터 읽어서
        //해쉬맵에 저장

        String sql;

        try {
            sql = "SELECT * FROM korex.corridor ORDER BY NAME asc";
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
    
    private void test(){
        String sql;

        try {
            sql = "SELECT * FROM korex.volume_data where station_id = '0100VDE00100' order by time asc;";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                //Fetch a row
                String staID = resultSet.getString("station_ID");
                int data = resultSet.getInt("volume");
                System.out.println("sid : "+staID+", data : "+data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setStation() {
        //int 
        int slength = 1000;
        String sql;

        try {
            sql = "SELECT * FROM korex.station_infra";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                //Fetch a row
                String staID = resultSet.getString(InfraDatas.ID.getTableName());
                String name = resultSet.getString(InfraDatas.NAME.getTableName());
                double location = resultSet.getDouble(InfraDatas.LOCATION.getTableName());
                double startLocation = resultSet.getDouble(InfraDatas.START_LOC.getTableName());
                double endLocation = resultSet.getDouble(InfraDatas.END_LOC.getTableName());
                StationType type = StationType.getType(resultSet.getInt(InfraDatas.STATION_TYPE.getTableName()));
                int length = resultSet.getInt(InfraDatas.LENGTH.getTableName());
                int order = resultSet.getInt(InfraDatas.ORDER.getTableName());
                RoadType roadType = RoadType.get(resultSet.getString(InfraDatas.ROADTYPE.getTableName()));
                int lane = resultSet.getInt(InfraDatas.LANE.getTableName());
                float speedLimit = resultSet.getFloat(InfraDatas.SPEED_LIMIT.getTableName());
                String cor_ID = resultSet.getString(InfraDatas.COR_ID.getTableName());
                String section_name = resultSet.getString(InfraDatas.SECTION_NAME.getTableName());
                int isbusLane = resultSet.getInt(InfraDatas.ISBUSLANE.getTableName());
                String sec_ID = resultSet.getString(InfraDatas.SECTION_ID.getTableName());


                //Store a row
                HashMap<InfraDatas, Object> station = new HashMap();
                station.put(InfraDatas.ID, staID);
                station.put(InfraDatas.NAME, name);
                station.put(InfraDatas.LOCATION, location);
                station.put(InfraDatas.START_LOC, startLocation);
                station.put(InfraDatas.END_LOC, endLocation);
                station.put(InfraDatas.STATION_TYPE, type);
                station.put(InfraDatas.LENGTH, length);
                station.put(InfraDatas.ORDER, order);
                station.put(InfraDatas.ROADTYPE, roadType);
                station.put(InfraDatas.LANE, lane);
                station.put(InfraDatas.SPEED_LIMIT, speedLimit);
                station.put(InfraDatas.COR_ID, cor_ID);
                station.put(InfraDatas.SECTION_NAME, section_name);
                station.put(InfraDatas.ISBUSLANE, isbusLane);
                station.put(InfraDatas.SECTION_ID, sec_ID);
                
                stations.add(station);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<HashMap<InfraDatas, Object>> getCorridors() {
        return corridors;
    }

    public ArrayList<HashMap<InfraDatas, Object>> getStations(String Corid) {
        ArrayList<HashMap<InfraDatas, Object>> selStations = new ArrayList();
        for (HashMap<InfraDatas, Object> ss : stations) {
            String scid = (String) ss.get(InfraDatas.COR_ID);
//            System.out.println("Corid : "+Corid + ", scid : "+scid); 
            if (scid.equals(Corid)) {
                selStations.add(ss);
            }
        }
        return selStations;
    }
    
}
