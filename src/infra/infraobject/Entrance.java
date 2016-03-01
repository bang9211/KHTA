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
package infra.infraobject;

import infra.InfraDatas;
import infra.type.RnodeType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Entrance extends RNode{
    ArrayList<Detector> queue = new ArrayList<Detector>();
    ArrayList<Detector> passage = new ArrayList<Detector>();
    
    ArrayList<Detector> simqueue = new ArrayList<Detector>();
    ArrayList<Detector> simpassage = new ArrayList<Detector>();
    RampMeter meter;
    
    public Entrance(HashMap<InfraDatas,Object> datas) {
        super(datas,RnodeType.ENTRANCE);
        setEntranceDetector();
        setRampMeter(datas);
    }
    
    private void setEntranceDetector() {
        //real ramp detector
        for(Detector d : this.detectors.values()){
            if(d.getLaneType().isQueue())
                queue.add(d);
            else if(d.getLaneType().isPassage())
                passage.add(d);
        }
        
        //sim ramp detector
        for(Detector d : this.simdetectors.values()){
            if(d.getLaneType().isQueue())
                simqueue.add(d);
            else if(d.getLaneType().isPassage())
                simpassage.add(d);
        }
    }
    
    private void setRampMeter(HashMap<InfraDatas,Object> datas) {
        meter = new RampMeter(datas, queue, passage, simqueue, simpassage);
    }
    
    public ArrayList<Detector> getPassages() {
        if(!isSimMode)
            return passage;
        else
            return simpassage;
    }

    public ArrayList<Detector> getQueues() {
        if(!isSimMode)
            return queue;
        else
            return simqueue;
    }
    
    public RampMeter getRampMeter(){
        return meter;
    }
    
    @Override
    public String toString(){
        return getOrder() + " - "+ getName() + " (ENTRANCE)";
    }
}
