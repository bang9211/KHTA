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
package infra.simulation;

import infra.*;
import infra.infraobject.*;
import infra.simobjects.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SectionHelper {
    
    private Section section;
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<StationState> stationStates = new ArrayList<StationState>();
    private ArrayList<EntranceState> entranceStates = new ArrayList<EntranceState>();
    private ArrayList<ExitState> exitStates = new ArrayList<ExitState>();
//    private ArrayList<DMSImpl> DMSs = new ArrayList<DMSImpl>();
    private SimObjects simObjects = null;
    private ArrayList<SimMeter> meters;
    private ArrayList<SimDetector> detectors;
    private ArrayList<SimDMS> simDMSs;
    private SimInterval SimulationInterval;
    
    public SectionHelper(Section section, ArrayList<SimDetector> detectors, ArrayList<SimMeter> meters, ArrayList<SimDMS> simdms,
            SimInterval simInterval, SimObjects sobj) {
        this.section = section;
        this.detectors = detectors; 
        this.meters = meters;
        simDMSs = simdms;
        SimulationInterval = simInterval;
        simObjects = sobj;
        init();
    }

    /**
     * Returns station state according to station id
     * @param station_id
     * @return station state
     */
    public StationState getStationState(String station_id) {
        for (State s : states) {
            if (s.type.isStation() && station_id.equals(s.rnode.getID())) {
                return (StationState) s;
            }
        }
        return null;
    }

    /**
     * Returns all station states
     * @return station state list
     */
    public ArrayList<StationState> getStationStates() {
        return stationStates;
    }

    /**
     * Returns all entrance states
     * @return entrance state list
     */
    public ArrayList<EntranceState> getEntranceStates() {
        return getEntranceStates(false);
    }
    
    public ArrayList<EntranceState> getEntranceStates(boolean hasMeter){
        if(!hasMeter)
            return this.entranceStates;
        ArrayList<EntranceState> ess = new ArrayList<EntranceState>();
        for(EntranceState es : this.entranceStates){
            if(es.hasMeter())
                ess.add(es);
        }
        return ess;
    }
    
    public int getEntranceCount(boolean hasMeter){
        if(!hasMeter)
            return this.entranceStates.size();
        
        int cnt = 0;
        for(EntranceState es : this.entranceStates){
            if(es.hasMeter())
                cnt ++;
        }
        return cnt;
    }
    
    /**
     * Is station in VISSIM case file?
     * @param s station
     * @return true if given station in the case
     */
    private boolean isInMap(Station s) {
        Detector[] dets = s.getDetectorList();
        for (Detector d : dets) {
            if(d == null){
                return false;
            }
            for (SimDetector sd : detectors) {
                if (sd != null && sd.getDetectorId() == d.getID()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Initialize
     *   - Build up section structure
     */
    private void init() {
        Vector<Station> errorStation = new Vector<Station>();
        
        for (RNode rn : section.getRNodesWithExitEntrance()) {
            State prev = null;
            if (!states.isEmpty()) {
                prev = states.get(states.size() - 1);
            }
            
            StateInterval sitv = null;
            if(SimulationInterval != null)
                    sitv = SimulationInterval.getState(rn.getID());
            
            if (rn.getNodeType().isStation()) {
                if (!isInMap((Station) rn)) {
                    errorStation.add((Station)rn);
                    continue;
                }
                
                StationState nss = new StationState((Station) rn, section,simObjects,SimulationInterval);
                states.add(nss);
                stationStates.add(nss);
            }
            if(rn.getNodeType().isEntrance()){
                EntranceState en = new EntranceState((Entrance) rn, simObjects,SimulationInterval);
                states.add(en);
                entranceStates.add(en);
            }
            if(rn.getNodeType().isExit()){
                ExitState ex = new ExitState((Exit) rn);
                states.add(ex);
                exitStates.add(ex);
            }
            
            if (prev != null) {
                State cur = states.get(states.size() - 1);
                prev.downstream = cur;
                cur.upstream = prev;
            }
        }
        
        setMeters();
        setStation(errorStation);
//        setDMS();
        

        // DEBUG
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            sb.append("[" + String.format("%02d", i) + "] ");
            if (state.type.isStation()) {
                StationState ss = (StationState)state;
                sb.append(((Station) state.rnode).getID()+ " -> ");
            }
             if (state.type.isEntrance()) {
                EntranceState e = (EntranceState) state;
                if(e.meter != null) sb.append("Ent(" + e.meter.getID() + ")");
                else sb.append("Ent(" + e.rnode.getName() + ")");
            }
            if (state.type.isExit()) {
                sb.append("Ext(" + state.rnode.getName() + ")");
            }
//            sb.append(" (distance to downstream = " + state.distanceToDownState + ")\n");
        }
        System.out.println(sb.toString());
    }
    
    private void CheckStationError(Vector<Station> errorStation) {
        /**
         * Check Station Error
         */
        boolean ischeck = false;
        for(Station es : errorStation){
            if(es.getID() != null)
                ischeck = true;
        }
        if(!errorStation.isEmpty() && ischeck){
            System.out.println("!!Warning!!");
            System.out.println("Station Information is not correct in CASEFILE");
            System.out.println("Check Detector in CASEFILE below lists");
            for(Station es : errorStation){
                if(es.getID() != null)
                    System.out.println("-- Station ID : "+es.getID()+", RNode : "+es.getID() );
            }
        }
    }
    
    /**
     * Return entrance state which include given ramp meter
     * @param meter ramp meter to find entrance
     * @return entrance state
     */
    private EntranceState findStateHasMeter(SimMeter meter) {        
        for (int i = 0; i < states.size(); i++) {
//            System.out.println(states.get(i).id);
            State state = states.get(i);
            if (!state.type.isEntrance()) {
                continue;
            }
            EntranceState s = (EntranceState) state;
            if (s.hasDetector(meter.getMerge())
                    || s.hasDetector(meter.getGreen())
                    || s.hasDetector(meter.getPassage())
                    || s.hasDetector(meter.getByPass())
                    || s.hasDetector(meter.getQueue())) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Return upstream station state
     * @param idx rnode-index of state
     * @return upstream station state
     */
    public StationState getUpstreamStationState(int idx) {
        if (idx <= 0 || idx >= states.size()) {
            return null;
        }
        for (int i = idx - 1; i >= 0; i--) {
            State st = states.get(i);
            if (st.type.isStation()) {
                return (StationState) st;
            }
        }
        return null;
    }
    
    /**
     * Return downstream station state
     * @param idx rnode-index of state
     * @return downstream station state
     */
    public StationState getDownstreamStationState(int idx) {
        if (idx < 0 || idx >= states.size() - 1) {
            return null;
        }
        for (int i = idx + 1; i < states.size(); i++) {
            State st = states.get(i);
            if (st.type.isStation()) {
                return (StationState) st;
            }
        }
        return null;
    }

    private void setMeters() {
        for (SimMeter m : meters) {
            String name = m.getID();
            if (name.contains("_L")) {
                name = name.split("_")[0];
            }
            EntranceState st = findStateHasMeter(m);            
            if (st == null) {
                System.out.println("Cannot find entrance for " + m.getID());
                continue;
            }

            st.meter = m;
        }
    }

    private void setStation(Vector<Station> errorStation) {
        CheckStationError(errorStation);
        
        //Set up the Up and the Down Stream
        for(int i = 0; i < stationStates.size();i++){
            StationState current = stationStates.get(i);
            if(i > 0){
                StationState upstream = stationStates.get(i-1);
                current.setUpstreamStationState(upstream);
            }
            
            if(i < stationStates.size() - 1){
                StationState downstream = stationStates.get(i+1);
                current.setDownStreamStationState(downstream);
            }
        }
    }

    /**
     * I think that Real DMS Name is L35xxx_1 <-L35WS53_1,  L35WS53_2
     */
    private void setDMS() {
       throw new UnsupportedOperationException("Not supported yet.");
//        //setDMS
//        this.DMSs = (ArrayList<DMSImpl>)section.getDMS();
//        
//        for(SimDMS d : simDMSs){
//            DMSImpl dim = findDMS(d);
//            if(dim == null){
////                System.out.println("Cannot find DMS for " + d.getId());
//                continue;
//            }else{
//            }
//            
//            dim.setSimDMS(d);
//        }
//        
//        //search error
//        boolean isvslerrfirst = true;
//        if(simDMSs != null && !simDMSs.isEmpty()){
//            for(DMSImpl dm : DMSs){
//                if(isvslerrfirst){
//                    System.out.println("Desired Speed Matching Error for VSL");
//                    isvslerrfirst = false;
//                }
//                
//                if(dm.hasAllSimDMS()){
//                    System.out.println(dm.getId() + " : Correct All DMS");
//                    for(DMS d : dm.getDMSList()){
//                        System.out.println("  ---"+d.getId()+" : "+d.hasSimDMS());
//                    }
//                }else{
//                    System.out.println(dm.getId() + " : Incorrect DMS");
//                    for(DMS d : dm.getDMSList()){
//                        System.out.println("  ---"+d.getId()+" : "+d.hasSimDMS());
//                    }
//                }
//                
//            }
//        }
    }

    /**
     * @param d
     * @return 
     */
//    private DMSImpl findDMS(SimDMS d) {
//        throw new UnsupportedOperationException("Not supported yet.");
//        for(DMSImpl dm : DMSs){
//            if(dm.hasDMS(d.getId())){
//                return dm;
//            }
//        }
//        
//        return null;
//    }
}
