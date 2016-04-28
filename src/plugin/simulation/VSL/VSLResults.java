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
//import edu.umn.natsrl.infra.infraobjects.DMSImpl;
//import infra.Period;
//import infra.Section;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.TreeMap;
//import plugin.simulation.VSL.algorithm.VSLStationState;
//import plugin.simulation.VSL.algorithm.VSLVersion;
//import util.PropertiesWrapper;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLResults implements Comparable{
//    private String Name;
//    private Date created;
//    private Section section;
//    private Period period;
//    private TreeMap<Double,VSLResultStation> station = new TreeMap<Double,VSLResultStation>();
//    private TreeMap<Double,VSLResultDMS> DMSs = new TreeMap<Double,VSLResultDMS>();
//    private TreeMap<Integer,String> map = new TreeMap<Integer,String>();
//    private TreeMap<Integer,VSLResultStation> mapStation = new TreeMap<Integer,VSLResultStation>();
//    private TreeMap<Integer,VSLResultDMS> mapDMSs = new TreeMap<Integer,VSLResultDMS>();
//    private VSLResultConfig config = new VSLResultConfig();
//    
//    VSLVersion vslversion;
//    
//    /**
//     * Initialize VSLResult from VSL MilePointList
//     * @param _section
//     * @param ml 
//     */
//    VSLResults(Section _section, VSLMilePointList ml, VSLVersion vv) {
//        vslversion = vv;
//        section = _section;
//        Name = section.getName();
//        config.setConfig();
//        setNodes(ml);
//    }
//
//    /**
//     * Initialize VSLResult from Saved Data
//     * @param _Name
//     * @param _created 
//     */
//    VSLResults(String _Name, Date _created, VSLVersion vv) {
//        Name = _Name;
//        created = _created;
//        vslversion = vv;
//    }
//
//    /**
//     * Set Station and DMSs from VSLMilePointList
//     * @param ml 
//     */
//    private void setNodes(VSLMilePointList ml) {
//        //init Station
//        for(VSLStationState s : ml.getStationStates().values()){
//            station.put(s.getMilePoint(), new VSLResultStation(s));
//            System.out.println("test Mile Point : "+s.getID()+"-"+s.getMilePoint());
//        }
//        
//        //init DMS
//        for(DMSImpl d : ml.getDMSs().values()){
//            DMSs.put(d.getMilePoint(section.getName()),new VSLResultDMS(d.getMilePoint(section.getName()),d.getId()));
//            System.out.println("test Mile Point(DMS) : "+d.getId()+d.getMilePoint(section.getName()));
//        }
//        
//        updateMap();
//    }
//    
//    /**
//     * get Station Data
//     * @return TreeMap<MilePoint, VSLResultStation>
//     */
//    public TreeMap<Double,VSLResultStation> getStations(){
//        return station;
//    }
//    
//    /**
//     * get DMS Datas
//     * @return TreeMap<MilePoint, VSLResultDMS>
//     */
//    public TreeMap<Double,VSLResultDMS> getDMSs(){
//        return DMSs;
//    }
//    
//    /**
//     * get MilePoint Map 
//     * @return TreeMap<index, Label>
//     */
//    public TreeMap<Integer,String> getMilePointListLayout(){
//        return map;
//    }
//    
//    /**
//     * get Stations included Map index
//     * @return TreeMap<index, VSLResultStation>
//     */
//    public TreeMap<Integer,VSLResultStation> getMapStations(){
//        return mapStation;
//    }
//    
//    /**
//     * get DMS included Map index
//     * @return TreeMap<index, VSLResultDMS>
//     */
//    public TreeMap<Integer, VSLResultDMS> getMapDMSs(){
//        return mapDMSs;
//    }
//    
//    /**
//     * get MilePoint
//     * @return 
//     */
//    public List<Double> getMilePoints(){
//        List<Double> mp = new ArrayList<Double>();
//        mp.addAll(station.keySet());
//        mp.addAll(DMSs.keySet());
//        Collections.sort(mp);
//        return mp;
//    }
//    
//    /**
//     * get MilePoint to String
//     * @return 
//     */
//    public List<String> getMilePointstoString(){
//        List<String> mp = new ArrayList<String>();
//        for(Double d : getMilePoints()){
//            if(d != null){
//                mp.add(String.valueOf(d));
//            }
//        }
//        return mp;
//    }
//    
//    /**
//     * add DMS from saved Data
//     * @param dkey
//     * @param ndms 
//     */
//    void addDMS(Double dkey, VSLResultDMS ndms) {
//        DMSs.put(dkey, ndms);
//        updateMap();
//    }
//    
//    /**
//     * Add Station from saved data
//     * @param dkey
//     * @param nstation 
//     */
//    void addStation(Double dkey, VSLResultStation nstation) {
//        station.put(dkey, nstation);
//        updateMap();
//    }
//
//    /**
//     * get Section
//     * @return 
//     */
//    public Section getSection() {
//        return section;
//    }
//    
//    /**
//     * get Created
//     * @return 
//     */
//    public Date getCreated(){
//        return created;
//    }
//    
//    @Override
//    public String toString(){
//        String n = Name;
//        n += ", Date : "+created.toString();
//        if(vslversion != null){
//            n += ", VSLVersion : "+vslversion.toString();
//        }
//        return n;
//    }
//    
//    @Override
//    public int compareTo(Object o) {        
//        VSLResults r = (VSLResults)o;        
////        return this.name.compareTo(r.name);
//        Calendar current = Calendar.getInstance();
//        Calendar com = Calendar.getInstance();
//        current.setTime(this.getCreated());
//        com.setTime(r.getCreated());
//        
//        if(current.getTimeInMillis() > com.getTimeInMillis())
//            return -1;
//        else
//            return 1;
//        
//    }
//
//    /**
//     * get Result name
//     * @return 
//     */
//    public String getName() {
//        return Name;
//    }
//
//    /**
//     * get Infra Data by MilePoint key
//     * @param d
//     * @return 
//     */
//    public VSLResultInfra getDataByKey(Double d) {
//        if(this.station.get(d) != null){
//            return station.get(d);
//        }else if(DMSs.get(d) != null){
//            return DMSs.get(d);
//        }else{
//            return null;
//        }
//    }
//
//    /**
//     * is Station by Milepoint key
//     * @param key
//     * @return 
//     */
//    public boolean isStationByKey(double key) {
//        if(station.get(key) != null){
//            return true;
//        }else{
//            return false;
//        }
//    }
//    
//    /**
//     * is DMS by milepoint key
//     * @param key
//     * @return 
//     */
//    public boolean isDMSByKey(double key){
//        if(DMSs.get(key) != null){
//            return true;
//        }else{
//            return false;
//        }
//    }
//    
//    /**
//     * get Data Length
//     * @return 
//     */
//    public int getDataLength(){
//        if(station.lastEntry() != null && station.lastEntry().getValue().getRollingSpeeds() != null){
//            return station.lastEntry().getValue().getRollingSpeeds().length;
//        }else if(DMSs.lastEntry() != null && DMSs.lastEntry().getValue().getSpeedLimit() != null){
//            return DMSs.lastEntry().getValue().getSpeedLimit().length;
//        }else{
//            return 0;
//        }
//    }
//
//    /**
//     * Update Map Datas
//     */
//    private void updateMap() {
//        int i = 0;
//        clearMap();
//        for(Double key : getMilePoints()){
//            int cidx = i;
//            for(int k = cidx; k < (int)(key*10); k++){
//                map.put(k, String.valueOf(((double)k/10)));
//                i++;
//            }
//            String name = null;
//            if(station.get(key) != null){
//                name = station.get(key).getID();
//                mapStation.put(i, station.get(key));
//            }else if(DMSs.get(key) != null){
//                name = DMSs.get(key).getID();
//                mapDMSs.put(i, DMSs.get(key));
//            }
//            
//            if(name != null){
//                map.put(i, name);
//                i++;
//            }
//        }
//        
//        map.keySet().iterator();
//    }
//
//    /**
//     * clear Map Datas
//     */
//    private void clearMap() {
//        map.clear();
//        mapStation.clear();
//        mapDMSs.clear();
//    }
//
//    public ArrayList<VSLResultStation> getNearStationbyID(String iD) {
//        Integer key = null;
//        ArrayList<VSLResultStation> rstation = new ArrayList<VSLResultStation>();
//        for(int k : mapStation.keySet()){
//            VSLResultStation v = mapStation.get(k);
//            if(v.getID().equals(iD)){
//                key = k;
//                break;
//            }
//        }
//        
//        if(key == null){
//            return null;
//        }
//        
//        if(mapStation.lowerEntry(key) != null){
//            rstation.add(mapStation.lowerEntry(key).getValue());
//        }
//        
//        if(mapStation.get(key) != null){
//            rstation.add(mapStation.get(key));
//        }
//        
//        if(mapStation.higherEntry(key) != null){
//            rstation.add(mapStation.higherEntry(key).getValue());
//        }
//        
//        return rstation;
//    }
//
//    public VSLVersion getVSLVersion() {
//        return vslversion;
//    }
//
//    public void setPeriod(Period p){
//        period = p.clone();
//    }
//    
//    public Period getPeriod() {
//        if(period == null)
//            return null;
//        else
//            return period.clone();
//    }
//    
//    public boolean equalData(Object tm){
//        VSLResults tv = (VSLResults)tm;
//        return tv.Name.equals(this.Name);
//            
//    }
//
//    void saveConfig(PropertiesWrapper prop) {
//        config.save(prop);
//    }
//
//    void loadConfig(PropertiesWrapper prop) {
//        config.load(prop);
//    }
//
//    public VSLResultConfig getConfig() {
//        return config;
//    }
//}
