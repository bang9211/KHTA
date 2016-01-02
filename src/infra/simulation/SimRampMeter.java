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
package infra.simulation;

import infra.simobjects.*;
import java.util.ArrayList;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimRampMeter {
    public ArrayList<Double> rate = new ArrayList<Double>();
    public SimMeter meter;
    protected double lastRate = 0;
    protected double lastbeforeRate = 0;
    
    public SimRampMeter(SimMeter _meter){
        meter = _meter;
    }
   
    /**
     * set Rate
     * @param currentStep 
     */
    protected void setRate(double rnext) {
        lastbeforeRate = lastRate;
        lastRate = rnext;
        float redTime = calculateRedTime(rnext);
        redTime = Math.round(redTime * 10) / 10f;
        meter.setRate((byte)1);
        meter.setRedTime(redTime);
    }
    
    public double getLastRate(){
        if(lastRate == 0)
            return SimulationConfig.MAX_RATE;
        else
            return lastRate;
        
    }
    /**
    * Return red time that converted from rate
    * @param rate
    * @return red time in seconds
    */
    private float calculateRedTime(double rate) {
        float cycle = 3600 / (float)rate;
        return Math.max(cycle - meter.GREEN_YELLOW_TIME, SimulationConfig.MIN_RED_TIME);
    }
}
