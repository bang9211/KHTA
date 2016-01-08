///*
// * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
// * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package plugin.simulation.VSL;
//
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLStationState;
//import edu.umn.natsrl.infra.infraobjects.DMSImpl;
//import infra.Section;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.TreeMap;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLMilePointList {
//
//    private List<Double> sortedKey = new ArrayList<Double>();
//    private TreeMap<Double, Object> list = new TreeMap<Double,Object>();
//    private TreeMap<Double, VSLMilePointType> type = new TreeMap<Double,VSLMilePointType>();
//    
//    private TreeMap<Integer,String> map = new TreeMap<Integer,String>();
//    private TreeMap<Integer,VSLStationState> stationstates = new TreeMap<Integer,VSLStationState>();
//    private TreeMap<Integer,DMSImpl> dmss = new TreeMap<Integer,DMSImpl>();
//    
//    public VSLMilePointList(List<VSLStationState> st, List<DMSImpl> dms){
//        setList(st,dms);
//    }
//    
//    private void setList(List<VSLStationState> st, List<DMSImpl> dms){
//        for(VSLStationState s : st){
//            list.put(s.getMilePoint(), s);
//            type.put(s.getMilePoint(), VSLMilePointType.STATION);
//        }
//        
//        Section section = st.get(0).getSection();
//        for(DMSImpl d : dms){
//            list.put(d.getMilePoint(section.getName()), d);
//            type.put(d.getMilePoint(section.getName()), VSLMilePointType.DMS);
//        }
//        
//        list.keySet().iterator();
//        type.keySet().iterator();
//        setMilePointListValues();
//    }
//    
//    private void setMilePointListValues(){
//        int i = 0;
//        for(Double key : list.keySet()){
//            VSLMilePointType ty = this.type.get(key);
//            
//            String Name = null;
//            if(ty.isStation()){
//                VSLStationState st = (VSLStationState)list.get(key);
//                Name = st.getID();
//                stationstates.put(i, st);
//            }else if(ty.isDMS()){
//                DMSImpl dm = (DMSImpl)list.get(key);
//                Name = dm.getId();
//                dmss.put(i, dm);
//            }
//            map.put(i, Name);
//            i++;
//        }
//        stationstates.keySet().iterator();
//        dmss.keySet().iterator();
//        map.keySet().iterator();
//    }
//    
//    public TreeMap<Integer,String> getMilePointListLayout(){
//        return map;
//    }
//    
//    public TreeMap<Integer,VSLStationState> getStationStates(){
//        return stationstates;
//    }
//    
//    public TreeMap<Integer,DMSImpl> getDMSs(){
//        return dmss;
//    }
//    
//    public static enum VSLMilePointType{
//        STATION(),
//        DMS();
//        
//        boolean isStation(){return this==STATION;}
//        boolean isDMS(){return this==DMS;}
//    }
//}
