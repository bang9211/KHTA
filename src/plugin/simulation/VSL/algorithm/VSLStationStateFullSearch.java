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

import infra.simulation.StationState;
import infra.simulation.vsl.algorithm.VSLConfig;
import java.util.Map;
import java.util.NavigableMap;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLStationStateFullSearch extends VSLStationState{
    int cnt = 1;
    
    public VSLStationStateFullSearch(StationState s) {
        super(s.getStation(),s.getSection(),s.getSimObject(),s.getSimulationInterval());
        setUpstreamStationState(s.getUpstreamStationState());
        setDownStreamStationState(s.getDownStreamStationState());
    }

    @Override
    public void calculateBottleneck(double m,
        NavigableMap<Double, VSLStationState> upstream)
    {
        Double mp = upstream.lowerKey(m);
        while(mp != null && isTooClose(m - mp)){
            mp = upstream.lowerKey(mp);
        }
        
        if(mp != null){
            VSLStationState sp = upstream.get(mp);
            Double d = m - mp;
            acceleration = calculateAcceleration(simGroup,sp, d);
            checkThresholds();
            
            if(isAboveBottleneckSpeed()){
                setBottleneck(false);
            }else if(isBeforeStartCount()){
                setBottleneck(false);
            }else{
                setBottleneck(true);
            }
            adjustStream(m,upstream);
//            adjustStream_old();
        }else{
            clearBottleneck();
        }
    }
    
    private void adjustStream(double m, NavigableMap<Double, VSLStationState> upstream) {
        //Find VSS between Current and Upstream Station that acc is less than 0
        VSLStationState current = this;
//        System.out.println("current : "+current.getID()+"("+current.acceleration+","+current.bottleneck+","+current.p_bottleneck);
        if(!findVSS(m,upstream)){
            return;
        }
        
        VSLStationState s = this;
        upstream.put(m, s);
        Map.Entry<Double, VSLStationState> entry = upstream.floorEntry(m);
        while(entry != null){
            VSLStationState sp = entry.getValue();
            Double ap = sp.acceleration;
            Double a = s.acceleration;
            
            if(ap != null && ap >= 0){
                break;
            }
//            System.out.println(">>ComStation : "+s.getID()+", acc:"+s.acceleration+", isbSpd:"+s.isAboveBottleneckSpeed()+", a:"+a);
//            System.out.println(">>Com2Station : "+sp.getID()+", acc:"+sp.acceleration+", isbSpd:"+sp.isAboveBottleneckSpeed()+", ap:"+ap);
            if(a != null && ap != null && !sp.isAboveBottleneckSpeed()
                    && ap < this.getVSLMovingAcc() && a >= ap){
//                System.out.println("CStation : "+sp.getID()+", acc:"+sp.acceleration+", isbSpd:"+sp.isAboveBottleneckSpeed());
                s.bottleneck = false;
                s.n_bottleneck = 0;
                s = sp;
                s.bottleneck = true;
                
                while(s.isBeforeStartCount()){
                    s.n_bottleneck++;
                }
            }else{
//                System.out.println("--NStation : "+sp.getID()+", acc:"+sp.acceleration+", isbSpd:"+sp.isAboveBottleneckSpeed());
                sp.bottleneck = false;
                sp.n_bottleneck = 0;
            }
            entry = upstream.lowerEntry(entry.getKey());
        }
        
    }
    
    private boolean findVSS(double m, NavigableMap<Double, VSLStationState> upstream) {
        Double mp = m;
        VSLStationState sp = this;
        boolean isVSS = false;
//        System.out.println("----Finding Station---");
        while(mp != null){
            Double ap = sp.acceleration;
//            System.out.println("--ComS :"+sp.getID()+", acc:"+sp.acceleration);
            if(ap != null && ap >= 0){
                break;
            }
            
            if(sp.bottleneck || sp.p_bottleneck){
//                System.out.println("-----Find VSS : "+sp.getID());
                isVSS = true;
            }
            
            mp = upstream.lowerKey(mp);
            if(mp != null){
                sp = upstream.get(mp);
            }
        }
        
        return isVSS;
    }
    
    private int getVSLMovingAcc() {
        return VSLConfig.VSL_MOVING_ACCEL;
    }
}