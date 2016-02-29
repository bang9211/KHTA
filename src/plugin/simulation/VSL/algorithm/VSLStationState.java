/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
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
package plugin.simulation.VSL.algorithm;

import infra.Section;
import infra.infraobject.Station;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import infra.simulation.SimulationGroup;
import infra.simulation.StationState;
import infra.simulation.vsl.algorithm.VSLConfig;
import java.util.NavigableMap;
import util.DistanceUtil;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLStationState extends StationState{
    protected Double acceleration = null; //Acceleration
    
    /** Count of iterations where station was a bottleneck */
    public int n_bottleneck = 0;
    
    /** Bottleneck exists flag */
    public boolean bottleneck = false;
    
    /** Bottleneck flag from previous time step.  This is needed when
     * checking if a bottleneck should be adjusted downstream. */
    public boolean p_bottleneck = false;
    protected VSLStationState upstreamVSLState;
    protected VSLStationState downstreamVSLState;
    
    public int VSLCONTROLTHRESHOLD = 0; //VSL Control Threshold
    public double VSLCONTROLDISTANCE = 0; // VSL Control Distance
    
    protected SimulationGroup simGroup = SimulationGroup.VSL;
    
    public VSLStationState(Station _station, Section _section, SimObjects simObjects){
        super(_station, _section, simObjects, null);
    }
    
    public VSLStationState(Station _station, Section _section, SimObjects simObjects, SimInterval sitv){
            super(_station,_section,simObjects,sitv);
    }

    public VSLStationState(StationState s) {
        this(s.getStation(),s.getSection(),s.getSimObject());
        this.setUpstreamStationState(s.getUpstreamStationState());
        this.setDownStreamStationState(s.getDownStreamStationState());
    }

    public void calculateBottleneck(double m, NavigableMap<Double, VSLStationState> upstream){
        
    }
    
    public void calculateControlThreshold(double m, NavigableMap<Double, VSLStationState> upstream){
        
    }
    
    /**
     * Calculate whether the station is a bottleneck
     */
    public void calculateBottleneck(){
        VSLStationState upstream = this.getUpstreamVSLStationState();
        int Pdistance = 0;
        if(upstream == null)
            return;
        Pdistance = upstream.getdistanceToDownstreamStationState();
        
        while(upstream != null && isTooClose(Pdistance)){
            upstream = upstream.getUpstreamVSLStationState();
            Pdistance += upstream.getdistanceToDownstreamStationState();
        }
        if(upstream != null){
            acceleration = calculateAcceleration(simGroup,upstream, Pdistance);
            
            //check the bottleneck thresholds
            checkThresholds();
            
            if(isAboveBottleneckSpeed()){ //if station speed is above the bottleneck id speed ex) cspeed > 55mph
                setBottleneck(false);
            }else if(isBeforeStartCount()){ //if the number of intervals is lower than start count ex) count < 3
                setBottleneck(false);
                adjustDownstream();
            }else{
                setBottleneck(true);
                adjustUpstream();
            }
        }else{
            clearBottleneck();
        }
    }
    
    protected boolean isTooClose(double d){
        return d < VSLConfig.VSL_MIN_STATION_MILE;
    }
    protected boolean isTooClose(int distance) {
        return DistanceUtil.getFeetToMile(distance) < VSLConfig.VSL_MIN_STATION_MILE;
    }

    /** Check the bottleneck thresholds */
    protected void checkThresholds() {
        if(isAccelerationValid() && acceleration < getThreshold()){
            n_bottleneck++;
        }else{
            n_bottleneck = 0;
        }
    }
    
    /** Check if acceleration is valid */
    protected boolean isAccelerationValid() {
        return acceleration != null;
    }
    
    /** Get the current deceleration threshold */
    protected int getThreshold() {
        if(isBeforeStartCount()){
            return getStartThreshold();
        }else{
            return getStopThreshold();
        }
    }

    /** Test if the number of intervals is lower than start count */
    protected boolean isBeforeStartCount() {
        return n_bottleneck < VSLConfig.VSA_START_INTERVALS;
    }

    /** Get the starting deceleration threshold */
    protected int getStartThreshold() {
        return -1 * VSLConfig.VSL_VSS_DECISION_ACCEL;
    }

    /** Get the stopping deceleration threshold */
    protected int getStopThreshold() {
        return -1 * VSLConfig.VSL_TURNOFF_ACCEL;
    }

    /** Test if station speed is above the bottleneck id speed */
    protected boolean isAboveBottleneckSpeed() {
//        System.out.println("AGGSpd : "+this.getAggregateRollingSpeed()+"VSL_BS_THRESHOLD : "+VSLConfig.VSL_BS_THRESHOLD);
        return this.getAggregateRollingSpeed(simGroup) > VSLConfig.VSL_BS_THRESHOLD;
    }

    /** Set the bottleneck flag */
    protected void setBottleneck(boolean b) {
        p_bottleneck = bottleneck;
        bottleneck = b;
    }

    /**
     * Adjust the bottleneck downstream if necessary.
     * @param sp Immediately upstream station */
    protected void adjustDownstream(){
        if(this.getDownstreamVSLStationState() == null){
            return;
        }
        
        VSLStationState upstream = this.getUpstreamVSLStationState();
        
        Double ap = upstream.acceleration;
        Double a = acceleration;
        if(a != null && ap != null && a < ap && upstream.p_bottleneck){
            // Move bottleneck downstream
            // Don't use setBottleneck, because we don't want
            // p_bottle to be updated
            upstream.bottleneck = false;
            bottleneck = true;
            // Bump the bottleneck count so it won't just
            // shut off at the next time step
            while(isBeforeStartCount())
                    n_bottleneck++;
        }
    }
    
    /** Adjust the bottleneck upstream if necessary */
    protected void adjustUpstream(){
        VSLStationState upstream = this.getUpstreamVSLStationState();
        VSLStationState current = this;
        while(upstream != null){
            Double ap = upstream.acceleration;
            Double a = current.acceleration;
            
            if(a != null && ap != null && a > ap){
                // Move bottleneck upstream
                // Don't use setBottleneck, because we don't
                // want p_bottle to be updated
                current.bottleneck = false;
                current = upstream;
                current.bottleneck = true;
            }else{
                break;
            }
            upstream = upstream.getUpstreamVSLStationState();
        }
        
        // Bump the bottleneck count so it won't just
        // shut off at the next time step
        while(current.isBeforeStartCount()){
            current.n_bottleneck++;
        }
    }
    
    /**
     * Clear the station as a bottleneck
     */
    public void clearBottleneck() {
        n_bottleneck = 0;
        setBottleneck(false);
        acceleration = null;
    }

    /**
     * set Upstream VSL Station State
     * @param upstreamVSL 
     */
    public void setUpstreamVSLStationState(VSLStationState upstreamVSL) {
        upstreamVSLState = upstreamVSL;
    }
    /**
     * set Downstream VSL Station State
     * @param downstreamVSL 
     */
    public void setDownStreamVSLStation(VSLStationState downstreamVSL) {
        downstreamVSLState = downstreamVSL;
    }
    
    /**
     * get Upstream VSL Station State
     * @return 
     */
    public VSLStationState getUpstreamVSLStationState(){
        return upstreamVSLState;
    }
    
    /**
     * get Downstream VSL Station State
     * @return 
     */
    public VSLStationState getDownstreamVSLStationState(){
        return downstreamVSLState;
    }

    /** Check if the station is a bottleneck for the given distance */
    public boolean isBottleneckFor(double d) {
//        System.out.print("----- D-"+d+", isbot:"+bottleneck+", ");
        return this.bottleneck && isBottleneckInRange(d);
    }

    /** Check if the (bottleneck) station is in range */
    protected boolean isBottleneckInRange(double d) {
        if(d > 0){
//            System.out.print("D>0(upstream) : "+getUpstreamDistance());
            return d < getUpstreamDistance();
        }else{
//            System.out.print("D>0(upstream) : "+getDownstreamDistance());
            return -d < getDownstreamDistance();
        }
    }

    /** Get the downstream bottleneck distance */
    protected double getDownstreamDistance() {
        return VSLConfig.VSL_MIN_STATION_MILE;
    }

    /** Get the upstream bottleneck distance */
    protected double getUpstreamDistance() {
        double lim = this.getStation().getSpeedLimit();
        double sp = this.getAggregateRollingSpeed(simGroup);
        if(sp > 0 && sp < lim){
            int acc = -getControlThreshold();
//            System.out.println(acc);
            return (lim * lim - sp * sp) / (2 * acc);
        }else{
            return 0;
        }
    }
    
    /** Get the control deceleration threshold */
    protected int getControlThreshold() {
            return -1 * VSLConfig.VSL_CONTROL_THRESHOLD;
    }
    
    public int getSpeedLimit(){
        return this.getStation().getSpeedLimit();
    }
    
    public Integer getFeetPoint(){
        return this.getStation().getStationFeetPoint(this.getSection().getName());
    }

    public Double getMilePoint() {
        return DistanceUtil.getFeetToMile(getFeetPoint());
    }
    
    public Double getAcceleration(){
        return acceleration;
    }
    
    public int getVSSControlThreshold(){
        return VSLCONTROLTHRESHOLD;
    }
}
