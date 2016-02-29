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
public enum SpeedforLowK {
    FixedSpeed(0,"Fixed Speed",65,true),
    TransitionSpeed(1,"Transition Speed",-1,false),
    SpeedLimit(2,"Speed Limit",-1,false),
    SpeedLimita(3,"Speed Limit + a",10,false);
    
    int src;
    String name;
    double value;
    boolean hasValue;
    SpeedforLowK(int _src,String _name, double _value, boolean _hasvalue){
        src = _src;
        name = _name;
        value = _value;
        hasValue = _hasvalue;
    }
    
    public static SpeedforLowK getTypebyID(int _src){
        for(SpeedforLowK ty : SpeedforLowK.values()){
            if(ty.getSRC() == _src){
                return ty;
            }
        }
        return null;
    }
    
    public void setValue(double v){
        value = v;
    }
    public double getValue(){
        return value;
    }
    public int getSRC(){
        return src;
    }
    @Override
    public String toString(){
        return name;
    }
    public boolean hasValue(){
        return hasValue;
    }
    public boolean isFixedSpeed(){return this == FixedSpeed;}
    public boolean isTransitionSpeed(){return this == TransitionSpeed;}
    public boolean isSpeedLimit(){return this == SpeedLimit;}
    public boolean isSpeedLimita(){return this == SpeedLimita;}
}
