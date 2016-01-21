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
import exception.DBException;
import infra.Direction;
import infra.InfraDatas;
import infra.InfraObject;
import infra.Period;
import infra.simobjects.SimObjects;
import infra.type.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.KHTAParam;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RNode extends InfraObject implements Comparable{
    protected int distanceToDownstreamNode = -1;
    protected int distanceToUpstreamNode = -1;
    //Node Order
    private int order = -1;
    protected double loc = -1;
    private Corridor corridor = null;
    private Direction direction = Direction.ALL;
    protected RnodeType nodetype = RnodeType.NONE;
    
    //ISBUSLANE
    protected boolean isBusLane = false;
    
    protected double confidence = -1;     
    
    //lane
    protected int lane = 1;
    
    //Detectors must include the Mainline, Bus lane and so on
    protected HashMap<String,Detector> detectors = new HashMap<String,Detector>();
    //Detectors for Simulation
    protected HashMap<String,Detector> simdetectors = new HashMap<String,Detector>();
    
    /**
     * Simulation Mode check
     * if Simulation mode -> rnode's data is simulation data
     * else -> rnode's data is real data
     */
    private boolean isSimMode = false;
    
    protected boolean isFirstNode = false;
    protected boolean isLastNode = true;
    
    public RNode(HashMap<InfraDatas,Object> datas, RnodeType _nodetype){
        super(datas);
        nodetype = _nodetype;
        Double _loc = (Double)getProperty(InfraDatas.LOCATION);
        loc = _loc == null ? -1 : _loc;
        Integer it = (Integer)super.getProperty(InfraDatas.ISBUSLANE);
        if(it == 1)
            isBusLane = true;
        
        it = (Integer)super.getProperty(InfraDatas.LANE);
        lane = it == null ? -1 : it;
        
        it = (Integer)super.getProperty(InfraDatas.ORDER);
        order = it == null ? -1 : it;
        
//        direction = (Direction)getProperty(InfraDatas.DIRECTION);
        initRealDetectors();
        initSimulationDetectors();
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
    public void loadData(Period period, DataLoadOption dopt, SimObjects sobj){
        if(sobj != null){
            isSimMode = true;
            try {
                for(Detector d : this.simdetectors.values()){
                        d.loadData(period, dopt, sobj);
                }
            } catch (DBException ex) {
                
            }
            getSpeed();
        }else{            
            isSimMode = false;
            
            DetectorThread[] dtlist = new DetectorThread[detectors.size()];
            int cnt = 0;
            for(Detector d : this.detectors.values()){
                dtlist[cnt] = new DetectorThread(d, period, dopt, sobj);
                dtlist[cnt].start();
                cnt ++;
            }

            cnt = 0;
            try {
                while (true) {
                    dtlist[cnt++].join();
                    if (cnt == dtlist.length) {
                        break;
                    }
                }
                //pre processing
                getSpeed();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public double[] getSpeed(){ return getData(TrafficType.SPEED);}
    public double[] getDensity() { return getData(TrafficType.DENSITY);}
    public double[] getFlow() { return getData(TrafficType.FLOW); }
//    public double[] getFlowForAverageLaneFlow() { return getData(TrafficType.FLOWFORAVERAGE); } //modify soobin Jeon 02/15/2012
    public double[] getAverageLaneFlow() { return getData(TrafficType.AVERAGEFLOW); } //modify soobin Jeon 02/15/2012
    public double[] getVolume() { return getData(TrafficType.VOLUME); }
    
    public double[] getData(TrafficType type){
        return makeAdjustData(MakeData(type), type);
    }
    
    /**
     * Make Data file to array
     * @param type
     * @return 
     */
    private double[][] MakeData(TrafficType type){
        HashMap<String,Detector> ds = null;
        if(!isSimMode)
            ds = detectors;
        else
            ds = simdetectors;
        double[][] ddata = new double[ds.size()][];
        int idx = 0;
        for (Detector d : ds.values()) {
            ddata[idx++] = d.getData(type);
//            System.out.println("data["+(idx-1)+"]= "+d.getID()+" - "+ddata[idx-1].length);
        }

        return ddata;
    }
    
    /**
     * make Station data using all detector data
     * @param data
     * @param atype
     * @return 
     */
    private double[] makeAdjustData(double[][] data, TrafficType atype){
        if(data == null || data.length == 0) return null;
        double[] avg = new double[data[0].length];
        double totalValidCount = 0;
        double totalDataCount = 0;
        
        for(int i=0; i<data[0].length; i++) {
            double sum = 0;
            double validCount = 0;            
            for(int detIdx=0;detIdx<data.length; detIdx++)
            {
                /*
                 * Modify to check missing detectors
                 * Check Missing detectors in Station
                 * modify soobin Jeon 02/13/2012
                 */
//                if(ds[detIdx].isMissing()){
//                    System.out.println("Detector="+ds[detIdx].getID()+", isMissing : "+ds[detIdx].isMissing());
//                    continue;
//                }
                
                totalDataCount++;
                
                double v = data[detIdx][i];                        
                
                if(v > 0)
                {                    
                    sum += v;
                    validCount++;
                }else if(atype.isDensity())
                    validCount++;
            }    
            totalValidCount += validCount;
            
            if(validCount > 0) {
                    if(atype.isFlow() || atype.isVolume()){
                        avg[i] = sum;
                    }
                    else if(atype.isAverageFlow() || atype.isDensity()){
//                        System.out.println("Lane Count : "+getLaneCount());
                        avg[i] = sum / getLaneCount();
                    }
                    else{
                        avg[i] = sum/validCount;
                    }
            }///validCount;//this.roundUp(sum / validCount, 2);
            else {
//                System.out.println("valid Count : "+validCount+" missing");
                avg[i] = KHTAParam.MISSING_DATA;
            }            
        }
//        System.out.println("========================================\n");
        this.confidence = totalValidCount / totalDataCount * 100;
//        if(confidence < 50) isMissing = true;
        return avg;
    }
    
    /**
     * init Detectors
     */
    private void initRealDetectors() {
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
    
    private void initSimulationDetectors() {
        if(nodetype.isDMS()) return;
        if(simdetectors != null && !simdetectors.isEmpty()) return;
        
        //init Default Detector
        if(!nodetype.isEntrance()){
            for(int i=0;i<getLaneCount();i++){
                String dnum = String.valueOf(i+1);
                String did = getDetectorID(LaneType.MAINLINE) + dnum;
                String dname = getDetectorName(LaneType.MAINLINE) + dnum;
                HashMap<InfraDatas, Object> dd = new HashMap();
                dd.put(InfraDatas.ID, did);
                dd.put(InfraDatas.NAME, dname);
                simdetectors.put(did, new Detector(dd, LaneType.MAINLINE, this));
            }
        }else{ //
            HashMap<InfraDatas, Object> dd = new HashMap();
            //Queue Detector
            String did = getDetectorID(LaneType.QUEUE);
            dd.put(InfraDatas.ID, did);
            dd.put(InfraDatas.NAME, getDetectorName(LaneType.QUEUE));
            simdetectors.put(did, new Detector(dd, LaneType.QUEUE, this));
            //Passage Detector
            dd.clear();
            did = getDetectorID(LaneType.PASSAGE);
            dd.put(InfraDatas.ID, did);
            dd.put(InfraDatas.NAME, getDetectorName(LaneType.PASSAGE));
            simdetectors.put(did, new Detector(dd, LaneType.PASSAGE, this));
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
    
    /**
     * for Real Detectors
     */
    public HashMap<String, Detector> getDetectors(){
        return detectors;
    }
    
    public Detector[] getDetectorList(){
        Vector<Detector> dlist = new Vector<Detector>();
        for(Detector d : this.detectors.values()) {
            dlist.add(d);
        }
        return dlist.toArray(new Detector[dlist.size()]);
    }
    
    public boolean hasDetector(String id) {
        for(Detector d : this.detectors.values()){
            if(d.getID().equals(id)) return true;
        }
        return false;
    }
    //-------------------------------------------------------------//
    
    /**
     * for Sim Detectors
     */
    
    public HashMap<String, Detector> getSimDetectors(){
        return simdetectors;
    }
    
    public Detector[] getSimDetectorList(){
        Vector<Detector> dlist = new Vector<Detector>();
        for(Detector d : this.simdetectors.values()) {
            dlist.add(d);
        }
        return dlist.toArray(new Detector[dlist.size()]);
    }
    
    public boolean hasSimDetector(String id) {
        for(Detector d : this.simdetectors.values()){
            if(d.getID().equals(id)) return true;
        }
        return false;
    }
    //-------------------------------------------------------------//

    public void setLocation(double loc) {
        this.loc = loc;
    }

    private String getDetectorID(LaneType lt) {
        return lt.dbsuffix+getID();
    }

    private String getDetectorName(LaneType laneType) {
        return laneType.dbsuffix+this.getName();
    }
    
    public boolean isMissing() {
        Iterator<Detector> itr = this.detectors.values().iterator();
        while(itr.hasNext()) {
            Detector d = itr.next();
            if(!d.isMissing()) {
                return false;
            }
        }
        return true;
    }
    
    public int getLaneCount(){
        return lane;
    }

    public void setFirstNode() {
        isFirstNode = true;
    }

    public void setLastNode() {
        isLastNode = true;
    }
    
    public boolean isFirstNode() {
        return isFirstNode;
    }
    
    public boolean isLastNode() {
        return isLastNode;
    }
    
    public double getConfidence() {
        return this.roundUp(this.confidence, 1);
    }
    
    public int getOrder(){
        return order;
    }
}
