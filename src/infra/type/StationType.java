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
public enum StationType {
    FTMS_MAINLINE("FTMS Mainline VDS",1),
    TTMS_TUNNEL("TTMS TUNNEL VDS",2),
    PRIVATE_HIGHWAY("Private Highway VDS",3),
    RAMP("Ramp VDS",4),
    VIRTUAL("Virtual VDS",5),
    NONE("No Information",6);
    
    private StationType(String _desc, int _code){
        desc = _desc;
        code = _code;
    }
    
    public final String desc;
    public final int code;
    
    public static StationType getType(int aInt) {
        for(StationType tt : StationType.values()){
            if(tt.code == aInt) return tt;
        }
        return NONE;
    }
    
    public boolean isRoadVDS(){
        return this.code < RAMP.code;
    }
}
