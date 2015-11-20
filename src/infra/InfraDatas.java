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
package infra;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum InfraDatas {
    //Integrated Properties
    ID("ID","String","ID"),
    NAME("Name","String","name"),
    
    //for Corridor
    ROADTYPE("Road Type","RoadType","roadType"), //Road Type
    DIRECTION("Direction","Direction","direction"),
    
    //for RNode
    LOCATION("location","double","location"),
    
    //for Station(VDS)
    COR_ID("Corridor ID","String","cor_ID"),
    START_LOC("Start Location","double","startLocation"),
    END_LOC("End Location","double","endLocation"),
    TYPE_CODE("Type Code", "int", "type"),
    LENGTH("Legnth","int","length"),
    ORDER("Node Order","int","order"),
    LANE("Lane", "int", "lane"),
    SPEED_LIMIT("Speed Limit", "float", "speedLimit"),
    SECTION_NAME("Section Name","String", "s_name"),
    ISBUSLANE("is bus lane","Intger", "isBusLane"),
    SECTION_ID("section ID","String", "sec_ID");
    
    
    
    private InfraDatas(String _desc, String _type, String _tableName){
        desc = _desc;
        type = _type;
        tableName = _tableName;
    }
    
    private final String desc;
    private final String type;
    private final String tableName;
    
    public static InfraDatas getbyTableName(String _tn){
        for(InfraDatas cd : InfraDatas.values()){
            if(_tn.equals(cd.tableName)) return cd;
        }
        return null;
    }

    public String getTableName() {
        return tableName;
    }
}
