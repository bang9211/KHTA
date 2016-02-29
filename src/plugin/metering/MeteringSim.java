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
package plugin.metering;

import infra.Section;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import infra.simulation.SimulationImpl;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class MeteringSim extends infra.simulation.Simulation implements SimulationImpl{
    Metering metering;
    SimObjects simObjects;
    
    public MeteringSim(String caseFile, int seed, Section section, VISSIMVersion v,SimInterval Intv, SimObjects simObjects){
        super(caseFile,seed,section,v,Intv, simObjects);
        this.simObjects = simObjects;
        
        SimInit();
    }
    
    @Override
    public void RunningInitialize() {
        super.RunningInitialize();
    }

    @Override
    public void ExecuteBeforeRun() {
        super.ExecuteBeforeRun();
    }

    @Override
    public void ExecuteAfterRun() {
        super.ExecuteAfterRun();
        RunMetering();
    }

    @Override
    public void DebugMassage() {
        DisplayStationState();
        if(MeteringConfig.isMeteringStep(simcount))
                metering.printEntrance();
    }

    @Override
    public void SimInit() {
        metering = new Metering(section,meters,detectors,simulationInterval, simObjects);
    }
    
        private void RunMetering() {
                metering.updateEntranceStates();
                if(MeteringConfig.isMeteringStep(simcount))
                        metering.run(samples, vc.getCurrentTime());
        }
}
