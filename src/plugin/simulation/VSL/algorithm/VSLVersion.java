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

import infra.simulation.StationState;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum VSLVersion {
    NEWVERSIONFULLSEARCH_PRO(4,"Travel Time based Variable Parameter"),
//    NEWVERSIONFULLSEARCH(3,"New Version_Full_Search"),
//    NEWVERSION(2,"New Version_old"),
    OLDVERSION(1,"Original Version Constant Parameter");

    int vid;
    String vlabel;
    
    VSLVersion(int _vid, String _vlabel){
        vid = _vid;
        vlabel = _vlabel;
    }
    
    public static VSLVersion getVSLVersion(int id){
        for(VSLVersion vv : VSLVersion.values()){
            if(vv.getSID() == id){
                return vv;
            }
        }
        
        return null;
    }
    
    public VSLStationState getVSLStationState(StationState cs){
        switch(vid){
            case 1 :
                return new VSLStationState(cs);
            case 2 :
                return new VSLStationStateNew(cs);
            case 3 : 
                return new VSLStationStateFullSearch(cs);
            case 4 : 
                return new VSLStationStateFullSearch_pro(cs);
            default :
                return new VSLStationState(cs);
        }
    }
    
    public VSStationFinder getVSStationFinder(Double mpoint){
        switch(vid){
            case 1 :
                return new VSStationFinder(mpoint);
            case 2 :
                return new VSStationFinderNew(mpoint);
            case 3 :
                return new VSStationFinderNew(mpoint);
            case 4 :
                return new VSStationFinderNew(mpoint);
            default :
                return new VSStationFinder(mpoint);
        }
    }
    
    public boolean isNewVERSION(){return this==this.NEWVERSIONFULLSEARCH_PRO;}
    public boolean isOldVERSION(){return this==this.OLDVERSION;}
    @Override
    public String toString(){
        return vlabel;
    }
    
    public int getSID(){
        return vid;
    }
}
