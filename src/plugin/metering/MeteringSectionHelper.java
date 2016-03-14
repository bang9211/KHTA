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

/*
 * SectionHelper.java
 *
 * Created on Jun 8, 2011, 10:26:49 AM
 */
package plugin.metering;

import java.util.ArrayList;
import infra.Infra;
import infra.Section;
import infra.infraobject.Corridor;
import infra.infraobject.Detector;
import infra.infraobject.Entrance;
import infra.infraobject.Exit;
import infra.infraobject.RNode;
import infra.infraobject.Station;
import infra.interfaces.IDetectorChecker;
import infra.simobjects.SimConfig;
import infra.simobjects.SimDetector;
import infra.simobjects.SimMeter;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import infra.simulation.SimulationGroup;
import infra.simulation.State;
import infra.simulation.StateInterval;
import infra.simulation.StateType;
import infra.type.RnodeType;
import infra.type.TrafficType;
import util.DistanceUtil;


/**
 *
 * @author Chongmyung Park
 */
public class MeteringSectionHelper {
    
    private Section section;
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<StationState> stationStates = new ArrayList<StationState>();
    private ArrayList<EntranceState> entranceStates = new ArrayList<EntranceState>();
    private ArrayList<ExitState> exitsStates = new ArrayList<ExitState>();
    //private SimObjects simObjects = SimObjects.getInstance();
    //private SimObjects = new SimObjects();
    private SimObjects simObjects;
    private ArrayList<SimMeter> meters;
    private ArrayList<SimDetector> detectors;
    Infra infra = Infra.getInstance();
    
    /** Enum for minimum limit control */
    enum MinimumRateLimit {
            pf,	/* passage failure */
            sl,	/* storage limit */
            wl,	/* wait limit */
            tm	/* target minimum */
    };
        
    /** Number of seconds for one time step */
    static private final int STEP_SECONDS = 10;
        
    /** Number fo time steps to check before start metering */
    static private final int START_STEPS = steps(90);

    /** Number of time steps to check before stop metering */
    static private final int STOP_STEPS = steps(300);

    /** Number of time steps to check before restart metering */
    static private final int RESTART_STEPS = steps(300);

    /** Maximum number of time steps needed for sample history */
    static private final int MAX_STEPS = Math.max(Math.max(START_STEPS,
            STOP_STEPS), RESTART_STEPS);
    static private final int MISSING_DATA = -1;
    
    public float MAX_RATE = MeteringConfig.MAX_RATE;    // 3600/2.1 (red = 0.1s, green + yellow = 2s)
    public float MIN_RATE = MeteringConfig.getMinRate();    // 3600/2.1 (red = 0.1s, green + yellow = 2s)
    public int MAX_RAMP_DENSITY = MeteringConfig.MAX_RAMP_DENSITY;
    public double PASSAGE_DEMAND_FACTOR = MeteringConfig.PASSAGE_DEMAND_FACTOR;
    public double MIN_RED_TIME = MeteringConfig.MIN_RED_TIME;  // minimum red time = 0.1 second
    
    private SimInterval SimulationInterval;
    
    static private int steps(int seconds) {
            float secs = seconds;
            return Math.round(secs / STEP_SECONDS);
    }
    
    /** Convert step volume count to flow rate.
        * @param vol Volume to convert (number of vehicles).
        * @param n_steps Number of time steps of volume.
        * @return Flow rate (vehicles / hour) */
       static private int flowRate(double vol, int n_steps) {
               if(vol >= 0) {
                       Interval period = new Interval(n_steps * STEP_SECONDS);
                       double hour_frac = period.per(Interval.HOUR);
                       return (int)Math.round(vol * hour_frac);
               } else
                       return MISSING_DATA;
       }

       /** Convert step volume count to flow rate (vehicles / hour) */
       static private int flowRate(double vol) {
               return flowRate(vol, 1);
       }
    
    /** Convert flow rate to volume for a given period.
    * @param flow Flow rate to convert (vehicles / hour).
    * @param period Period for volume (seconds).
    * @return Volume over given period. */
   static private double volumePeriod(int flow, int period) {
           if(flow >= 0 && period > 0) {
                   double hour_frac = Interval.HOUR.per(new Interval(period));
                   return flow * hour_frac;
           } else
                   return MISSING_DATA;
   }
   
   /** Filter a release rate for valid range */
    static public int filterRate(int r){
        r = Math.max(r, getMinRelease());
        return Math.min(r,getMaxRelease());
    }
    
    static public int getMinRelease(){
        return MeteringConfig.getMinRate().intValue();
    }
    
    static public int getMaxRelease(){
        return MeteringConfig.getMaxRate().intValue();
    }

    public MeteringSectionHelper(Section section, ArrayList<SimMeter> meters, ArrayList<SimDetector> detectors) {
            this(section,meters,detectors,null, null);
    }
    public MeteringSectionHelper(Section section, ArrayList<SimMeter> meters, ArrayList<SimDetector> detectors,
            SimInterval simInterval, SimObjects simObjects) {
        this.section = section;
        this.meters = meters;
        this.detectors = detectors; 
        this.simObjects = simObjects;
        if(simInterval == null){
                SimulationInterval = new SimInterval(section);
        }else
                SimulationInterval = simInterval;
        init();
    }

    /**
     * Returns station state according to station id
     * @param station_id
     * @return station state
     */
    public StationState getStationState(String station_id) {
        for (State s : states) {
            //if (s.type.isStation() && station_id.equals(s.rnode.getStationId())) {
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
        return entranceStates;
    }    
    
    /**
     * Returns average density between 2 station
     * @param upStation upstream station
     * @param downStation downstream station (not need to be next downstream of upStation)
     * @return average density (distance weight)
     */
    public double getAverageDensity(StationState upStation, StationState downStation,SimulationGroup sg)
    {
        return getAverageDensity(upStation, downStation, 0,sg);
    }
    
    public double getAverageDensity(StationState upStation, StationState downStation, int prevStep,SimulationGroup sg)
    {
        StationState cursor = upStation;
        if(cursor.equals(downStation))
            return cursor.getIntervalAggregatedDensity(sg,prevStep);
        
        double totalDistance = 0;
        double avgDensity = 0;
        while(true) {
            StationState dStation = this.getDownstreamStationState(cursor.idx);
            double upDensity = cursor.getIntervalAggregatedDensity(sg,prevStep);
            double downDensity = dStation.getIntervalAggregatedDensity(sg,prevStep);
            double middleDensity = (upDensity + downDensity) / 2;
            //double distance = TMO.getDistanceInMile(cursor.rnode, dStation.rnode);
            double distance = DistanceUtil.getDistanceInKM(cursor.rnode, dStation.rnode);
            double distanceFactor = distance / 3;
            totalDistance += distance;           
            avgDensity += (upDensity + middleDensity + downDensity) * distanceFactor;
            
            if(dStation.equals(downStation)) break;
            cursor = dStation;
        }
        return avgDensity / totalDistance;        
    }
    
    /**
     * Is station in VISSIM case file?
     * @param s station
     * @return true if given station in the case
     */
    private boolean isInMap(Station s) {
//        Detector[] dets = s.getDetectors(dc);
        Detector[] dets = s.getSimDetectorList();
        for (Detector d : dets) {
            for (SimDetector sd : detectors) {
//                if (sd.getDetectorId() == d.getDetectorId()) {
                if (sd.getDetectorId() == d.getID()) {
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

        for (RNode rn : section.getRNodesWithExitEntrance()) {
            State prev = null;
            if (!states.isEmpty()) {
                prev = states.get(states.size() - 1);
            }
            
            StateInterval sitv = null;
            if(SimulationInterval != null)
                    sitv = SimulationInterval.getState(rn.getID());

            if (rn.getNodeType() == RnodeType.STATION) {
                if (!isInMap((Station) rn)) {
                    continue;
                }
                states.add(new StationState((Station) rn,section,SimulationInterval));
            }
            if (rn.getNodeType() == RnodeType.ENTRANCE) {
                states.add(new EntranceState((Entrance) rn,SimulationInterval));
            }
            if (rn.getNodeType() == RnodeType.EXIT) {
                states.add(new ExitState((Exit) rn));
            }

            if (prev != null) {
                State cur = states.get(states.size() - 1);
                prev.downstream = cur;
                cur.upstream = prev;
            }
        }

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
        
        // FIXME: automate this routine!!
        EntranceState toRemove = null;
        for(StationState s : this.stationStates) {
            s.setAssociatedEntrances();
            /*
            if(s.id.equals("S43")) {
                for(EntranceState es : s.getAssociatedEntrances()) {
                    if(es.meter.getId().equals("M35WN27")) {
                        toRemove = es;
                        break;
                    }
                }
                s.associatedEntrances.remove(toRemove);
            }
            if(s.id.equals("S34")) {
                s.associatedEntrances.add(toRemove);
            }
             */
        }

        
        
        // DEBUG
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < states.size(); i++) {
//            State state = states.get(i);
//            sb.append("[" + String.format("%02d", state.idx) + "] ");
//            if (state.type.isStation()) {
//                StationState ss = (StationState)state;
//                sb.append(((Station) state.rnode).getStationId() + " -> ");
//                for(EntranceState es : ss.getAssociatedEntrances()) {
//                    if(es != null && es.meter != null) sb.append(es.meter.getId() + ", ");
//                }
//            }
//            if (state.type.isEntrance()) {
//                EntranceState e = (EntranceState) state;
//                if(e.meter != null) sb.append("Ent(" + e.meter.getId() + ")");
//                else sb.append("Ent(" + e.rnode.getLabel() + ")");
//            }
//            if (state.type.isExit()) {
//                sb.append("Ext(" + state.rnode.getLabel() + ")");
//            }
//            sb.append(" (distance to downstream = " + state.distanceToDownState + ")\n");
//        }
//        System.out.println(sb.toString());
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
            if (s.hasDetector(meter.getPassage())
                    || s.hasDetector(meter.getQueue())) {
                return s;
            }
        }
        return null;
    }

//    /**
//     * State class to organize for metering
//     */
//    public class State {
//
//        StateType type;
//        public String id;
//        int idx;
//        int easting, northing;
//        State upstream;
//        State downstream;
//        RNode rnode;
//        double distanceToDownState = 0;
//
//        public State(String id, RNode rnode) {
//            this.id = id;
//            this.rnode = rnode;
//            if(rnode != null) {
//                this.easting = rnode.getEasting();
//                this.northing = rnode.getNorthing();
//            }
//            this.idx = states.size();
//        }
//
//        public boolean hasDetector(SimDetector sd) {
//            if (sd == null) {
//                return false;
//            }
//            return this.rnode.hasDetector(sd.getId());
//        }
//
//        public boolean hasDetector(SimDetector[] sds) {
//            if (sds == null) {
//                return false;
//            }
//            for (SimDetector sd : sds) {
//                if (this.rnode.hasDetector(sd.getId())) {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }

    /**
     * State class that represents entrance
     */
    public class EntranceState extends State {
        public Entrance entrance;
        public SimMeter meter;
        public StationState associatedStation;
        public DIR associatedStationDir;
        
        /** Queue denand history (vehicles / hour) */
        private final BoundedSampleHistory demandHist =
                new BoundedSampleHistory(steps(300));
        
        private final BoundedSampleHistory demandvolHist =
                new BoundedSampleHistory(steps(300));
        
        /** Cumulative demand history (vehicles) */
        private final BoundedSampleHistory demandAccumHist = 
                new BoundedSampleHistory(steps(300));
        
        /** Target queue demand rate (vehicles / hour) */
        private int target_demand = 0;
        
        /** Passage sampling failure (latches until queue empty) */
        private boolean passage_failure = false;
        
        /** Cumulative passage count (vehicles) */
        private int passage_accum = 0;
        
        /** Ramp passage history(vehicles / hour) */
        private final BoundedSampleHistory passageHist = 
                new BoundedSampleHistory(MAX_STEPS);
        
        /** Time queue has been empty (steps) */
        private int queueEmptyCount = 0;

        /** Time queue has been full (steps) */
        private int queueFullCount = 0;

        /** Controlling minimum rate limit */
        private MinimumRateLimit limit_control = null;

        /** Metering rate flow history (vehicles / hour) */
        private final BoundedSampleHistory rateHist =
                new BoundedSampleHistory(MAX_STEPS);

        /** Segment density history (vehicles / mile) */
        private final BoundedSampleHistory segmentDensityHist =
                new BoundedSampleHistory(MAX_STEPS);
        
        /** Number of steps queue must be empty before resetting accumulators */
	private final int QUEUE_EMPTY_STEPS = steps(90);
        
        /** Ratio for max rate to target rate */
	private final float TARGET_MAX_RATIO = MeteringConfig.TARGET_MAX_RATIO;

	/** Ratio for min rate to target rate */
	private final float TARGET_MIN_RATIO = MeteringConfig.TARGET_MIN_RATIO;

	/** Ratio for target waiting time to max wait time */
	private final float WAIT_TARGET_RATIO = MeteringConfig.WAIT_TARGET_RATIO;

	/** Ratio for target storage to max storage */
	private final float STORAGE_TARGET_RATIO = MeteringConfig.STORAGE_TARGET_RATIO;
        
//        ArrayList<Double> cumulativeDemand = new ArrayList<Double>();
//        ArrayList<Double> cumulativeMergingVolume = new ArrayList<Double>();
//        ArrayList<Double> rateHistory = new ArrayList<Double>();        
//        HashMap<Integer, Double> segmentDensityHistory = new HashMap<Integer, Double>(); // <dataCount, K>
//        double lastDemand = 0;
//        double lastVolumeOfRamp = 0;
        int lastRate = 0;
        boolean isMetering = false;
        boolean isRateUpdated = false;
        boolean isActiveMetering = false;
        int maxWaitTimeIndex = MeteringConfig.MAX_WAIT_TIME_INDEX;
        private int noBottleneckCount;
        private StationState bottleneck;
        public boolean hasBeenStoped = false;
        
        int minimumRate = 0;
        int maximumRate = 0;
        

        
        
        public EntranceState(Entrance e, SimInterval _sitv) {
            super(e.getID(), e,_sitv);
            this.entrance = e;
            type = StateType.ENTRANCE;
            entranceStates.add(this);
            //String thislabel = e.getLabel();
            String thislabel = e.getID();
            
            // check if it is freeway-to-freeway entrance
            for(Corridor c : infra.getCorridors()) {
                if(c.getID().contains(thislabel)) {
                    maxWaitTimeIndex = MeteringConfig.MAX_WAIT_TIME_INDEX_F2F;
                    break;
                }
            }
        }
        
        /**
         * Return output in this time interval
         */        
        public double getMergingVolume() {
            return getMergingVolume(0);
        }
        
        /**
         * Return output before 'prevStep' time step
         */        
        public double getMergingVolume(int prevStep) {
            return volumePeriod(this.passageHist.get(prevStep).intValue(),STEP_SECONDS);
        }        
        
        /**
         * Return demand in this time interval
         */
        public double getDemandVolume() {
            return volumePeriod(this.demandHist.get(0).intValue(),STEP_SECONDS);
        }
        
        public double getDemandVolume_ex(){
            return demandvolHist.get(0);
        }
        
        /**
        * Get historical ramp flow.
        * @param prevStep Time step in past.
        * @param secs Number of seconds to average.
        * @return ramp flow at 'prevStep' time steps ago
        */
        private int getFlow(int prevStep, int secs){
            Double p = passageHist.average(prevStep, steps(secs));
            if(p != null)
                return (int)Math.round(p);
            else
                return getMaxRelease();
        }
        
        private int getFlow(int prevStep){
            return getFlow(prevStep, 30);
        }
        /**
         * Return rate in previous time step
         */
        public double getRate() {
            int r = lastRate;
            return r > 0 ? r : getFlow(0, 90);
            // initial rate = average(last 3 flows) or MAX_RATE
//            if(this.lastRate == 0 ) {
////                this.lastRate = MAX_RATE;
//                this.lastRate = this.getMergingVolume() * 120;
//                double flowSum = 0;
//                int cnt = 0;
//                
//                for(int i=0; i<3; i++) {
//                    flowSum += calculateRampVolume(i) * 120;
//                    cnt++;
//                }
//                
//                if(cnt > 0) this.lastRate = flowSum / cnt;
//                else this.lastRate = MAX_RATE;  // no flow
//            }
//            
//            return this.lastRate;
        }
        
        public double getRate(int prevStep) {
            if(this.rateHist.get(prevStep) == null)
                return -1;
            else
                return this.rateHist.get(prevStep);
//            int index = rateHistory.size() - prevStep - 1;
//            return rateHistory.get(index);
        }  
        
        public int countRateHistory()
        {
            return rateHist.size();
        }               
        
        /**
         * Return minimum rates to guarantee 
         *     - max waiting time
         *     - max density 
         * @return minimum rates
         */
        public int getMinimumRate() {
            return this.minimumRate;
        }
        
        public int getMaximumRate(){
            return maximumRate;
        }
        
        private int calculateMaximumRate() {
            int target_max = Math.round(target_demand *
                    TARGET_MAX_RATIO);
            return filterRate(Math.max(target_max, getMinimumRate()));
        }
        
        /**
         * Calculate minimum rate
         */
        private int calculateMinimumRate() {
            if(passage_failure){
                limit_control = MinimumRateLimit.pf;
                return target_demand;
            }else{
                int r = queueStorageLimit();
                limit_control = MinimumRateLimit.sl;
                int rr = queueWaitLimit();
                if(rr > r){
                    r = rr;
                    limit_control = MinimumRateLimit.wl;
                }
                rr = targetMinRate();
                if(rr > r){
                    r = rr;
                    limit_control = MinimumRateLimit.tm;
                }
                
                return filterRate(r);
            }
        }
        
        /** Caculate queue storage limit.  Project into the future the
		 * duration of the target wait time.  Using the target demand,
		 * estimate the cumulative demand at that point in time.  From
		 * there, subtract the target ramp storage volume to find the
		 * required cumulative passage volume at that time.
		 * @return Queue storage limit (vehicles / hour). */
        private int queueStorageLimit(){
            double proj_arrive = volumePeriod(target_demand,
                    targetWaitTime());
            Double demand_proj = cumulativeDemand() + proj_arrive;
            int req = Math.round(demand_proj.floatValue() - targetStorage().floatValue());
            int pass_min = req - passage_accum;
            return flowRate(pass_min, steps(targetWaitTime()));
        }
        
        /** Calculate the target storage on the ramp (vehicles) */
        private Double targetStorage(){
            return maxStorage() * STORAGE_TARGET_RATIO;
        }
        
        /** Get the target wait time(seconds) */
        private int targetWaitTime(){
            return Math.round(maxWaitTime() * WAIT_TARGET_RATIO);
        }
        
        /** get the max wait time (seconds) */
        private int maxWaitTime(){
            SimMeter m = meter;
            if(m != null)
                return m.getMeter().getMaxWait();
            else
                return MeteringConfig.DEFAULT_MAX_WAIT;
        }
        
        /** Calculate target minimum rate
         * @return Target minimum rate (vehicles / hour). */
        private int targetMinRate(){
            return Math.round(target_demand * TARGET_MIN_RATIO);
        }
        
        /**Calculate queue wait limit (minimum rate)
         @return Queue wait limit (vehicles / hour) */
        private int queueWaitLimit(){
            int wait_limit = 0;
            int wait_target = targetWaitTime();
            int wait_steps = steps(wait_target);
            
            for(int i = 1;i <= wait_steps; i++){
                int dem = Math.round(cumulativeDemand(
                        wait_steps - i).floatValue());
                int pass_min = dem - passage_accum;
                int limit = flowRate(pass_min, i);
                wait_limit = Math.max(limit, wait_limit);
            }
            return wait_limit;
        }
        
        /**
         * Calculate minimum rate
         */
//        private void calculateMinimumRate_old() {
//
//            //calculateMaxWaitTime();
//            
//            int currentIdx = this.cumulativeDemand.size()-1;
//            if(currentIdx < maxWaitTimeIndex) {
//                minimumRate = this.getMergingVolume() * 120;
//                return;
//            }
//                                   
//            // cumulative demand 4 min ago
//            double Cd_4mAgo = this.cumulativeDemand.get(currentIdx - maxWaitTimeIndex);
//            
//            // current cumulative demand
//            double Cd_current = this.cumulativeDemand.get(currentIdx);
//            
//            // current cumulative passage flow
//            double Cf_current = this.cumulativeMergingVolume.get(currentIdx);
//            
//            // minimum rates to guarantee 4 min waitting time
//            minimumRate = ( Cd_4mAgo - Cf_current ) * 120;
//            
//            // DEBUG
//            //if(minimumRate > 0) {
//                System.out.println(
//                        "    - "+ this.meter.getId()+" Minimum Rate = " + String.format("%.2f", minimumRate) + 
//                        ", Ci = "+String.format("%.2f", Cd_current) +                        
//                        ", Ci4 = " + String.format("%.2f", Cd_4mAgo) + 
//                        ", Co = "+ String.format("%.2f", Cf_current) +
//                        ", Input Volume = "+ String.format("%.2f", this.getDemandVolume()) +
//                        ", Output Volume = "+ String.format("%.2f", this.getMergingVolume())
//
//                );                 
//            //}
//            
////            if(minimumRate > 0) {
////                // expected ramp density with calculated min rates
////                double rampExpectedDensity = getExpectedRampDensity(minimumRate);
////
////                // DEBUG
////                System.out.println(
////                        "    - "+ this.meter.getId()+" Minimum Rate = " + String.format("%.2f", minimumRate) + 
////                        ", ExpectedDensity = " + String.format("%.2f", rampExpectedDensity) + 
////                        ", Ci = "+String.format("%.2f", Cd_current) +                        
////                        ", Ci4 = " + String.format("%.2f", Cd_4mAgo) + 
////                        ", Co = "+ String.format("%.2f", Cf_current) +
////                        ", Input Volume = "+ String.format("%.2f", this.getDemandVolume()) +
////                        ", Output Volume = "+ String.format("%.2f", this.getMergingVolume())
////
////                );            
////
////
////                // if expacted ramp density > max ramp density using minRates
////                if(rampExpectedDensity > MAX_RAMP_DENSITY) {                
////                    
////                    minimumRate = getMinimumRateForMaxRampDensity(); 
////                }
////            }
////                        
//            if(minimumRate <= 0) {
//                minimumRate = MeteringConfig.getMinRate();
//            }                        
//        }
        
        /**
         * Calculate demand and output
         */
        public void updateState() {
            if(this.meter == null) return;
            
            this.isRateUpdated = false;
            
            updateDemandState();
            updatePassageState();
            updateQueueState();
            
//            System.out.println(meter.getId()+"- TWaitTime:"+targetWaitTime()+", queueLength:"+this.queueLength()+", qSL:"+queueStorageLimit()+", QwaitLimit:"+queueWaitLimit()
//                    +"TminRate:"+targetMinRate());
                    
            minimumRate = calculateMinimumRate();
            maximumRate = calculateMaximumRate();
            
            
        }
        
        private void updateDemandState(){
            double demand_vol = calculateQueueDemand();
            double demand_rate = flowRate(demand_vol);
            demandHist.push(demand_rate);
            demandvolHist.push(demand_vol);
            double demand_accum = cumulativeDemand() + demand_vol;
            demandAccumHist.push(demand_accum);
            target_demand = targetDemand();
        }
        
        /** Calculate ramp queue demand.  Normally, this would be an
		 * integer value, but when estimates are used, it may need to
		 * have a fractional part.
		 * @return Ramp queue demand for current period (vehicles) */
        private double calculateQueueDemand(){
            double vol = calculateRampDemand();
            System.out.print(meter.getID()+"-");
            if(vol >= 0){
                System.out.print("OK!-");
                if(isQueueOccupancyHigh()){
                    queueFullCount++;
                    System.out.print("OCC_High");
                }
                else{
                    queueFullCount = 0;
                }
//                if(queueFullCount > 0){
//                    System.out.print("-OCCFULL");
//                    vol += estimateQueueUndercount();
//                }
                System.out.println();
                return vol;
            } else{
                System.out.print("ERROR");
                System.out.println();
                int target = getDefaultTarget(meter);
                return volumePeriod(target, STEP_SECONDS);
            }
        }
        
        /** Estimate the queue undercount (vehicles) */
        private double estimateQueueUndercount() {
            double full_secs = queueFullCount * STEP_SECONDS;
            double r = Math.min(2 * full_secs / MeteringConfig.MAX_WAIT_TIME, 1);
            double under = maxStorage() - queueLength();
            return Math.max(r * under, 1);
        }
        
        //Fix me
        private double maxStorage(){
            //int stor_ft = meter.getMeter().getStorage() * entrance.getLanes();
            int stor_ft = (int)meter.getMeter().getStorage() * entrance.getLaneCount();
            double JAM_VPF = (double)MeteringConfig.MAX_RAMP_DENSITY / MeteringConfig.METER_PER_KM;
//            System.out.println("meter("+meter.getID()+") : stor="+meter.getMeter().getStorage()+
//                    "("+entrance.getLaneCount()+"), storft="+stor_ft+
//                    ", md="+MeteringConfig.MAX_RAMP_DENSITY+", mpk="+MeteringConfig.METER_PER_KM+
//                    ", JAMVPF="+JAM_VPF+", s*j="+stor_ft*JAM_VPF);
            return stor_ft * JAM_VPF;
        }
        
        /** Estimate the length of queue (vehicles) */
        public double queueLength(){
            return cumulativeDemand() - passage_accum;
        }
        
        /** Calculate target demand rate at queue detector.
		 * @return Target demand flow rate (vehicles / hour) */
        private int targetDemand(){
            Double avg_demand = demandHist.average();
            if(avg_demand != null)
                return (int)Math.round(avg_demand);
            else
                return getDefaultTarget(meter);
        }
        /**
         * need fix
         * Get the default target metering rate (vehicles / hour) */
        private int getDefaultTarget(SimMeter m){
            if( m != null)
                return MeteringConfig.MAX_RATE;
            else
                return MeteringConfig.MAX_RATE;
        }
        
        /** Update ramp passage output state */
        private void updatePassageState(){
            int passage_vol = calculatePassageCount();
//            if(passage_vol < 0)
//                passage_failure = true;
            double passage_rate = flowRate(passage_vol);
            passageHist.push(passage_rate);
            passage_accum += passage_vol;
        }
        
        /** Calculate passage count (vehicles).
		 * @return Passage vehicle count */
        private int calculatePassageCount() {
            if (meter == null) {
                return MISSING_DATA;
            }
            SimDetector[] pDets = meter.getPassage();
            Double vol = (double) 0;
            if (pDets != null) {
                for (int i = 0; i < pDets.length; i++) {
                    double d = pDets[i].getData(TrafficType.VOLUME);
                    if (d > 0) {
                        vol += d;
                    }
                }
            }

            if (vol >= 0) {
                return vol.intValue();
            }

            return MISSING_DATA;
        }
        
        private void updateQueueState(){
            if(isQueueEmpty())
                queueEmptyCount++;
            else
                queueEmptyCount = 0;
            if(queueEmptyCount > QUEUE_EMPTY_STEPS)
                resetAccumulators();
        }
        
        private boolean isQueueEmpty(){
            return !isQueueOccupancyHigh() &&
                    isDemandBelowpassage();
        }
        
        private boolean isDemandBelowpassage(){
            return queueLength() < 0;
        }
        
//        private void updateDemandState_old() {
//            double p_volume = calculateRampVolume();
//            double demand = calculateRampDemand();
//            
//            double prevCd = 0;
//            double prevCq = 0;
//            
//            if(this.cumulativeDemand.size()>0) prevCd = this.cumulativeDemand.get(this.cumulativeDemand.size()-1);
//            if(this.cumulativeMergingVolume.size()>0) prevCq = this.cumulativeMergingVolume.get(this.cumulativeMergingVolume.size()-1);
//            
//            this.cumulativeDemand.add(prevCd + demand);
//            this.cumulativeMergingVolume.add(prevCq + p_volume);
//            
//            this.lastDemand = demand;                
//            this.lastVolumeOfRamp = p_volume;   
//        }
        
        /**
         * Return ramp demand
         * @return 
         */
        private double calculateRampDemand() {
            
            if(this.meter == null) return 0;
            
            SimDetector[] qDets = this.meter.getQueue();           
            
            double demand = 0;
            double p_flow = calculateRampVolume();
            
            // queue detector is ok
            if(qDets != null) {
                for(int i=0; i<qDets.length; i++) {
                    double d = (int)simObjects.getDetector(qDets[i].getID()).getData(TrafficType.VOLUME);
                    if(d > 0) demand += d;
                }
                
                return demand;
            }
                        
            return p_flow * PASSAGE_DEMAND_FACTOR;                                    
        }
        
        /**
         * Return ramp flow now
         * @return ramp flow
         */
        private double calculateRampVolume() {
            return calculateRampVolume(0);
        }
        
        /**
         * Return ramp flow before given prevStep intervals
         * @param prevStep
         * @return ramp flow
         */
        private double calculateRampVolume(int prevStep) {
            if (this.meter == null) {
                return 0;
            }

            SimDetector[] pDets = this.meter.getPassage();

            double p_volume = 0;

            if (pDets != null) {
                for (int i = 0; i < pDets.length; i++) {
                    double d = simObjects.getDetector(pDets[i].getID()).getData(TrafficType.VOLUME, prevStep);
                    if (d > 0) {
                        p_volume += d;
                    }
                }

                return p_volume;
            }

            return 0;
        }
        
        public double getMaxOccupancy(){
            double occ = 0;
            for(SimDetector det : meter.getQueue()){
                double d_occ = det.getData(TrafficType.OCCUPANCY, 0);
                if(d_occ != -1)
                    occ = Math.max(d_occ, occ);
            }
            return occ;
        }
        
        private boolean isQueueOccupancyHigh() {
            return getMaxOccupancy() > MeteringConfig.QUEUE_OCC_THRESHOLD;
        }
        
        private Double cumulativeDemand(int i){
            Double d = demandAccumHist.get(i);
            if(d != null)
                return d;
            else
                return 0d;
        }
        
        private double cumulativeDemand(){
            return cumulativeDemand(0);
        }
        

        /**
         * Set metering rate
         * @param Rnext 
         */
        public void setRate(double Rnext) {
            if(this.meter == null) return;
            int r = (int)Math.round(Rnext);
            r = Math.max(r, minimumRate);
            r = Math.min(r, maximumRate);
            this.lastRate = r;
            float redTime = calculateRedTime(lastRate);
            redTime = Math.round(redTime * 10) / 10f;
            this.isRateUpdated = true;
            this.meter.setRate((byte)1);
            this.meter.setRedTime(redTime);
        }
        
        public void startMetering() {
            this.isMetering = true;
            resetAccumulators();
        }
        
        public void stopMetering() {
            this.isMetering = false;
            this.rateHist.clear();
//            this.lastDemand = 0;
//            this.lastVolumeOfRamp = 0;
            this.lastRate = 0;            
//            this.rateHistory.clear();
            this.noBottleneckCount = 0;
            this.hasBeenStoped = true;
            this.meter.setRate(SimConfig.METER_RATE_FLASH);
            resetAccumulators();
        }
        
        private void resetAccumulators(){
            passage_failure = false;
            demandAccumHist.clear();
            passage_accum = 0;
            queueEmptyCount = 0;
            queueFullCount = 0;
        }
        
        /**
         * Return red time that converted from rate
         * @param rate
         * @return red time in seconds
         */
        private float calculateRedTime(double rate) {
            float cycle = 3600 / (float)rate;
            return Math.max(cycle - this.meter.GREEN_YELLOW_TIME, MeteringConfig.MIN_RED_TIME);
        }

        public void saveRateHistory(double Rnext) {
           this.rateHist.push(Rnext);
        }

        public void saveSegmentDensityHistory(double Kt) {
            this.segmentDensityHist.push(Kt);
        }
        
        public Double getSegmentDensity(int prev)
        {
            return segmentDensityHist.get(prev);            
        }

        public void addNoBottleneckCount() {
            this.noBottleneckCount++;
        }
        
        public void resetNoBottleneckCount() {
            this.noBottleneckCount = 0;
        }
        
        public int getNoBottleneckCount() {
            return this.noBottleneckCount;        
        }

        public void setBottleneck(StationState bottleneck) {
            this.bottleneck = bottleneck;
        }

        public StationState getBottleneck() {
            return this.bottleneck;
        }
        
        public boolean hasMeter(){
            if(meter != null)
                return true;
            else
                return false;
        }
    }
    
    
    
    /**
     * State class that represents exit
     */
    class ExitState extends State {

        Exit exit;
        
        public ExitState(Exit e) {
            super(e.getID(), e,null);
            this.exit = e;
            type = StateType.EXIT;
            exitsStates.add(this);
        }     
        
    }
    
    /**
     * State class that represents station
     */
    public class StationState extends infra.simulation.StationState {

        //EntranceState associatedEntrance;
        DIR associatedDir;
        int stationIdx = 0;
        
        int noBsCount = 0;
        boolean isBottleneck = false;
        boolean isPrevBottleneck = false;
        boolean isConsecutiveBS = false;
                
        public IDetectorChecker dc = Metering.dc;
        ArrayList<EntranceState> associatedEntrances = new ArrayList<EntranceState>();
        double Kc = MeteringConfig.Kc;
        boolean isPrimaryBottleneck = false;
        double coordinateKc = -1;
        int trendIndicator;
        int coordinateLimit = 1;
        private boolean isPrevPrimaryBottleneck = false;
        int idx;
                
        public StationState(Station s, Section _sec, SimInterval _sitv) {  
            super(s, _sec,simObjects,_sitv);
            idx = states.size();
            stationStates.add(this);
            
            this.stationIdx = stationStates.size()-1;
        }

        /**
         * Return acceleration from current station to down station
         * @param lastSampleIndex
         * @return 
         */
        public double getAcceleration(SimulationGroup sg) {
            double u2 = this.getIntervalAggregatedSpeed(sg);
            StationState downStationState = getDownstreamStationState(idx);
            if (downStationState == null) {
                return 0;
            }
            double u1 = downStationState.getIntervalAggregatedSpeed(sg);
            //return (u1 * u1 - u2 * u2) / (2 * TMO.getDistanceInMile(this.rnode, downStationState.rnode));
            return (u1 * u1 - u2 * u2) / (2 * DistanceUtil.getDistanceInKM(this.rnode, downStationState.rnode));
        }
        
        /**
         * 수정필요!! mile -> km
         * Associated Meter to Station
         *   - Iterate from upstream to downstream
         *   - Upstream meter will be associated to the station 
         *     when distance(Upstream Meter, Station) less than 500 feet 
         *          or Meter is not associated to any station
         *   - Downstream meter will be associated to the station
         *     when distance(Downstream Meter, Station) less than 1 mile         
         */
        private void setAssociatedEntrances() {
           
            ArrayList<EntranceState> upstreamEntrances = this.getUpstreamEntrances();
            ArrayList<EntranceState> downstreamEntrances = this.getDownstreamEntrances();
            
            StationState us = null, ds = null;
            if(this.stationIdx > 0) us = stationStates.get(this.stationIdx-1);
            if(this.stationIdx < stationStates.size()-1) ds = stationStates.get(this.stationIdx+1);
                        
            if(us != null) {
                for(EntranceState es : upstreamEntrances) {                    
                    int d = this.getDistanceToUpstreamEntrance(es);         //m
                    int ud = us.getDistanceToDownstreamEntrance(es) ;       //m
                    
                    // very close(?) or not allocated with upstream station                    
                    //if( ( d < 800 && ud > 500) || es.associatedStation == null) {
                    //if( ( d < 500 && d < ud) || es.associatedStation == null) {
                    if( ( d < MeteringConfig.UPSTREAM_THRESHOLD_DISTANCE && d < ud) || es.associatedStation == null) {
                        if(es.associatedStation != null) {
                            es.associatedStation.associatedEntrances.remove(es);
                        }                        
                        associatedEntrances.add(es);
                        es.associatedStation = this;
                    }
                    
                    
                }
            }
            
            if(ds != null) {
                for(EntranceState es : downstreamEntrances) {
                    int d = this.getDistanceToDownstreamEntrance(es);
                    //if(d < 5280) {
                    if(d < MeteringConfig.METER_PER_MILE) {
                        associatedEntrances.add(es);
                        es.associatedStation = this;
                    }
                }
            }
        }
        
        public ArrayList<EntranceState> getAssociatedEntrances() {
            return associatedEntrances;
        }
        
        public int getDistanceToUpstreamEntrance(EntranceState es) {
            if(this.idx <= 0) return -1;
            int distance = 0;
            State cursor = this;
            State s = null;
            
            boolean found = false;
            for(int i=this.idx-1; i>=0; i--) {
                s = states.get(i);
            
                //미터로
                distance += DistanceUtil.getDistanceInM(cursor.rnode, s.rnode);
                if(s.equals(es)) {
                    found = true;
                    break;
                }
                cursor = s;
            }
            if(found) return distance;
            else return -1;
        }
        
        public int getDistanceToDownstreamEntrance(EntranceState es) {
            if(this.idx >= states.size()-1) return -1;
            int distance = 0;
            State cursor = this;
            State s = null;
            
            boolean found = false;
            for(int i=this.idx+1; i<states.size(); i++) {
                s = states.get(i);
                distance += DistanceUtil.getDistanceInM(cursor.rnode, s.rnode);
                if(s.equals(es)) {
                    found = true;
                    break;
                }
                cursor = s;
            }
            if(found) return distance;
            else return -1;
        }        

        /**
         * Return upstream entrances up to next upstream station
         * @return upstream entrance list
         */
        public ArrayList<EntranceState> getUpstreamEntrances() {
            ArrayList<EntranceState> list = new ArrayList<EntranceState>();
            if(this.idx <= 0) return list;
                 
            for(int i=this.idx-1; i>=0; i--) {
                State s = states.get(i);
                if(s.type.isStation()) break;
                if(s.type.isEntrance() && ((EntranceState)s).meter != null) list.add((EntranceState)s);
            }        
            return list;
        }    
        
        /**
         * Return downstream entrances up to next downstream station
         * @return downstream entrance list
         */        
        public ArrayList<EntranceState> getDownstreamEntrances() {
            ArrayList<EntranceState> list = new ArrayList<EntranceState>();
            if(this.idx >= states.size()-1) return list;

            for(int i=this.idx+1; i<states.size(); i++) {
                State s = states.get(i);
                if(s.type.isStation()) break;
                if(s.type.isEntrance()&& ((EntranceState)s).meter != null) list.add((EntranceState)s);
            }        
            return list;
        }          

        public void afterMetering() {
            // initialize variables to control coordinate
            if(!this.isPrimaryBottleneck) {
                this.trendIndicator = 0;                
                this.coordinateLimit = 1;
            }
            this.coordinateKc = -1;
            this.isPrevPrimaryBottleneck = this.isPrimaryBottleneck;
            
            this.isPrevBottleneck = this.isBottleneck;
            this.isBottleneck = false;
            this.isPrimaryBottleneck = false;
            this.isConsecutiveBS = false;
            
        }

        public void updatePrimaryState(SimulationGroup sg) {
            if(!this.isPrevPrimaryBottleneck) {
                this.trendIndicator = 0;
            } else if(this.getIntervalAggregatedDensity(SimulationGroup.Meter) > this.getIntervalAggregatedDensity(sg,1)) {
                this.trendIndicator++;
            } else if(this.getIntervalAggregatedDensity(SimulationGroup.Meter) < this.getIntervalAggregatedDensity(sg,1)) {
                this.trendIndicator--;
            }
            this.trendIndicator = Math.min(5, this.trendIndicator);
            this.trendIndicator = Math.max(-5, this.trendIndicator);
        }
                
    }
    
    /**
     * Backup
     * State class that represents station
     */
//    public class StationState extends State {
//
//        SimStation station;
//        //EntranceState associatedEntrance;
//        DIR associatedDir;
//        int stationIdx = 0;
//        double aggregatedDensity = 0;
//        double aggregatedSpeed = 0;
//        
//        int MOVING_U_AVG_WINDOW = 2;
//        int MOVING_K_AVG_WINDOW = 2;
//        
//        int MAX_SPEED_ALPHA = 10;
//        int lastSpeedAggCount = 0;
//        int noBsCount = 0;
//        boolean isBottleneck = false;
//        boolean isPrevBottleneck = false;
//        boolean isConsecutiveBS = false;
//        double NEARBY_DISTANCE = 2000;  // 2000 feet
//                
//        public IDetectorChecker dc = Metering.dc;
//        ArrayList<EntranceState> associatedEntrances = new ArrayList<EntranceState>();
//        double Kc = MeteringConfig.Kc;
//        boolean isPrimaryBottleneck = false;
//        double coordinateKc = -1;
//        int trendIndicator;
//        int coordinateLimit = 1;
//        private boolean isPrevPrimaryBottleneck = false;
//        int idx;
//                
//        public StationState(Station s, StateInterval _sitv) {  
//            super(s.getStationId(), s,_sitv);
//            idx = states.size();
//            this.station = simObjects.getStation(s.getStationId());
//            type = StateType.STATION;
//            stationStates.add(this);
//            this.stationIdx = stationStates.size()-1;
//        }
//
//        /**
//         * Return acceleration from current station to down station
//         * @param lastSampleIndex
//         * @return 
//         */
//        public double getAcceleration() {
//            double u2 = this.getAggregatedSpeed();
//            StationState downStationState = getDownstreamStationState(idx);
//            if (downStationState == null) {
//                return 0;
//            }
//            double u1 = downStationState.getAggregatedSpeed();
//            return (u1 * u1 - u2 * u2) / (2 * TMO.getDistanceInMile(this.rnode, downStationState.rnode));
//        }
//        
//        /**
//         * Return aggregated density
//         * @return 
//         */
//        public double getAggregatedDensity() {
//            return getAggregatedDensity(0);
//        }        
//        
//        /**
//         * Returns aggregated density before given prevStep time step
//         * @return 
//         */
//        public double getAggregatedDensity(int prevStep) {
//            double sum = 0;
//            int validCount = 0;
//            for(int i=0; i<MOVING_K_AVG_WINDOW; i++) {
//                double k = station.getData(dc, TrafficType.DENSITY, prevStep+i);
//                //debug
////                double k = station.getData(dc, TrafficType.DENSITY, prevStep+i);
//
//                if(k > 0) {
//                    sum += k;
//                    validCount++;
//                }
//            }
//            if(validCount == 0 || sum < 0) return 0;
//            
//            return sum/validCount;
//        }   
//        
//        public double getAggregatedSpeed() {
//            return getAggregatedSpeed(0);
//        }        
//
//        public double getAggregatedSpeed(int prevStep) {
//            return getMovingAverageSpeed(prevStep, MOVING_U_AVG_WINDOW);
//        }
//
//        public double getMovingAverageSpeed(int prevStep, int howManySteps)
//        {
//            double sum = 0;
//            int validCount = 0;
//            for(int i=0; i<howManySteps; i++) {
//                double u = station.getData(dc, TrafficType.SPEED, prevStep+i);
//                if(u > 0) {
//                    sum += u;
//                    validCount++;
//                }
//            }
//            if(validCount == 0 || sum < 0) return 0;
//            return sum/validCount;                        
//        }
//        
//        
//        /**
//         * Associated Meter to Station
//         *   - Iterate from upstream to downstream
//         *   - Upstream meter will be associated to the station 
//         *     when distance(Upstream Meter, Station) less than 500 feet 
//         *          or Meter is not associated to any station
//         *   - Downstream meter will be associated to the station
//         *     when distance(Downstream Meter, Station) less than 1 mile         
//         */
//        private void setAssociatedEntrances() {
//           
//            ArrayList<EntranceState> upstreamEntrances = this.getUpstreamEntrances();
//            ArrayList<EntranceState> downstreamEntrances = this.getDownstreamEntrances();
//            
//            StationState us = null, ds = null;
//            if(this.stationIdx > 0) us = stationStates.get(this.stationIdx-1);
//            if(this.stationIdx < stationStates.size()-1) ds = stationStates.get(this.stationIdx+1);
//                        
//            if(us != null) {
//                for(EntranceState es : upstreamEntrances) {                    
//                    int d = this.getDistanceToUpstreamEntrance(es);                    
//                    int ud = us.getDistanceToDownstreamEntrance(es) ;
//                    
//                    // very close(?) or not allocated with upstream station                    
//                    //if( ( d < 800 && ud > 500) || es.associatedStation == null) {
//                    if( ( d < 500 && d < ud) || es.associatedStation == null) {
//                        if(es.associatedStation != null) {
//                            es.associatedStation.associatedEntrances.remove(es);
//                        }                        
//                        associatedEntrances.add(es);
//                        es.associatedStation = this;
//                    }
//                    
//                    
//                }
//            }
//            
//            if(ds != null) {
//                for(EntranceState es : downstreamEntrances) {
//                    int d = this.getDistanceToDownstreamEntrance(es);
//                    if(d < 5280) {
//                        associatedEntrances.add(es);
//                        es.associatedStation = this;
//                    }
//                }
//            }
//        }
//        
//        public ArrayList<EntranceState> getAssociatedEntrances() {
//            return associatedEntrances;
//        }
//        
//        public int getDistanceToUpstreamEntrance(EntranceState es) {
//            if(this.idx <= 0) return -1;
//            int distance = 0;
//            State cursor = this;
//            State s = null;
//            
//            boolean found = false;
//            for(int i=this.idx-1; i>=0; i--) {
//                s = states.get(i);
//            
//                distance += TMO.getDistanceInFeet(cursor.rnode, s.rnode);
//                if(s.equals(es)) {
//                    found = true;
//                    break;
//                }
//                cursor = s;
//            }
//            if(found) return distance;
//            else return -1;
//        }
//        
//        public int getDistanceToDownstreamEntrance(EntranceState es) {
//            if(this.idx >= states.size()-1) return -1;
//            int distance = 0;
//            State cursor = this;
//            State s = null;
//            
//            boolean found = false;
//            for(int i=this.idx+1; i<states.size(); i++) {
//                s = states.get(i);
//                distance += TMO.getDistanceInFeet(cursor.rnode, s.rnode);
//                if(s.equals(es)) {
//                    found = true;
//                    break;
//                }
//                cursor = s;
//            }
//            if(found) return distance;
//            else return -1;
//        }        
//
//        /**
//         * Return upstream entrances up to next upstream station
//         * @return upstream entrance list
//         */
//        public ArrayList<EntranceState> getUpstreamEntrances() {
//            ArrayList<EntranceState> list = new ArrayList<EntranceState>();
//            if(this.idx <= 0) return list;
//                 
//            for(int i=this.idx-1; i>=0; i--) {
//                State s = states.get(i);
//                if(s.type.isStation()) break;
//                if(s.type.isEntrance() && ((EntranceState)s).meter != null) list.add((EntranceState)s);
//            }        
//            return list;
//        }    
//        
//        /**
//         * Return downstream entrances up to next downstream station
//         * @return downstream entrance list
//         */        
//        public ArrayList<EntranceState> getDownstreamEntrances() {
//            ArrayList<EntranceState> list = new ArrayList<EntranceState>();
//            if(this.idx >= states.size()-1) return list;
//
//            for(int i=this.idx+1; i<states.size(); i++) {
//                State s = states.get(i);
//                if(s.type.isStation()) break;
//                if(s.type.isEntrance()&& ((EntranceState)s).meter != null) list.add((EntranceState)s);
//            }        
//            return list;
//        }          
//        public double getVolume(){
//            return this.station.getData(dc,TrafficType.VOLUME);
//        }
//        public double getFlow(){
//            return this.station.getData(dc,TrafficType.FLOW);
//        }
//        /**
//         * @deprecated 
//         * @return 
//         */
//        public double getSpeed() {
//            return this.station.getData(dc, TrafficType.SPEED);
//        }
//
//        /**
//         * @deprecated 
//         * @return 
//         */
//        public double getDensity() {
//            return this.station.getData(dc, TrafficType.DENSITY);
////            return this.station.getDataForDebug(dc, TrafficType.DENSITY);
//        }
//        
//        public void afterMetering() {
//            // initialize variables to control coordinate
//            if(!this.isPrimaryBottleneck) {
//                this.trendIndicator = 0;                
//                this.coordinateLimit = 1;
//            }
//            this.coordinateKc = -1;
//            this.isPrevPrimaryBottleneck = this.isPrimaryBottleneck;
//            
//            this.isPrevBottleneck = this.isBottleneck;
//            this.isBottleneck = false;
//            this.isPrimaryBottleneck = false;
//            this.isConsecutiveBS = false;
//            
//        }
//
//        public void updatePrimaryState() {
//            if(!this.isPrevPrimaryBottleneck) {
//                this.trendIndicator = 0;
//            } else if(this.getAggregatedDensity() > this.getAggregatedDensity(1)) {
//                this.trendIndicator++;
//            } else if(this.getAggregatedDensity() < this.getAggregatedDensity(1)) {
//                this.trendIndicator--;
//            }
//            this.trendIndicator = Math.min(5, this.trendIndicator);
//            this.trendIndicator = Math.max(-5, this.trendIndicator);
//        }
//                
//    }
}
