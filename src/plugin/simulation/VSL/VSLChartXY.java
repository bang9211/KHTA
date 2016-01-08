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
//import edu.umn.natsrl.chart.TICASChartXY;
//import edu.umn.natsrl.infra.infraobjects.DMSImpl;
//import info.monitorenter.gui.chart.Chart2D;
//import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
//import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
//import infra.simulation.SimulationGroup;
//import java.awt.Color;
//import java.util.TreeMap;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLChartXY extends TICASChartXY{
//    public VSLChartXY(TreeMap<Integer,String> xformat, TreeMap<Integer, String> yformat){
//        super(xformat,yformat);
//        chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);
//    }
//    public void AddVSLResultStationSpeedGraph(TreeMap<Integer,VSLResultStation> stations, String name){
//        AddVSLResultStationSpeedGraph(stations,name,null);
//    }
//    public void AddVSLResultStationSpeedGraph(TreeMap<Integer,VSLResultStation> stations, String name,Integer idx){
//        double[] xdata = new double[stations.size()];
//        double[] speeddata = new double[stations.size()];
//        double[] VSLdata = new double[stations.size()];
//        
//        int cnt = 0;
//        for(Integer key : stations.keySet()){
//            int sindx = 0;
//            if(idx == null){
//                sindx = stations.get(key).getRollingSpeeds().length - 1;
//            }else{
//                sindx = idx;
//            }
//            
//            xdata[cnt] = key;
//            speeddata[cnt] = stations.get(key).getRollingSpeeds()[sindx];
//            boolean cvss = stations.get(key).getCurrentVSS()[sindx];
//            VSLdata[cnt] = cvss ? speeddata[cnt] : 0;
//            cnt++;
//        }
//        
//        AddStationGraph(xdata,speeddata,VSLdata,name);
//    }
//    
//    public void AddStationSpeedGraph(TreeMap<Integer,VSLStationState> stations, String name){
//        double[] xdata = new double[stations.size()];
//        double[] speeddata = new double[stations.size()];
//        double[] VSLdata = new double[stations.size()];
//        int cnt = 0;
//        for(Integer key : stations.keySet()){
//            xdata[cnt] = key;
//            speeddata[cnt] = stations.get(key).getAggregateRollingSpeed(SimulationGroup.VSL);
//            
//            cnt++;
//        }
//        AddStationGraph(xdata,speeddata,name);
//    }
//    
//    private void AddStationGraph(double[] xdata, double[] speeddata, String name){
//        AddStationGraph(xdata,speeddata,null,name);
//    }
//    
//    private void AddStationGraph(double[] xdata, double[] speeddata, double[] vsldata, String name){
//        super.addXYGraph(xdata, speeddata, name);
//        super.addXYGraph(xdata, speeddata, name+"DISC",new TracePainterDisc(),Color.red);
//        if(vsldata != null){
//            super.addXYGraph(xdata, vsldata, "VSS",new TracePainterVerticalBar(3,chart),Color.red);
//        }
//    }
//    
//    public void AddDMSSpeedGraph(TreeMap<Integer,DMSImpl> dmss, String name){
//        double[] xdata = new double[dmss.size()];
//        double[] ydata = new double[dmss.size()];
//        int cnt = 0;
//        for(Integer key : dmss.keySet()){
//            xdata[cnt] = key;
//            ydata[cnt] = dmss.get(key).getSpeedLimit();
//            cnt++;
//        }
//        AddDMSGraph(xdata,ydata,name);
//    }
//    
//    public void AddMapDMSSpeedGraph(TreeMap<Integer,VSLResultDMS> dmss, String name){
//        AddMapDMSSpeedGraph(dmss,name,null);
//    }
//    public void AddMapDMSSpeedGraph(TreeMap<Integer,VSLResultDMS> dmss, String name, Integer idx){
//        double[] xdata = new double[dmss.size()];
//        double[] ydata = new double[dmss.size()];
//        int cnt = 0;
//        for(Integer key : dmss.keySet()){
//            xdata[cnt] = key;
//            
//            int sindx = 0;
//            if(idx == null){
//                sindx = dmss.get(key).getSpeedLimit().length - 1;
//            }else{
//                sindx = idx;
//            }
//            ydata[cnt] = dmss.get(key).getSpeedLimit()[sindx];
//            cnt++;
//        }
//        AddDMSGraph(xdata,ydata,name);
//    }
//    
//    private void AddDMSGraph(double[] xdata, double[] ydata, String name){
//        super.addXYGraph(xdata, ydata, name, new TracePainterDisc(10), Color.BLACK);
//    }
//    
//    public void AddMapDMSActualSpeedGraph(TreeMap<Integer,VSLResultDMS> dmss, String name){
//        AddMapDMSActualSpeedGraph(dmss,name,null);
//    }
//    public void AddMapDMSActualSpeedGraph(TreeMap<Integer,VSLResultDMS> dmss, String name, Integer idx){
//        double[] xdata = new double[dmss.size()];
//        double[] ydata = new double[dmss.size()];
//        int cnt = 0;
//        for(Integer key : dmss.keySet()){
//            xdata[cnt] = key;
//            
//            int sindx = 0;
//            if(idx == null){
//                sindx = dmss.get(key).getActualSpeedLimit().length - 1;
//            }else{
//                sindx = idx;
//            }
//            ydata[cnt] = dmss.get(key).getActualSpeedLimit()[sindx];
//            cnt++;
//        }
//        AddDMSActualGraph(xdata,ydata,name);
//    }
//    
//    private void AddDMSActualGraph(double[] xdata, double[] ydata, String name){
//        super.addXYGraph(xdata, ydata, name, new TracePainterDisc(4), Color.BLUE);
//    }
//    
//    public void AddMapDMSSTAGraph(TreeMap<Integer,VSLResultDMS> dmss, String name){
//        AddMapDMSSTAGraph(dmss,name,null);
//    }
//    public void AddMapDMSSTAGraph(TreeMap<Integer,VSLResultDMS> dmss, String name, Integer idx){
//        double[] xdata = new double[dmss.size()];
//        double[] ydata = new double[dmss.size()];
//        int cnt = 0;
//        for(Integer key : dmss.keySet()){
//            xdata[cnt] = key;
//            
//            int sindx = 0;
//            if(idx == null){
//                sindx = dmss.get(key).getActualSpeedLimit().length - 1;
//            }else{
//                sindx = idx;
//            }
//            
//            if(dmss.get(key).getSTA()[sindx] == 1)
//                ydata[cnt] = dmss.get(key).getActualSpeedLimit()[sindx];
//            else
//                ydata[cnt] = -1;
//            cnt++;
//        }
//        AddDMSSTAGraph(xdata,ydata,name);
//    }
//    
//    private void AddDMSSTAGraph(double[] xdata, double[] ydata, String name){
////        super.addXYGraph(xdata, ydata, name, new TracePainterDisc(6), Color.RED);
//        super.addXYGraph(xdata, ydata, name, new TracePainterVerticalBar(2,chart), Color.ORANGE);
//    }
//}
