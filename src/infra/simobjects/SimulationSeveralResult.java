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
package infra.simobjects;

import khta.SimulationResult;
import evaluation.DataLoadOption;
import evaluation.Interval;
import infra.Period;
import infra.Section;
import infra.simulation.SimulationConfig;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author soobin Jeon
 */
public class SimulationSeveralResult {
    //private SimObjects sobject = SimObjects.getInstance();
    private RandomSeed simRandom = RandomSeed.getInstance();
    //private ArrayList<SimObjects> SimObjList = SimObjects.getArrayListInstance();
    private Section simSection;
    private Period simPeriod;
    
    public SimulationSeveralResult(Section section, Period period){
        this.simSection = section;
        this.simPeriod = period;
    }
    
    private boolean checkName(String name)
    {
        if(name.isEmpty()) {
            System.out.println("Save Fail : Result name is required");
//            JOptionPane.showMessageDialog(rootPane, "Fail : Result name is required");
            return false;
        }
        if(name.length() > 128) {
            System.out.println("Save Fail : Result name is long. (128 characters limit)");
//            JOptionPane.showMessageDialog(rootPane, "Fail : Result name is long. (128 characters limit)");
            return false;
        }
        String eName = SimulationResult.getCacheFileName(name);
        if(eName == null) {
            System.out.println("Save Fail : MD5 algorithm does not exist!!");
//            JOptionPane.showMessageDialog(rootPane, "Fail : MD5 algorithm does not exist!!");
            return false;            
        }
        
        return true;
    }
    private String getDesc(){
        return simRandom.getName()+","+simRandom.getSeed(simRandom.getCurrentSeedPoint()-1)+","+simRandom.getCurrentSeedPoint()+","+simRandom.getSimulationKey();
//        return "SEED List Name : " + simRandom.getName() + "\n" + "SEED NUMBER : " + simRandom.getSeed(simRandom.getCurrentSeedPoint()-1) +
//                "\n" + "SEED COUNT : " + simRandom.getCurrentSeedPoint() +
//                "\n" + "SEED KEY : " + simRandom.getSimulationKey();
    }
    public static String getKey(String name, String key){
        return name + "_" + key;
    }
    private String getName(){
        return getKey(simRandom.getName(),simRandom.getSimulationKey())+(simRandom.getCurrentSeedPoint()-1);
    }
    private String getName(String key, String num){
        return key + num;
    }
    public void saveResult()
    {
        String name = getName();
        String desc = getDesc();
        if(!checkName(name)) return;
        int RunningInterval = SimulationConfig.RunningInterval;
        try {
            System.out.println("SAVE : period=" + simPeriod);
            System.out.println("SAVE : section="+simSection.getName());
            simSection.loadData(simPeriod, DataLoadOption.setSimulationMode(Interval.get(RunningInterval)));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        SimulationResult sr = new SimulationResult(name, desc, simSection, simPeriod,RunningInterval,true,getKey(simRandom.getName(),simRandom.getSimulationKey()));
        
        try {
            sr.save();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public ArrayList<SimulationResult> getSimulationResults(SimulationResult sr){
        return LoadResult(sr,DataType.GETSIMULATIONRESULTS);
    }
    public void LoadResult(SimulationResult sr){
        LoadResult(sr,DataType.SETSIMOBJLIST);
    }
    
    public ArrayList<SimulationResult> LoadResult(SimulationResult sr, DataType type){
        int simulationlength = sr.getSimulationLegnth();
        String simKey = sr.getDataKey();
        String path = SimulationResult.SAVE_PROP_DIR_SUB + File.separator;
        ArrayList<SimulationResult> sResultList = new ArrayList<SimulationResult>();
        for(int i=0;i<simulationlength;i++){
            SimObjects sObj = new SimObjects();
            String sName = simKey+i;
//            System.out.println(SimulationResult.getCacheFileName(sName));
//            System.out.println(path+sName);
            SimulationResult subsr = SimulationResult.load(path+SimulationResult.getCacheFileName(sName));
            try{
                sObj.setRandomSeed(Integer.parseInt(subsr.getDESC(SimulationResult.DESC_RSEED)));
            }catch(Exception e){
                System.out.println("Data Load Error : SEED load fail");
            }
            subsr.setSubData(true);
//            System.out.println("set DataficData");
            if(type.IsSetSimObjectListMode()){
                subsr.setTrafficDataToDetectors(sObj);
                //this.SimObjList.add(sObj);
            }else if(type.IsGetSimulationMode()){
                sResultList.add(subsr);
            }
        }
        
        if(type.IsSetSimObjectListMode())
            return null;
        else if(type.IsGetSimulationMode())
            return sResultList;
        else
            return null;
    }
    
    public enum DataType{
        GETSIMULATIONRESULTS,
        SETSIMOBJLIST;
        
        public boolean IsGetSimulationMode(){
            return (this == this.GETSIMULATIONRESULTS);
        }
        public boolean IsSetSimObjectListMode(){
            return (this == this.SETSIMOBJLIST);
        }
    }
}
