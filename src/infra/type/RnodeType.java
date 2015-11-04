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
package infra.type;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum RnodeType {
    NONE("None",0),
    STATION("Station",1),
    ENTRANCE("Entrance",2),
    EXIT("Exit",3),
    DMS("DMS",4);
    
    private RnodeType(String _desc, int _code){
        desc = _desc;
        code = _code;
    }
    
    public final String desc;
    public final int code;
    
    public static RnodeType get(String name){
        for(RnodeType tt : RnodeType.values()){
            if(name.equals(tt.name())) return tt;
        }
        return NONE;
    }
    
    public boolean isStation() {return this==STATION;}
    public boolean isEntrance() {return this==ENTRANCE;}
    public boolean isExit() {return this==EXIT;}
    public boolean isDMS() {return this==DMS;}
    
    @Override
    public String toString(){
        return desc;
    }
}
