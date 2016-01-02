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
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class StateInterval {
        private Interval RUNNING_INTERVAL = Interval.getDefaultSimulationInterval();
        private int DEFAULT_INTERVAL = Interval.getMinTMCInterval();
        private HashMap<Integer,Integer> intervalList = new HashMap<Integer,Integer>();
        public StateInterval(){;}
        public StateInterval(int value, Interval ritv){
                RUNNING_INTERVAL = ritv;
                addIntervalAllGroup(value);
        }
        
        /**
         * Add Interval
         * @param gid
         * @param value 
         */
        public void addInterval(SimulationGroup gid, int value){                
                intervalList.put(gid.getSRC(), value);
        }
        
        public void addIntervalAllGroup(int value){
                for(SimulationGroup sg : SimulationGroup.values()){
                        intervalList.put(sg.getSRC(), value);
                }
        }
        
        /**
         * return Interval By Group ID
         * @param gid
         * @return interval value
         */
        public int getIntervalByGID(SimulationGroup gid){
                Integer value = intervalList.get(gid.getSRC());
                if(value == null)
                        return DEFAULT_INTERVAL;
                else
                        return value;
        }

        /**
         * Update Group's Interval
         * @param sg
         * @param sintv 
         */
        public void updateInterval(SimulationGroup sg, int sintv) {
                if(!hasGroup(sg))
                        return;
                removeGroupInterval(sg);
                addInterval(sg,sintv);
        }
        
        /**
         * has Group?
         * @param sg
         * @return 
         */
        private boolean hasGroup(SimulationGroup sg){
                return intervalList.get(sg.getSRC()) != null;
        }

        
        private void removeGroupInterval(SimulationGroup sg) {
                intervalList.remove(sg.getSRC());
        }

        void updateRunningInterval(Interval intv) {
                RUNNING_INTERVAL = intv;
        }
        
        public Interval getRunningInterval(){
                return RUNNING_INTERVAL;
        }
}
