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
public enum DensityAggregation {
    MovingKAvg(0,"Moving K Average",65,true),
    LAB(1,"Option 1(LAB)",-1,false),
    TMC(2,"Option 2(TMC)",-1,false);
    int src;
    String name;
    double value;
    boolean hasValue;
    DensityAggregation(int _src,String _name,double _value, boolean _hasvalue){
        src = _src;
        name = _name;
        value = _value;
        hasValue = _hasvalue;
    }
    
    public static DensityAggregation getTypebyID(int _src){
        for(DensityAggregation ty : DensityAggregation.values()){
            if(ty.getSRC() == _src){
                return ty;
            }
        }
        return null;
    }
    
    public void setValue(double _v){
        value = _v;
    }
    
    public double getValue(){
        return value;
    }
    public int getSRC(){
        return src;
    }
    public boolean hasValue(){
        return hasValue;
    }
    @Override
    public String toString(){
        return name;
    }
    public boolean isMovingKAvg(){return this==MovingKAvg;}
    public boolean isLAB(){return this==LAB;}
    public boolean isTMC(){return this==TMC;}
}
