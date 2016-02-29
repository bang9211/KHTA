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

import edu.umn.natsrl.infra.infraobjects.DMSImpl;
import infra.Section;
import infra.simulation.vsl.algorithm.VSLConfig;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class VSLSTAOption1 implements VSLSTAImpl{
    Section section;
    public VSLSTAOption1(Section _section){
        section = _section;
    }
    
    @Override
    public boolean findSAT(TreeMap<Double, DMSImpl> dmsMap, VSStationFinder cVSS_finder, boolean isFirst) {
        Map.Entry<Double, DMSImpl> entry = dmsMap.lastEntry();
        if(entry == null)
            return isFirst;
        DMSImpl currentDMS = entry.getValue();
        Double cm = entry.getKey();
        entry = dmsMap.lowerEntry(cm);
        DMSImpl prevDMS = entry == null ? null : entry.getValue();
        
        initSTA(currentDMS);
//        System.out.print("DMSID("+isFirst+" : "+currentDMS.getId()+"-"+cm);
        if(currentDMS.isStarted() && prevDMS != null && !prevDMS.isStarted()){
            setSAT(prevDMS,currentDMS,isFirst);
            return false;
        }
        return isFirst;
//        boolean isFirstDMS = true;
//        
//        for(int i = 0 ; i < cdms.size() ; i++){
//            DMSImpl currentDMS = cdms.get(i);
//            DMSImpl nextDMS = i+1 == cdms.size() ? null : cdms.get(i+1);
//            initSTA(currentDMS);
//            
//            if(nextDMS == null || !nextDMS.isStarted())
//                continue;
//            
//            setSAT(currentDMS, nextDMS, isFirstDMS);
//            isFirstDMS = false;
//        }
    }
    
    private void setSAT(DMSImpl prevDMS, DMSImpl currentDMS, boolean firstDMS) {
        Double distance = currentDMS.getMilePoint(section.getName()) - prevDMS.getMilePoint(section.getName());
        //after 2nd DMS
        if(!firstDMS){
            if(prevDMS.isStarted() || !CoverDistance(distance) || CoverageSpeed(currentDMS) || currentDMS.isUpstreamVSS())
                return;
        }
        prevDMS.setSTA(1);
        setActualSpeedforSTA(prevDMS);
        
    }

    private boolean CoverDistance(Double distance) {
        return VSLConfig.coverDistance >= distance;
    }

    private boolean CoverageSpeed(DMSImpl nextDMS) {
        return VSLConfig.coverageSpeed >= nextDMS.getSpeedLimit();
    }

    private void initSTA(DMSImpl currentDMS) {
        currentDMS.setSTA(0);
    }

    private void setActualSpeedforSTA(DMSImpl currentDMS) {
        if(currentDMS.getActualSpeedLimit() == 0)
            currentDMS.setActualVSA(60);
    }
    
}
