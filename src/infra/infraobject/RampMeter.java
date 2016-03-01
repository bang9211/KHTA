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

import infra.InfraDataMode;
import infra.InfraDatas;
import infra.InfraObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RampMeter extends InfraObject{
    private List<String> queue;
    private List<String> passage;
    private List<String> simqueue;
    private List<String> simpassage;
    private int Length;
    private int MAX_WAIT = 240;
    
    public RampMeter(HashMap<InfraDatas,Object> datas) {
        super(datas);
    }

    RampMeter(HashMap<InfraDatas, Object> datas, ArrayList<Detector> queue, ArrayList<Detector> passage, ArrayList<Detector> simqueue, ArrayList<Detector> simpassage) {
        super(datas);
        this.queue = insertIDs(queue);
        this.passage = insertIDs(passage);
        this.simqueue = insertIDs(simqueue);
        this.simpassage = insertIDs(simpassage);
        
        Integer it;
        it = (Integer)super.getProperty(InfraDatas.LENGTH);
        Length = it == null ? -1 : it;
    }
    
    private ArrayList<String> insertIDs(ArrayList<Detector> det){
        ArrayList<String> dids = new ArrayList<String>();
        for(Detector d : det){
            dids.add(d.getID());
        }
        return dids;
    }
    
    public List<String> getQueues(InfraDataMode idm){
        if(idm.isRealMode())
            return queue;
        else
            return simqueue;
    }
    
    public List<String> getPassages(InfraDataMode idm){
        if(idm.isRealMode())
            return passage;
        else
            return simpassage;
    }

    public double getStorage() {
        return Length;
    }
    
    public int getMaxWait(){
        return MAX_WAIT;
    }
}
