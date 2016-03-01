/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
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
package plugin.simulation.VSL.algorithm;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum BottleneckSpeed {
    BASEDBOTTLENECKSPEED(0,"ui,t <= X"),
    TRENDBOTTLENECKSPEED(1,"ui,t-2, ui,t-1, ui,t <= X");
    
    int sid;
    String name;
    BottleneckSpeed(int _sid, String _name){
        sid = _sid;
        name = _name;
    }
    
    public int getSID(){
        return sid;
    }
    
    public static BottleneckSpeed getbyID(int id){
        for(BottleneckSpeed sa : BottleneckSpeed.values()){
            if(sa.getSID() == id){
                return sa;
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    boolean isBASEON(){return this == this.BASEDBOTTLENECKSPEED;}
    boolean isTRENDON(){return this == this.TRENDBOTTLENECKSPEED;}
}
