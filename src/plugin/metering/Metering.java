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
package plugin.metering;

import infra.Section;
import infra.infraobject.Detector;
import infra.infraobject.Station;
import infra.interfaces.IDetectorChecker;
import infra.simobjects.SimDetector;
import infra.simobjects.SimMeter;
import infra.simobjects.SimObjects;
import infra.simobjects.SimStation;
import infra.simulation.SimInterval;
import infra.simulation.SimulationGroup;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.Logger;
import plugin.metering.MeteringSectionHelper.EntranceState;
import plugin.metering.MeteringSectionHelper.StationState;

/**
 *
 * @author Chongmyung Park
 * 
 */
public class Metering {

    private ArrayList<SimDetector> detectors;
    public static MeteringSectionHelper sectionHelper;
    private ArrayList<SimStation> stations = new ArrayList<SimStation>();
    //private SimObjects simObjects = SimObjects.getInstance();
    private SimObjects simObjects;
    private BottleneckFinder bottleneckFinder;
    public double Kjam = MeteringConfig.Kjam;
    public double MAX_RATE = MeteringConfig.MAX_RATE;
    private float simTime = 0f;
    private int dataCount = 0;
    private Logger meteringStartAndStopLog;
    private Logger rateLog;
    private Logger rampDensityLog;
    private Logger speedLog;
    private Logger densityLog;
    private Logger bottleneckLog;
    //private Logger waitTimeLog;    
    private List<Double> bottleneckKHistory = new ArrayList<Double>();
    private List<Double> corridorKHistory = new ArrayList<Double>();
    private boolean meteringStarted = false;
    private boolean checkStopCondition = false;
    private int avgDensityWindow = 30;
    private int avgDensityTrend = 10;
    private int singleBottleneckCount = 0;
    
    private final SimInterval SimulationInterval;
    
    public static IDetectorChecker dc = new IDetectorChecker() {

        @Override
        public boolean check(Detector d) {
//            if (d.isAbandoned() || d.isHov()) {
//                return false;
//            }
            return true;
        }
    };

    /**
     * Executes metering algorithm
     * @param dataCount collected traffic data count - 1
     */
    public void run(int dataCount, float simTime) {

        this.simTime = simTime;
        System.out.println("sim Time : "+simTime);
        this.dataCount = dataCount;

        if (dataCount == 0) {
            makeLogHead();
        }

        System.out.println("[" + String.format("%3d", dataCount) + "]=======================================");
        
//        updateEntranceStates();

        if (dataCount < 6) {
            return;
        }

        bottleneckFinder.findBottlenecks((int)this.simTime);

        calculateMeteringRates();
        
        if(meteringStarted && !checkStopCondition) checkCorridorState();
        else if(checkStopCondition) stopMetering();

        
//        adjustKb();
        
        makeLog(dataCount);

        afterMetering();

    }

    /**
     * Calculate demand and output for all entrances
     */
    public void updateEntranceStates() {
        ArrayList<EntranceState> entranceStates = sectionHelper.getEntranceStates();
        for (EntranceState e : entranceStates) {
            e.updateState();
        }
    }

    /**
     * Calculate metering rates for all bottlenecks
     */
    private void calculateMeteringRates() {

        ArrayList<StationState> stationStates = sectionHelper.getStationStates();

        // Print debug message
        System.out.print("  - Bottlenecks : ");
        for (int i = stationStates.size() - 1; i >= 0; i--) {
            StationState station = stationStates.get(i);
            if (station.isBottleneck) {
                System.out.print(station.id + ", ");
            }
        }
        System.out.println();

        // downstream boundary -> upstream
        for (int i = stationStates.size() - 1; i >= 0; i--) {
            StationState station = stationStates.get(i);
            if (!station.isBottleneck) {
                continue;
            }
            calculateMeteringRates(station, i);
        }

        // when meter is not in zone
        for (StationState s : stationStates) {
            defaultMetering(s);
        }

        System.out.println("---");
    }

    /**
     * Calculate metering rates for a bottleneck
     * @param bottleneck bottleneck station
     */
    private void calculateMeteringRates(StationState bottleneck, int stationIndex) {

        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        System.out.println("  [Calculate Metering - Bottleneck = " + bottleneck.id);


        // calculate rates for entrance associated with bottleneck
        for (EntranceState es : bottleneck.getAssociatedEntrances()) {
            // equation
            double Rnext = equation(bottleneck, es);
            if (!isMetering(bottleneck, null, es, Rnext)) {
                continue;
            }
            es.setBottleneck(bottleneck);
            es.setRate(Rnext);
        }

        // calculate rates 
        // from upstream station of bottleneck to upstream boundary or next bottleneck
        for (int i = stationIndex - 1; i >= 0; i--) {

            // break, if upstation is bottleneck
            StationState upStation = stationStates.get(i);
            if (upStation.isBottleneck) {
                break;
            }

            for (EntranceState es : upStation.getAssociatedEntrances()) {
                double Rnext = equation(bottleneck, upStation, es);
                if (!isMetering(bottleneck, upStation, es, Rnext)) {
                    continue;
                }
                es.setBottleneck(bottleneck);
                es.setRate(Rnext);
            }

        }
    }

    /**
     * Check corridor average density condition
     */
    private void checkCorridorState() {
        
        List<StationState> stations = sectionHelper.getStationStates();
        
        int bottleneckCount = 0;
        
        StationState downstreamBS = null;
        
        // calculate sum of density of bottlenecks
        for (StationState s : stations) {
            if (s.isBottleneck) {
                downstreamBS = s;
                bottleneckCount++;
            }
        }

        double avgK = 0;
        StationState upStation = stations.get(0);
        if(downstreamBS != null) avgK = sectionHelper.getAverageDensity(upStation, downstreamBS,SimulationGroup.Meter);
                                      
        corridorKHistory.add(avgK);
        int size = this.corridorKHistory.size();
        
        if(bottleneckCount > 1) {
            singleBottleneckCount = 0;
            return;
        }
                       
        if(size < avgDensityWindow + avgDensityTrend) return;

        // check avg K of corridor average density
        for(int i=0; i<avgDensityTrend; i++)
        {
            double ma_next = movingAverage(this.corridorKHistory, i, avgDensityWindow);
            double ma_prev = movingAverage(this.corridorKHistory, i+1, avgDensityWindow);
            if(ma_next > ma_prev) return;
        }
        
        MeteringConfig.Kb = MeteringConfig.Kd;
        MeteringConfig.BottleneckTrendCount = 4;
        this.checkStopCondition = true;        
    }        
    
    /**
     * Check corridor average density condition
     */
    private void checkCorridorState_old() {
        
        List<StationState> stations = sectionHelper.getStationStates();
        StationState upStation = stations.get(0);
        StationState downStation = stations.get(stations.size()-1);
        double avgK = sectionHelper.getAverageDensity(upStation, downStation,SimulationGroup.Meter);
        
        corridorKHistory.add(avgK);
        int size = this.corridorKHistory.size();
        
        int bottleneckCount = 0;
        
        StationState bs = null;
        // calculate sum of density of bottlenecks
        for (StationState s : stations) {
            if (s.isBottleneck) {
                bs = s;
                bottleneckCount++;
            }
        }
        
        if(bottleneckCount > 1) {
            singleBottleneckCount = 0;
            return;
        }        
//        singleBottleneckCount++;        
//        if(singleBottleneckCount < 10) return;
               
        if(size < avgDensityWindow + avgDensityTrend) return;
        //if(bs != null && bs.getAggregatedDensity() >= MeteringConfig.Kd) return;
        
        // check avg K of corridor average density
        for(int i=0; i<avgDensityTrend; i++)
        {
            double ma_next = movingAverage(this.corridorKHistory, i, avgDensityWindow);
            double ma_prev = movingAverage(this.corridorKHistory, i+1, avgDensityWindow);
            if(ma_next > ma_prev) return;
        }
        
        MeteringConfig.Kb = MeteringConfig.Kd;
        MeteringConfig.BottleneckTrendCount = 4;
        this.checkStopCondition = true;
        
    }    
    
    /**
     * Stop metering of ramp meter satisfying conditions
     */
    private void stopMetering() {

        ArrayList<StationState> stationStates = sectionHelper.getStationStates();

        int N = MeteringConfig.stopDuration;

        // iterate from downstream to upstream
        boolean hasBottleneck = false;
        for (int i = stationStates.size() - 1; i >= 0; i--) {

            StationState s = stationStates.get(i);
            
            // station is bottleneck
            if (s.isBottleneck) {
                hasBottleneck = true;
                // set entrance's no-bottleneck count 0
                for (EntranceState es : s.getAssociatedEntrances()) {
                    es.resetNoBottleneckCount();
                }
                continue;
            }
            
            // for all entrances
            for (EntranceState es : s.getAssociatedEntrances()) 
            {
                // if meter isn't working, pass
                if (!es.isMetering) continue;

                // if rate history is short, pass (do not stop)
                if (es.countRateHistory() < N) continue;
                
                boolean shouldStop = false;
                // COMMON STOP CONDITION : segment density is low for n times
                for (int k = 0; k < N; k++) {
                    Double sk = es.getSegmentDensity(k);
                    if (sk > MeteringConfig.Kd) {
                        shouldStop = true;
                        break;
                    }
                }             
                
                if(shouldStop) continue;
                
                /////////////////////////////////////////////////////////////
                // No bottleneck at downstream side
                /////////////////////////////////////////////////////////////
                
                // Stop Condition 1 : no bottleneck at downstream side for n times
                if (!hasBottleneck) {
                    es.addNoBottleneckCount();

                    // if there's no bottleneck for a long time (N times)
                    if (es.getNoBottleneckCount() >= N) {                        
                        System.out.println(this.dataCount + "(1) : " + es.meter.getID() + " is stoped!!");
                        meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getID() + ",stop,1");
                        es.stopMetering();
                        continue;
                    }
                    
                    // end of checking about has-not-bottleneck case
                    continue;                    
                }
                
                
                /////////////////////////////////////////////////////////////
                // Bottleneck exists at downstream side
                /////////////////////////////////////////////////////////////                
                boolean satisfyRateCondition = true;
                
                es.resetNoBottleneckCount();

                StringBuilder segK = new StringBuilder();
                segK.append(",SK");
                
                // Stop Condition 2 : qj,t <= Rj,t for n times
                //   - n : 10 (5min)
                for (int k = 0; k < N; k++) {
                    double q = es.getMergingVolume(k) * 120;
                    double rate = es.getRate(k);
                    if (q > rate) {
                        satisfyRateCondition = false;
                    }
                    Double sk = es.getSegmentDensity(this.dataCount - k);
                    segK.append("," + sk);
                }            
                
                if ( satisfyRateCondition ) {
                    System.out.println(this.dataCount + "(2) : " + es.meter.getID() + " is stoped!!");
                    meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getID() + ",stop,2" + segK.toString());
                    es.stopMetering();
                }

            } // loop : entrances

        } // loop : stations

    }
    
    /**
     * Return moving average
     * @param list data list
     * @param prevStep 
     * @param howManySteps
     * @return 
     */
    private double movingAverage(List<Double> list, int prevStep, int howManySteps)
    {
        double sum = 0;
        int validCount = 0;
        int size = list.size();
        for(int i=0; i<howManySteps; i++) {
            int idx = size-prevStep-i-1;
            if(idx < 0) break;
            double u = list.get(idx);
            if(u > 0) {
                sum += u;
                validCount++;
            }
        }
        if(validCount == 0 || sum < 0) return 0;
        return sum/validCount;           
    }
    
    private boolean isMetering(StationState bs, StationState us, EntranceState es, double Rnext) {

        es.saveRateHistory(Rnext);

        // Condition 0 : already started
        if (es.isMetering) {
            return true;
        }

        ///////////////////////////////////
        // Check Start Conditions
        ///////////////////////////////////

        // Before stopped
        //    - SegmentDensity >= Kb
        //    - OR -
        //    - Merging flow of ramp >= KstartRate * Allocated Rate (for n times)
        // After stopped
        //    - SegmentDensity >= Kb
        //    - AND -
        //    - Merging flow of ramp >= KstartRate * Allocated Rate (for n times)

        boolean satisfyDensityCondition = false;
        boolean satisfyRateCondition = false;

        // segment density (average density from upstream station to bottleneck)
        if(!es.hasBeenStoped) {
            // Start Condition 1 : segment density > Kthres_start
            double segmentDensity = bs.getIntervalAggregatedDensity(SimulationGroup.Meter);
            if (us != null) {
                segmentDensity = sectionHelper.getAverageDensity(us, bs,SimulationGroup.Meter);
            }            
            if (segmentDensity >= MeteringConfig.Kb) {
                satisfyDensityCondition = true;
            }
            
            // TRY @ 20110930
//            double segmentDensity = 0;
//            satisfyDensityCondition = true;
//            for (int i = 0; i < 3; i++) {
//                segmentDensity = bs.getAggregatedDensity(i);
//                if (us != null) {
//                    segmentDensity = sectionHelper.getAverageDensity(us, bs, i);
//                }                 
//                if (segmentDensity < MeteringConfig.Kb) {
//                    satisfyDensityCondition = false;
//                }
//            }

            // Start Condition 2 : merging flow of ramp > Rate * 0.8
            if (es.countRateHistory() >= 3) {
                satisfyRateCondition = true;
                for (int i = 0; i < 3; i++) {
                    double q = es.getMergingVolume(i) * 120;
                    double rate = es.getRate(i);
                    if (q < MeteringConfig.KstartRate * rate) {
                        satisfyRateCondition = false;
                    }
                }
            }
            
            if(satisfyRateCondition || satisfyDensityCondition) {
                meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getID() + ",start,KC="+satisfyDensityCondition+",RC="+satisfyRateCondition+",BS=" + bs.id + ",SK=" + segmentDensity);
                System.out.println(this.dataCount + " : " + es.meter.getID() + " is started!!");
                es.startMetering();        
                meteringStarted = true;
                return true;                
            } 
            return false;
        }
        
        // After stoping ...
        double segmentDensity = 0;    
        
        if(es.id == "M169S46") {
            System.out.println("M169S46 (after stopping) =================================");
        }        
        
        // if rate history is short, pass (do not start)
        if (es.countRateHistory() < 10) {
            return false;
        }
        
        for(int i=0; i<10; i++) {            
            if (us != null) segmentDensity = sectionHelper.getAverageDensity(us, bs, i,SimulationGroup.Meter);
            else segmentDensity = bs.getIntervalAggregatedDensity(SimulationGroup.Meter,i);
            
            // Start Condition 1 : segment density > Kthres_start
            if (segmentDensity < MeteringConfig.Kb) {
                System.out.println("  * SegmentDensity is low : " + segmentDensity + " < Kb:" + MeteringConfig.Kb);
                return false;
            }            
            
            double q = es.getMergingVolume(i) * 120;
            double rate = es.getRate(i);
            if (q < MeteringConfig.KstartRate * rate) {
                System.out.println("  * Merging volume : " + q + " < StartRate : " + MeteringConfig.KstartRate * rate);
                return false;
            }                        
        }
        
        meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getID() + ",start,KC="+satisfyDensityCondition+",RC="+satisfyRateCondition+",BS=" + bs.id + ",SK=" + segmentDensity);
        System.out.println(this.dataCount + " : " + es.meter.getID() + " is started!!");
        es.startMetering();        
        meteringStarted = true;
        return true;
    }

    /**
     * Calculate metering rates for all operating ramp but not in zone
     *   - do local metering
     * @param bottleneck bottleneck station
     */
    private void defaultMetering(StationState stationState) {
        for (EntranceState e : stationState.getAssociatedEntrances()) {
            if (e != null && e.isMetering && !e.isRateUpdated) {
                double Rnext = equation(stationState, null, e);
                e.setRate(Rnext);
            }
        }
    }

    /**
     * Calculate metering rates
     * @param stationState
     * @return 
     */
    private double equation(StationState stationState, EntranceState entrance) {
        return equation(stationState, null, entrance);
    }

    /**
     * Calculate metering rates
     * @param bottleneck
     * @return 
     */
    private double equation(StationState bottleneck, StationState upstream, EntranceState entrance) {

        //                       | Kt
        //                       |                 +
        //                       |
        //                       |
        //                       |             + 
        //                       |
        //                       + 
        //                       |
        //                       |
        //                 +     |
        //    +                  |
        // --p0------------p1----p2------------p3---p4-----> Kd-Kt
        //                       |
        //                       |
        // p0's x = Kd - Kjam
        // p2's x = 0
        // p4's x = Kd

        double Kt = bottleneck.getIntervalAggregatedDensity(SimulationGroup.Meter);
        if (upstream != null) {
            Kt = sectionHelper.getAverageDensity(upstream, bottleneck,SimulationGroup.Meter);
        }

        entrance.saveSegmentDensityHistory(Kt);

        boolean cooridnated = false;
        double Kc = bottleneck.Kc;
        if (bottleneck.coordinateKc > 0) {
            cooridnated = true;
            Kc = bottleneck.coordinateKc;
        }
        
        double Rmin = entrance.getMinimumRate();
        double Rmax = entrance.getMaximumRate();
        double Rt = entrance.getRate();
        
        double K_DES = MeteringConfig.Kd;
        double x = K_DES - Kt;
        double K_JAM = MeteringConfig.Kjam;
        
//        System.out.println(entrance.meter.getId() + " : Rmin-"+Rmin+", Rmax-"+Rmax+", Rt-"+Rt);
        
        KPoint p0 = new KPoint(K_DES - K_JAM, Rmin / Rt);
        KPoint p1 = new KPoint((K_DES - K_JAM) / 3,
                Rmin / Rt + (1 - Rmin / Rt) / 3);
        KPoint p2 = new KPoint(0, 1);
        if(Rmin >= Rt)
                p0.y = p1.y = p2.y = Rmin / Rt;
        KPoint p4 = new KPoint(K_DES, Rmax / Rt);

        // Mainline graph connection 2 points
        double alpha = getAlpha(p0, p2, p4, x);

        // Ramp meter rate for next time interval
        double Rnext = Rt * alpha;

//        double cycle = 3600 / Rnext;
//        double redTime = Math.round(Math.max(cycle - 2, 0.1) * 10) / 10f;
//        if (upstream == null) {
//            System.out.print("    " + bottleneck.id
//                    + "(" + entrance.meter.getId() + ")");
//        } else {
//            System.out.print("    " + bottleneck.id
//                    + " ~ " + upstream.id + "(" + entrance.meter.getId() + ")");
//        }
//        System.out.println(
//                " : Rnext=" + String.format("%.2f", Rnext)
//                + ", Kc=" + String.format("%.2f", Kc) + "(" + cooridnated + ")"
//                + ", Kd=" + String.format("%.2f", K_DES)
//                + ", redTime=" + String.format("%.2f", redTime)
//                + ", k=" + String.format("%.2f", Kt));
////                + ", alpha=" + String.format("%.2f", alpha)
////                + ", Rmin=" + String.format("%.2f", Rmin)
////                + ", Rt=" + String.format("%.2f", Rt)
////                + ", ent_demand=" + String.format("%.2f", d)
////                + ", ent_flow=" + String.format("%.2f", q));
//        ///////////////////////////////////////////////////////////////////////
        return Rnext;
    }
    
    /** Calculate Alpha Value */
    private double getAlpha(KPoint P0, KPoint P2, KPoint P4, double x) {
        KPoint start = P0, end = P2;
        if(x >= 0) {
            start = P2;
            end = P4;
        } else {
            start = P0;
            end = P2;
        }
        double yd = end.y - start.y;
        double xd = end.x - start.x;
        if(xd != 0)
            return (yd / xd) * (x - start.x) + start.y;
        else
            return 0;
    }

    /**
     * Post metering process
     */
    private void afterMetering() {
        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        ArrayList<EntranceState> entranceStates = sectionHelper.getEntranceStates();

        for (StationState s : stationStates) {
            s.afterMetering();
        }
        for (EntranceState es : entranceStates) {
            es.setBottleneck(null);
        }
    }

    public Metering(Section section, ArrayList<SimMeter> meters, ArrayList<SimDetector> detectors) {
            this(section,meters,detectors,null, null);
    }
    /**
     * Constructor
     * @param section
     * @param meters
     * @param detectors 
     * @param sitv 
     * @param simObjects 
     */
    public Metering(Section section, ArrayList<SimMeter> meters, ArrayList<SimDetector> detectors,
            SimInterval sitv, SimObjects simObjects) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmm");
        String time = format.format(new Date());
        meteringStartAndStopLog = new Logger("metering_log (" + time + ")", "csv");
        rateLog = new Logger("rate_log (" + time + ")", "csv");
        speedLog = new Logger("speed_log (" + time + ")", "csv");
        densityLog = new Logger("density_log (" + time + ")", "csv");
        bottleneckLog = new Logger("bottleneck_log (" + time + ")", "csv");
//        waitTimeLog = new Logger("waittime_log (" + time + ")", "csv");
        rampDensityLog = new Logger("rampdensity_log (" + time + ")", "csv");
        SimulationInterval = sitv;
        this.detectors = detectors;
        sectionHelper = new MeteringSectionHelper(section, meters, detectors,sitv, simObjects);
        this.simObjects = simObjects;
        for (Station s : section.getStations()) {
            if (!isInMap(s)) {
                continue;
            }
            stations.add(simObjects.getStation(s.getID()));
        }
        bottleneckFinder = new BottleneckFinder(sectionHelper);
    }

    /**
     * Is station in VISSIM case file?
     * @param s station
     * @return true if given station in the case
     */
    private boolean isInMap(Station s) {
//        Detector[] dets = s.getDetectors(dc);
        Detector[] dets = s.getDetectorList();
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
     * Make log file head
     */
    private void makeLogHead() {

        if (!MeteringConfig.UseMetering) {
            return;
        }

        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        ArrayList<EntranceState> entranceStates = sectionHelper.getEntranceStates();

        StringBuilder info = new StringBuilder();
        info.append("Simulation Start Time = " + new Date().toString() + "\n");
        info.append("Kjam = " + MeteringConfig.Kjam + "\n");
        info.append("Kc = " + MeteringConfig.Kc + "\n");
        info.append("Kd = " + MeteringConfig.Kd + "\n");
        info.append("Kb = " + MeteringConfig.Kb + "\n");
        info.append("Kstop = " + MeteringConfig.Kstop + "\n");
        info.append("StopDuration = " + MeteringConfig.stopDuration + "\n");
        info.append("StopBSTrend= " + MeteringConfig.stopBSTrend + "\n");
        info.append("StopUpstreamCount= " + MeteringConfig.stopUpstreamCount + "\n");
        info.append("Ab = " + MeteringConfig.Ab + "\n");
        info.append("Max Wait Time1 = " + MeteringConfig.MAX_WAIT_TIME_MINUTE + "\n");
        info.append("Max Wait Time for Freeway-to-Freeway Ramp = " + MeteringConfig.MAX_WAIT_TIME_MINUTE_F2F + "\n");
        info.append("Max Red Time = " + MeteringConfig.MAX_RED_TIME + "\n");
        info.append("Random Seed = " + MeteringConfig.RANDOM_SEED + "\n");
        info.append("Case File = " + MeteringConfig.CASE_FILE + "\n");

        speedLog.println(info.toString());
        densityLog.println(info.toString());
        rateLog.println(info.toString());
        bottleneckLog.println(info.toString());
        meteringStartAndStopLog.println(info.toString());
//        waitTimeLog.println(info.toString());
                
        speedLog.print("idx");
        densityLog.print("idx");
        // logging : making head for speed and density
        for (StationState s : stationStates) {
            speedLog.print("," + s.id);
            densityLog.print("," + s.id);
        }
        speedLog.println("");
        densityLog.println("");

        // logging : making head for metering rates
        rateLog.print("idx");
        rampDensityLog.print("idx");
//        waitTimeLog.print("idx");
        for (int i = 0; i < entranceStates.size(); i++) {
            EntranceState e = entranceStates.get(i);
            if (e.meter == null) {
                continue;
            }
            rateLog.print("," + e.meter.getID());
//            waitTimeLog.print("," + e.meter.getId());
            rampDensityLog.print("," + e.meter.getID());
        }
        rateLog.println("");
        rampDensityLog.println("");
//        waitTimeLog.println("");
        System.out.println(info.toString());
    }

    /**
     * Make log data
     * @param dataCount 
     */
    private void makeLog(int dataCount) {
        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        
        if (!MeteringConfig.UseMetering) {
            return;
        }


        ArrayList<EntranceState> entranceStates = sectionHelper.getEntranceStates();

        // logging : station speed and density
        speedLog.print(dataCount);
        densityLog.print(dataCount);
        bottleneckLog.print(dataCount);

        for (int i = 0; i < stationStates.size(); i++) {
            StationState s = stationStates.get(i);
            speedLog.print("," + s.getIntervalAggregatedSpeed(SimulationGroup.Meter));
            densityLog.print("," + s.getIntervalAggregatedDensity(SimulationGroup.Meter));
            if (s.isBottleneck) {
                bottleneckLog.print("," + s.id);
            }
        }
        speedLog.println("");
        densityLog.println("");
        bottleneckLog.println("");
        //bottleneckLog.println(","+this.bottleneckKHistory.get(this.bottleneckKHistory.size()-1));

        // logging : metering rate
        rateLog.print(dataCount);
//        waitTimeLog.print(dataCount);
        rampDensityLog.print(dataCount);
        for (EntranceState e : entranceStates) {
            if (e.meter == null) {
                continue;
            }

            // display
            System.out.println(
                    "  !! " + e.meter.getID()
                    + ", flow=" + String.format("%.2f", e.getMergingVolume()*120)
                    + ", demand=" + String.format("%.2f", e.getDemandVolume()*120)
                    + ", rate=" + String.format("%.2f", e.meter.getReleaseRate()));

            rateLog.print("," + e.meter.getReleaseRate());
//            waitTimeLog.print("," + ( (e.maxWaitTimeIndex+1)/2));
        }

        rateLog.println("");
//        waitTimeLog.println("");
        rampDensityLog.println("");

    }

    /**
     * flush log file
     */
    public void writeLog() {
        if (!MeteringConfig.UseMetering) {
            return;
        }
        try {
            this.bottleneckLog.writeLog();
            this.rateLog.writeLog();
//            this.waitTimeLog.writeLog();
            this.speedLog.writeLog();
            this.densityLog.writeLog();
            this.rampDensityLog.writeLog();
            this.meteringStartAndStopLog.writeLog();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void DisplayStationState() {
            //for Station debuging
            ArrayList<StationState> stationStates = sectionHelper.getStationStates();
            for (int i = 0; i < stationStates.size(); i++) {
                System.out.println(stationStates.get(i).getID() 
                        + " : T_Q="+String.format("%.1f",stationStates.get(i).getIntervalFlow(SimulationGroup.Meter))
                        + " k=" +String.format("%.1f", stationStates.get(i).getIntervalAggregatedDensity(SimulationGroup.Meter))
                        + " u=" + String.format("%.1f", stationStates.get(i).getIntervalSpeed(SimulationGroup.Meter))
                        + " SI=" + String.format("%.1f", stationStates.get(i).getStateInterval(SimulationGroup.Meter))
                        + " Cr=" + stationStates.get(i).getSimulationInterval().getCurrentRunTime());
            }
    }

    public void printEntrance() {
            System.err.println("=====Metering State======================");
            for(int i=0;i<sectionHelper.getEntranceStates().size();i++){
                EntranceState es = sectionHelper.getEntranceStates().get(i);
                if(es.hasMeter())
                    System.err.println(es.meter.getID() + " : " + "Queue Demand="+es.getDemandVolume()+"("+es.getDemandVolume_ex()+")"
                            +", Passage="+es.getMergingVolume()+",Queue="+es.queueLength()+", Rate="+es.meter.getReleaseRate()
                            +"("+es.getRate()+"), occ="+es.getMaxOccupancy());
            }
    }

        public SimInterval getSimulationInterval() {
                return this.SimulationInterval;
        }



    class KPoint {

        double x;
        double y;

        public KPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    
    /**
     * @deprecated 
     * @param bs
     * @return 
     */
//    private String checkBSTrend(StationState bs) {
//        StringBuilder bsK = new StringBuilder();
//        bsK.append(",BSK(" + bs.id + ")");
//        boolean stopMetering = true;
//        for (int k = 0; k < MeteringConfig.stopBSTrend; k++) {
//            double cMovingAvgSpeed = bs.getMovingAverageSpeed(k, MeteringConfig.stopDuration);
//            double pMovingAvgSpeed = bs.getMovingAverageSpeed(k + 1, MeteringConfig.stopDuration);
//            bsK.append("," + cMovingAvgSpeed);
//            if (cMovingAvgSpeed >= pMovingAvgSpeed) {
//                stopMetering = false;
//            }
//        }
//        if (MeteringConfig.stopBSTrend > 0 && stopMetering) {
//            return bsK.toString();
//        } else {
//            return null;
//        }
//    }

//    /**
//     * @deprecated 
//     */
//    private void stopMetering_backup() {
//
//        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
//
//        int N = MeteringConfig.stopDuration;
//
//        // iterate from downstream to upstream
//        boolean hasBottleneck = false;
//        for (int i = stationStates.size() - 1; i >= 0; i--) {
//
//            StationState s = stationStates.get(i);
//            
//            // station is bottleneck
//            if (s.isBottleneck) {
//                hasBottleneck = true;
//                // set entrance's no-bottleneck count 0
//                for (EntranceState es : s.getAssociatedEntrances()) {
//                    es.resetNoBottleneckCount();
//                }
//                continue;
//            }
//            
//            // for all entrances
//            for (EntranceState es : s.getAssociatedEntrances()) 
//            {
//                // if meter isn't working, pass
//                if (!es.isMetering) {
//                    continue;
//                }
//                
//                // metering stop condition flag
//                boolean stopMetering = true;
//                
//                // if rate history is short, pass (do not stop)
//                if (es.countRateHistory() < N) {
//                    continue;
//                }
//
//                StringBuilder segK = new StringBuilder();
//                segK.append(",SK");
//                
//                // Stop Condition 1 : qj,t <= Rj,t for n times
//                //   - n : 10 (5min)
//                for (int k = 0; k < N; k++) {
//                    double q = es.getMergingVolume(k);
//                    double rate = es.getRate(k);
//                    if (q > rate) {
//                        stopMetering = false;
//                    }
//                    Double sk = es.getSegmentDensity(this.dataCount - k);
//                    segK.append("," + sk);
//                }
//
//                // if merging flow of ramp is high, pass
//                if (!stopMetering) continue;
//                
//
//
//                // Stop Condition 2 : 
//                //      if there's no bottleneck in downstream side of this meter                
//                if (!hasBottleneck) {
//                    es.addNoBottleneckCount();
//
//                    // if there's no bottleneck for a long time (N times)
//                    if (es.getNoBottleneckCount() >= N) {
//                        
//                        // find upstream station that not associated this entrance
//                        List<StationState> upstreamStations = new ArrayList<StationState>();
//                        if(MeteringConfig.stopUpstreamCount > 0) {
//                            for (int k = s.stationIdx - 1; k >= 0; k--) {
//                                StationState us = stationStates.get(k);
//                                boolean associatedStation = false;
//                                for (EntranceState tes : us.getAssociatedEntrances()) {
//                                    if (tes.equals(es)) {
//                                        associatedStation = true;
//                                    }
//                                }
//                                if (!associatedStation) {
//                                    upstreamStations.add(us);
//                                    if (upstreamStations.size() >= MeteringConfig.stopUpstreamCount) {
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//
//                        boolean hasUpstreamBS = false;
//                        for (StationState us : upstreamStations) {
//                            if (us.noBsCount < N) {
//                                hasUpstreamBS = true;
//                            }
//                        }
//
//                        if (!hasUpstreamBS) {
//                            System.out.println(this.dataCount + "(1) : " + es.meter.getId() + " is stoped!!");
//                            meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getId() + ",stop,1" + segK.toString());
//                            es.stopMetering();
//                            continue;
//                        }
//                    }
//                    
//                    // end of checking about has-not-bottleneck case
//                    continue;
//                    
//                }
//                
//                // if has-bottleneck
//                es.resetNoBottleneckCount();
//
//                // Stop Condition 3 : segment density is low for n times
//                for (int k = 0; k < N; k++) {
//                    Double sk = es.getSegmentDensity(this.dataCount - k);
//                    if (sk > MeteringConfig.Kstop) {
//                        stopMetering = false;
//                        break;
//                    }
//                }
//
//                // Stop Condition 4 : bottleneck's density trend
//                if (stopMetering) {
//
//                    // check bottleneck's density trend for 'stopBSTrend' previous time steps
//                    String bsTrend = null;
//                    if(MeteringConfig.stopBSTrend > 0) bsTrend = checkBSTrend(es.getBottleneck());
//                    
//                    if (bsTrend != null) {
//                        System.out.println(this.dataCount + "(2) : " + es.meter.getId() + " is stoped!!");
//                        meteringStartAndStopLog.println(this.dataCount + "," + es.meter.getId() + ",stop,2" + segK.toString() + bsTrend);
//                        es.stopMetering();
//                    }
//                }
//
//            } // loop : entrances
//
//        } // loop : stations
//
//
//    }
    
    
    
    private boolean overKc = false;
    private int overKcCount = 0;
    
    /**
     * @deprecated 
     */
    private void adjustKb() {
        List<StationState> stationStates = sectionHelper.getStationStates();
        
        // sum of K of bottlenecks
        double sumKofBS = 0.0;
        
        // bottleneck count
        int bc = 0;
        
        // calculate sum of density of bottlenecks
        for (int i = stationStates.size() - 1; i >= 0; i--) {
            StationState station = stationStates.get(i);
            if (station.isBottleneck) {
                sumKofBS += station.getIntervalAggregatedDensity(SimulationGroup.Meter);
                bc++;
            }
        }
        
        // average K of bottlenecks
        double avgKofBS = 0;
        
        // update average density of bottlenecks
        if(bc == 0) this.bottleneckKHistory.add(0D);
        else {
            avgKofBS = sumKofBS/bc;
            this.bottleneckKHistory.add(avgKofBS);
        }
        
        // count over-Kc
        if(avgKofBS > MeteringConfig.Kc) {
            this.overKcCount++;
        } else this.overKcCount = 0;
        
        // if over-Kc-count is greater than 10 (5min), over Kc = true
        if(this.overKcCount > 10) {
            this.overKc = true;
        }
        
        // if avg K of bottlenecks is greater than Kd, pass
        if(avgKofBS > MeteringConfig.Kd) return;

        // adjusting runs after avg K of bottlenecks is over Kc
        if(!overKc) return;
        
        // if already applied, pass
        if(MeteringConfig.Kb == MeteringConfig.Kd) return;
        
        // check history size
        int size = this.bottleneckKHistory.size();
        if(size < MeteringConfig.stopDuration+3) return;
        
        // check avg K of bottlenecks trend
        for(int i=0;i<3; i++)
        {
            double ma = movingAverage(this.bottleneckKHistory, i, MeteringConfig.stopDuration);
            double ma_prev = movingAverage(this.bottleneckKHistory, i+1, MeteringConfig.stopDuration);
            if(ma > ma_prev) return;
        }
        
        System.out.println("[Bottleneck Threshold Update] " + MeteringConfig.Kb + " => " + MeteringConfig.Kd);

        MeteringConfig.Kb = MeteringConfig.Kd;
        
    }
    
}
