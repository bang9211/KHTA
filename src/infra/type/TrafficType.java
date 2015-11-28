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
public enum TrafficType {
    VOLUME(0),
    SPEED(1),
    FLOW(2),
    DENSITY(3),
    AVERAGEFLOW(4),
    SPEEDFORSTATION(5),
    UNKNOWN(99);
    
    final int id;
    
    private TrafficType(int id){
        this.id = id;
    }
    
    public int getTrafficTypeId(){
        return id;
    }
    
    public static TrafficType getTrafficType(int id){
        for(TrafficType tt : TrafficType.values()){
            if(tt.id == id)return tt;
        }
        return UNKNOWN;
    }
    
    public boolean isVolume() { return this == VOLUME; }
    public boolean isSpeed() { return this == SPEED; }
    public boolean isFlow() { return this == FLOW; }
    public boolean isDensity() { return this == DENSITY; }
    public boolean isAverageFlow() { return this == AVERAGEFLOW; }
    public boolean isSppedForStation(){return this == SPEEDFORSTATION;}
}
