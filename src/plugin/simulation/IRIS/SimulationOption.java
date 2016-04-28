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
package plugin.simulation.IRIS;

//import edu.umn.natsrl.ticas.plugin.simulation.VSL.VSLSim;
import infra.Section;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import infra.simulation.Simulation;
import plugin.metering.MeteringSim;
//import plugin.simulation.VSL.VSLSim;
import plugin.simulation.VSL.algorithm.VSLVersion;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum SimulationOption {
    VSL_METERING(0,"VSL/Metering"),
    VSL(1,"VSL MODE"),
    METERING(2,"Metering Mode");
    int sid;
    String name;
    SimulationOption(int _sid, String _name){
        sid = _sid;
        name = _name;
    }
    
    public Simulation getSimulationOption(String caseFile, int seed, Section section, VISSIMVersion v, VSLVersion _vv,
            SimInterval SimIntv, SimObjects simObjects){
        if(isVSLMETERING()){
//            return new irisSim(caseFile,seed,section,v,_vv,SimIntv);
            return null;
        }else if(isVSLMODE()){
//            return new VSLSim(caseFile,seed,section,v,_vv,SimIntv);
            return null;
        }else if(isMETERING()){
            return new MeteringSim(caseFile,seed,section,v,SimIntv, simObjects);
        }else
            return null;
    }
    
    public boolean isVSLMETERING(){return this==VSL_METERING;}
    public boolean isVSLMODE(){return this==VSL;}
    public boolean isMETERING(){return this==METERING;}
}
