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

import infra.Direction;
import infra.InfraDatas;
import infra.InfraObject;
import infra.type.*;
import java.util.*;
import trafficsimulationanalysis.TempData;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RNode extends InfraObject implements Comparable{
    protected int distanceToDownstreamNode = -1;
    protected int distanceToUpstreamNode = -1;
    
    protected double loc = -1;
    private Corridor corridor = null;
    private Direction direction = Direction.ALL;
    protected RnodeType nodetype = RnodeType.NONE;
    
    public RNode(HashMap<InfraDatas,Object> datas, RnodeType _nodetype){
        super(datas);
        nodetype = _nodetype;
        Double _loc = (Double)getProperty(InfraDatas.LOCATION);
        loc = _loc == null ? -1 : _loc;
        direction = (Direction)getProperty(InfraDatas.DIRECTION);
    }
    
    //Set Corridor
    public void setCorridor(Corridor nc){
        corridor = nc;
    }
    
    @Override
    public String toString(){
        return "ID:"+super.getID()+", Name : "+super.getName()+", Type : "+nodetype.toString()+", Loc : "+loc;
    }

    @Override
    public int compareTo(Object o) {
        RNode comrnode = (RNode)o;
        double _loc = comrnode.getLocation();
        if(loc == _loc)
            return 0;
        else if(loc > _loc)
            return 1;
        else
            return -1;
    }
    
    /**
     * Data list
     */
    
    /**
     * get RNode Location
     * @return double
     */
    public double getLocation(){
        return loc;
    }
    
   /**
    * get Direction
    * @return double
    */
    public Direction getDirection(){
        return direction;
    }
    
    public Corridor getCorridor(){
        return corridor;
    }
    
    public RnodeType getNodeType(){
        return nodetype;
    }
}
