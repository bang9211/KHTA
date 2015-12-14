/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth, US) and
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

package evaluation;

/**
 *
 * @author Chongmyung Park
 */
public enum ContourType {
    
    SPEED("Station Speed"),
    TOTAL_FLOW("Station Total Flow"),
    OCCUPANCY("OCCUPANCY"),
    DENSITY("DENSITY"),
    TT("TRAVEL TIME"),
    STT("Snapshot Travel Time");

    String str;
    
    ContourType(String s){
            str = s;
    }

        @Override
    public String toString(){
            return str;
    }
        
    public boolean isSpeedContour() { 
        return (this == SPEED); 
    }
    public boolean isTotalFlowContour() { 
        return (this == TOTAL_FLOW);     
    }    
    public boolean isAverageFlowContour() { 
        return (this == OCCUPANCY); 
    }        
    public boolean isDensityContour() { 
        return (this == DENSITY); 
    }
    public boolean isTTContour(){
            return (this == TT);
    }
    public boolean isSTTContour(){
            return (this == STT);
    }
}
