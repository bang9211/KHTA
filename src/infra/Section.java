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
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
        
        for(RNode cr : corridor.getRNodes()){
            for(String rid : stringList){
                if(cr.getID().equals(rid))
                    AddRNode(cr);
            }
        }
        
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
        for(RNode cr : corridor.getRNodes()){
            if(!addstart && cr.getID().equals(snid))
                addstart = true;
            
            if(addstart && cr.getID().equals(enid)){
                addstart = false;
                isLast = true;
            }
            
            if(addstart || isLast){
                AddRNode(cr);
                if(isLast){
                    isLast = false;
                }
            }
        }
        
        if(section.isEmpty())
            throw new Exception("RNode is empty");
    }
    
    @Override
    public String toString(){
        return getName();
    }
}
