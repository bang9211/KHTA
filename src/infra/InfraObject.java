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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class InfraObject implements Serializable{
    private final String id;
    private String name;
    private HashMap<InfraDatas, Object> properties = new HashMap();
    
    public InfraObject(HashMap<InfraDatas, Object> _pro){
        properties = _pro;
        id = (String)getProperty(InfraDatas.ID);
        name = (String)getProperty(InfraDatas.NAME);
    }
    
    //get Property from properties
    protected Object getProperty(InfraDatas key){
        return properties.get(key);
    }
    
    //get ID
    public String getID(){
        return id;
    }
    
    //get NAME
    public String getName(){
        return name;
    }
    
    public void setName(String _name){
        name = _name;
    }
    
    protected double[] toDoubleArray(Vector<Double> data)
    {        
        if(data == null) return null;        
        double[] ret = new double[data.size()];
        for(int i=0; i<data.size(); i++) {
            ret[i] = data.get(i).doubleValue();
        }
            //ret[i] = this.roundUp(data.get(i), 2);
        return ret;
    }
    
    protected int[] toIntArray(Vector<Integer> data)
    {
        if(data == null) return null;
        int[] ret = new int[data.size()];
        for(int i=0; i<data.size(); i++)
            ret[i] = data.get(i).intValue();
        return ret;
    }    

    protected String[] toStringArray(Vector<String> data)
    {
        if(data == null) return null;
        String[] ret = new String[data.size()];
        for(int i=0; i<data.size(); i++)
            ret[i] = data.get(i);
        return ret;
    }  
    
    protected double roundUp(double n, int precision )
    {
        return (double) ( Math.round(n*Math.pow(10, precision)) / Math.pow(10, precision) );
    }
}
