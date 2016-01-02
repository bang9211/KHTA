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
package infra.simobjects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RandomSeed {
    private static RandomSeed StaticInstance = new RandomSeed(null);
    
    private ArrayList<Integer> seeds = new ArrayList<Integer>();
    private String Name = null;
    
    private boolean isClean = true;
    private boolean isSingle = false;
    private int SEEDCOUNT = 0;
    private String SIMULATION_KEY= null;
    
    public static RandomSeed getInstance(){
        return StaticInstance;
    }
    
    public RandomSeed(String Name){
        setName(Name);
    }
    
    public boolean AddSeed(int seed){
        if(!IsSeed(seed)){
            seeds.add(seed);
            return true;
        }
        else
            return false;
    }
    
    public void setSeed(ArrayList<Integer> ar){
        seeds.clear();
        for(int s : ar){
            seeds.add(s);
        }
    }
    public void setInstance(RandomSeed rd){
        this.setName(rd.Name);
        this.setSeed(rd.getSeeds());
    }
    
    public boolean RemoveSeed(int seed){
        try{
            seeds.remove(seed);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean IsSeed(int seed){
        for(int s : seeds){
            if(seed == s)
                return true;
        }
        return false;
    }
    
    public boolean IsSeedEmpty(){
        return seeds.isEmpty();
    }
    
    public ArrayList<Integer> getSeeds(){
        return seeds;
    }
    public int getSeed(int point){
        return this.seeds.get(point);
    }
    
    public List<String> getSeedList(){
        List<String> l = new ArrayList<String>();
        for(int _d : seeds){
            l.add(String.valueOf(_d));
        }
        return l;
        
    }
    public int getLength(){
        return seeds.size();
    }
    public String getName(){
        return this.Name;
    }
    public void setName(String Name){
        this.Name = Name;
        this.isClean = false;
    }
    
    public void Clear(){
        this.Name = null;
        this.seeds.clear();
        this.isClean = true;
        this.SIMULATION_KEY = null;
        setSeedPointFirst();
    }
    
    public boolean IsClean(){
        if(this.isSingle){
            if(this.isClean && this.seeds.isEmpty())
                return true;
            else
                return false;
        }else{
            if(this.isClean && this.Name == null && this.seeds.isEmpty()){
                return true;
            }
            else
                return false;
        }
    }
    public void setSingle(boolean _flag){
        this.isSingle = _flag;
    }
    public boolean isSingle(){
        return this.isSingle;
    }
    public boolean IsEmpty(){
        if(this.isClean || (this.Name == null && !this.isSingle) || this.seeds.isEmpty()){
            return true;
        }
        else
            return false;
    }
    
    //for count
    public int NextSeed(){
        if(this.SEEDCOUNT == this.getLength())
            return -1;
        return this.seeds.get(SEEDCOUNT++);
    }

    public int getCurrentSeedPoint(){
        return SEEDCOUNT;
    }
    public void setSeedPointFirst(){
        this.SEEDCOUNT = 0;
    }
    public int hasFirstSeed(){
        if(!this.IsSeedEmpty()){
            return this.seeds.get(0);
        }else
            return -1;
    }
    public boolean IsEndSeed(){
        if(this.SEEDCOUNT == this.getLength())
            return true;
        else
            return false;
    }
    
    //for Simulation Instance
    public void MakeSimulationKey(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyHHmmss");
        this.SIMULATION_KEY = dateFormat.format(calendar.getTime());
    }
    public String getSimulationKey(){
        return this.SIMULATION_KEY;
    }
    public boolean hasSimulationKey(){
        if(this.SIMULATION_KEY == null)
            return false;
        else
            return true;
    }
}
