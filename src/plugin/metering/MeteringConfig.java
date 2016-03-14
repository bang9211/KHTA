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

import evaluation.Interval;
import util.PropertiesWrapper;

/**
 *
 * @author Chongmyung Park
 */
public class MeteringConfig {
    
    private static PropertiesWrapper prop = new PropertiesWrapper();
    private static String configFile = "metering.config";
    public static int MeteringInterval = Interval.I30SEC.second;
    public static double Kc = 25;
    public static double Kd_Rate = 0.8;
    public static double Kd = Kc*Kd_Rate;
    //public static double Kjam = 180;
    public static double Kjam = 112;
    //public static double Kstop = 20;
    public static double Kstop = 12.5;
    public static double KstartRate = 0.8;
    
    public static int MAX_RATE = 1714;    // 3600/2.1 (red = 0.1s, green + yellow = 2s)
    public static int MAX_RAMP_DENSITY = 112;
    final static int METER_PER_KM = 1000; //m
//    public static int FEET_PER_MILE = 5280;
    public static double PASSAGE_DEMAND_FACTOR = 1.15;
    
    public static int MAX_RED_TIME = 13;  // max red time = 13 second    
    public static float MIN_RED_TIME = 0.1f;  // minimum red time = 0.1 second    
    public static int MAX_WAIT_TIME_MINUTE = 4; // 4 minute in 30 seconds unit
    public static int MAX_WAIT_TIME_MINUTE_F2F = 2; // for Freeway to Freway ramp
    public static int MAX_WAIT_TIME_INDEX = 7; // 4 minute in 30 seconds unit
    public static int MAX_WAIT_TIME_INDEX_F2F = 3; // 2 minute in 30 seconds unit
    public static String CASE_FILE = "";
    public static int RANDOM_SEED = 10;
    public static boolean UseMetering = true;
    //public static double Kb = 30;
    public static double Kb = 15.625;
    //public static int Ab = 1000;
    public static int Ab = 1609;
    public static boolean UseCoordination = false;
    public static int stopDuration = 10;    // 5min
    public static int stopBSTrend = 3;
    public static int stopUpstreamCount = 3;
    static int BottleneckTrendCount = 2;
    
    /** Queue occupancy override threshold */
    static final int QUEUE_OCC_THRESHOLD = 25;
    public static int DEFAULT_MAX_WAIT = 240;
    public static int MAX_WAIT_TIME = 240;
    /** Ratio for max rate to target rate */
    static public final float TARGET_MAX_RATIO = 1.3f;

    /** Ratio for min rate to target rate */
    static public final float TARGET_MIN_RATIO = 0.7f;

    /** Ratio for target waiting time to max wait time */
//    static public final float WAIT_TARGET_RATIO = 0.75f;
    static public final float WAIT_TARGET_RATIO = 0.95f;

    /** Ratio for target storage to max storage */
//    static public final float STORAGE_TARGET_RATIO = 0.75f;
    static public final float STORAGE_TARGET_RATIO = 1.0f;
    
    /** For Associate Metering **/
    final static int UPSTREAM_THRESHOLD_DISTANCE = 152; //m
    final static int METER_PER_MILE = 1600;
    
    public static void setMaxWaitTime(int minute) {
        MAX_WAIT_TIME_MINUTE = minute;
        MAX_WAIT_TIME_INDEX = (minute * 2) -1;
        MAX_WAIT_TIME = minute * 60;
    }
    
    public static void setMaxWaitTimeF2F(int minute) {
        MAX_WAIT_TIME_MINUTE_F2F = minute;
        MAX_WAIT_TIME_INDEX_F2F = (minute * 2) -1;
    }    
    
    public static Float getMinRate() {
        return 3600 / ((float)MAX_RED_TIME + 2);
    }
    
    public static Float getMaxRate(){
        return 3600 / (MIN_RED_TIME + 2);
    }
    
    public static boolean isMeteringStep(int runtime){
            return runtime != 0 && runtime % MeteringConfig.MeteringInterval == 0;
    }
    
    public static void saveConfig() {
        prop.put("Kc", Kc);
        prop.put("Kd", Kd);
        prop.put("Kd_Rate", Kd_Rate);
        prop.put("Kjam", Kjam);
        prop.put("Kb", Kb);
        prop.put("Kstop", Kstop);
        prop.put("StopDuration", stopDuration);
        prop.put("StopBSTrend", stopBSTrend);
        prop.put("StopUpstreamCount", stopUpstreamCount);
        prop.put("Ab", Ab);
        prop.put("MAX_RAMP_DENSITY", MAX_RAMP_DENSITY);
        prop.put("MAX_RED_TIME", MAX_RED_TIME);
        prop.put("MAX_WAIT_TIME_INDEX", MAX_WAIT_TIME_INDEX);        
        prop.put("MAX_WAIT_TIME_INDEX_F2F", MAX_WAIT_TIME_INDEX_F2F);        
        prop.put("CASE_FILE", CASE_FILE);        
        prop.put("RANDOM_SEED", RANDOM_SEED);   
        prop.put("UseCoordination", UseCoordination);
        prop.put("METER_INTERVAL", MeteringInterval);
        prop.save(configFile);
    }
    
    public static void loadConfig() {
        PropertiesWrapper p = PropertiesWrapper.load(configFile);
        if(p != null) {
            prop = p;
            Kc = prop.getDouble("Kc");
            Kd = prop.getDouble("Kd");
            Kd_Rate = prop.getDouble("Kd_Rate");
            Kjam = prop.getDouble("Kjam");
            Kb = prop.getDouble("Kb");
            Kstop = prop.getDouble("Kstop");
            stopDuration = prop.getInteger("StopDuration");
            stopBSTrend = prop.getInteger("StopBSTrend");
            stopUpstreamCount = prop.getInteger("StopUpstreamCount");
            Ab = prop.getInteger("Ab");
            MAX_RAMP_DENSITY = prop.getInteger("MAX_RAMP_DENSITY");
            MAX_RED_TIME = prop.getInteger("MAX_RED_TIME");   
            MAX_WAIT_TIME_INDEX = prop.getInteger("MAX_WAIT_TIME_INDEX");
            MAX_WAIT_TIME_INDEX_F2F = prop.getInteger("MAX_WAIT_TIME_INDEX_F2F");
            CASE_FILE = prop.get("CASE_FILE");
            RANDOM_SEED = prop.getInteger("RANDOM_SEED");
            UseCoordination = prop.getBoolean("UseCoordination");
            MeteringInterval = prop.getInteger("METER_INTERVAL");
        }
    }


            
}
