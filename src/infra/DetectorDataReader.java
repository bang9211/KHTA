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
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DetectorDataReader {
    private Period period;
    private Detector detector;
    private boolean isLoaded = false;
    protected HashMap<TrafficType, Vector<Double>> dataMap = new HashMap();
    
    public DetectorDataReader(Detector detector, Period period){
        this.detector = detector;
        this.period = period;
        dataMap.put(TrafficType.VOLUME, new Vector<Double>());
        dataMap.put(TrafficType.SPEED, new Vector<Double>());
        dataMap.put(TrafficType.DENSITY, new Vector<Double>());
    }
    
    /**
     * Data Load from Database
     */
    public void load(){
        loadDatafromDB(TrafficType.VOLUME,dataMap.get(TrafficType.VOLUME));
        loadDatafromDB(TrafficType.SPEED,dataMap.get(TrafficType.SPEED));
        isLoaded = true;
        //adjustTrafficData(read(TrafficType.VOLUME), read(TrafficType.SPEED));
    }
    
    public double[] read(TrafficType trafficType){
        if(!isLoaded) load();
        Vector<Double> data = dataMap.get(trafficType);
        double[] pdata = new double[data.size()];
        for(int i=0;i<pdata.length;i++) pdata[i] = data.get(i);
        return pdata;
    }

    /**
     * Load Data from DB by Period
     * @param trafficType
     * @param data 
     */
    private void loadDatafromDB(TrafficType trafficType, Vector<Double> data) {
        
    }
}
