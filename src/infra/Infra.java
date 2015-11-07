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
import infra.infraobject.Station;
import java.util.ArrayList;
import java.util.HashMap;
import trafficsimulationanalysis.tempMySQL;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Infra {
    ArrayList<Corridor> corridors = new ArrayList();
    tempMySQL sqlserver = null;
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
    public void load(tempMySQL _sqlserver){
        sqlserver= _sqlserver;
        //set Corridors from DB
        System.out.println("Set Corridors....");
        setCorridors();
        System.out.println("Corridors setting completed");
        
        //set Rnodes to each Corridor
        System.out.println("Set Rnodes....");
        setRnodes();
        System.out.println("Rnodes setting completed");
        //set RampMeter
        //Need to addtion
        
        //preprocess Corridor objects
        preProcessCorridor();
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
}
