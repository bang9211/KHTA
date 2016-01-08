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
package infra.simulation;

import infra.Period;
import infra.Section;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;
import khta.SimulationResult;
import plugin.PluginFrame;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class SimulationUtil {
    /**
     * load simulation results from local disk
     * set data to table
     */
    public static ArrayList<SimulationResult> loadSimulationResults() {
        File file = new File(SimulationResult.SAVE_PROP_DIR);  
        File[] files = file.listFiles();  
        if(files == null) return null;
        
        
        ArrayList<SimulationResult> res = new ArrayList<SimulationResult>();
        for (int i = 0; i < files.length; i++)  
        {  
            if(files[i].isDirectory()) continue;
           SimulationResult s = SimulationResult.load(files[i]);
           if(s == null) continue;
           res.add(s);
        }
        
        Collections.sort(res);
        
        return res;
    }
    
    public static void SaveSimulation(Section simSection, Period simPeriod,PluginFrame simFrame) {
        if(simSection == null || simPeriod == null) {
            JOptionPane.showMessageDialog(simFrame, "Empty simulation result");
            return;
        }
        // Detectors must have data, before this routine
        SimulationResultSaveDialog srd = new SimulationResultSaveDialog(simFrame, simSection, simPeriod);
        srd.setLocationRelativeTo(simFrame);
        srd.setVisible(true);
    }
    
    /**
     * load VSL simulation results from local disk
     * set data to table
     */
//    public static ArrayList<VSLResults> loadVSLSimulationResults() {
//        File file = new File(VSLResultManager.SAVE_VSL_DIR);  
//        File[] files = file.listFiles();  
//        if(files == null) return null;
//        
//        
//        ArrayList<VSLResults> res = new ArrayList<VSLResults>();
//        for (int i = 0; i < files.length; i++)  
//        {  
//            if(files[i].isDirectory()) continue;
//            VSLResultManager vm = VSLResultManager.load(files[i]);
//            if(vm == null) continue;
//            res.add(vm.LoadResults());
//        }
//        
//        Collections.sort(res);
//        
//        return res;
//    }
//    
//    public static void DeleteVSLSimulationResult(VSLResults result){
//        File file = new File(VSLResultManager.SAVE_VSL_DIR);  
//        File[] files = file.listFiles();  
//        if(files == null) return;
//        
//        
//        for (int i = 0; i < files.length; i++)  
//        {  
//            if(files[i].isDirectory()) continue;
//            VSLResultManager vm = VSLResultManager.load(files[i]);
//            if(vm == null) continue;
//            VSLResults tr = vm.LoadResults();
//            System.out.println(tr.getName());
//            if(tr.equalData(result)){
//                File dfile = new File(files[i].getAbsolutePath());
//                dfile.delete();
//                break;
//            }
//        }
//    }
//    
//
//    public static void SaveVSLSimulation(Section simSection, Period simPeriod, PluginFrame simFrame, VSLResults vslResults) {
//        if(simSection == null || simPeriod == null){
//            JOptionPane.showMessageDialog(simFrame, "Empty simulation result");
//            return;
//        }
//        VSLSimulationResultSaveDialog vslsrd = new VSLSimulationResultSaveDialog(simFrame,simSection,simPeriod,vslResults);
//        vslsrd.setLocationRelativeTo(simFrame);
//        vslsrd.setVisible(true);
//        
//    }
}
