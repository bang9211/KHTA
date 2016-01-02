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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum SimulationGroup {
        DEFAULT(0,"Default Simulation"),
        VSL(1,"Variable Speed Limit"),
        Meter(2,"RampMetering");
        private int src;
        private String desc;
        
        SimulationGroup(int _src, String _desc){
                src = _src;
                desc = _desc;
        }
        
        public int getSRC(){
                return src;
        }
        
        public String getDesc(){
                return desc;
        }
}
