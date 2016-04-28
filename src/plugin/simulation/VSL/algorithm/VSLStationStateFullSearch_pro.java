///*
// * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
// * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package plugin.simulation.VSL.algorithm;
//
//import infra.simulation.StationState;
//import infra.simulation.vsl.algorithm.VSLConfig;
//import java.util.Map;
//import java.util.NavigableMap;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLStationStateFullSearch_pro extends VSLStationState{
//    int cnt = 1;
//    
//    public VSLStationStateFullSearch_pro(StationState s) {
//        super(s.getStation(),s.getSection(),s.getSimObject(),s.getSimulationInterval());
//        setUpstreamStationState(s.getUpstreamStationState());
//        setDownStreamStationState(s.getDownStreamStationState());
//    }
//    
////    public int VSLCONTROLTHRESHOLD = 0;
////    public double VSLCONTROLDISTANCE = 0;
//
//    /** Acceleration Sample Count*/
//    protected int s_count = 4;
//    /** Acceleration History
//     * Add Count of Acceleration Trending Upward */
//    protected double[] AccelerationHistory = new double[s_count];
//
//    /** Update Acceleration array with a new sample */
//    protected void updateAcceleration(double s) {
//            System.arraycopy(AccelerationHistory, 0, AccelerationHistory, 1,
//                    AccelerationHistory.length - 1);
//            // Clamp the speed to 10 mph above the speed limit
//            AccelerationHistory[0] = s;
//    }
//    
//    protected boolean isAccHistoryValid(){
//        return AccelerationHistory.length == s_count;
//    }
//    
//    protected double[] RollingSpeedHistory = new double[s_count];
//    protected void updateRollingSpeed(double u){
//        System.arraycopy(RollingSpeedHistory, 0, RollingSpeedHistory, 1,
//                    RollingSpeedHistory.length - 1);
//            // Clamp the speed to 10 mph above the speed limit
//            RollingSpeedHistory[0] = u;
//    }
//    protected boolean isRollingSpeedValid(){
//        return RollingSpeedHistory.length == s_count;
//    }
//        
//    @Override
//    public void calculateBottleneck(double m,
//        NavigableMap<Double, VSLStationState> upstream)
//    {
////        System.out.println("Calculate VSL - NEW");
//        Double mp = upstream.lowerKey(m);
//        while(mp != null && isTooClose(m - mp)){
//            mp = upstream.lowerKey(mp);
//        }
//        
//        if(mp != null){
//            VSLStationState sp = upstream.get(mp);
//            Double d = m - mp;
//            acceleration = calculateAcceleration(simGroup,sp, d);
//            updateAcceleration(acceleration); //added
//            updateRollingSpeed(getAggregateRollingSpeed(simGroup));
//
////            System.out.print(this.getStation()+"-");
//            checkThresholds();
////            System.out.print("-"+this.n_bottleneck+"-");
//            if(isAccidentSpeed()){
////                System.out.println("Yeah-1");
//                setBottleneck(true);
//            }else if(isAboveBottleneckSpeed()){
////                System.out.println("Yeah-2");
//                setBottleneck(false);
//            }else if(isBeforeStartCount()){
////                System.out.print("-"+this.n_bottleneck+"-");
////                System.out.println("Yeah-3");
//                setBottleneck(false);
//            }else{
////                System.out.println("4 Time!");
//                setBottleneck(true);
//            }
//            adjustStream(m,upstream);
//        }else{
//            clearBottleneck();
//        }
//    }
//    
//    @Override
//    protected void checkThresholds() {
//        if(isBeforeStartCount()){ //Start Threshold
//            AccCheckThreshold ac = VSLConfig.accCheckThreshold;
//            if(ac.isBASEON()){
////                System.out.print("-BASEON-");
//                checkBasedThresholds();
//            }
//            else if(ac.isTRENDON())
//                checkTrendThresholds();
//            else if(ac.isNewTRENDON()){
//                    if(stateInterval.getIntervalByGID(simGroup) == 30)
//                        checkTrendThresholds();
//                    else
//                        checkNewTrendThresholds();
//            }
//            else 
//                checkBasedThresholds();
//        }else
//            checkStopThreshold();
//    }
//    
//    protected void checkStopThreshold(){
//        if(isAccelerationValid() && acceleration < getStopThreshold()){
//            n_bottleneck++;
//        }else{
//            n_bottleneck = 0;
//        }
//    }
//    
//    protected void checkBasedThresholds(){
//        if(isAccelerationValid() && acceleration < getStartThreshold()){
////            System.out.print(n_bottleneck);
//            n_bottleneck++;
////            System.out.print("=N++-"+n_bottleneck);
//        }else{
//            n_bottleneck = 0;
//        }
//    }
//    
//    protected void checkTrendThresholds(){
//        if(isAccTrend()){
//            n_bottleneck = 3;
//        }else{
//            n_bottleneck = 0;
//        }
//    }
//    
//    /**
//     * Check Acceleration Upward Trending
//     * eq) if a,t-2 > a,t-1 > a,t and a,t < -1500 then, set Bottleneck
//     * @return 
//     */
//    protected boolean isAccTrend(){
//        if(!isAccHistoryValid())
//            return false;
//        
//        Double bacc = Double.MAX_VALUE;
//        int accsize = AccelerationHistory.length-1;
//        int minsize = accsize - 3;
//        for(int i=0; i<=accsize-minsize;i++){
//            Double acc = AccelerationHistory[accsize-i];
//            if(acc == null || acc >= 0 ||  acc >= bacc)
//                return false;
//            else
//                bacc = acc;
//        } 
//        
//        return acceleration < getStartThreshold();
//    }
//    
//    protected void checkNewTrendThresholds(){
//            if(isAccNewTrend()){
//                    n_bottleneck = 3;
//            }else{
//                    n_bottleneck = 0;
//            }
//    }
//    
//    protected boolean isAccNewTrend(){
//            if(!isAccHistoryValid()){
//                    return false;
//            }
//            System.out.print("acctest : "+this.getID()+", ");
//            int accsize = AccelerationHistory.length-1;
//            int cnt = 0;
//            for(int i=0;i<=accsize;i++){
//                    Double acc = AccelerationHistory[i];
//                    System.out.print(" -"+acc);
//                    if(acc < getStartThreshold())
//                            cnt ++;
//            }
//            System.out.println(" (cnt = "+cnt+")");
//            if(cnt > 2)
//                    return true;
//            else
//                    return false;
//    }
//    
//    private void adjustStream(double m, NavigableMap<Double, VSLStationState> upstream) {
//        Double mu = findVSS(m,upstream);
//        if(mu == null){
//            return;
//        }
//        
//        boolean isUpdate = false;
//        Map.Entry<Double, VSLStationState> entry = upstream.higherEntry(mu);
//        VSLStationState s = entry.getValue();
////        System.out.println("adJustVSS");
//        while(entry != null){
//            VSLStationState sp = entry.getValue();
//            Double ap = sp.acceleration;
////            System.out.print(sp.getID() + " : ");
//            if(ap != null && ap >= 0)
//                isUpdate = true;
////            System.out.print(", ap="+ap+", u="+sp.getAggregateRollingSpeed()+", bot="+!isAboveBottleneckSpeed(sp.getAggregateRollingSpeed())+", end="+isUpdate);
//            if(ap != null && !isBottleneckSpeed(sp.getAggregateRollingSpeed(simGroup))
//                    && ap < this.getVSLMovingAcc() && !isUpdate){
////                System.out.print(" Change Bottleneck! ");
//                s.bottleneck = false;
//                s.n_bottleneck = 0;
//                s = sp;
//                s.bottleneck = true;
//                
//                while(s.isBeforeStartCount()){
//                    s.n_bottleneck++;
//                }
//            }else{
//                sp.bottleneck = false;
//                sp.n_bottleneck = 0;
//            }
////            System.out.println(sp.bottleneck);
//            entry = upstream.higherEntry(entry.getKey());
//        }
//        
//    }
//    
//    /**
//     * Find VSS between Current and Upstream Station
//     * Stop finding, if acc > 0
//     * without Speed < VSLRangeThreshold(35mph)
//     * @param m
//     * @param upstream
//     * @return MilePoint of Most Upstream Station with acc > 0, if there is VSS in Stream 
//     */
//    private Double findVSS(double m, NavigableMap<Double, VSLStationState> upstream) {
//        
//        Double mp = m;
//        VSLStationState sp = null;
//        boolean isVSS = false;
////        System.out.println("Find VSS");
//        while(mp != null){
//            sp = upstream.get(mp);
//            Double ap = sp.acceleration;
////            System.out.println(sp.getID());
//            if(ap != null && ap >= 0 && sp.getAggregateRollingSpeed(simGroup) > getVSLRangeThreshold()
//                    && !(mp == m && sp.p_bottleneck))
//                break;
//            else{
//                if(sp.bottleneck || sp.p_bottleneck)
//                    isVSS = true;
//                mp = upstream.lowerKey(mp);
//            }
//        }
////        System.out.println("isVSS : "+isVSS + ", MP : "+mp);
//        mp = mp != null ? mp : upstream.firstKey();
//        if(isVSS){
//            return mp;
//        }
//        else
//            return null;
//    }
//    
//    @Override
//    public void calculateControlThreshold(double m, NavigableMap<Double, VSLStationState> upstream){
//            calculateControlThreshold__TTMODE_NOLIMIT(m,upstream);   
//    }
//    
//    private void calculateControlThreshold__TTMODE_NOLIMIT(double m, NavigableMap<Double, VSLStationState> upstream) {
////        System.out.println("CALC NOLIMIT");
//        double cm = m;
//        double mp = m; // milepoint : most end station
//        double uMax = getAggregateRollingSpeed(simGroup); //Max u
//        double sum = uMax; //speed sum between Station of Max u and Last Station
//        int scnt = 1;
//        Map.Entry<Double, VSLStationState> entry = upstream.lowerEntry(mp);
//        while(entry != null){ //calculate Travel Time Boundary
//            mp = entry.getKey();
//            if(isCalculateDistance(cm-mp,VSLConfig.VSL_ZONE_LENGTH_MILE)){
//                VSLStationState s = entry.getValue();
//                double u = s.getAggregateRollingSpeed(simGroup);
//            
//                if(u >= uMax){
//                    uMax = u;
//                    sum = uMax;
//                    scnt = 1;
//                }else if(uMax != 0){
//                    if(scnt == 1){
//                        sum += u;
//                        scnt ++;
//                    }
//                }else{
//                    mp = upstream.higherKey(mp);
//                    break;
//                }
////                
//                if(s.bottleneck)
//                        break;
//                
//            }else{
//                break;
//            }
//            
//            entry = upstream.lowerEntry(entry.getKey());
//        }
//        double u1 = sum / scnt;
//        double ugap = u1 - getAggregateRollingSpeed(simGroup);
//        double tt = calculateTravelTime(mp,upstream);
//        double distance = m - mp;
//        if(ugap >= 0 && distance > 0){
//            VSLCONTROLTHRESHOLD = -(int)(ugap / tt);
//            VSLCONTROLTHRESHOLD = Math.max(VSLCONTROLTHRESHOLD, -1 * VSLConfig.VSL_CONTROL_THRESHOLD);
//            VSLCONTROLDISTANCE = distance;
//        }
//        else{
//            VSLCONTROLTHRESHOLD = 0;
//            VSLCONTROLDISTANCE = 0;
//        }
//    }
//    
//    private void calculateControlThreshold__TTMODE_NOLIMIT_backup(double m, NavigableMap<Double, VSLStationState> upstream) {
////        System.out.println("Calculate THREsHOLD - NEW");
//        double cm = m;
//        double cu = getAggregateRollingSpeed(simGroup); //
//        boolean cbottleneck = this.bottleneck;
//        double mp = m; // milepoint : most end station
//        double uMax = getAggregateRollingSpeed(simGroup); //Max u
//        double sum = uMax; //speed sum between Station of Max u and Last Station
//        int scnt = 1;
//        
//        Map.Entry<Double, VSLStationState> entry = upstream.lowerEntry(mp);
//        while(entry != null){ //calculate Travel Time Boundary
//            mp = entry.getKey();
////            System.out.print("m - mp : "+(cm-mp)+" - ");
//            if(isCalculateDistance(cm-mp,2.0d)){
//                VSLStationState s = entry.getValue();
////                System.out.print("Station "+s.getID()+"..proc");
//                double u = s.getAggregateRollingSpeed(simGroup);
//                
//                if(u >= uMax){
//                    uMax = u;
//                    sum = uMax;
//                    scnt = 1;
//                }
//                else if(uMax != 0){// && (uMax - u) <= 10){
//                    if(scnt == 1){
//                        sum += u;
//                        scnt ++;
//                    }
//                }else{
//                    mp = upstream.higherKey(mp);
//                    break;
//                }
////                System.out.println("cu = "+cu+", su = "+s.getAggregateRollingSpeed()+", su-cu="+(u-cu)+"-"+isContinueSpeed(u-cu));
////                if(s.bottleneck && cbottleneck && u > cu && isContinueSpeed(u-cu)){
////                    uMax = u;
////                    sum = uMax;
////                    scnt = 1;
////                    cm = mp;
////                    cu = u;
////                    cbottleneck = s.bottleneck;
////                    s.VSLCONTROLDISTANCE = 0;
////                }
//                if(s.bottleneck){
//                    break;
//                }
//                
//            }else{
////                System.out.println();
//                break;
//            }
//            
//            entry = upstream.lowerEntry(entry.getKey());
//        }
//        
//        double u1 = sum / scnt;
//        double ugap = u1 - getAggregateRollingSpeed(simGroup);
//        double tt = calculateTravelTime(mp,upstream);
//        
////        System.out.println("End Station : "+upstream.get(mp).getID());
////        System.out.println("Sum / cnt = x : "+sum+" / "+scnt+" = "+(sum/scnt));
//        
//        double distance = m - mp;
////        System.out.print("TTLMODE : "+this.getID());
//        if(ugap >= 0 && distance > 0){
//            VSLCONTROLTHRESHOLD = -(int)(ugap / tt);
//            VSLCONTROLTHRESHOLD = Math.max(VSLCONTROLTHRESHOLD, -1 * VSLConfig.VSL_CONTROL_THRESHOLD);
//            VSLCONTROLDISTANCE = distance;
////            System.out.println(" - "+VSLCONTROLTHRESHOLD+", dis="+VSLCONTROLDISTANCE);
//        }
//        else{
//            VSLCONTROLTHRESHOLD = 0;
//            VSLCONTROLDISTANCE = 0;
////            System.out.println(" - "+VSLCONTROLTHRESHOLD);
//        }
//    }
//    
//    /**
//     * Calculate A with Travel Time  
//     * a = V1 - Vvss / TravelTime
//     * @param m
//     * @param upstream 
//     */
//    private void calculateControlThreshold_TTMODE(double m, NavigableMap<Double, VSLStationState> upstream) {
//        double cu = getAggregateRollingSpeed(simGroup); //Current Speed, if it's VSS
//        double mp = m; // milepoint : most end station
//        double uMax = getAggregateRollingSpeed(simGroup); //Max u
//        double sum = uMax; //speed sum between Station of Max u and Last Station
//        int scnt = 1;
//        
//        Map.Entry<Double, VSLStationState> entry = upstream.lowerEntry(mp);
//        while(entry != null){ //calculate Travel Time Boundary
//            mp = entry.getKey();
////            System.out.print("m - mp : "+(m-mp)+" - ");
//            if(isCalculateDistance(m-mp,2.0d)){
//                VSLStationState s = entry.getValue();
////                System.out.print("Station "+s.getID()+"..proc");
//                double u = s.getAggregateRollingSpeed(simGroup);
//                if(u >= uMax){
//                    uMax = u;
//                    sum = uMax;
//                    scnt = 1;
//                }
//                else if(uMax != 0){// && (uMax - u) <= 10){
//                    sum += u;
//                    scnt ++;
//                }else{
//                    mp = upstream.higherKey(mp);
//                    break;
//                }
//                
////                System.out.println("cu = "+cu+", su = "+s.getAggregateRollingSpeed()+", ca = "+this.acceleration+", sa="+s.acceleration);
//                if((s.bottleneck && s.getAggregateRollingSpeed(simGroup) < cu) ||
//                        (s.bottleneck && (s.getAggregateRollingSpeed(simGroup) >= cu && s.acceleration < this.acceleration)))
//                    break;
//            }else{
////                System.out.println();
//                break;
//            }
//            
//            entry = upstream.lowerEntry(entry.getKey());
//        }
//        
//        double u1 = sum / scnt;
//        double ugap = u1 - getAggregateRollingSpeed(simGroup);
//        double tt = calculateTravelTime(mp,upstream);
//        
////        System.out.println("End Station : "+upstream.get(mp).getID());
////        System.out.println("Sum / cnt = x : "+sum+" / "+scnt+" = "+(sum/scnt));
//        
//        double distance = m - mp;
////        System.out.print("TTMODE : "+this.getID());
//        if(ugap >= 0 && distance > 0){
//            VSLCONTROLTHRESHOLD = -(int)(ugap / tt);
//            VSLCONTROLTHRESHOLD = Math.max(VSLCONTROLTHRESHOLD, -1 * VSLConfig.VSL_CONTROL_THRESHOLD);
//            VSLCONTROLDISTANCE = distance;
////            System.out.println(" - "+VSLCONTROLTHRESHOLD);
//        }
//        else{
//            VSLCONTROLTHRESHOLD = 0;
//            VSLCONTROLDISTANCE = 0;
////            System.out.println(" - "+VSLCONTROLTHRESHOLD);
//        }
//    }
//    
//    private double calculateTravelTime(double em, NavigableMap<Double, VSLStationState> upstream){
//        Map.Entry<Double, VSLStationState> entry = upstream.lastEntry();
//        double tt = 0;
//        while(entry != null && (upstream.lowerEntry(entry.getKey()) != null)){
//            double cm = entry.getKey();
//            double um = upstream.lowerEntry(entry.getKey()).getKey();
//            double cu = upstream.get(cm).getAggregateRollingSpeed(simGroup);
//            double uu = upstream.get(um).getAggregateRollingSpeed(simGroup);
//            
//            double distance = (cm - um) / 3;
//            //calculate Travel Time
//            tt += (distance / cu) + (distance / ((cu+uu) / 2)) + (distance / uu);
////            System.out.println(entry.getValue().getID()+" - "+tt);
//            if(um == em)
//                break;
//            
//            entry = upstream.lowerEntry(entry.getKey());
//        }
//        
//        return tt;
//    }
//    
//    private boolean isCalculateDistance(double d){
//        return isCalculateDistance(d,1.5d);
//    }
//    
//    private boolean isCalculateDistance(double d, double limit){
//        Double distance = limit;
//        if(d >= 0 && d <= distance)
//            return true;
//        else return false;
//    }
//    
//    /** Get the upstream bottleneck distance */
//    @Override
//    protected double getUpstreamDistance() {
//        return VSLCONTROLDISTANCE;
//    }
//    
//    /** Get the control deceleration threshold */
//    @Override
//    protected int getControlThreshold() {
//            return VSLCONTROLTHRESHOLD;
//    }
//    
//    private int getVSLMovingAcc() {
//        return -1 * VSLConfig.VSL_MOVING_ACCEL;
//    }
//
//    private double getVSLRangeThreshold() {
//        return VSLConfig.VSL_RANGE_THRESHOLD;
//    }
//    
//    @Override
//    protected boolean isAboveBottleneckSpeed(){
//        BottleneckSpeed bspd = VSLConfig.bottleneckSpeedType;
//        
//        if(bspd.isBASEON())
//            return isBasedBottleneckSpeed();
//        else if(bspd.isTRENDON())
//            return isTrendBottleneckSpeed();
//        else
//            return isBasedBottleneckSpeed();
//    }
//    
//    protected boolean isBasedBottleneckSpeed(){
//        return this.getAggregateRollingSpeed(simGroup) > VSLConfig.VSL_BS_THRESHOLD;
//    }
//    
//    protected boolean isTrendBottleneckSpeed(){
//        double u = VSLConfig.VSL_BS_THRESHOLD;
//        if(isRollingSpeedValid() && 
//                RollingSpeedHistory[0] <= u && RollingSpeedHistory[1] <= u && RollingSpeedHistory[2] <= u)
//            return false;
//        else
//            return true;
//    }
//    
//    private boolean isAccidentSpeed() {
//        if(!VSLConfig.isAccidentSpeed)
//            return false;
//        
//        if(getAggregateRollingSpeed(simGroup) <= VSLConfig.AccidentSpeed)
//            return true;
//        else
//            return false;
//    }
//    
//    protected boolean isBottleneckSpeed(double speed){
//        return speed > VSLConfig.VSL_BS_THRESHOLD;
//    }
//    
//    
//    
//    
//}