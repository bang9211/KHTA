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
package infra.simulation;

import infra.infraobject.RNode;
import infra.simobjects.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class State {

   public StateType type;
   public String id;
   public State upstream;
   public State downstream;
   public RNode rnode;
   public StateInterval stateInterval = null;
   public SimInterval simInterval = null;
   public State(){

   }
   public State(String id, RNode rnode, SimInterval simIntv) {
       this();
       this.id = id;
       this.rnode = rnode;
       
       simInterval = simIntv;
       if(simInterval != null)
                stateInterval = simInterval.getState(rnode.getID());
   }

   public boolean hasDetector(SimDetector sd) {
       if (sd == null) {
           return false;
       }
       return this.rnode.hasSimDetector(sd.getID());
   }

   public boolean hasDetector(SimDetector[] sds) {
       if (sds == null) {
           return false;
       }
       for (SimDetector sd : sds) {
           if (this.rnode.hasSimDetector(sd.getID())) {
               return true;
           }
       }
       return false;
   }

   public State getDownStreamState(){
       return this.downstream;
   }

   public String getID(){
       return id;
   }

   public String getLabel(){
       return rnode.getName();
   }
   
   public SimInterval getSimulationInterval(){
           return this.simInterval;
   }
   
   /**
    * get State Interval
    * @return 
    */
   public StateInterval getStateInterval(){
           return this.stateInterval;
   }
   
   public double getStateInterval(SimulationGroup sg){
           return stateInterval.getIntervalByGID(sg);
   }
   
   public double getSimulationRunningInterval(){
           return stateInterval.getRunningInterval().second;
   }
   
   public int getCurrentRunTime(){
           return simInterval.getCurrentRunTime();
   }
   
   public RNode getRNode(){
       return rnode;
   }
}
