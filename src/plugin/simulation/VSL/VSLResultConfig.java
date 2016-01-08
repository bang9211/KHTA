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
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.AccCheckThreshold;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.BottleneckSpeed;
//import util.PropertiesWrapper;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLResultConfig {
//    /**
//     * Not Fixed Value
//     */
//    public int VSL_VSS_DECISION_ACCEL = 1500;  //VSL VDA VSS Decision Acceleration (unit : mile / h^2)
//    public int VSL_CONTROL_THRESHOLD = 1000; //VSL ZDA 
//    public int VSL_BS_THRESHOLD = 55;
//    public int VSL_TURNOFF_ACCEL = 750; //VSL TOA 
//    public double VSL_MIN_STATION_MILE = 0.2d;
//    public int VSA_START_INTERVALS = 3;
//    public double VSL_RANGE_THRESHOLD = 35;
//    public int VSL_MOVING_ACCEL = 600;
//    public double VSL_VSS_CONTINUE_SPEED = 5.0d;
//    public double VSL_ZONE_LENGTH_MILE = 1.5d;
//    
//    /**
//     * Fixed Value
//     */
//    public double VSL_MIN_SPEED = 30.0;
//    public double VSL_MAX_SPEED = 50.0;
//    public double VSL_MIN_CHANGE = 5.0;
//    public int FTM = 5280;   // 1 mile = 5280 feets
//    
//    public int Interval = 30;
//    
//    //VSS Identification
//    public BottleneckSpeed bottleneckSpeedType = BottleneckSpeed.BASEDBOTTLENECKSPEED;
//    public AccCheckThreshold accCheckThreshold = AccCheckThreshold.BASEDCHECKTHRESHOLD;
//    public boolean isAccidentSpeed = false;
//    public double AccidentSpeed = 25;
//    
//    //Slow Traffic Ahead Sign
//    public Double coverDistance = 1.0d;
//    public Integer coverageSpeed = 45;
//    
//    public void save(PropertiesWrapper prop){
//        prop.put("VSL_VDA", VSL_VSS_DECISION_ACCEL);
//        prop.put("ZDA", VSL_CONTROL_THRESHOLD);
//        prop.put("VBT", VSL_BS_THRESHOLD);
//        prop.put("TOA", VSL_TURNOFF_ACCEL);
//        prop.put("VMD", VSL_MIN_STATION_MILE);
//        prop.put("MOVE_DEC", VSL_MOVING_ACCEL);
//        prop.put("MOVE_SPD", VSL_RANGE_THRESHOLD);
//        prop.put("ACCIDENTSPEED", AccidentSpeed);
//        prop.put("ACT", accCheckThreshold.getSID());
//        prop.put("BST", bottleneckSpeedType.getSID());
//        prop.put("ISACCSPEED", isAccidentSpeed);
//        prop.put("VSLZONEMILE", VSL_ZONE_LENGTH_MILE);
//        prop.put("STA_COVERDISTANCE", coverDistance);
//        prop.put("STA_COVERSPEED", coverageSpeed);
//    }
//    public void load(PropertiesWrapper p){
//        if(p != null){
//            //exe
//            VSL_VSS_DECISION_ACCEL = p.getInteger("VSL_VDA");
//            VSL_CONTROL_THRESHOLD = p.getInteger("ZDA");
//            VSL_BS_THRESHOLD = p.getInteger("VBT");
//            VSL_TURNOFF_ACCEL = p.getInteger("TOA");
//            VSL_MIN_STATION_MILE = p.getDouble("VMD");
//            VSL_MOVING_ACCEL = p.getInteger("MOVE_DEC");
//            VSL_RANGE_THRESHOLD = p.getDouble("MOVE_SPD");
//            AccidentSpeed = p.getDouble("ACCIDENTSPEED");
//            accCheckThreshold = AccCheckThreshold.getbyID(p.getInteger("ACT"));
//            bottleneckSpeedType = BottleneckSpeed.getbyID(p.getInteger("BST"));
//            isAccidentSpeed = p.getBoolean("ISACCSPEED");
//            VSL_ZONE_LENGTH_MILE = p.getDouble("VSLZONEMILE");
//            coverDistance = p.getDouble("STA_COVERDISTANCE");
//            coverageSpeed = p.getInteger("STA_COVERSPEED");
//        }   
//    }
//
//    public void setConfig() {
//        VSL_VSS_DECISION_ACCEL = VSLConfig.VSL_VSS_DECISION_ACCEL;
//        VSL_CONTROL_THRESHOLD = VSLConfig.VSL_CONTROL_THRESHOLD;
//        VSL_BS_THRESHOLD = VSLConfig.VSL_BS_THRESHOLD;
//        VSL_TURNOFF_ACCEL = VSLConfig.VSL_TURNOFF_ACCEL;
//        VSL_MIN_STATION_MILE = VSLConfig.VSL_MIN_STATION_MILE;
//        VSL_MOVING_ACCEL = VSLConfig.VSL_MOVING_ACCEL;
//        VSL_RANGE_THRESHOLD = VSLConfig.VSL_RANGE_THRESHOLD;
//        AccidentSpeed = VSLConfig.AccidentSpeed;
//        accCheckThreshold = VSLConfig.accCheckThreshold;
//        bottleneckSpeedType = VSLConfig.bottleneckSpeedType;
//        isAccidentSpeed = VSLConfig.isAccidentSpeed;
//        VSL_ZONE_LENGTH_MILE = VSLConfig.VSL_ZONE_LENGTH_MILE;
//        coverDistance = VSLConfig.coverDistance;
//        coverageSpeed = VSLConfig.coverageSpeed;
//    }
//}
