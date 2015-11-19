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
import infra.InfraObject;
import infra.Period;
import infra.type.*;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Detector extends InfraObject{
    private String detector_id;
    private LaneType lanetype = LaneType.None;
    transient private Period period;
    transient private int MinInterval;
    private RNode r_node;
    
    transient private Vector<Double> volume = new Vector<Double>();
    transient private Vector<Double> speed = new Vector<Double>();
    transient private Vector<Double> flow = new Vector<Double>();
    transient private Vector<Double> density = new Vector<Double>();
    
    public Detector(HashMap<InfraDatas,Object> datas, LaneType ltype, RNode r_node) {
        super(datas);
        detector_id = this.getID();
        lanetype = ltype;
    }
    
    //기본 데이터를 가지고 있는 최종단 노드

    void loadData(Period period) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setRNode(RNode rnode){
        r_node = rnode;
    }
    
    public LaneType getLaneType(){
        return lanetype;
    }
}
