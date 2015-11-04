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

import infra.infraobject.*;
import java.util.ArrayList;
import java.util.Random;

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
        Random randomGenerator = new Random();
        Corridor cor = new Corridor("id01","Cor01");
        for(int i=0;i<30;i++){
            int r = randomGenerator.nextInt(4);
            String id = "i"+i;
            String name = "name"+id;
            int loc = randomGenerator.nextInt(4000);
            RNode nrnode = null;
            RNode exnode = null;
//            System.out.println("r = "+r);
            switch(r){
                case 0:
                case 1:
                    nrnode = new Station(new TempData(id,name,loc));
                    break;
                case 2:
                    nrnode = new Entrance(new TempData(id,name,loc));
                    exnode = new Exit(new TempData(id+"ex",name,loc+50));
                    break;
                case 3:
                    nrnode = new DMS(new TempData(id,name,loc));
                    break;
            }
            cor.addRNode(nrnode);
            if(exnode != null)
                cor.addRNode(exnode);
//            System.out.println("insert -> "+nrnode.toString());
        }
        cor.sortAllNode();
        
        for(RNode r : cor.getRNodes()){
            System.out.println(r.toString());
        }
        
        System.out.println("Stations");
        for(RNode r : cor.getStations()){
            System.out.println(r.toString());
        }
        
    }
    
}
