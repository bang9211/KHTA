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

import evaluation.Interval;
import infra.Section;
import infra.infraobject.RNode;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimInterval {
        Section section;
        
        /**
         * Simulation Interval
         */
        private Interval SimulationRunningInterval = Interval.getDefaultSimulationInterval();
        /**
         * Processed interval List included each Simulation Option at each States
         */
        private HashMap<String,StateInterval> states = new HashMap<String, StateInterval>();
        
        private int CurrentRunTime = 0;
        
//        private SimulationGroup simGroup = SimulationGroup.DEFAULT;
        
        /**
         * Default setup
         * Running Interval = 30sec
         * State Interval = 30sec
         * @param s 
         */
        public SimInterval(Section s){
                this(s,Interval.getDefaultSimulationInterval());
        }
        
        /**
         * * Default setup
         * Running Interval = 30sec
         * need to use the setSimulationRunningInterval, if you want to set the RunningInterval
         * @param section
         * @param State Interval
         */
        public SimInterval(Section s, Interval itv){
                this(s,itv,Interval.getDefaultSimulationInterval());
        }
        
        /**
         * 
         * @param s Section
         * @param itv StateInterval
         * @param ritv RunningInteval
         */
        public SimInterval(Section s, Interval itv, Interval ritv){
                section = s;
                SimulationRunningInterval = ritv;
                init(itv.second);
        }
        
        /**
         * Initalization States
         */
        private void init(int interval) {
                for (RNode rn : section.getRNodesWithExitEntrance()) {
                        AddState(rn,interval);
                }
        }

        /**
         * Add State
         * @param rn
         * @param sintv 
         */
        private void AddState(RNode rn, int sintv) {
                states.put(rn.getID(), new StateInterval(sintv,SimulationRunningInterval));
        }
        
        public void UpdateAllStates(SimulationGroup simulationGroup, int second) {
                for(String key : states.keySet()){
                        UpdateStateInterval(key,simulationGroup,second);
                }
        }
        
        public void UpdateStateInterval(RNode rn, SimulationGroup sg, int sintv){
                UpdateStateInterval(rn.getID(),sg,sintv);
        }
        
        public void UpdateStateInterval(String rid, SimulationGroup sg, int sintv) {
                StateInterval state = states.get(rid);
                if(state == null)
                        return;
                
                state.updateInterval(sg,sintv);
        }
        
        public HashMap<String,StateInterval> getStates(){
                return states;
        }
        
        public void setSimulationRunningInterval(Interval intv){
                for(String key : states.keySet()){
                        StateInterval state = states.get(key);
                        state.updateRunningInterval(intv);
                }
                SimulationRunningInterval = intv;
        }        
        public Interval getSimulationRunningInterval(){
                return SimulationRunningInterval;
        }

        public StateInterval getState(String id) {
                return states.get(id);
        }

        /**
         * Always
         * Running Interval smaller than State's Interval
         * State's Interval % Running Interval == 0
         * @return 
         */
        public boolean isCorrect() {
                int rintv = SimulationRunningInterval.second;
                int sitv = 0;
                for(String key : states.keySet()){
                        StateInterval s = states.get(key);
                        for(SimulationGroup sg : SimulationGroup.values()){
                                sitv = s.getIntervalByGID(sg);
                                if(sitv < rintv || sitv % rintv != 0){
                                        printErrMsg(rintv,sitv);
                                        return false;
                                }
                        }
                }
                return true;
        }

        private void printErrMsg(int rintv, int sitv) {
                System.err.println("Interval Error :");
                System.err.println(" - Current Running Interval : "+rintv);
                System.err.println(" - Current State Interval : "+sitv);
                System.err.println(" always Running Interval < State Interval, State Interval % RunningInterval == 0");
        }

        public void setRunTime(int simcount) {
                this.CurrentRunTime = simcount;
        }

        public int getCurrentRunTime() {
                return CurrentRunTime;
        }

//        public void setSimulationGroup(SimulationGroup sg) {
//                simGroup = sg;
//        }
//        
//        public SimulationGroup getSimulationGroup(){
//                return simGroup;
//        }
        
        public SimInterval Clone(){
                SimInterval s = new SimInterval(section);
                s.setSimulationRunningInterval(Interval.get(SimulationRunningInterval.second));
                for(String key : this.getStates().keySet()){
                        StateInterval si = this.getState(key);
                        for(SimulationGroup sg : SimulationGroup.values()){
                                s.UpdateStateInterval(key, sg, si.getIntervalByGID(sg));
                        }
                }
                return s;
        }
}
