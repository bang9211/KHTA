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
package infra.infraobject;

import infra.InfraDatas;
import infra.Section;
import infra.type.*;
import java.util.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Station extends RNode{
    //Station Type(VDS Zone Type CODE)
    private StationType stationtype = StationType.NONE;
    //Start Location from Start Point
    protected double Start_Loc = -1;
    //End Location from Start Point
    protected double End_Loc = -1;
    //Length
    private int Length = -1;
    //roadType
    protected String roadType = null;
    //speedLimit
    protected float speedLimit;
    //Section Name
    protected String SectionName;
    //DB Section ID
    protected String DB_Section_ID;
    
    private HashMap<String, Station> downstreamStation = new HashMap<String, Station>();
    private HashMap<String, Station> upstreamStation = new HashMap<String, Station>();
    private HashMap<String, Double> distanceToDownstreamStation = new HashMap<String, Double>();    // unit = km
    private HashMap<String, Double> distanceToUpstreamStation = new HashMap<String, Double>();    // unit = km    
    
    public Station(HashMap<InfraDatas,Object> datas) {
        super(datas,RnodeType.STATION);
        Integer it;
        Double dt;
        Float ft;
        
        it = (Integer)super.getProperty(InfraDatas.LENGTH);
        Length = it == null ? -1 : it;
        
        dt = (Double)super.getProperty(InfraDatas.START_LOC);
        Start_Loc = dt == null ? -1 : dt;
        
        dt = (Double)super.getProperty(InfraDatas.END_LOC);
        End_Loc = dt == null ? -1 : dt;
        
        ft = (Float)super.getProperty(InfraDatas.SPEED_LIMIT);
        speedLimit = ft == null ? -1 : ft;
        
        SectionName = (String)super.getProperty(InfraDatas.SECTION_NAME);
        
        DB_Section_ID = (String)super.getProperty(InfraDatas.SECTION_ID);
        
        stationtype = (StationType)super.getProperty(InfraDatas.STATION_TYPE);
    }
    
    public float getSpeedLimit(){
        return speedLimit;
    }
    
    @Override
    public String toString(){
        return getOrder() + " - "+getName();
    }

    public String getSectionName() {
        return SectionName;
    }
    
    public String getDBSectionID(){
        return this.DB_Section_ID;
    }
    
    public boolean isBusLane(){
        return isBusLane;
    }
    
    public double getStartLocation(){
        return this.Start_Loc;
    }
    
    public double getEndLocation(){
        return this.End_Loc;
    }
    
    public StationType getStationType(){
        return this.stationtype;
    }
    
    public void setLocation(double sloc, double eloc){
        this.Start_Loc = sloc;
        this.End_Loc = eloc;
    }
    
    public void setDistanceToDownstreamStation(String sectionName, Double distance){
        this.distanceToDownstreamStation.put(sectionName, distance);
    }
    
    public void setDistanceToUpstreamStation(String sectionName, Double distance){
        this.distanceToUpstreamStation.put(sectionName, distance);
    }
    
    public double getDistanceToDownstreamStation(String sectionName){
        if(this.distanceToDownstreamStation.get(sectionName) == null){
            return -1;
        }
        Double v = this.distanceToDownstreamStation.get(sectionName);        
        return v;
    }
    
    public double getDistanceToUpstreamStation(String sectionName){
        if(this.distanceToUpstreamStation.get(sectionName) == null){
            return -1;
        }
        Double v = this.distanceToUpstreamStation.get(sectionName);        
        return v;
    }

    public void setUpstreamStation(String sectionName, Station upStation) {
        this.upstreamStation.put(sectionName, upStation);
    }

    public void setDownstreamStation(String sectionName, Station downStation) {
        downstreamStation.put(sectionName, downStation);
        downStation.setUpstreamStation(sectionName, this);
    }
}
