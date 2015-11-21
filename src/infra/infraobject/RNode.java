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

import evaluation.DataLoadOption;
import infra.Direction;
import infra.InfraDatas;
import infra.InfraObject;
import infra.Period;
import infra.simobjects.SimObjects;
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
    
    //ISBUSLANE
    protected boolean isBusLane = false;
    
    //Detectors must include the Mainline, Bus lane and so on
    protected HashMap<String,Detector> detectors = new HashMap<String,Detector>();
    
    public RNode(HashMap<InfraDatas,Object> datas, RnodeType _nodetype){
        super(datas);
        nodetype = _nodetype;
        Double _loc = (Double)getProperty(InfraDatas.LOCATION);
        loc = _loc == null ? -1 : _loc;
        int it = (Integer)super.getProperty(InfraDatas.ISBUSLANE);
        if(it == 1)
            isBusLane = true;
//        direction = (Direction)getProperty(InfraDatas.DIRECTION);
        initDetectors();
    }
    
    //Set Corridor
    public void setCorridor(Corridor nc){
        corridor = nc;
        setDirection(corridor.getDirection());
    }
    
    private void setDirection(Direction d){
        direction = d;
    }
    
    /**
     * Data Load on Detector
     * @param period
     * @param dopt
     * @throws OutOfMemoryError 
     */
    public void loadData(Period period, DataLoadOption dopt) throws OutOfMemoryError
    {
        loadData(period,dopt,null);
    }
    
    /**
     * Data Load on Detector
     * @param period 
     * @param dopt 
     * @param sobj 
     */
    public void loadData(Period period, DataLoadOption dopt, SimObjects sobj) throws OutOfMemoryError{
        for(Detector d : this.detectors.values()){
            if(sobj == null)
                d.loadData(period, dopt);
            else
                d.loadData(period, dopt,sobj);
        }
    }
    
    /**
     * init Detectors
     */
    private void initDetectors() {
        if(nodetype.isDMS()) return;
        if(detectors != null && !detectors.isEmpty()) return;
        if(detectors == null) detectors = new HashMap<String, Detector>();
        
        //init Default Detector
        if(!nodetype.isEntrance()){
            String did = getDetectorID(LaneType.MAINLINE);
            HashMap<InfraDatas, Object> dd = new HashMap();
            dd.put(InfraDatas.ID, did);
            dd.put(InfraDatas.NAME, getDetectorName(LaneType.MAINLINE));
            detectors.put(did, new Detector(dd, LaneType.MAINLINE, this));
            
            //init Additional Lane on Mainlane
            if(isBusLane){
                did = getDetectorID(LaneType.BUS);
                dd.clear();
                dd.put(InfraDatas.ID, did);
                dd.put(InfraDatas.NAME, getDetectorName(LaneType.BUS));
                detectors.put(did, new Detector(dd, LaneType.BUS, this));
            }
            
        }else{ //Entrance Ramp
            HashMap<InfraDatas, Object> dd = new HashMap();
            //Queue Detector
            String did = getDetectorID(LaneType.QUEUE);
            dd.put(InfraDatas.ID, did);
            dd.put(InfraDatas.NAME, getDetectorName(LaneType.QUEUE));
            detectors.put(did, new Detector(dd, LaneType.QUEUE, this));
            //Passage Detector
            dd.clear();
            did = getDetectorID(LaneType.PASSAGE);
            dd.put(InfraDatas.ID, did);
            dd.put(InfraDatas.NAME, getDetectorName(LaneType.PASSAGE));
            detectors.put(did, new Detector(dd, LaneType.PASSAGE, this));
        }
    }
    
    @Override
    public String toString(){
        return "ID:"+super.getID()+", Name : "+super.getName()+", Type : "+nodetype.toString()+", Loc : "+loc;
    }

    /**
     * Temporary Location -> Order
     * Compare with other RNode by Location
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Object o) {
        RNode comrnode = (RNode)o;
        double _loc = comrnode.getLocation();
        NodeOrder no = getCorridor().getOrder();
        
        int ret = 0;
        
        if(loc == _loc)
            ret =  0;
        else if(loc > _loc)
            ret = 1;
        else
            ret = -1;
        
        if(no == NodeOrder.FORWARD)
            return ret;
        else
            return ret * -1;
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
    
    public HashMap<String, Detector> getDetectors(){
        return detectors;
    }

    public void setLocation(double loc) {
        this.loc = loc;
    }

    private String getDetectorID(LaneType lt) {
        return lt.dbsuffix+getID();
    }

    private Object getDetectorName(LaneType laneType) {
        return laneType.dbsuffix+this.getName();
    }
    
}
