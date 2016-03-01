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
public enum AccCheckThreshold {
    BASEDCHECKTHRESHOLD(0,"ai,t-2, ai,t-1, ai,t <= X"),
    TRENDCHECKTHRESHOLD(1,"ai,t-2 >= ai,t-1 >= ai,t, ai,t <= X"),
    NEWTREND(2,"(ai,t-3, ai,t-2, ai,t-1, ai,t) <= X, count=3");
    
    int sid;
    String name;
    AccCheckThreshold(int _sid, String _name){
        sid = _sid;
        name = _name;
    }
    
    public int getSID(){
        return sid;
    }
    
    public static AccCheckThreshold getbyID(int id){
        for(AccCheckThreshold sa : AccCheckThreshold.values()){
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
    
    boolean isBASEON(){return this == this.BASEDCHECKTHRESHOLD;}
    boolean isTRENDON(){return this == this.TRENDCHECKTHRESHOLD;}
    boolean isNewTRENDON(){return this==this.NEWTREND;}
}
