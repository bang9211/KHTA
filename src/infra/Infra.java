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
import infra.infraobject.RNode;
import infra.infraobject.Station;
import java.awt.PopupMenu;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.KHTAParam;
import util.DataFetcher;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Infra {
    ArrayList<Corridor> corridors = new ArrayList();
    List<Section> sections = new ArrayList();
    DataFetcher sqlserver = null;
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
    public void load(DataFetcher _sqlserver){
        sqlserver= _sqlserver;
        //set Corridors from DB
        System.out.println("Set Corridors....");
        setCorridors();
        System.out.println("Corridors setting completed");
        
        //set Rnodes to each Corridor
        System.out.println("Set Rnodes....");
        setRnodes();
        System.out.println("Rnodes setting completed");
        
        //preprocess Corridor objects
        preProcessCorridor();
        
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
        System.out.println("Set RNodes");
        for(Corridor cor : corridors){
            //Search Rnodes in the DB
            //Add Station
//            System.out.println("Cor ID " +cor.getID() + ", NAME : "+cor.getName());
            for(HashMap<InfraDatas, Object> ss : sqlserver.getStations(cor.getID())){
                Station ns = new Station(ss);
                //Add all Rnodes into the corridor
                cor.addRNode(ns);
            }
            
            //Add Entrance
            //Add Exit
            //Add DMS
            
            //Sort Rnode
            cor.sortAllNode();
        }
        
    }

    private void preProcessCorridor() {
        //set Rnode upstream and downstream node
        //set Rnode upstream and downstream length
        //do I go for it??
    }

    public Corridor getCorridors(String get) {
        for(Corridor c : corridors){
            if(c.getID().equals(get))
                return c;
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
}
