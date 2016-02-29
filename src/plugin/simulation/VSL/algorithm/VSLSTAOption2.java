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

import edu.umn.natsrl.infra.Section;
import edu.umn.natsrl.infra.infraobjects.DMSImpl;
import edu.umn.natsrl.ticas.Simulation.SimulationGroup;
import edu.umn.natsrl.ticas.plugin.simulation.VSL.VSLConfig;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLSTAOption2 implements VSLSTAImpl{
    Section section;
    TreeMap<Double, VSLStationState> smap;
    private final Double sMile = 1.5d;
    public VSLSTAOption2(Section s, TreeMap<Double, VSLStationState> _smap) {
        section = s;
        smap = _smap;
    }
    @Override
    public boolean findSAT(TreeMap<Double, DMSImpl> dmsMap, VSStationFinder cVSS_finder, boolean isFirst) {
        Map.Entry<Double, DMSImpl> entry = dmsMap.lastEntry();
        if(entry == null || cVSS_finder.vss == null)
            return isFirst;
        DMSImpl currentDMS = entry.getValue(); //Current DMS
        Double cm = entry.getKey(); //Current DMS MilePoint
        entry = dmsMap.lowerEntry(cm);
        DMSImpl prevDMS = entry == null ? null : entry.getValue(); //Previous DMS
        
        initSTA(currentDMS);
//        System.out.print("DMSID("+isFirst+") : "+currentDMS.getId()+"-"+cm);
        if(currentDMS.isStarted() && prevDMS != null && !prevDMS.isStarted()){
            VSLStationState checkStation = getCheckStation(cVSS_finder.vss.getMilePoint(), smap);
//            System.out.print("cStation : "+checkStation.getID());
            setSAT(prevDMS,currentDMS,isFirst,checkStation);
//            System.out.println();
            return false;
        }
//        System.out.println();
        return isFirst;
    }
    
    private void setSAT(DMSImpl prevDMS, DMSImpl currentDMS, boolean firstDMS, VSLStationState cstation) {
        //after 2nd DMS
        if(!firstDMS){
            if(prevDMS.isStarted() || currentDMS.isUpstreamVSS() || !checkStationSpeedLimit(cstation))
                return;
        }
        
        prevDMS.setSTA(1);
        setActualSpeedforSTA(prevDMS);
        
    }

    private void initSTA(DMSImpl currentDMS) {
        currentDMS.setSTA(0);
    }

    private void setActualSpeedforSTA(DMSImpl currentDMS) {
        if(currentDMS.getActualSpeedLimit() == 0)
            currentDMS.setActualVSA(60);
    }

    private boolean checkStationSpeedLimit(VSLStationState cstation) {
        if(cstation.getAggregateRollingSpeed(SimulationGroup.VSL) >= cstation.getSpeedLimit())
            return true;
        else
            return false;
    }

    /**
     * get Station of 1.5mile from VSS Station
     * @param milePoint
     * @param smap1
     * @return 
     */
    private VSLStationState getCheckStation(Double milePoint, TreeMap<Double, VSLStationState> smap1) {
        Double dis = milePoint - sMile;
        Map.Entry<Double, VSLStationState> entry = smap1.lowerEntry(dis);
        if(entry == null)
            entry = smap1.firstEntry();
        return entry.getValue();
    }
    
}
