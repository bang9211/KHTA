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
    ID("id","String","id"),
    NAME("name","String","name"),
    
    //for Corridor
    ROADTYPE("roadtype","RoadType","roadtype"), //Road Type
    SHORTNAME("Short Name","String","shortname"), ////short name
    STARTNAME("Start Name","String","startname"),//Start Location Name
    ENDNAME("End Name","String","endname"),//End Location Name
    STARTNODE("Start Node","String","startnode"),//Start Node
    ENDNODE("End Node","String","endnode"),// End Node
    FREESPEED("Freeway Speed","int","freespeed"),
    JAMSPEED("Jam Speed","int","jamspeed"),
    DIRECTION("Direction","Direction","direction"),
    
    //for RNode
    LOCATION("location","double","loc"),
    
    //for Station(VDS)
    COR_ID("Corridor ID","String","cor_id"),
    START_LOC("Start Location","double","startloc"),
    END_LOC("End Location","double","endloc"),
    LENGTH("Legnth","int","length"),
    ORDER("Node Order","int","order");
    
    
    private InfraDatas(String _desc, String _type, String _tableName){
        desc = _desc;
        type = _type;
        tableName = _tableName;
    }
    
    public final String desc;
    public final String type;
    public final String tableName;
    
    public static InfraDatas getbyTableName(String _tn){
        for(InfraDatas cd : InfraDatas.values()){
            if(_tn.equals(cd.tableName)) return cd;
        }
        return null;
    }
}
