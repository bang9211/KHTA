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
package vissimcom;

import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum VSA {
    V15("VSL-15", 15),
    V20("VSL-20", 20),
    V25("VSL-25", 25),
    V30("VSL-30", 30),    
    V35("VSL-35", 35),
    V40("VSL-40", 40),
    V45("VSL-45", 45),    
    V50("VSL-50", 50),
    V55("VSL-55", 55),    
    V60("VSL-60", 60),
    V65("VSL-65", 65),    
    V70("VSL-70", 70),
    V75("VSL-75", 75),
    ;
    
    /** Storage for desired speed distribution number corresponding to VSA */
    private static HashMap<String, Integer> desiredSpeedDistributionNumber = new HashMap<String, Integer>();
    
    /** Members */
    public final String desiredSpeedDistributionName;
    public final int speed;

    /** Constructor */
    private VSA(String desiredSpeedDistributionName, int speed) {
        this.desiredSpeedDistributionName = desiredSpeedDistributionName;
        this.speed = speed;
    }
    
    /**
     * Get VSA by desired speed distribution name
     * @param name Desired speed distribution name
     * @return VSA
     */
    private static VSA getVSAbyName(String name)
    {
        for(VSA v : VSA.values()) {
            if(name.equals(v.desiredSpeedDistributionName)) return v;
        }
        return null;
    }
    
    /**
     * Get VSA by desired speed
     * @param speed VSA speed
     * @return VSA corresponding to speed
     */
    public static VSA getVSA(int speed) {
        for(VSA v : VSA.values()) {
            if(speed == v.speed) return v;
        }
        return null;        
    }
    
    public static VSA getMaxVSA(){
        Integer maxVSA = 0;
        VSA cvsa = null;
        for(VSA v : VSA.values()){
            if(maxVSA < v.speed && v.getDSDNumber() >= 0){
                maxVSA = v.speed;
                cvsa = v;
            }
        }
        return cvsa;
        
    }
    
    /**
     * Set desired speed decision number to VSA enum
     * @param name Desired speed decision name in VISSIM case file
     * @param num  Desired speed decision number in VISSIM case file
     */
    protected static void setDSDNumber(String name, int num)
    {
        VSA.desiredSpeedDistributionNumber.put(name, num);
    }
    
    /**
     * @return Desired speed decision number in VISSIM case
     */
    public int getDSDNumber()
    {
        Integer n = VSA.desiredSpeedDistributionNumber.get(this.desiredSpeedDistributionName);
        return (n != null ? n : -1);
    }
    
    public int getAltDSDNumber() {
        Integer n = VSA.desiredSpeedDistributionNumber.get(this.desiredSpeedDistributionName+"A");
        return (n != null ? n : -1);
    }
}
