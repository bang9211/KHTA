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
import infra.type.RoadType;
import java.util.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Corridor extends InfraObject{
    private Direction dir;
    
    //Road Type Code
    protected RoadType roadtype = null;
    private ArrayList<RNode> rnodes = new ArrayList();
    private ArrayList<Station> stations = new ArrayList();
    private ArrayList<Entrance> entrances = new ArrayList();
    private ArrayList<Exit> exits = new ArrayList();
    private ArrayList<DMS> dms = new ArrayList();
    
    private String shortName = null; //short name
    private String StartName = null; //Start Location Name
    private String EndName = null; //End Location Name
    private String StartNode = null; //Start Node
    private String EndNode = null; // End Node
    private int freeSpeed = -1;
    private int JamSpeed = -1;
    
    
    
    public Corridor(String _id, String _name){
        super(_id,_name);
    }
    
    //Add RNode
    public void addRNode(RNode n){
        n.setCorridor(this);
        rnodes.add(n);
        ClassifyNodes(n);
        Collections.sort(rnodes);
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
        Collections.sort(stations);
        Collections.sort(entrances);
        Collections.sort(exits);
        Collections.sort(dms);
    }
}
