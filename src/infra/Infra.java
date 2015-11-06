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
import java.util.ArrayList;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Infra {
    ArrayList<Corridor> corridors = new ArrayList();
    
    public ArrayList<Corridor> getCorridors(){
        return corridors;
    }
    
    public void load(){
        //set Corridors from DB
        setCorridors();
        //set Rnodes to each Corridor
        setRnodes();
        //preprocess Corridor objects
        preProcessCorridor();
    }

    private void setCorridors() {
        //Add all Corridor into the corridors List
    }

    private void setRnodes() {
        for(Corridor cor : corridors){
            //Search Rnodes in the DB
            
            //Add all Rnodes into the corridor
            
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
