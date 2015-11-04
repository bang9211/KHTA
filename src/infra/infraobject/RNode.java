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
    
    //Location
    protected double Loc = -1;
    
    protected Corridor corridor = null;
    //Direction
    protected Direction direction = Direction.ALL;
    
    protected RnodeType nodetype = RnodeType.NONE;
    
    public RNode(TempData data) {
        super(data.id,data.name);
        Loc = data.Loc;
    }
    
    //Set Corridor
    public void setCorridor(Corridor nc){
        corridor = nc;
    }
    
    @Override
    public String toString(){
        return "ID:"+super.id+", Name : "+super.Name+", Type : "+nodetype.toString()+", Loc : "+Loc;
    }

    @Override
    public int compareTo(Object o) {
        RNode comrnode = (RNode)o;
        Double _loc = comrnode.Loc;
        if(Loc == _loc)
            return 0;
        else if(Loc > _loc)
            return 1;
        else
            return -1;
    }
}
