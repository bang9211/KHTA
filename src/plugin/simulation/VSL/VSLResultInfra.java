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
package plugin.simulation.VSL;

import java.util.ArrayList;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLResultInfra {
    protected String id;
    private Double milepoint = null;
    
    public VSLResultInfra(String _id, Double mile){
        id = _id;
        milepoint = mile;
    }
    
    protected double[] getArraytoDouble(ArrayList<Double> data){
        double[] d = new double[data.size()];
        for(int i=0;i<data.size();i++){
            d[i] = data.get(i);
        }
        return d;
    }
    
    protected boolean[] getArraytoBoolean(ArrayList<Boolean> data){
        boolean[] d = new boolean[data.size()];
        for(int i=0;i<data.size();i++){
            d[i] = data.get(i);
        }
        return d;
    }
    
    protected int[] getArraytoInt(ArrayList<Integer> data){
        int[] d = new int[data.size()];
        for(int i=0;i<data.size();i++){
            d[i] = data.get(i);
        }
        return d;
    }

    protected double[] convertBooleantoDoubleArray(boolean[] current) {
        double[] cdata = new double[current.length];
        for(int i=0;i<cdata.length;i++){
            cdata[i] = convertBooleantoDouble(current[i]);
        }
        return cdata;
    }
    
    protected boolean[] convertDoubletoBooleanArray(double[] current){
        boolean[] cdata = new boolean[current.length];
        for(int i=0;i<cdata.length;i++){
            cdata[i] = convertDoubletoBoolean(current[i]);
        }
        return cdata;
    }
    
    protected boolean convertDoubletoBoolean(double var){
        return var == 0 ? true : false;
    }
    protected double convertBooleantoDouble(boolean var){
        return var == true ? 0 : 1;
    }
    
    protected void addAllDatas(int[] par, ArrayList<Integer> data){
        for(int p : par){
            data.add(p);
        }
    }
    protected void addAllDatas(double[] par, ArrayList<Double> data){
        for(double p : par){
            data.add(p);
        }
    }
    
    protected void addAllDatas(boolean[] par, ArrayList<Boolean> data){
        for(boolean p : par){
            data.add(p);
        }
    }
    
    public String getID(){
        return this.id;
    }
    
    public Double getMilePoint(){
        return this.milepoint;
    }
}
