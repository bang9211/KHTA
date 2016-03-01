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

import infra.simulation.SimulationGroup;
import java.util.ArrayList;
import plugin.metering.MeteringSectionHelper.StationState;

/**
 *
 * @author Chongmyung Park
 */
public class BottleneckFinder {

    MeteringSectionHelper sectionHelper;
    private int runTime = 0;
    /**
     * Constructor
     * @param sectionHelper 
     */
    public BottleneckFinder(MeteringSectionHelper sectionHelper) {
        this.sectionHelper = sectionHelper;
    }

    /**
     * Find bottlenecks
     */
    public void findBottlenecks(int _runTime) {
        runTime = _runTime;
        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        for (int i = 0; i < stationStates.size(); i++) {
            System.out.println(stationStates.get(i).id + " : k=" + stationStates.get(i).getIntervalAggregatedDensity(SimulationGroup.Meter));
        }

        // find bottleneck candidates
        for (int i = 0; i < stationStates.size(); i++) {
            StationState s = stationStates.get(i);
            
            if (s.getIntervalAggregatedDensity(SimulationGroup.Meter) < MeteringConfig.Kb) continue;
            
            boolean increaseTrend = true;
            boolean highDensity = true;
            
            for(int j=0; j<MeteringConfig.BottleneckTrendCount; j++) {
                double k = s.getIntervalAggregatedDensity(SimulationGroup.Meter,j);
                double pk = s.getIntervalAggregatedDensity(SimulationGroup.Meter,j+1);
                if(k < pk) {
                    increaseTrend = false;
                }
                if(k < MeteringConfig.Kb || pk < MeteringConfig.Kb) highDensity = false;
            }
            
            if(s.isPrevBottleneck || increaseTrend || highDensity) {
                s.isBottleneck = true;
            } 
        }
        
//        // most downstream bottleneck must satisfy 'acceleration' condition
//        for(int i=stationStates.size()-1; i>=0; i--) {
//            StationState s = stationStates.get(i);
//            if(!s.isBottleneck) continue;
//            if(i > 0 ) {
//                StationState us = stationStates.get(i-1);
//                if(us.getAcceleration() < MeteringConfig.Ab) {
//                    s.isBottleneck = false;
//                } else {
//                    // it satisfy acceleration condition
//                    break;
//                }
//            }
//        }
               
        // merge zone by distnace and acceleration
        // iterate from downstream to upstream
        for(int i=stationStates.size()-1; i>=0; i--) {
            StationState s = stationStates.get(i);
            double k = s.getIntervalAggregatedDensity(SimulationGroup.Meter);
            
            if(!s.isBottleneck) continue;
           
            // check zone to merge
            for(int j=s.stationIdx-1; j>=0; j--) {
                StationState us = stationStates.get(j);
                if(!us.isBottleneck) continue;
                // close bottleneck                
                if(s.stationIdx - us.stationIdx < 3) {
                    if(us.getIntervalAggregatedDensity(SimulationGroup.Meter) > k && us.getAcceleration(SimulationGroup.Meter) > MeteringConfig.Ab) {
                        // close but independent BS
                        break;
                    }
                    // close -> merge
                    us.isBottleneck = false;
                } else if(us.getAcceleration(SimulationGroup.Meter) > MeteringConfig.Ab) {
                    // acceleration is heigh -> BS
                    break;
                } else {
                    // merge
                    us.isBottleneck = false;
                }
            }
        }
        
        if(MeteringConfig.UseCoordination) bottleneckCoordinate();
        
        for (StationState s : stationStates) {
            if(!s.isBottleneck) ++s.noBsCount;
            else s.noBsCount = 0;
        }
    }

    /**
     * Coordinate bottlenecks
     */
    private void bottleneckCoordinate() {
        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        
        // Find primary bottlenecks
        for(StationState s : stationStates) {
            if(!s.isBottleneck) continue;
            if(s.getIntervalAggregatedDensity(SimulationGroup.Meter) > s.Kc) s.isPrimaryBottleneck = true;
        }
        
        // Coordinate bottlenecks
        for(int i=stationStates.size()-1; i>=0; i--) {
            StationState primaryBS = stationStates.get(i);
            if(!primaryBS.isPrimaryBottleneck) continue;
            
            primaryBS.updatePrimaryState(SimulationGroup.Meter);            
            double primaryK = getZoneDensity(primaryBS);            
            System.out.println("      Primary Bottleneck : " + primaryBS.id +" ------------- (TR="+primaryBS.trendIndicator+")");
            // coordinate up to 1 upstream bottleneck
            
            if(primaryBS.trendIndicator > 2) primaryBS.coordinateLimit = 2;
            int coordinateBSCount = 0;
            
            // iterate to upstream from primary bottleneck
            for(int j=primaryBS.stationIdx-1; j>=0; j--) {
                StationState localBS = stationStates.get(j);
                if(!localBS.isBottleneck) continue;
                if(localBS.isPrimaryBottleneck) break;
                double localK = getZoneDensity(localBS);
                double KcRatio = localK / primaryK;
                KcRatio = Math.min(1.0, KcRatio);
                KcRatio = Math.max(0.8, KcRatio);
                localBS.coordinateKc = primaryBS.Kc * KcRatio;
                System.out.println("         - "+localBS.id+" : k="+localK+", primaryK="+primaryK+", ratio="+KcRatio+", cKc="+localBS.coordinateKc);
                // by first coordinate? or secondary coordinate?
                if(++coordinateBSCount >= primaryBS.coordinateLimit) break;
            }
        }        
    }
    
    /**
     * Return average density of the zone that given station is bottleneck
     * @param bottleneck
     * @return 
     */
    private double getZoneDensity(StationState bottleneck)
    {
        if(bottleneck.stationIdx == 0) return bottleneck.getIntervalAggregatedDensity(SimulationGroup.Meter);
        
        ArrayList<StationState> stationStates = sectionHelper.getStationStates();
        StationState upstreamBound = null;
        for(int i=bottleneck.stationIdx-1; i>=0; i--)
        {
            StationState us = stationStates.get(i);
            if(us.isBottleneck) break;
            upstreamBound = us;
        }
        
        if(upstreamBound == null) return bottleneck.getIntervalAggregatedDensity(SimulationGroup.Meter);
        
        return sectionHelper.getAverageDensity(upstreamBound, bottleneck,SimulationGroup.Meter);
    }
    
}
