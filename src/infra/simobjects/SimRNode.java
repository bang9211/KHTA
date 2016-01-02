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
package infra.simobjects;

import infra.infraobject.Detector;
import infra.infraobject.RNode;
import infra.type.TrafficType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimRNode extends SimObject{
    
    protected RNode rnode;
    protected HashMap<String, SimDetector> detectors = new HashMap<String, SimDetector>();
    SimObjects simObjects = null;
    
    public SimRNode(RNode rnode, SimObjects simObjects) {
        super(rnode.getID());
        this.rnode = rnode;
        this.simObjects = simObjects;
        for(SimDetector sd : getDetectors()){
            this.detectors.put(sd.getID(), sd);
        }
    }
    
    /**
     * SimObject에 Detector가 등록되어 있는지 검사
     * @return 
     */
    public ArrayList<SimDetector> getDetectors() {        
        Detector[] dets = this.rnode.getDetectorList();
//        SimDetector[] simDets = new SimDetector[dets.length];
        ArrayList<SimDetector> simDets = new ArrayList<SimDetector>();
        for(int i=0; i<dets.length; i++) {
//            SimDetector tempsim = simObjects.getDetectorWithoutNull("D"+dets[i].getDetectorId());
            SimDetector tempsim = simObjects.getDetectorWithoutNull(dets[i].getID());
            if(tempsim != null)
                simDets.add(tempsim);
        }
        return simDets;
    }
    
    public double getData(TrafficType type) {
        return getData(type, 0);
    }
    
    /**
     * Returns data before given period time step
     */
    public double getData(TrafficType type, int period) {

        double sum = 0;
        int idx = 0;
        int validCount = 0;
        for(SimDetector d : this.detectors.values())
        {
            double value = d.getData(type, period);
            if(type.isSpeed()){
            }
            if(value <= 0) continue;
            sum += value;
            //debug
//            if("S316".equals(this.rnode.getStationId())) {
//            if(type.isDensity()) {
//                System.out.println("  - "+this.rnode.getStationId()+" : " + d.getId() + "'s q("+period+"), length("+d.detector.isAbandoned()+")"
//                        + "(isstation:"+d.detector.isStationOrCD()+")"
//                        + " = " + value
//                        + " u : " + d.getData(TrafficType.SPEED,period)
//                         + " q : " + d.getData(TrafficType.FLOW,period)
//                        + " s : " + d.getData(TrafficType.SCAN,period)
//                        + " s : " + d.detector.getFieldLength());
//            }
//            }
            validCount++;
        }        
        
        if(validCount > 0) {
                if(type.isFlow() || type.isVolume())
                    return sum;
                else
                    return sum/validCount;
        }
        else return -1;
    }

    @Override
    public void reset() {
        detectors.clear();
    }
    
}
