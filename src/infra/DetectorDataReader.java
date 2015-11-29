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
package infra;

import infra.infraobject.Detector;
import infra.type.TrafficType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import exception.DBException;
import util.DataFetcher;
import util.KHTAParam;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DetectorDataReader extends DataFetcher{
    private Period period;
    private Detector detector;
    private boolean isLoaded = false;
    protected HashMap<TrafficType, Vector<Double>> dataMap = new HashMap();
    protected HashMap<TrafficType, String> TableMap = new HashMap();
    
    //DB field Name
    public static final String STATION_ID = "station_id";
    public static final String S_DATE = "s_date";
    public static final String LANETYPE = "lanetype";
    public static final String VOLUME = "volume";
    
    //DB TABLE NAME
    public static final String VOLUME_TABLE = "volume";
    public static final String SPEED_TABLE = "speed";
    
    public DetectorDataReader(Detector detector, Period period){
        super(KHTAParam.DB_NAME, KHTAParam.DB_URL, KHTAParam.USER_ID, KHTAParam.PASSWORD);
        this.detector = detector;
        this.period = period;
        dataMap.put(TrafficType.VOLUME, new Vector<Double>());
        dataMap.put(TrafficType.SPEED, new Vector<Double>());
//        dataMap.put(TrafficType.DENSITY, new Vector<Double>());
        
        TableMap.put(TrafficType.VOLUME, VOLUME_TABLE);
        TableMap.put(TrafficType.SPEED, SPEED_TABLE);
    }
    
    /**
     * Data Load from Database
     */
    public void load() throws DBException{
//        System.out.println(detector.getID()+" - Start DB");
        try {
//            System.out.println(detector.getID()+" - Connection");
            this.connectDatabase();
        } catch (ClassNotFoundException ex) {
            throw new DBException(DBException.DBEX.ConnectionError);
        } catch (SQLException ex) {
            throw new DBException(DBException.DBEX.ConnectionError);
        }
        
        try {
            
            loadDatafromDB(TableMap.get(TrafficType.VOLUME),dataMap.get(TrafficType.VOLUME));
            loadDatafromDB(TableMap.get(TrafficType.SPEED),dataMap.get(TrafficType.SPEED));
        } catch (Exception ex) {
            throw new DBException(DBException.DBEX.LoadError);
        }
        
        try {
            this.disconnectDatabase();
        } catch (SQLException ex) {
            throw new DBException(DBException.DBEX.DisconnectionError);
        }
        isLoaded = true;
    }
    
    public double[] read(TrafficType trafficType) throws DBException{
        if(!isLoaded) load();
        Vector<Double> data = dataMap.get(trafficType);
        if(data == null) return null;
        double[] pdata = new double[data.size()];
        for(int i=0;i<pdata.length;i++) pdata[i] = data.get(i);
        return pdata;
    }

    /**
     * Load Data from DB by Period
     * @param trafficType
     * @param data 
     */
    private void loadDatafromDB(String tableName, Vector<Double> data) throws Exception{
//        System.out.println(detector.getIDforDB());
        String sql = "SELECT * FROM korex."+tableName+"  use index(sid_date) where "
                + STATION_ID+"='"+detector.getIDforDB()+"' and "
                + S_DATE+" > '"+period.getStartDateString()+"' and "
                + S_DATE+" <= '"+period.getEndDateString()+"' and "
                + LANETYPE+" = '"+detector.getLaneType().code+"' "
                + "order by "+S_DATE+" asc";
        
//        System.out.println(sql);
        
        int idx = 0;
        resultSet = statement.executeQuery(sql);
        HashMap<String,Double> tdata = new HashMap();
        
        while(resultSet.next()){
            String sid = resultSet.getString("station_id");
            String s_date = resultSet.getString("s_date");
            int ltype = resultSet.getInt("lanetype");
            double ddata = resultSet.getDouble("data");
            
//            System.out.println("sid-"+sid+", s_date-"+s_date
//                    +", ltype-"+ltype+", data-"+ddata);
            
            tdata.put(s_date.split("\\.")[0], ddata);
        }
        
        for(String ct : period.getTimelineWithDate()){
            Double d = tdata.get(ct);
//            System.out.println(ct+" "+d);
            
            if(d!=null)
                data.add(d);
            else
                data.add((double)-1);
        }
    }
}
