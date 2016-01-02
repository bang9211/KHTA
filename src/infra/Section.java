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
package infra;

import evaluation.DataLoadOption;
import infra.infraobject.Corridor;
import infra.infraobject.RNode;
import infra.infraobject.RNodeThread;
import infra.infraobject.Station;
import infra.simobjects.SimObjects;
import infra.type.RnodeType;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.KHTAParam;
import util.PropertiesWrapper;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Section implements Serializable{
    public static final String SECTION_NAME = "section.name";
    public static final String SECTION_DESC = "section.desc";
    public static final String SECTION_RNODES = "section.rnodes";
    public static final String SECTION_CORRIDOR = "section.corridor";
    
    private transient Infra infra;
    private PropertiesWrapper prop;
    private Corridor corridor = null;
    private List<RNode> section = new ArrayList<RNode>();
    private ArrayList<Station> stations = new ArrayList<Station>();
    private String name;
    private String desc;
    
    
    public Section(String name, String desc, String corid){
        this.name = name;
        this.desc = desc;
        prop = new PropertiesWrapper();
        infra = Infra.getInstance();
        corridor = setCorridor(corid);
    }
    
    public void save() throws Exception{
        File sectionChacheDir = new File(KHTAParam.SECTION_DIR);
        if(!sectionChacheDir.mkdir() && !sectionChacheDir.exists()){
            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n"+sectionChacheDir);
        }
        
        prop.put(SECTION_NAME, getName());
        prop.put(SECTION_DESC, getDesc());
        prop.put(SECTION_RNODES, getRnodeIds());
        prop.put(SECTION_CORRIDOR, getCorridorID());
        String ename = getCacheFileName(name);
        String filename = KHTAParam.SECTION_DIR + File.separator + ename;
        prop.save(filename, "Section : "+this.name);
    }
    
    public static Section load(String filepath){
        try{
            PropertiesWrapper prop = PropertiesWrapper.load(filepath);
            Section s = new Section(prop.get(Section.SECTION_NAME)
                    , prop.get(Section.SECTION_DESC)
                    , prop.get(Section.SECTION_CORRIDOR));
            s.setNodes(prop.getStringList(Section.SECTION_RNODES));
            return s;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Loads traffic data for all stations in section
     */
    public void loadData(Period period, DataLoadOption dopt) throws OutOfMemoryError {
        loadData(period,dopt,null);
    }
    
//    private int rnodesize = section.size();
    private int stidx = 0;
    private int tidx = 0;
    private int qs = 0;
    private final int QueueSize = 30;
    
    public void loadData(final Period period, final DataLoadOption dopt, final SimObjects sobj) throws OutOfMemoryError{
        System.out.println("Load Section Data..");
        //initialization
        stidx = 0;
        tidx = 0;
        qs = 0;
        RNodeThread.Callback cbmsg = new RNodeThread.Callback() {
            @Override
            public synchronized void IsLoaded(RNode r) {
//                System.out.println("Load RNode - "+r.getID()+" ["+(stidx+1)+"/"+section.size()+"]");
                loadRNode(period, dopt, sobj, this);
                stidx ++;
            }
        };
        loadRNode(period, dopt, sobj, cbmsg);
        
        while(stidx < section.size()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Section.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        while(tidx < rnodesize){
//            
////            RNodeThread
//        }
    }
    
    private synchronized void loadRNode(Period period, DataLoadOption dopt, SimObjects sobj, RNodeThread.Callback cbmsg){
        if(qs != 0)
            qs--;
//        System.out.println("Load RNode 11"+"Qsize : "+QueueSize+", rnodesize : "+section.size()+", ("+qs+","+tidx+")");
        while(qs < QueueSize && tidx < section.size()){
            RNode r = section.get(tidx);
            RNodeThread rn = new RNodeThread(r, period, dopt, sobj);
            rn.setCallback(cbmsg);
            rn.start();
//            System.out.println("RNode["+qs+"] :"+r.getID()+" Start!");
            qs ++;
            tidx ++;
        }
    }
    
    private int LoadCount = 0;
    public void loadData_old(Period period, DataLoadOption dopt, SimObjects sobj) throws OutOfMemoryError{
        System.out.println("Load Section Data..");
        int QueueCount = 10;
        int cnt = 0;
        int fcnt = 0;
        LoadCount = 1;
        RNodeThread[] rlist = new RNodeThread[QueueCount];
        
        RNodeThread.Callback cbmsg = new RNodeThread.Callback() {
            @Override
            public void IsLoaded(RNode r) {
                System.out.println("Load RNode - "+r.getName()+" ["+(LoadCount++)+"/"+section.size()+"]");
            }
        };
        
        for(RNode s : section){
            rlist[cnt] = new RNodeThread(s, period, dopt, sobj);
            rlist[cnt].setCallback(cbmsg);
            cnt ++;
            fcnt++;
            if(cnt % QueueCount == 0 || fcnt == section.size()){
                StartLoad(rlist, period, dopt, sobj);
                rlist = new RNodeThread[QueueCount];
                cnt = 0;
            }
            
        }
    }
    
    private void StartLoad(RNodeThread[] rlist, Period period, DataLoadOption dopt, SimObjects sobj){
        for(int i=0;i<rlist.length;i++) {
            if(rlist[i] == null)
                continue;
            else
                rlist[i].start();
        }
        
        int cnt = 0;
        try {
            while (true) {
                if(rlist[cnt] != null){
                    rlist[cnt].join();
                }
                
                cnt ++;
                
                if (cnt == rlist.length) {
                    break;
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * get Rnode Ids for Save
     * @return 
     */
    private List<String> getRnodeIds() {
        List<String> rid = new ArrayList();
        for(RNode r : section){
            rid.add(r.getID());
        }
        return rid;
    }
    
    public Station[] getStations(){
        Station[] s = new Station[stations.size()];
        return stations.toArray(s);
    }

    /**
     * File Name
     * @param name
     * @return 
     */
    public static String getCacheFileName(String name) {
        return name;
    }

    /**
     * set Rnode based on Rnode List
     * @param stringList
     * @throws NoneCorridorException 
     */
    private void setNodes(List<String> stringList) throws Exception {
        if(corridor == null)
            throw new Exception("Corridor is null");
        
        for(String rid : stringList){
            for(RNode cr : corridor.getRNodes()){
                if(cr.getID().equals(rid))
                    AddRNode(cr);
            }
        }
        
        //set Stations
        setStation();
        
        setStationOrganization();
        
        if(section.isEmpty())
            throw new Exception("RNode is empty");
    }
    
    /**
     * Add All RNode
     * @param rnodes 
     */
    public void AddRNodes(ArrayList<RNode> rnodes){
        for(RNode n : rnodes){
            AddRNode(n);
        }
    }
    
    /**
     * Add RNode
     * @param rnode 
     */
    public void AddRNode(RNode rnode){
        if(corridor == null)
            corridor = rnode.getCorridor();
        section.add(rnode);
    }

    /**
     * get Corridor ID
     * @return 
     */
    private String getCorridorID() {
        return corridor.getID();
    }

    /**
     * set Corridor
     * @param get
     * @return 
     */
    private Corridor setCorridor(String get) {
        return infra.getCorridors(get);
//        if(corridor == null)
//            System.err.println("There is no corridor with ID : "+get);
    }
    
    public List<RNode> getRNodes(){
        return section;
    }

    /**
     * RNode Creation for Creating new Section
     * @param snid
     * @param enid
     * @throws RNodeCreationException 
     */
    void createRNodes(String snid, String enid) throws Exception{
        if(corridor == null)
            throw new Exception("Corridor is Empty");
        
        boolean addstart = false;
        boolean isLast = false;
        for(int i=0;i<corridor.getRNodes().size();i++){
            RNode cr = corridor.getRNodes().get(i);
            if(!addstart && cr.getID().equals(snid))
                addstart = true;
            
            if(addstart && cr.getID().equals(enid)){
                addstart = false;
                isLast = true;
            }
            
            if(addstart || isLast){
                AddRNode(cr);
                if(isLast){
                    break;
                }
            }
            
            if(i == corridor.getRNodes().size()-1 && isLast == false)
                i = 0;
        }

        if(section.isEmpty())
            throw new Exception("RNode is empty");
        else{
            setStation();
            setStationOrganization();
        }
            
    }
    
    private void setStationOrganization() {
        if(stations.size() == 0)
            return;
        
        Station upStation = stations.get(0);
        Station cstation = null;
        double distance = 0;
        for(int i=1;i<stations.size();i++){
            cstation = stations.get(i);
            upStation.setDownstreamStation(name, cstation);
            
            /**
             * 순환선의 경우 마지막 노드가 끝나고 첫번째 노드가 다시 시작할 때 거리가 0부터 시작하므로 따로 계산해줘야 함
             * End Node -> Start Node 일때
             * |------End Node------|  |------Start Node------|
             * SL                  EL==SL                    EL
             * Distance End Node and Start Node => EL - End Node + StartNode - SL
             * SL = StartLocation
             * EL = EndLocation
             */
            if(cstation.getLocation() != upStation.getLocation() && upStation.getOrder() > cstation.getOrder()){
                double cstation_StartLoc = 0;
                if(!cstation.isFirstNode()){
                    RNode fn = corridor.getStations().get(0);
                    cstation_StartLoc = fn.getLocation();
//                    System.out.println("firstStation : "+fn.getName()+", "+cstation_StartLoc);
                    distance = (Math.round((cstation.getLocation() - cstation_StartLoc)*100d)/100d);
                }else
                    distance = (Math.round((upStation.getEndLocation() - upStation.getLocation() + cstation.getLocation())*100d)/100d);
                
            }
            else
                distance = Math.round(Math.abs(cstation.getLocation() - upStation.getLocation())*100d)/100d;
            
            upStation.setDistanceToDownstreamStation(name, distance);
            cstation.setDistanceToUpstreamStation(name, distance);
            upStation = cstation;
        }
    }
    
    @Override
    public String toString(){
        return getName();
    }
    
    public int getFeetInSection(Station upstation, Station downstation)
    {
        Station[] stations = this.getStations();
        int distance = 0;
        boolean isStarted = false;
        for(int i=0; i<stations.length-1; i++)
        {
            if(stations[i].equals(upstation)) isStarted = true;
            if(isStarted) {
                distance += stations[i].getDistanceToDownstreamStation(this.name);
                if(stations[i+1].equals(downstation)) break;
            }
        }
        return distance;
    }

    private void setStation() {
        for(RNode r : section){
            if(r.getNodeType() == RnodeType.STATION){
                Station station = (Station)r;
                stations.add(station);
            }
        }
    }
    
    public RNode[] getRNodesWithExitEntrance()
    {      
        List<RNode> fsection = section;
        
        List<RNode> list = new ArrayList<RNode>();
        
        for(RNode rn : fsection) {                
            
            if(rn.getNodeType().isStation())
                list.add(rn);
            else if(rn.getNodeType().isEntrance())
                    list.add(rn);
            else if(rn.getNodeType().isExit())
                    list.add(rn);
        }
        
        return list.toArray(new RNode[list.size()]);
    }
}
