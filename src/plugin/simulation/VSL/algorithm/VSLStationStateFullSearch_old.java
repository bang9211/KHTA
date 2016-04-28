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
//import infra.SectionHelper.StationState;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLStationStateFullSearch_old extends VSLStationState{
//    int cnt = 1;
//
//    public VSLStationStateFullSearch_old(StationState s) {
//        super(s.getStation(),s.getSection(),s.getSimObject(),s.getSimulationInterval());
//        setUpstreamStationState(s.getUpstreamStationState());
//        setDownStreamStationState(s.getDownStreamStationState());
//    }
//
//    @Override
//    public void calculateBottleneck() {
//        VSLStationState upstream = this.getUpstreamVSLStationState();
//        int Pdistance = 0;
//        if(upstream == null)
//            return;
//        Pdistance = upstream.getdistanceToDownstreamStationState();
//        
//        while(upstream != null && isTooClose(Pdistance)){
//            upstream = upstream.getUpstreamVSLStationState();
//            Pdistance += upstream.getdistanceToDownstreamStationState();
//        }
//        
//        if(upstream != null){
//            acceleration = calculateAcceleration(simGroup,upstream, Pdistance);
//        
//            //check the bottleneck thresholds
//            checkThresholds();
//            
//            if(isAboveBottleneckSpeed()){ //if station speed is above the bottleneck id speed ex) cspeed > 55mph
//                setBottleneck(false);
//            }else if(isBeforeStartCount()){ //if the number of intervals is lower than start count ex) count < 3
//                setBottleneck(false);
////                adjustDownstream();
////                adjustUpstream(false);
//            }else{
//                setBottleneck(true);
////                adjustUpstream(true);
//            }
//            adjustStream();
//        }else{
//            clearBottleneck();
//        }
//    }
//    
//    private void adjustStream() {
//        VSLStationState maxStation = null;
//        VSLStationState current = this;
//        
////        System.out.println("current : "+current.getID()+"("+current.acceleration+","+current.bottleneck+","+current.p_bottleneck);
//        if(!current.bottleneck && !current.p_bottleneck){
//            return;
//        }
//        //if stations is less than bottleneck speed, we should calculate vss without current station
//        maxStation = adUpstream(current);
//        adDownstream(maxStation);
//        
//    }
//    
//    private VSLStationState adUpstream(VSLStationState current) {
//        VSLStationState upstream = current.getUpstreamVSLStationState();
//        VSLStationState maxStation = current;
//
//        if(upstream == null){
//            return maxStation;
//        }
//        
//        while(upstream != null){
//            Double ap = upstream.acceleration;
//            Double a = maxStation.acceleration;
////            System.out.println("fUp-max : "+maxStation.getID()+"("+maxStation.acceleration+","+maxStation.bottleneck+","+maxStation.p_bottleneck+"), com : "+upstream.getID()+"("+upstream.acceleration+")");
//            if(ap == null || ap > 0){
//                break;
//            }
//            
//            if(ap != null && a != null && ap < a){
//                maxStation.bottleneck = false;
//                maxStation.n_bottleneck = 0;
//                maxStation = upstream;
//                maxStation.bottleneck = true;
//                while(maxStation.isBeforeStartCount()){
//                    maxStation.n_bottleneck++;
//                }
//            }else{
//                upstream.bottleneck = false;
//                upstream.n_bottleneck = 0;
//            }
//            upstream = upstream.getUpstreamVSLStationState();
//        }
//        
//        return maxStation;
//    }
//    
//    private VSLStationState adDownstream(VSLStationState maxStation) {
//        VSLStationState downstream = maxStation.getDownstreamVSLStationState();
//        if(downstream == null){
//            return maxStation;
//        }
//        
//        while(downstream != null){
////            System.out.println("fDn-max : "+maxStation.getID()+"("+maxStation.acceleration+"), com : "+downstream.getID()+"("+downstream.acceleration+")");
//            Double ap = downstream.acceleration;
//            Double a = maxStation.acceleration;
//            
//            if(ap == null || ap > 0){
//                break;
//            }
//            
//            if(ap != null && a != null && ap < a){
//                maxStation.bottleneck = false;
//                maxStation.n_bottleneck = 0;
//                maxStation = downstream;
//                maxStation.bottleneck = true;
//                while(maxStation.isBeforeStartCount()){
//                    maxStation.n_bottleneck++;
//                }
//            }else{
//                downstream.bottleneck = false;
//                downstream.n_bottleneck = 0;
//            }
//            
//            downstream = downstream.getDownstreamVSLStationState();
//        }
//        
//        return maxStation;
//    }
//
//    @Override
//    protected void adjustDownstream() {
//        if(this.getDownstreamVSLStationState() == null){
//            return;
//        }
//        
//        VSLStationState upstream = this.getUpstreamVSLStationState();
//        
//        Double ap = upstream.acceleration;
//        Double a = acceleration;
//        if(a != null && ap != null && a < ap && upstream.p_bottleneck){
//            // Move bottleneck downstream
//            // Don't use setBottleneck, because we don't want
//            // p_bottle to be updated
//            upstream.bottleneck = false;
//            upstream.n_bottleneck = 0;
//            bottleneck = true;
//            // Bump the bottleneck count so it won't just
//            // shut off at the next time step
//            while(isBeforeStartCount())
//                    n_bottleneck++;
//        }
//    }
//
////    @Override
//    protected void adjustUpstream(boolean cbottleneck) {
//        VSLStationState upstream = this.getUpstreamVSLStationState();
//        VSLStationState current = this;
//        while(upstream != null){
//            if(adjustUpstreamCondition(cbottleneck,upstream, current)){
//                // Move bottleneck upstream
//                // Don't use setBottleneck, because we don't
//                // want p_bottle to be updated
//                current.bottleneck = false;
//                current.n_bottleneck = 0;
//                current = upstream;
//                current.bottleneck = true;
//                
//                // Bump the bottleneck count so it won't just
//                // shut off at the next time step
//                while(current.isBeforeStartCount()){
//                    current.n_bottleneck++;
//                }
//            }else{
//                break;
//            }
//            upstream = upstream.getUpstreamVSLStationState();
//        }
//    }
//    
//    private boolean adjustUpstreamCondition(boolean cbottleneck, VSLStationState upstream, VSLStationState current) {
//        Double ap = upstream.acceleration;
//        Double a = current.acceleration;
//        
//        if(cbottleneck && a != null && ap != null && a > ap){
//            return true;
//        }else if(!cbottleneck && a != null && ap != null
//                && (current.bottleneck || current.p_bottleneck) && a > ap && ap <= getStopThreshold()){
//            return true;
//        }else
//            return false;
//    }
//}