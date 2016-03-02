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
package infra.simobjects;

import infra.Infra;
import infra.infraobject.Detector;
import infra.infraobject.RampMeter;
import infra.infraobject.Station;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * for Simulation
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimObjects {

    private Infra infra = Infra.getInstance();
//    
//    private static SimObjects realInstance = new SimObjects();
//    private static ArrayList<SimObjects> simInstances = new ArrayList<SimObjects>();
//    
    private HashMap<String, SimStation> stations = new HashMap<String, SimStation>();
    private HashMap<String, SimDMS> dmss = new HashMap<String, SimDMS>();
    private HashMap<String, SimMeter> meters = new HashMap<String, SimMeter>();
    private HashMap<String, SimDetector> detectors = new HashMap<String, SimDetector>();
//
//    //Random seed
    private int Randomseed = -1;
//    
//    public static SimObjects getInstance() {
//        return realInstance;
//    }
//    
//    public static ArrayList<SimObjects> getArrayListInstance(){
//        return simInstances;
//    }
//    
    public SimObjects(){
        
    }
    
    /**
     * 
     * @param station_id station id (e.g. S42)
     * @return simulation station object
     */    
    public SimStation getStation(String station_id)
    {
        SimStation s = stations.get(station_id);
        if(s == null) {
            Station st = infra.getStation(station_id);
            if(st == null) return null;
            else {
                SimStation ss = new SimStation(st, this);
                this.stations.put(station_id, ss);
                return ss;
            }
        } else return s;
    }
    
    public SimDetector getDetector(String detector_id)
    {
        SimDetector sd = detectors.get(detector_id);
        if(sd == null) {
            //System.out.println("detector id : "+detector_id);
            Detector d = infra.getSimulationDetector(detector_id);
            if(d == null) return null;
            else {
                SimDetector dd = new SimDetector(d);
                this.detectors.put(detector_id, dd);
                return dd;
            }
        } else return sd;
    }    
    
    public SimDetector getDetectorWithoutNull(String detector_id){
        SimDetector sd = detectors.get(detector_id);
        return sd;
    }

    public SimMeter getMeter(String meter_id)
    {
        SimMeter sd = meters.get(meter_id);
        if(sd == null) {
            RampMeter d = infra.getMeter(meter_id);
            if(d == null) {
                System.err.println("There no RampMeter in KHTA");
                return null;
            }
            else {
                sd = new SimMeter(d,this);
                this.meters.put(meter_id, sd);
                return sd;
            }
        } else return sd;
    }       

    /**
     * @see 
     * @param dms_id
     * @return 
     */
    // FIXME : infra doesn't have DMS objects 
    // because tms_xml.config doesn't include DMS information
    public SimDMS getDms(String dms_id)
    {
        throw new UnsupportedOperationException("Not supported yet.");
//        SimDMS sd = dmss.get(dms_id);
//        if(sd == null) {
//            // it may be returned null
//            DMS d = tmo.getInfra().getDMS(dms_id);
//            if(d == null) {
//                d = new DMS(dms_id);
//                sd = new SimDMS(d);
//                // add dms into infra, because infra doesn't have DMS                 
//                tmo.getInfra().addInfraObject(d);
//                this.dmss.put(dms_id, sd);
//                return sd;
//            }
//            else {
//                SimDMS dd = new SimDMS(d);
//                this.dmss.put(dms_id, dd);
//                return dd;
//            }
//        } else return sd;
    }       

    public void reset() {
        loopReset(this.detectors);
        loopReset(this.dmss);
        loopReset(this.meters);
        loopReset(this.stations);
    }
    
    private <T> void loopReset(HashMap<String, T> map) {
        Iterator<T> itr = map.values().iterator();
        while(itr.hasNext()) {
            ((SimObject)itr.next()).reset();            
        }
    }
    
    public void setRandomSeed(int seed){
        this.Randomseed = seed;
    }
    public int getRandomSeed(){
        return this.Randomseed;
    }
}
