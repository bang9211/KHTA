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

import infra.simulation.SimulationGroup;
import infra.simulation.vsl.algorithm.VSLConfig;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSStationFinder {
    
    static private int getControlThreshold(){
        return -1 * VSLConfig.VSL_CONTROL_THRESHOLD;
    }
    /**
     * Mile point to search for VSS
     */
    protected final Double ma;
    
    /**
     * upstream station
     */
    protected VSLStationState su;
    
    /**
     * upstream mile point
     */
    protected Double mu;
    
    /**
     * downstream station
     */
    protected VSLStationState sd;
    
    /**
     * downstream mile point
     */
    protected Double md;
    
    /**
     * Found VSS
     */
    protected VSLStationState vss;
    
    /**
     * Mile Point at found vss
     */
    protected Double vss_mp;
    
    protected SimulationGroup simGroup = SimulationGroup.VSL;
    
    /**
     * for NEAR Station
     */
    public VSStationFinder(Double m){
        ma = m;
    }
    
    public boolean check(Double m, VSLStationState s){
        if(m < ma){
            su = s;
            mu = m;
        }else if(md == null || md > m){
            sd = s;
            md = m;
        }
        
        if((vss_mp == null || vss_mp > m) && s.isBottleneckFor(m - ma)){
            vss = s;
            vss_mp = m;
        }
        return false;
    }
    
//    public boolean isDownStreamVSS(){
//        if(vss != null && sd != null){
//            if(su != null && vss.getID().equals(su.getID())){
//                return false;
//            }else
//                return true;
//        }else
//            return false;
//    }
    
    public VSLStationState getNearStation(){
        if(su != null && sd != null){
            System.out.print("mu("+su.getID()+"):"+mu+", md("+sd.getID()+"):"+md+", ");
            System.out.print("ma-mu:"+Math.abs(ma-mu)+", ma-md:"+Math.abs(ma-md));
            if(Math.abs(ma-mu) <= Math.abs(ma-md)){
                System.out.print(", S"+su.getID()+"-"+su.getAggregateRollingSpeed(simGroup)+" : "+getSpeedLimit());
                return su;
            }
            else{
                System.out.print(", S"+sd.getID()+"-"+sd.getAggregateRollingSpeed(simGroup)+" : "+getSpeedLimit());
                return sd;
            }
        }else if(su != null){
            System.out.print("mu("+su.getID()+"):"+mu);
            System.out.print(", S"+su.getID()+"-"+su.getAggregateRollingSpeed(simGroup)+" : "+getSpeedLimit());
            return su;
        }else if(sd != null){
            System.out.print("mu("+sd.getID()+"):"+md);
            System.out.print(", S"+sd.getID()+"-"+sd.getAggregateRollingSpeed(simGroup)+" : "+getSpeedLimit());
            return sd;
        }else{
            return null;
        }
    }
    
    /** Check if a valid VSS was found */
    public boolean foundVSS() {
            return vss != null;
    }
    
    public VSLStationState getVSS(){
        return vss;
    }
    
    /** Get the speed limit */
    public Integer getSpeedLimit() {
            if(su != null && sd != null){
                    return Math.min(su.getSpeedLimit(), sd.getSpeedLimit());
            }
            else if(su != null){
                    return su.getSpeedLimit();
            }
            else if(sd != null){
                    return sd.getSpeedLimit();
            }
            else{
                    return null;
            }
    }
    
    /** Calculate the advisory speed */
    public Double calculateSpeedAdvisory() {
        if(vss != null && vss_mp != null) {
            Double spd = vss.getAggregateRollingSpeed(simGroup);
            if(spd > 0){
                return calculateSpeedAdvisory(spd, vss_mp - ma);
            }
        }
        return null;
    }
    
    /** Calculate a speed advisory.
    * @param spd Average speed at VSS.
    * @param d Distance upstream of station.
    * @return Speed advisory. */
    protected Double calculateSpeedAdvisory(double spd, double d){
        if(d > 0){
            int acc = -getControlThreshold();
            double s2 = spd * spd + 2.0 * acc * d;
//            System.out.print(", acc="+acc+", s2="+s2);
            if(s2 < 0){
                return null;
            }
//            System.out.print(", adv="+(double)Math.sqrt(s2));
            return (double)Math.sqrt(s2);
        }else{
            return spd;
        }
    }
    
    /** Debug the finder */
    public void debug() {
            double a = calculateSpeedAdvisory();
            System.out.println("adv: " + a +
                        ", upstream: " + su +
                        ", downstream: " + sd +
                        ", vss: " + vss +
                        ", speed: " + getSpeed() +
                        ", limit: " + getSpeedLimit());
    }
    
    /** Get the speed*/
    protected Double getSpeed(){
        if(su != null && sd != null){
            double u0 = su.getAggregateRollingSpeed(simGroup);
            double u1 = sd.getAggregateRollingSpeed(simGroup);
            if(u0 > 0 && u1 > 0){
                return Math.min(u0,u1);
            }
            if(u0 > 0){
                return u0;
            }
            if(u1 > 0){
                return u1;
            }
        }
        return null;
    }

    /*
     * Check DMS Signal condition
     * When the Speed of Upstream and Downstream Station speed from the DMS are slower than DMS Speed
     * the speed of DMS is maintained.
     * Speed of DMS must be lower than actual speed.
     * else,
     * DMS speed = MIN(speed of Upstream Station, Speed of Downstream Station);
     */
    Double checkBound(Integer s) {
        if(s == null)
            return null;
        Double setSpeed = (double)s;
        
        if(su != null && sd != null){
            Double uu = su.getAggregateRollingSpeed(simGroup);
            Double du = sd.getAggregateRollingSpeed(simGroup);
            if(uu <= 0 || du <= 0)
                return setSpeed;
            
            if(setSpeed >= uu && setSpeed > du){
                return Math.min(uu, du);
            }
            else
                return setSpeed;
        }else
            return setSpeed;
    }

    boolean isUpstreamVSS() {
        if(su != null && vss != null && su.getID().equals(vss.getID()))
            return true;
        else
            return false;
    }
}
