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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import util.PropertiesWrapper;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class RandomSeeds {
    private HashMap<String, RandomSeed> randomseeds = new HashMap<String, RandomSeed>();
    private PropertiesWrapper prop = new PropertiesWrapper();
    
    private String K_NAME = "name";
    private String K_DATA = "data";
    private String K_SEED_COUNT = "count";
    
    public final static String SAVE_PROP_NAME = "rseeds.data";
//    public static Load(){
//        
//    }

    public RandomSeeds(){;}
    private RandomSeeds(PropertiesWrapper _prop) {
        this.prop = _prop.clone();
        int Count = prop.getInteger(this.K_SEED_COUNT);
        
        for(int i=0;i<Count;i++){
            RandomSeed rd = new RandomSeed(prop.get(this.K_NAME+"."+i));
            for(String s : prop.getStringArray(this.K_DATA+"."+i)){
                rd.AddSeed(Integer.parseInt(s));
            }
            this.AddRandomSeed(rd);
        }
    }
    
    private boolean AddRandomSeed(RandomSeed rd){
        RandomSeed newr = new RandomSeed(null);
        newr.setInstance(rd);
        
        if(newr.IsEmpty()){
            System.out.println("Empty Seed");
            return false;
        }
        
        if(!IsRandomSeed(newr.getName())){
            //System.out.println("ok randomseed!"+newr.getName());
            randomseeds.put(newr.getName(), newr);
            return true;
        }else
            return false;
        
        
    }
    public void UpdateRandomSeed(RandomSeed rd){
        RandomSeed newr = new RandomSeed(null);
        newr.setInstance(rd);
        
        if(newr.IsEmpty()){
//            System.out.println("Empty Seed");
            return;
        }
        
        if(IsRandomSeed(newr.getName())){
            randomseeds.remove(newr.getName());
        }
        randomseeds.put(newr.getName(), newr);
        
        this.Save();
    }
    
    public void RemoveRandomSeed(RandomSeed rd){
        if(IsRandomSeed(rd.getName())){
            randomseeds.remove(rd.getName());
        }
        this.Save();
    }
    
    public void clearGroup(){
        randomseeds.clear();
        this.Save();
    }
    public Collection<RandomSeed> getRandomSeeds(){
        return randomseeds.values();
    }
    
    public int getSize(){
        return randomseeds.size();
    }
    //public boolean Delete
    
    public boolean IsRandomSeed(String Name){
        if(randomseeds.get(Name) == null)
            return false;
        else
            return true;
    }
    public RandomSeed getRandomSeedInstance(String name){
        return randomseeds.get(name);
    }
    public ArrayList<String> getRandomSeedNameList(){
        ArrayList<String> _ar = new ArrayList<String>();
        for(RandomSeed rd : this.getRandomSeeds()){
            _ar.add(rd.getName());
        }
        return _ar;
    }
    
    public void Save(){
        int count = 0;
        prop = new PropertiesWrapper();
        prop.put(this.K_SEED_COUNT, getRandomSeeds().size());
        
        for(RandomSeed rd : getRandomSeeds()){
            prop.put(K_NAME+"."+count, rd.getName());
            prop.put(K_DATA+"."+count, rd.getSeedList());
            count ++;
        }
        
        prop.save(RandomSeeds.SAVE_PROP_NAME,"Random Seed Data");
        
    }
    
    public static RandomSeeds load(File _f){
        return load(_f.getAbsoluteFile());
    }
    
    public static RandomSeeds load(String _path){
        try{
            PropertiesWrapper prop = PropertiesWrapper.load(_path);
            return new RandomSeeds(prop);
        }catch(Exception ex){
            System.out.println("There is no random seed in directory : " + _path);
            return new RandomSeeds();
        }
    }
    
}
