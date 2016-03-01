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

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLStationStateNew extends VSLStationState{
    int cnt = 1;
    
    public VSLStationStateNew(StationState s) {
        super(s.getStation(),s.getSection(),s.getSimObject(),s.getSimulationInterval());
        setUpstreamStationState(s.getUpstreamStationState());
        setDownStreamStationState(s.getDownStreamStationState());
    }

    @Override
    public void calculateBottleneck() {
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
                adjustUpstream(false);
            }else{
                setBottleneck(true);
                adjustUpstream(true);
            }
        }else{
            clearBottleneck();
        }
    }

    @Override
    protected void adjustDownstream() {
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
            upstream.n_bottleneck = 0;
            bottleneck = true;
            // Bump the bottleneck count so it won't just
            // shut off at the next time step
            while(isBeforeStartCount())
                    n_bottleneck++;
        }
    }

//    @Override
    protected void adjustUpstream(boolean cbottleneck) {
        VSLStationState upstream = this.getUpstreamVSLStationState();
        VSLStationState current = this;
        while(upstream != null){
            if(adjustUpstreamCondition(cbottleneck,upstream, current)){
                // Move bottleneck upstream
                // Don't use setBottleneck, because we don't
                // want p_bottle to be updated
                current.bottleneck = false;
                current.n_bottleneck = 0;
                current = upstream;
                current.bottleneck = true;
                
                // Bump the bottleneck count so it won't just
                // shut off at the next time step
                while(current.isBeforeStartCount()){
                    current.n_bottleneck++;
                }
            }else{
                break;
            }
            upstream = upstream.getUpstreamVSLStationState();
        }
    }
    
    private boolean adjustUpstreamCondition(boolean cbottleneck, VSLStationState upstream, VSLStationState current) {
        Double ap = upstream.acceleration;
        Double a = current.acceleration;
        
        if(cbottleneck && a != null && ap != null && a > ap){
            return true;
        }else if(!cbottleneck && a != null && ap != null
                && (current.bottleneck || current.p_bottleneck) && a > ap && ap <= getStopThreshold()){
            return true;
        }else
            return false;
    }
    
}
