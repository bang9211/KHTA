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
package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class can be used for saving the all class data.
 * Example : Section-> save(), load() function
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class PropertiesWrapper implements Serializable {

    public Properties prop = new Properties();
    private final String DELIM = ",";

    public PropertiesWrapper() {
    }
    
    public PropertiesWrapper(Properties prop) {
        this.prop = prop;
    }
    
    public static PropertiesWrapper load(String filepath) {
        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(filepath);
            prop.load(fis);
            fis.close();
            return new PropertiesWrapper(prop);
        } catch (Exception ex) {
            return null;
        }           
    }    
    
    public boolean save(String filepath) {
        return save(filepath, "");
    }
    
    public boolean save(String filepath, String comment) {
        try {
            FileOutputStream fileOut = null;
            fileOut = new FileOutputStream(filepath);
            prop.store(fileOut, comment);
            fileOut.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public void put(String key, String value) {
        this.prop.put(key, value);
    }

    public void put(String key, float value) {
        this.prop.put(key, String.format("%f", value));
    }     
    
    public void put(String key, double value) {
        this.prop.put(key, String.format("%f", value));
    }       
    
    public void put(String key, int value) {
        this.prop.put(key, String.format("%d", value));
    }       

    public void put(String key, long value) {
        this.prop.put(key, String.format("%d", value));
    }    

    public void put(String key, List<String> objs) {
        put(key, objs.toArray(new String[objs.size()]));
    }
    
    public void put(String key, Boolean value) {
        this.prop.put(key, value.toString());
    }
    
    public void put(String key, Date value) {
        this.put(key, value.getTime());
    }    
    
    public void put(String key, String[] objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(objs[i]);
        }
        this.prop.put(key, sb.toString());
    }    

    public void put(String key, double[] objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            if (i != 0) {
                sb.append(DELIM);
            }
            sb.append(objs[i]);
        }
        this.prop.put(key, sb.toString());
    }   
    
    public String get(String key) {
        return this.prop.getProperty(key);
    }
    
    public int getInteger(String key) {
        try{
            return Integer.parseInt(this.prop.getProperty(key));
        } catch(Exception ex) {
            return 0;
        }
    }  
    
    public long getLong(String key) {
        try{
            return Long.parseLong(this.prop.getProperty(key));
        } catch(Exception ex) {
            return 0;
        }
    }        
    
    public float getFloat(String key) {
        try{
            return Float.parseFloat(this.prop.getProperty(key));
        } catch(Exception ex) {
            return 0;
        }
    }     
    
    public double getDouble(String key) {
        try{
            return Double.parseDouble(this.prop.getProperty(key));
        } catch(Exception ex) {
            return 0;
        }
    }       
    
    public boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(this.prop.getProperty(key));
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Date getDate(String key) {
        long time = getLong(key);
        return new Date(time);
    }    

    public List<String> getStringList(String key) {
        List<String> vec = new ArrayList<String>();
        String str = prop.getProperty(key);
        String[] arr = str.split(DELIM);
        if (arr.length == 0) return null;
        for(int i=0; i<arr.length; i++) {
            vec.add(arr[i]);
        }        
        return vec;
    }  
    
    public String[] getStringArray(String key) {
        List<String> vec = getStringList(key);
        return vec.toArray(new String[vec.size()]);
    }     
    
    public List<Double> getDoubleVector(String key) {
        List<Double> vec = new ArrayList<Double>();
        String str = prop.getProperty(key);
        String[] arr = str.split(DELIM);
        if (arr.length == 0) return new ArrayList<Double>();
        for(int i=0; i<arr.length; i++) {
            vec.add(Double.parseDouble(arr[i]));
        }        
        return vec;
    }
    
    public double[] getDoubleArray(String key) {
        String str = prop.getProperty(key);
        String[] csv = str.split(DELIM);
        double[] arr = new double[csv.length];
        for (int i = 0; i < csv.length; i++) {
            arr[i] = Double.parseDouble(csv[i]);
        }
        return arr;
    }
    
    public int[] getIntegerArray(String key) {
        String str = prop.getProperty(key);
        String[] csv = str.split(DELIM);
        int[] arr = new int[csv.length];
        for (int i = 0; i < csv.length; i++) {
            arr[i] = Integer.parseInt(csv[i]);
        }
        return arr;
    }
    
    @Override
    public PropertiesWrapper clone() {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            output = new ObjectOutputStream(byteOutput);
            output.writeObject(this);
            output.flush();
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(byteOutput.toByteArray());
            input = new ObjectInputStream(bin);

            return (PropertiesWrapper) input.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }    
}