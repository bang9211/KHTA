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

import infra.*;
import infra.type.NodeOrder;
import infra.type.RoadType;
import java.util.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Corridor extends InfraObject{
    private ArrayList<RNode> rnodes = new ArrayList();
    private ArrayList<Station> stations = new ArrayList();
    private ArrayList<Entrance> entrances = new ArrayList();
    private ArrayList<Exit> exits = new ArrayList();
    private ArrayList<DMS> dms = new ArrayList();
    
    private NodeOrder nodeorder = NodeOrder.FORWARD;
    
    private RoadType rodetype = null;
    private Direction direction = null;
    
    private double totalLength = 0;
    
    private boolean isLoopLine = false;
    //private String shortName = null; //short name
    //private String StartName = null; //Start Location Name
    //private String EndName = null; //End Location Name
    //private String StartNode = null; //Start Node
    //private String EndNode = null; // End Node
    //private int freeSpeed = -1;
    //private int JamSpeed = -1;
    
    public Corridor(HashMap<InfraDatas, Object> datas){
        super(datas);
        rodetype = (RoadType)getProperty(InfraDatas.ROADTYPE);
        direction = (Direction)getProperty(InfraDatas.DIRECTION);
        //shortName =  (String)getProperty(InfraDatas.SHORTNAME);
        //StartName =  (String)getProperty(InfraDatas.STARTNAME);
        //EndName = (String)getProperty(InfraDatas.ENDNAME);
        //StartNode = (String)getProperty(InfraDatas.STARTNODE);
        //EndNode = (String)getProperty(InfraDatas.ENDNODE);
        //Integer temp;
        //temp = (Integer)getProperty(InfraDatas.FREESPEED);
        //freeSpeed = temp == null ? -1 : temp;
        //temp = (Integer)getProperty(InfraDatas.JAMSPEED);
        //JamSpeed = temp == null ? -1 : temp;
        
        setLoopLine();
    }
    
    //Add RNode
    public void addRNode(RNode n){
        n.setCorridor(this);
        rnodes.add(n);
        ClassifyNodes(n);
        
//        Collections.sort(rnodes);
    }
    
    //get Rnodes
    public ArrayList<RNode> getRNodes(){
        return rnodes;
    }
    
    //get stations
    public ArrayList<Station> getStations(){
        return stations;
    }
    
    //get Entrances
    public ArrayList<Entrance> getEntrances(){
        return entrances;
    }
    
    //get Exits
    public ArrayList<Exit> getExits(){
        return exits;
    }
    
    //get DMS
    public ArrayList<DMS> getDMS(){
        return dms;
    }
    
    //Classify the Node to each List
    private void ClassifyNodes(RNode n) {
        if(n.nodetype.isStation())
            stations.add((Station)n);
        else if(n.nodetype.isEntrance())
            entrances.add((Entrance)n);
        else if(n.nodetype.isExit())
            exits.add((Exit)n);
        else if(n.nodetype.isDMS())
            dms.add((DMS)n);
    }
    
    //Sort All Collections
    public void sortAllNode(){
        Collections.sort(rnodes);
        Collections.sort(stations);
        Collections.sort(entrances);
        Collections.sort(exits);
        Collections.sort(dms);
    }
    
      /**
     * get Road Type
     * @return RoadType
     */
    public RoadType getRoadType(){
        return this.rodetype;
    }
    
    public Direction getDirection(){
        return this.direction;
    }
    
    /*
    public String getShortName(){
        return this.shortName;
    }
    
    public String getStartName(){
        return this.StartName;
    }
    
    public String getEndName(){
        return this.EndName;
    }
    
    public String getStartNode(){
        return this.StartNode;
    }
    
    public String getEndNode(){
        return this.EndNode;
    }
    
    public int getFreeSpeed(){
        return this.freeSpeed;
    }
    
    public int getJAMSpeed(){
        return this.JamSpeed;
    }
    */
    
    @Override
    public String toString(){
        return this.getName();
    }

    public void setOrder(NodeOrder nodeOrder) {
        nodeorder = nodeOrder;
    }
    
    public NodeOrder getOrder(){
        return nodeorder;
    }
    
    public void setTotalLength(double l){
        totalLength = l;
    }

    public double getTotalLength() {
        return totalLength;
    }

    private void setLoopLine() {
        final String s_T = "순환";
        if(getName().contains(s_T))
            this.isLoopLine = true;
        else
            this.isLoopLine = false;
    }
    
    public boolean isLoopLine(){
        return isLoopLine;
    }
}
