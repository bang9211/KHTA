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
package trafficsimulationanalysis;

import infra.Infra;
import infra.infraobject.*;
import java.util.ArrayList;
import java.util.Random;
import infra.InfraDatas;
/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class TrafficSimulationAnalysis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        tempMySQL tq = new tempMySQL(1000);
        tq.setup();
        
        Infra infra = Infra.getInstance();
        infra.load(tq);
        
        TrafficSimulationAnalysis ta = new TrafficSimulationAnalysis();
        ta.infratest();
    }
    
    public void infratest(){
        Infra infra = Infra.getInstance();
        for(Corridor cor : infra.getCorridors()){
            System.out.println("Corridor Name:"+cor.getName()
                    +"\tID:"+cor.getID()
                    +"\tDirection:"+cor.getDirection());
            for(RNode st : cor.getRNodes()){
                System.out.println("-- RnodeID:"+st.getID()
                        +"\tname:"+st.getName()
                        +"\ttype:"+st.getNodeType().toString()
                        +"\tLocation:"+st.getLocation()
                        +"\t\tDirection:"+st.getDirection().toString());
            }
            
        }
        
    }
    
}
