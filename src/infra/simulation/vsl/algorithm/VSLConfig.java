/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
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
package infra.simulation.vsl.algorithm;

import evaluation.Interval;
import plugin.simulation.VSL.algorithm.AccCheckThreshold;
import plugin.simulation.VSL.algorithm.BottleneckSpeed;
import plugin.simulation.VSL.algorithm.DensityAggregation;
import plugin.simulation.VSL.algorithm.MaxSpeed;
import plugin.simulation.VSL.algorithm.SpeedAggregation;
import plugin.simulation.VSL.algorithm.SpeedforLowK;
import util.KHTAConfig;
import util.PropertiesWrapper;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class VSLConfig extends KHTAConfig{
    private static String configFile = "VSLConfig.config";
    
    /**
     * Not Fixed Value
     */
    public static int VSL_VSS_DECISION_ACCEL = 1500;  //VSL VDA VSS Decision Acceleration (unit : mile / h^2)
    public static int VSL_CONTROL_THRESHOLD = 1000; //VSL ZDA 
    public static int VSL_BS_THRESHOLD = 55;
    public static int VSL_TURNOFF_ACCEL = 750; //VSL TOA 
    public static double VSL_MIN_STATION_MILE = 0.2d;
    public static int VSA_START_INTERVALS = 3;
    public static double VSL_RANGE_THRESHOLD = 35;
    public static int VSL_MOVING_ACCEL = 600;
    public static double VSL_VSS_CONTINUE_SPEED = 5.0d;
    public static double VSL_ZONE_LENGTH_MILE = 1.5d;
    
    public static SpeedAggregation SPEED_SPEED_AGGREGATION = SpeedAggregation.LAB;
    public static DensityAggregation SPEED_DENSITY_AGGREGATION = DensityAggregation.MovingKAvg;
    public static SpeedforLowK SPEED_SPEED_FOR_LOW_K = SpeedforLowK.FixedSpeed;
    public static MaxSpeed SPEED_MAX_SPEED = MaxSpeed.NoLimit;
    
    /**
     * Fixed Value
     */
    public static double VSL_MIN_SPEED = 30.0;
    public static double VSL_MAX_SPEED = 50.0;
    public static double VSL_MIN_CHANGE = 5.0;
    public static int FTM = 5280;   // 1 mile = 5280 feets
    
    //VSS Identification
    public static BottleneckSpeed bottleneckSpeedType = BottleneckSpeed.BASEDBOTTLENECKSPEED;
    public static AccCheckThreshold accCheckThreshold = AccCheckThreshold.BASEDCHECKTHRESHOLD;
    public static boolean isAccidentSpeed = false;
    public static double AccidentSpeed = 25;
    
    //Slow Traffic Ahead Sign
    static public Double coverDistance = 1.0d;
    public static Integer coverageSpeed = 45;
    
    //VSL STA Type
//    public static VSLSTAType vslSTAtype = VSLSTAType.OPTION2;
    
    //VSL Interval
    public static int VSL_INTERVAL = Interval.I30SEC.second;
    
    //VSL interval stationlist
    public static String VSL_INTERVAL_STATIONLIST = "";
    
    //adjust 10sec interval as like 30 sec
    public static boolean INTERVAL_ADJUST = true;
    
    public static boolean isVSLStep(int runtime){
            return runtime != 0 && runtime % VSL_INTERVAL == 0;
    }
    
    public static void save(){
        prop.put("VSL_VDA", VSL_VSS_DECISION_ACCEL);
        prop.put("ZDA", VSL_CONTROL_THRESHOLD);
        prop.put("VBT", VSL_BS_THRESHOLD);
        prop.put("TOA", VSL_TURNOFF_ACCEL);
        prop.put("VMD", VSL_MIN_STATION_MILE);
        prop.put("SA", SPEED_SPEED_AGGREGATION.getSRC());
        prop.put("DA", SPEED_DENSITY_AGGREGATION.getSRC());
        prop.put("SLK", SPEED_SPEED_FOR_LOW_K.getSRC());
        prop.put("SMS", SPEED_MAX_SPEED.getSRC());
        prop.put("MOVE_DEC", VSL_MOVING_ACCEL);
        prop.put("MOVE_SPD", VSL_RANGE_THRESHOLD);
        prop.put("ACCIDENTSPEED", AccidentSpeed);
        prop.put("ACT", accCheckThreshold.getSID());
        prop.put("BST", bottleneckSpeedType.getSID());
        prop.put("ISACCSPEED", isAccidentSpeed);
        prop.put("VSLZONEMILE", VSL_ZONE_LENGTH_MILE);
        prop.put("STA_COVERDISTANCE", coverDistance);
        prop.put("STA_COVERSPEED", coverageSpeed);
        prop.put("VSL_INTERVAL",VSL_INTERVAL);
        prop.put("VSL_INTERVAL_STATIONLIST", VSL_INTERVAL_STATIONLIST);
        saveConfig(configFile);
    }
    public static void load(){
        PropertiesWrapper p = loadConfig(configFile);
        if(p != null){
            //exe
            VSL_VSS_DECISION_ACCEL = p.getInteger("VSL_VDA");
            VSL_CONTROL_THRESHOLD = p.getInteger("ZDA");
            VSL_BS_THRESHOLD = p.getInteger("VBT");
            VSL_TURNOFF_ACCEL = p.getInteger("TOA");
            VSL_MIN_STATION_MILE = p.getDouble("VMD");
            VSL_MOVING_ACCEL = p.getInteger("MOVE_DEC");
            VSL_RANGE_THRESHOLD = p.getDouble("MOVE_SPD");
            SPEED_SPEED_AGGREGATION = SpeedAggregation.getTypebyID(p.getInteger("SA"));
            SPEED_DENSITY_AGGREGATION = DensityAggregation.getTypebyID(p.getInteger("DA"));
            SPEED_SPEED_FOR_LOW_K = SpeedforLowK.getTypebyID(p.getInteger("SLK"));
            SPEED_MAX_SPEED = MaxSpeed.getTypebyID(p.getInteger("SMS"));
            AccidentSpeed = p.getDouble("ACCIDENTSPEED");
            accCheckThreshold = AccCheckThreshold.getbyID(p.getInteger("ACT"));
            bottleneckSpeedType = BottleneckSpeed.getbyID(p.getInteger("BST"));
            isAccidentSpeed = p.getBoolean("ISACCSPEED");
            VSL_ZONE_LENGTH_MILE = p.getDouble("VSLZONEMILE");
            coverDistance = p.getDouble("STA_COVERDISTANCE");
            coverageSpeed = p.getInteger("STA_COVERSPEED");
            VSL_INTERVAL = p.getInteger("VSL_INTERVAL");
            VSL_INTERVAL_STATIONLIST = p.get("VSL_INTERVAL_STATIONLIST");
        }   
    }
}
