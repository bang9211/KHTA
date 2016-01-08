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
//package plugin.simulation.VSL;
//
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLStationState;
//import edu.umn.natsrl.infra.infraobjects.DMSImpl;
//import edu.umn.natsrl.ticas.Simulation.SimulationImpl;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLAlgorithm;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLVersion;
//import edu.umn.natsrl.vissimcom.VISSIMVersion;
//import info.monitorenter.gui.chart.views.ChartPanel;
//import infra.Section;
//import infra.infraobject.Station;
//import infra.simobjects.SimDMS;
//import infra.simulation.SimInterval;
//import infra.simulation.SimulationGroup;
//import infra.simulation.StationState;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.TreeMap;
//import jcw.VISSIM_COMSERVERLib.Simulation;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLSim extends Simulation implements SimulationImpl{
//    
//    VSLAlgorithm vsl;
//    private ArrayList<VSLStationState> VSLStationStates = new ArrayList<VSLStationState>();
//    TreeMap<Double, VSLStationState> StationMap = new TreeMap<Double, VSLStationState>();
//    
//    //for Graph X axis
//    HashMap<Integer,String> xmap;
//    VSLChartXY chart;
//    ChartPanel cpn;
//    
//    VSLMilePointList ml;
//    VSLResults vslresult;
//    
//    VSLVersion vslversion;
//    
//    public VSLSim(String caseFile, int seed, Section section, VISSIMVersion v, VSLVersion _vv, SimInterval sInterval){
//        super(caseFile,seed,section,v,sInterval);
//        
//        vslversion = _vv;
//        SimInit();
//    }
//    
//    @Override
//    public void SimInit() {
////        this.DebugInterval = this.runInterval;
//        /*
//         * Set VSLStationState
//         * set Up, down VSLStation
//         */
//        for(int i=0;i<sectionHelper.getStationStates().size();i++){
//            StationState cs = sectionHelper.getStationStates().get(i);
//            
//            VSLStationState current = vslversion.getVSLStationState(cs);
//            
//            if(cs.getUpstreamStationState() != null){
//                VSLStationState upstreamVSL = VSLStationStates.get(i-1);
//                current.setUpstreamVSLStationState(upstreamVSL);
//                upstreamVSL.setDownStreamVSLStation(current);
//            }
//            
//            VSLStationStates.add(current);
//            //Add Map
//            StationMap.put(current.getMilePoint(), current);
//            StationMap.keySet().iterator();
//            
//        }
//        initDebug();
//        ml = new VSLMilePointList(VSLStationStates,section.getDMS());
//        vslresult = new VSLResults(section,ml,vslversion);
////        vslresult.setPeriod(period);
//    }
//    
//    @Override
//    public void RunningInitialize() {
//        super.RunningInitialize();
//        vsl = new VSLAlgorithm(VSLStationStates,this.section,vslversion,StationMap);
//        initChart();
////        Debug();
//    }
//
//    @Override
//    public void ExecuteBeforeRun() {
//        super.ExecuteBeforeRun();
//    }
//
//    @Override
//    public void ExecuteAfterRun() {
//        super.ExecuteAfterRun();
////        Update Station
//        for(VSLStationState cs : VSLStationStates){
//            cs.updateState(SimulationGroup.VSL);
//        }
//        
//        if(VSLConfig.isVSLStep(simcount)){
//                System.out.println("simcnt = "+simcount+" : startVSL");
//                vsl.Process();
//                updateDMS();
//        }
//        updateResults();
//    }
//    
//    @Override
//    public void DebugMassage() {
//            //for Station debuging
//            for (int i = 0; i < VSLStationStates.size(); i++) {
//                VSLStationState s = VSLStationStates.get(i);
//                System.out.println(s.getID() + " : TT_Q="+String.format("%.1f",s.getIntervalFlow(SimulationGroup.VSL))
//                        + " A_Q="+String.format("%.1f",s.getIntervalAverageLaneFlow(SimulationGroup.VSL))
//                        + " k=" +String.format("%.1f", s.getIntervalDensity(SimulationGroup.VSL))
//                        + " k_10 = "+String.format("%.1f", s.getDensity())
//                        + " k_iavg = "+String.format("%.1f", s.getIntervalAggregatedDensity(SimulationGroup.VSL))
//                        + " u=" + String.format("%.1f", s.getIntervalSpeed(SimulationGroup.VSL))//s.getAggregateRollingSpeed(SimulationGroup.VSL))
//                        + " agg="+s.getAggregateRollingSpeed(SimulationGroup.VSL)
//                        + " au="+s.getAverageRollingSpeed(SimulationGroup.VSL, 0,1) + ", "
//                        + s.getAverageRollingSpeed(SimulationGroup.VSL, 1,1) + ", "
//                        + s.getAverageRollingSpeed(SimulationGroup.VSL, 2,1)
//                        + s.getAverageRollingSpeed(SimulationGroup.VSL, 3,1)
//                        + s.getAverageRollingSpeed(SimulationGroup.VSL, 4,1)
//                        + " 10secU="+s.getAverageSpeed(0, 1) + ", "
//                        + s.getAverageSpeed(1, 1) + ", "
//                        + +s.getAverageSpeed(2, 1)
//                        + " v=" + s.getTotalVolume(0, getDebugIntervalIndex())
//                        + " acc = "+s.calculateAcceleration(SimulationGroup.VSL)
//                        + " sitv = "+s.getStateInterval(SimulationGroup.VSL)
//                        + " crunT = "+s.getCurrentRunTime());
//            }
//            
////            System.err.println("clearlog");
//            for(VSLStationState cvs : VSLStationStates){
//                System.err.println(cvs.getID()+"("+cvs.getMilePoint()+")" + " : "+"bcount = "+cvs.n_bottleneck+" , bottleneck = " + cvs.bottleneck+", pbottle = "+ cvs.p_bottleneck);
//            }
//            System.err.println();
//            System.err.println();
//            System.err.println("DMS Information");
//            for(DMSImpl d : section.getDMS()){
//                System.err.println(d.getId()+"("+d.getMilePoint(section.getName())+")" + " : "+d.isStarted()+", speedlimit : "+d.getSpeedLimit()+", sc="+simcount);
//            }
//            
//            updateChart();
//    }
//
//    private void Debug() {
//        if(VSLStationStates.isEmpty())
//            return;
//        
//        System.out.println("Node Lists");
//        StationState node = VSLStationStates.get(0);
//        while(true){
//            System.out.print(node.getID() + " - ");
////            if(node.getUpstreamStationState() != null){
////                System.out.print(node.getUpstreamStationState().getID()+"("+node.getStation().getDistanceToUpstreamStation(section.getName())+")");
////            }else
////                System.out.print("null");
//            
//            if(node.getDownStreamStationState() != null){
//                System.out.print(" - "+node.getDownStreamStationState().getID()+"("+node.getStation().getDistanceToDownstreamStation(section.getName())+")");
//                System.out.print(" - "+node.getdistanceToDownstreamStationState());
//            }else
//                System.out.print(" - null");
//            
//            
//            System.out.println();
////            System.out.println(node.getID() + " - "+node.getDownStreamStationState().getID()+"("+node.getStation().getDistanceToDownstreamStation(this.section.getName())+")"
////                    +" : "+node.getDownStreamStationState().getID()+"("+node.getdistanceToDownstreamStationState()+")");
//            if(node.getDownStreamStationState() != null){
//                node = node.getDownStreamStationState();
//            }else{
//                break;
//            }
//        }
//        
//        System.out.println("Tree Check");
//        for(VSLStationState vs : StationMap.values()){
//            System.out.println(vs.getID());
//        }
//        
//        System.out.println("VSL Check");
//        VSLStationState n = VSLStationStates.get(0);
//        while(true){
//            System.out.println(n.getID());
//            if(n.getDownstreamVSLStationState() != null){
//                n = n.getDownstreamVSLStationState();
//            }else{
//                break;
//            }
//        }
//        
//        System.out.println();
//        System.out.println("StationState List");
//        for(VSLStationState s : this.VSLStationStates){
//            System.out.println(s.getID() + " : "+s.getdistanceToDownstreamStationState());
//        }
//        System.out.println("RealStation List");
//        if(VSLStationStates.isEmpty())
//            return;
//        Station rstation = VSLStationStates.get(0).getStation();
//        while(true){
//            System.out.print(rstation.getStationId() + " : ");
//            Station down = rstation.getDownStation(this.section.getName());
//            if(down == null){
//                System.out.println("null");
//                break;
//            }
//            else{
//                System.out.println(down.getDistanceToUpstreamStation(this.section.getName()));
//                rstation = down;
//            }
//        }
//    }
//
//    private void initDebug() {
//        System.out.println("Station Interval List");
//        for(VSLStationState vs : StationMap.values()){
//            System.out.print(vs.getID()+"("+vs.getLabel()+","+vs.getStation().getStationId()+")");
//            System.out.println(" : "+(vs.getStateInterval() != null ? true : false)+ ", "+vs.getStateInterval().getIntervalByGID(SimulationGroup.VSL));
//        }
////        System.out.println();
////        System.out.println("DMS-Station Distance compare");
////        for(VSLStationState s : VSLStationStates){
////            String sname = section.getName();
//////            DMSImpl ddms = s.getStation().getDownstreamDMS(this.section.getName());
//////            int ddistance = s.getStation().getDistancetoDownstreamDMS(sname);
//////            DMSImpl udms = s.getStation().getUpstreamDMS(this.section.getName());
//////            int udistance = s.getStation().getDistancetoUpstreamDMS(sname);
//////            System.out.println("--"+s.getID());
//////            System.out.println("---U:"+udms.getId()+"("+udistance+")"+"-DownstreamStation : "+udms.getDownstreamStation(sname)+"("+udms.getDistancetoDownstreamStation(sname));
//////            System.out.println("---D:"+ddms.getId()+"("+ddistance+")"+"-UpstreamStation : "+ddms.getUpstreamStation(sname)+"("+ddms.getDistancetoUpstreamStation(sname));
////            System.out.print("--"+s.getID() + " : ");
////            if(s.getdistanceToUpstreamStationState() != -1){
////                System.out.print(s.getdistanceToUpstreamStationState());
////            }else{
////                System.out.print("0");
////            }
////            System.out.print(" - ");
////            System.out.println(s.getStation().getStationFeetPoint(this.section.getName()));
////        }
//        
//    }
//    
//    private void updateDMS() {
//        for(SimDMS sdms : this.simDMSs){
//            sdms.setVSAtoVISSIM(vc);
//        }
//    }
//
//    private void updateChart() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                chart.AddVSLResultStationSpeedGraph(vslresult.getMapStations(), "StationSpeed");
//                chart.AddMapDMSSpeedGraph(vslresult.getMapDMSs(), "DMSSpeedLimit");
//                chart.AddMapDMSActualSpeedGraph(vslresult.getMapDMSs(), "DMSActualSpeedLimit");
//                chart.AddMapDMSSTAGraph(vslresult.getMapDMSs(), "SLOW TRAFFIC AHEAD");
//                cpn.setSize(chartPanel.getSize());
//                chartPanel.getParent().validate();
//            }
//        }, 1);
//    }
//
//    private void initChart() {
////        xmap = new HashMap<Integer,String>();
////        int cnt = 0;
////        for(VSLResultStation s : vslresult.getStations().values()){
////            xmap.put(cnt, s.getID());
////            cnt++;
////        }
//        chartPanel.removeAll();
//        chart = new VSLChartXY(vslresult.getMilePointListLayout(),null);
//        cpn = new ChartPanel(chart.getChart());
//        cpn.setSize(chartPanel.getSize());
//        chartPanel.add(cpn);
//    }
//
//    private void updateResults() {
//        //add Station info
//        for (int i = 0; i < VSLStationStates.size(); i++) {
//            VSLStationState s = VSLStationStates.get(i);
//            VSLResultStation rs = vslresult.getStations().get(s.getMilePoint());
//            if(rs != null){
//                rs.addData(s);
//            }
//        }
//        
//        //add DMS info
//        for(DMSImpl d : section.getDMS()){
//            VSLResultDMS rd = vslresult.getDMSs().get(d.getMilePoint(section.getName()));
//            if(rd != null){
//                rd.addData(d);
//            }
//        }
//    }
//    
//    public VSLResults getVSLResults(){
//        return vslresult;
//    }
//}
