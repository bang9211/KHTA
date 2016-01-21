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

import infra.infraobject.Corridor;
import infra.infraobject.Detector;
import infra.infraobject.Entrance;
import infra.infraobject.Exit;
import infra.infraobject.RNode;
import infra.infraobject.Station;
import infra.type.NodeOrder;
import infra.type.StationType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.KHTAParam;
import util.InfraDataFetcher;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Infra {
    ArrayList<Corridor> corridors = new ArrayList();
    List<Section> sections = new ArrayList();
    InfraDataFetcher sqlserver = null;
    private static Infra infra = new Infra();
    public ArrayList<Corridor> getCorridors(){
        return corridors;
    }
    
    public static Infra getInstance(){
        return infra;
    }
    
    /**
     * Lod Infra Data from DB
     * @param _sqlserver 
     */
    public void load(InfraDataFetcher _sqlserver){
        sqlserver= _sqlserver;
        //set Corridors from DB
        System.out.println("Set Corridors....");
        setCorridors();
        System.out.println("Corridors setting completed");
        
        //set Rnodes to each Corridor
        System.out.println("Set Rnodes....");
        setRnodes();
        System.out.println("Rnodes setting completed");
        
        
        System.out.println("Section Loading..");
        //Load Section
        loadSection();
        System.out.println("Section Load Completed");
    }

    /**
     * Set Corridors from DB
     */
    private void setCorridors() {
        //Add all Corridor into the corridors List
        ArrayList<HashMap<InfraDatas, Object>> cors = sqlserver.getCorridors();
        for(HashMap<InfraDatas, Object> c : cors){
            Corridor ncor = new Corridor(c);
            corridors.add(ncor);
        }
    }

    /**
     * set RNodes
     * - Station (Complete)
     * - Entrance
     * - Exit
     * - DMS
     */
    private void setRnodes() {
        for(Corridor cor : corridors){
            RNode prevRNode = null;
            //Search Rnodes in the DB
            //Add Station
            for(HashMap<InfraDatas, Object> ss : sqlserver.getStations(cor.getID())){
                StationType vdstype = (StationType)ss.get(InfraDatas.STATION_TYPE);
                if(vdstype == null)
                    continue;
                RNode nn = null;
                if(vdstype.isRoadVDS())
                     nn = new Station(ss);
                else if(vdstype.isRamp()){
                    if (prevRNode != null) {
                        if (prevRNode.getNodeType().isExit()) {
                            nn = new Entrance(ss);
                        } else{
                            nn = new Exit(ss);
                        }
                    }
                }
                //Add all Rnodes into the corridor
                if(nn != null){
                    cor.addRNode(nn);
                    prevRNode = nn;
                }
            }
            
            if(cor.getRNodes().isEmpty())
                continue;
            //Add Entrance
            //Add Exit
            //Add DMS
//            System.out.println("Cor : "+cor.getName());
            //set Rnode Direction
//            System.out.println("Setting up the Rnode Direction..");
            setRNodeDirection(cor);
            //re-alignment
//            System.out.println("Alignment RNodes..");
            cor.sortAllNode();
            //set RNodeName(Station)
//            System.out.println("Insert RNode Name..");
            setRNodeName(cor);
            //Modify RNode Location
//            System.out.println("RNode Distance Calibration..");
            modifyRNodeLocation(cor);
            setCorridorTotalLength(cor);
            //set First and Last Node info
            PinNodeinfo(cor);
        }
        
    }
    
    /**
     * Modify RNode Location
     * if Direction is reverse
     * @param cor 
     */
    private void modifyRNodeLocation(Corridor cor){
        if(cor.getOrder().isFORWARD()) return;
        Station ss = cor.getStations().get(0);
        double sLoc = ss.getStartLocation() <= 0 ? ss.getLocation() : ss.getStartLocation();
        double cLoc = 0;
        double csloc = 0;
        double celoc = 0;
        Station cs = null;
            
        for(RNode r : cor.getRNodes()){
            cLoc = r.getLocation();
            r.setLocation(Math.round(Math.abs(sLoc - cLoc)*100d)/100d);
            
            if(r.getNodeType().isStation()){
                cs = (Station)r;
                csloc = cs.getStartLocation();
                celoc = cs.getEndLocation();
                cs.setLocation(Math.round(Math.abs(sLoc - csloc)*100d)/100d
                        , Math.round(Math.abs(sLoc - celoc)*100d)/100d);
            }
        }
    }

    /**
     * Set RNode Direction
     * FORWARD
     * REVERSE
     * @param cor 
     */
    private void setRNodeDirection(Corridor cor) {
        //Re Processing RNode
        if(cor.getRNodes().isEmpty()) return;
        ArrayList<RNode> rnodes = cor.getRNodes();
        Station firstStation = getFirstStation(rnodes);
        Station lastStation = getLastStation(rnodes);
//        System.out.println("first Station : "+firstStation.getName());
//        System.out.println("last Station : "+lastStation.getName());
        if(firstStation.getLocation() > lastStation.getLocation()){
            cor.setOrder(NodeOrder.REVERSE);
        }else{
            cor.setOrder(NodeOrder.FORWARD);
        }
    }
    
    /**
     * re set-up the Rnodes Name
     * @param cor 
     */
    private void setRNodeName(Corridor cor) {
        int ncnt = 0;
        String secid = null;
        int nodecnt = 0;
        int MaxRNode = cor.getRNodes().size()-1;
        for(RNode r : cor.getRNodes()){
            if(!r.getNodeType().isStation())
                continue;

            Station ns = (Station)r;
            //insert Name
            if(secid == null || !secid.equals(ns.getDBSectionID())){
                secid = ns.getDBSectionID();
                ncnt = 0;
            }
            if(nodecnt == MaxRNode)
                ns.setName(AdjustName(ns.getSectionName(), -1));
            else
                ns.setName(AdjustName(ns.getSectionName(), ncnt++));
            nodecnt ++;
        }
    }
    
    /**
     * get FirstStation on Corridor by order
     * @param rnodes
     * @return 
     */
    private Station getFirstStation(ArrayList<RNode> rnodes){
        //find order 1 and last order
        Station firstStation = null;
        for (RNode r : rnodes) {
            if (r.getNodeType().isStation()) {
                Station cs = (Station) r;
                if (cs.getOrder() == 0) {
                    continue;
                }

                if (firstStation == null) {
                    firstStation = cs;
                } else {
                    if (firstStation.getOrder() > cs.getOrder()) {
                        firstStation = cs;
                    }
                }
            }
        }
        
        return firstStation;
    }
    
    /**
     * get lastStation on Corridor by order
     * @param rnodes
     * @return 
     */
     private Station getLastStation(ArrayList<RNode> rnodes){
        //find order 1 and last order
        Station lastStation = null;
        for (RNode r : rnodes) {
            if (r.getNodeType().isStation()) {
                Station cs = (Station) r;
                if (cs.getOrder() == 0) {
                    continue;
                }

                if (lastStation == null) {
                    lastStation = cs;
                } else {
                    if (cs.getStationType().isRoadVDS() && lastStation.getOrder() < cs.getOrder()) {
                        lastStation = cs;
                    }
                }
            }
        }
        
        return lastStation;
    }
    
    /**
     * Adjust the Station Name
     * @param sectionName
     * @param cnt
     * @return 
     */
    private String AdjustName(String sectionName, int cnt) {
        String spname = cnt != -1 ? sectionName.split(" - ")[0] : sectionName.split(" - ")[1];
        String num = cnt <= 0 ? "" : "_"+cnt;
        if(spname != null)
            return spname+num;
        else
            return sectionName+num;
    }
    
    public Corridor getCorridors(String get) {
        for(Corridor c : corridors){
            if(c.getID().equals(get))
                return c;
        }
        return null;
    }
    
    public Station getStation(String get){
        for(Corridor c : corridors){
            for(Station s : c.getStations()){
                if(s.getID().equals(get))
                    return s;
            }
        }
        return null;
    }
    
    public Detector getSimulationDetector(String detector_id) {
        for(Corridor c : corridors){
            for(Station s : c.getStations()){
                for(Detector d : s.getSimDetectorList()){
                    if(d.getID().equals(detector_id))
                        return d;
                }
            }
        }
        return null;
    }

    /**
     * Create Section Data
     * @param sname
     * @param corid
     * @param snid
     * @param enid
     * @throws SectionCreationException
     * @throws RNodeCreationException
     * @throws Exception 
     */
    public void CreateSection(String sname, String corid, String snid, String enid) throws Exception{
        if(hasSections(sname)){
            throw new Exception("Section Name is already used");
        }
        
        Section ns = new Section(sname, "", corid);
        ns.createRNodes(snid, enid);
        sections.add(ns);
        ns.save();
        System.out.println("Saving Section : "+ns.getName());
        for(RNode rn : ns.getRNodes()){
            System.out.println("RN : "+rn.getID());
        }
        
    }

    /**
     * has Sections in Infra
     * @param sname
     * @return 
     */
    private boolean hasSections(String sname) {
        if(sections.isEmpty())
            return false;
        else{
            for(Section s : sections){
                if(s.getName().equals(sname))
                    return true;
            }
        }
        return false;
    }

    /**
     * Load Section from the section directory
     */
    private void loadSection() {
        sections.clear();
        File file = new File(KHTAParam.SECTION_DIR);
        File[] files = file.listFiles();
        if(files == null) return;
        
        for(int i=0;i<files.length;i++){
            Section s = Section.load(files[i].getAbsolutePath());
            if(s == null) continue;
            if(s != null) sections.add(s);
            else{
                System.err.println("\""+files[i].getName()+"\" can not be loaded to section");
            }
        }
    }

    /**
     * get All Sections
     * @return Section[]
     */
    public Section[] getSections() {
        Section[] secs = new Section[sections.size()];
        for(int i=0;i<sections.size();i++){
            secs[i] = sections.get(i);
        }
        return secs;
    }

    /**
     * Delete Section from infra
     * @param ds 
     */
    public void deleteSection(Section ds) {
        for(int i=0;i<sections.size();i++){
            if(sections.get(i).getName().equals(ds.getName())){
                sections.remove(i);
                break;
            }
        }
    }

    /**
     * set Total Length
     * @param cor 
     */
    private void setCorridorTotalLength(Corridor cor) {
        Station ls = (Station)cor.getStations().get(cor.getStations().size()-1);
        if(ls == null) return;
        
        if(ls.getEndLocation() != 0)
            cor.setTotalLength(ls.getEndLocation());
        else
            cor.setTotalLength(ls.getLocation());
    }

    /**
     * set First and Last Node
     * @param cor 
     */
    private void PinNodeinfo(Corridor cor) {
        RNode fnode = cor.getRNodes().get(0);
        RNode lnode = cor.getRNodes().get(cor.getRNodes().size()-1);
        
        fnode.setFirstNode();
        lnode.setLastNode();
    }
}
