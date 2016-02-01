/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth, US) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
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
import infra.infraobject.Entrance;
import infra.infraobject.Exit;
import infra.infraobject.RNode;
import infra.infraobject.RampMeter;
import infra.infraobject.Station;
import infra.type.TrafficType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Chongmyung Park
 */
public class SectionHelper {
    
    private Section section;
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<StationState> stationStates = new ArrayList<StationState>();
    private ArrayList<EntranceState> entranceStates = new ArrayList<EntranceState>();
    private ArrayList<ExitState> exitsStates = new ArrayList<ExitState>();
    public static double PASSAGE_DEMAND_FACTOR = 1.15;
        
    public RNode[] getRNodes() {
        RNode[] rnodes = new RNode[states.size()];
        int idx = 0;
        for(State s : states) {
            rnodes[idx++] = s.rnode;
        }
        return rnodes;
    }

    public EntranceState getEntrance(Entrance rn) {
        for(EntranceState es : this.entranceStates) {
            if(es.rnode.equals(rn)) return es;
        }
        return null;
    }

    public ExitState getExit(Exit rn) {
        for(ExitState es : this.exitsStates) {
            if(es.rnode.equals(rn)) return es;
        }
        return null;        
    }

    public enum StateType {
        STATION, METER, ENTRANCE, EXIT;
        public boolean isStation() {return this == STATION;}
        public boolean isMeter() {return this == METER;}
        public boolean isEntrance() {return this == ENTRANCE;}
        public boolean isExit() {return this == EXIT;}
    }
    
    public SectionHelper(Section section, boolean debug) {
        this.section = section; 
        init(debug);
    }
    
    public SectionHelper(Section section) {
        this(section, false);
    }    
    

    public EntranceState getEntrance(int detector_id) {
        for(EntranceState es : this.entranceStates) {
            for(Detector d : es.rnode.getDetectorList()) {
                if(d.getID().equals(detector_id)) return es;
            }
        }
        return null;
                
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
     * Return all states
     * @return all states list
     */
    public ArrayList<State> getStates() {
        return this.states;
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
        return entranceStates;
    }    
    
    /**
     * Initialize
     *   - Build up section structure
     */
    private void init(boolean debug) {

        for (RNode rn : section.getRNodesWithExitEntrance()) {
            //System.out.println("Rnode : " + rn.getId() + " (" + rn.getStationId() +")");
            State prev = null;
            if (!states.isEmpty()) {
                prev = states.get(states.size() - 1);
            }

            if (rn.getNodeType().isStation()) {
                states.add(new StationState((Station) rn));
            }
            if (rn.getNodeType().isEntrance()) {
                states.add(new EntranceState((Entrance) rn));
            }
            if (rn.getNodeType().isExit()) {
                states.add(new ExitState((Exit) rn));
            }

            if (prev != null) {
                State cur = states.get(states.size() - 1);
                prev.downstream = cur;
                cur.upstream = prev;
//                prev.distanceToDownState = TMO.getDistanceInFeet(prev.rnode, rn);
            }
        }

        // DEBUG
//        if(debug) {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < states.size(); i++) {
//                State state = states.get(i);
//                sb.append("[" + state.idx + "] ");
//                if (state.type.isStation()) {
//                    StationState ss = (StationState)state;
//                    sb.append(((Station) state.rnode).getStationId());
//                }
//                if (state.type.isEntrance()) {
//                    EntranceState e = (EntranceState) state;
//                    sb.append("Ent(" + e.rnode.getLabel() + ")");
//                }
//                if (state.type.isExit()) {
//                    sb.append("Ext(" + state.rnode.getLabel() + ")");
//                }
//                sb.append(" (distance to downstream = " + state.distanceToDownState + ")\n");
//            }
//            System.out.println(sb.toString());
//        }
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


    /**
     * State class to organize for metering
     */
    public class State {

        public StateType type;
        String id;
        int idx;
        int easting, northing;
        State upstream;
        State downstream;
        RNode rnode;
        double distanceToDownState = 0;
        //public IDetectorChecker dc = SectionHelper.dc;
        
        public State(String id, RNode rnode) {
            this.id = id;
            this.rnode = rnode;
            if(rnode != null) {
//                this.easting = rnode.getEasting();
//                this.northing = rnode.getNorthing();
            }
            this.idx = states.size();
        }

        public boolean hasDetector(Detector sd) {
            if (sd == null) {
                return false;
            }
            return this.rnode.hasDetector(sd.getID());
        }

        public boolean hasDetector(Detector[] sds) {
            if (sds == null) {
                return false;
            }
            for (Detector sd : sds) {
                if (this.rnode.hasDetector(sd.getID())) {
                    return true;
                }
            }
            return false;
        }

        public RNode getRNode() {
            return rnode;
        }
        
    }

    /**
     * State class that represents entrance
     */
    public class EntranceState extends State {
        Entrance entrance;       
        ArrayList<Detector> queue;
        Detector passage, merge, bypass, green;
        
        public EntranceState(Entrance e) {
            super(e.getID(), e);
            this.entrance = e;
            type = StateType.ENTRANCE;
            entranceStates.add(this);
//            queue = entrance.getQueue();            
//            passage = entrance.getPassage();
//            merge = entrance.getMerge();
//            bypass = entrance.getBypass();
//            green = entrance.getGreen();
        }
        
        public RampMeter getMeter() {
//            TMO tmo = TMO.getInstance();
//            Collection<RampMeter> meters = tmo.getInfra().getInfraObjects(InfraType.METER);
//            Iterator<RampMeter> itr = meters.iterator();
//            while(itr.hasNext()) {
//                RampMeter meter = itr.next();                              
//                if(passage != null && passage.getId().equals(meter.getPassage())) return meter;
//                else if(merge != null && merge.getId().equals(meter.getMerge())) return meter;
//                else if(bypass != null && bypass.getId().equals(meter.getByPass())) return meter;
//                else if(green != null && green.getId().equals(meter.getGreen())) return meter;
//                else if(queue != null) {
//                    String[] qs = meter.getQueue();
//                    if(qs == null) continue;
//                    for(Detector d : queue) {
//                        for(String did : qs) {
//                            if(did.equals(d.getId())) return meter;
//                        }
//                    }
//                }
//            }
            return null;
        }
        
        public Station upstreamStation() {
            if(this.idx == 1) return null;
            for(int i=this.idx-1; i>=0; i--) {
                State s = states.get(i);
                if(s.type.isStation()) return (Station)s.rnode;                
            }
            return null;
        }
        
        public Station downstreamStation() {
            if(this.idx == states.size()-1) return null;
            for(int i=this.idx+1; i<states.size(); i++) {
                State s = states.get(i);
                if(s.type.isStation()) return (Station)s.rnode;                
            }
            return null;
        }  

        public Detector upstreamExitDetector() {
            if (this.idx == 1) {
                return null;
            }
            for (int i = this.idx - 1; i >= 0; i--) {
                State s = states.get(i);
                if (s.type.isExit()) {
                    return s.rnode.getDetectorList()[0];
                }
            }
            return null;
        }     
        
        /*
         *                   
         * 
         */
        
        /**
         * Return ramp demand consideration of D3614 <- Th 62 (Th100 Entrance)
         * modify by soobin Jeon // 2012.12.23
         * @return 
         */
//        public double[] getRampDemandbyD3614() {                                   
//           
//            // queue detector is ok
//            if(!queue.isEmpty()) {                
//                double[][] data;
//                int idx = 0;
//                int dcount = 0;
//                
//                for(Detector d : queue)
//                {
//                    System.out.print("Detector : " + d.getId() + " category : " + d.getCategory() + "(" +d.getLane()+")");
//                   if(!d.isAbandoned() || (d.getId().equals("D3614"))){
//                        System.out.print("OK!");
//                        if(!d.getId().equals("D5321")){
//                            System.out.println(" Read!");
//                            dcount ++;
//                        }
//                    }else
//                        System.out.println("Abandoned");
//                }
//                
//                data = new double[dcount][];
//                dcount = 0;
//                for(Detector d : queue)
//                {
//                    
//                    if(!d.isAbandoned()  || (d.getId().equals("D3614"))){
//                        if(!d.getId().equals("D5321"))
//                            data[idx++] = d.getData(TrafficType.FLOW);
//                    }
//                    else
//                        dcount ++;
//                }                   
//
//                if(queue.size() - dcount == 0){
//                    // if no queue detector, 
//                    // use passage flow * 1.15
//                    double[] p_flow = getRampFlow();
//                    if(p_flow == null) return null;
//                    for(int i=0; i<p_flow.length; i++) {
//                        p_flow[i] *= PASSAGE_DEMAND_FACTOR;
//                    }
//                    return p_flow;
//                }
//                
//                double[] demands = new double[data[0].length];
//                    
//                for(int i=0; i<data[0].length; i++) {
//                    double sum = 0;
//                    for(int detIdx=0;detIdx<data.length; detIdx++)
//                    {
//                        double v = data[detIdx][i];                        
//                        if(v > 0)
//                        {                    
//                            sum += v;
//                        }
//                    }    
//                    
//                    if(sum > 0) demands[i] = sum;
//                    else demands[i] = -1;
//                }
//
//                return demands;
//            
//            } else {
//                // if no queue detector, 
//                // use passage flow * 1.15
//                double[] p_flow = getRampFlow();
//                if(p_flow == null) return null;
//                for(int i=0; i<p_flow.length; i++) {
//                    p_flow[i] *= PASSAGE_DEMAND_FACTOR;
//                }
//                return p_flow;
//            }
//        }
        
        /**
         * Return ramp demand
         * modify by soobin Jeon // 2012.12.23
         * @return 
         */
        public double[] getRampDemand() {                                   
           
            // queue detector is ok
            if(!queue.isEmpty()) {                
                double[][] data;
                int idx = 0;
                int dcount = 0;
                
                for(Detector d : queue)
                {
//                    System.out.print("Detector : " + d.getId() + " category : " + d.getCategory() + "(" +d.getLane()+")");
                    dcount ++;
                }
                
                data = new double[dcount][];
                dcount = 0;
                for(Detector d : queue)
                {
                    data[idx++] = d.getData(TrafficType.FLOW);
                }                   

                if(queue.size() - dcount == 0){
                    // if no queue detector, 
                    // use passage flow * 1.15
                    double[] p_flow = getRampFlowNew();
                    if(p_flow == null) return null;
                    for(int i=0; i<p_flow.length; i++) {
                        p_flow[i] *= PASSAGE_DEMAND_FACTOR;
                    }
                    return p_flow;
                }
                
                double[] demands = new double[data[0].length];
                    
                for(int i=0; i<data[0].length; i++) {
                    double sum = 0;
                    for(int detIdx=0;detIdx<data.length; detIdx++)
                    {
                        double v = data[detIdx][i];                        
                        if(v > 0)
                        {                    
                            sum += v;
                        }
                    }    
                    
                    if(sum > 0) demands[i] = sum;
                    else demands[i] = -1;
                }

                return demands;
            
            } else {
                // if no queue detector, 
                // use passage flow * 1.15
                double[] p_flow = getRampFlow();
                if(p_flow == null) return null;
                for(int i=0; i<p_flow.length; i++) {
                    p_flow[i] *= PASSAGE_DEMAND_FACTOR;
                }
                return p_flow;
            }
        }
        
        
        
        /**
         * @deprecated 
         * Return ramp demand
         * @return 
         */
        public double[] getRampDemandOld() {                                   
            return getRampDemandOld(TrafficType.FLOW);
        }
        /**
         * @deprecated 
         * Return ramp demand
         * @return 
         */
        public double[] getRampDemandOld(TrafficType tType) {                                   
           
            // queue detector is ok
            if(!queue.isEmpty()) {                
                double[][] data = new double[queue.size()][];
                int idx = 0;
                for(Detector d : queue)
                {
                    data[idx++] = d.getData(tType);
                }                    

                double[] demands = new double[data[0].length];

                for(int i=0; i<data[0].length; i++) {
                    double sum = 0;
                    for(int detIdx=0;detIdx<data.length; detIdx++)
                    {
                        double v = data[detIdx][i];                        
                        if(v > 0)
                        {                    
                            sum += v;
                        }
                    }    
                    
                    if(sum > 0) demands[i] = sum;
                    else demands[i] = -1;
                }

                return demands;
            
            } else {
                // if no queue detector, 
                // use passage flow * 1.15
                double[] p_flow = getRampFlowNew();
                if(p_flow == null) return null;
                for(int i=0; i<p_flow.length; i++) {
                    p_flow[i] *= PASSAGE_DEMAND_FACTOR;
                }
                return p_flow;
            }
        }
        
        
         /**
         * Return ramp flow before given period intervals
         * @return ramp flow
         */
        public double[] getRampFlowNew() {
            
            double[] p_flow = null;           

            // passage detector is ok
            if(merge != null) {
                p_flow = merge.getData(TrafficType.FLOW);
            } else {
                // merge detector is ok
                if(passage != null) {
                    p_flow = passage.getData(TrafficType.FLOW);
                    // bypass detector is ok
                    if(bypass != null) {
                        double[] bypassFlow = bypass.getData(TrafficType.FLOW);
                        for(int i=0; i<bypassFlow.length; i++) {
                            if(bypassFlow[i]>0) p_flow[i] = Math.max(p_flow[i]-bypassFlow[i], 0);
                        }
                    }                                      
                }else if(bypass != null){
                    p_flow = bypass.getData(TrafficType.FLOW);
                }
            }    
            
            return p_flow;
        }
        
        /**
         * Return ramp flow before given period intervals
         * @deprecated 
         * @return ramp flow
         */
        public double[] getRampFlow() {
            
            double[] p_flow = null;           

            // passage detector is ok
            if(passage != null) {
                p_flow = passage.getData(TrafficType.FLOW);
            } else {
                // merge detector is ok
                if(merge != null) {
                    p_flow = merge.getData(TrafficType.FLOW);
                    // bypass detector is ok
                    if(bypass != null) {
                        double[] bypassFlow = bypass.getData(TrafficType.FLOW);
                        for(int i=0; i<bypassFlow.length; i++) {
                            if(bypassFlow[i]>0) p_flow[i] = Math.max(p_flow[i]-bypassFlow[i], 0);
                        }
                    }                                      
                }else if(bypass != null){
                    p_flow = bypass.getData(TrafficType.FLOW);
                }   
            }    
            
            return p_flow;
        }
        
        /**
         * Return ramp flow before given period intervals
         * @return ramp flow
         */
        public double[] getRampVolume() {
            
            double[] p_v = null;           

            // passage detector is ok
            if(merge != null) {
                p_v = merge.getData(TrafficType.VOLUME);
            } else {
                // merge detector is ok
                if(passage != null) {
                    p_v = passage.getData(TrafficType.VOLUME);
                    // bypass detector is ok
                    if(bypass != null) {
                        double[] bypassV = bypass.getData(TrafficType.VOLUME);
                        for(int i=0; i<bypassV.length; i++) {
                            if(bypassV[i]>0) p_v[i] = Math.max(p_v[i]-bypassV[i], 0);
                        }
                    }                                      
                }   
            }    
            
            return p_v;
        }
        
        
    }
    
    
    
    /**
     * State class that represents exit
     */
    public class ExitState extends State {

        Exit exit;
        
        public ExitState(Exit e) {
            super(e.getID(), e);
            this.exit = e;
            type = StateType.EXIT;
            exitsStates.add(this);
        }    
        
        public double[] getRampExitFlow() {            
            Detector[] detectors = rnode.getDetectorList();
            
            double[][] data = new double[detectors.length][];
            int idx = 0;
            for(Detector d : detectors)
            {
                data[idx++] = d.getData(TrafficType.FLOW);
            }                    

            double[] exitFlow = new double[data[0].length];

            for(int i=0; i<data[0].length; i++) {
                double sum = 0;
                for(int detIdx=0;detIdx<data.length; detIdx++)
                {
                    double v = data[detIdx][i];                        
                    if(v > 0)
                    {                    
                        sum += v;
                    }
                }    

                if(sum > 0) exitFlow[i] = sum;
                else exitFlow[i] = -1;
            }

            return exitFlow;
            
        }
        
    }
    
    /**
     * State class that represents station
     */
    public class StationState extends State {
        int stationIdx = 0;                

        public StationState(Station s) {
            super(s.getID(), s);
            type = StateType.STATION;
            stationStates.add(this);
            this.stationIdx = stationStates.size()-1;
        }
    }    
}
