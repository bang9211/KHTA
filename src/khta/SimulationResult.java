/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth, US) and
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

package khta;

import evaluation.Interval;
import infra.Period;
import infra.Section;
import infra.infraobject.Detector;
import infra.infraobject.RNode;
import infra.simobjects.SimDetector;
import infra.simobjects.SimObjects;
import infra.type.TrafficType;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import util.FileHelper;
import util.PropertiesWrapper;

/**
 *
 * @author Chongmyung Park
 */
public class SimulationResult implements Comparable {
    final String K_NAME = "name";
    final String K_DESC = "desc"; 
    final String K_CREATED = "created";
    final String K_PERIOD_START = "period.start";
    final String K_PERIOD_END = "period.end";
    final String K_PERIOD_INTERVAL = "period.interval";
    final String K_SECTION_NAME = "section.name"; 
    final String K_SECTION_DESC = "section.desc";
    final String K_SECTION_RNODES = "section.rnodes" ;
    final String K_SECTION_DETECTORS = "section.detectors";
    final String K_SECTION_CORRIDOR = "section.corridor";
    final String K_RUNNING_INTV = "rintv";
    public final static String SAVE_PROP_DIR = "simulationresults";    
    public final static String SAVE_DATA_DIR = SAVE_PROP_DIR+File.separator+"data";
    public final static String SAVE_PROP_DIR_SUB = SAVE_PROP_DIR+ File.separator + "subdata";    
    public final static String SAVE_DATA_DIR_SUB = SAVE_PROP_DIR + File.separator + "subdata" +File.separator+"data";
    private String name;
    private String desc;
    private Date created;
    private int RIntv;
    
    transient Section section;
    private Period period;
    
    private PropertiesWrapper prop = new PropertiesWrapper();
    private PropertiesWrapper data = new PropertiesWrapper();
    
    //Several Save
    private boolean isSubData = false;
    private String DataKey = null;
    private String DataLength = null;
    final String K_DATAKEY = "dkey";
    final String K_DATALENGTH = "dlength";
    public final static int DESC_SNAME = 0;
    public final static int DESC_RSEED = 1;
    public final static int DESC_POINT = 2;
    public final static int DESC_KEY = 3;
    
    public SimulationResult(String name, String desc, Section section, Period period,int RunningInterval) {
        SimulationResultInit(name,desc,section,period,RunningInterval);
    }
    
    public SimulationResult(String name, String desc, Section section, Period period, int RunningInterval, boolean subData, String _key) {
        this.isSubData = subData;
        this.DataKey = _key;
        SimulationResultInit(name,desc,section,period,RunningInterval);
    }
    public SimulationResult(String name, String desc, Section section, Period period, int RunningInterval, boolean subData, String _key, int _length) {
        this.isSubData = subData;
        this.DataKey = _key;
        this.DataLength = String.valueOf(_length);
        SimulationResultInit(name,desc,section,period,RunningInterval);
    }
    
    private void SimulationResultInit(String name, String desc, Section section, Period period, int RunningInterval){
        this.name = name;
        this.desc = desc;
        this.section = section;
        this.period = period;
        this.RIntv = RunningInterval;
        this.created = new Date();  
        
        prop.put(K_NAME, name);
        prop.put(K_DESC, desc);
        prop.put(K_RUNNING_INTV, RIntv);
        if(this.DataKey != null){
            prop.put(K_DATAKEY, this.DataKey);
        }
        if(this.DataLength != null)
            prop.put(K_DATALENGTH, this.DataLength);
        
        prop.put(K_CREATED, created);
        prop.put(K_SECTION_NAME, section.getName());
        prop.put(K_SECTION_DESC, section.getDesc());
        prop.put(K_SECTION_DETECTORS, getDetectorIds(section));
        prop.put(K_SECTION_RNODES, section.getRnodeIds());
        prop.put(K_SECTION_CORRIDOR, section.getCorridorID());
        prop.put(K_PERIOD_START, period.startDate);
        prop.put(K_PERIOD_END, period.endDate);
        prop.put(K_PERIOD_INTERVAL, period.interval);
        
        if(!IsListData()){
            readTrafficDataFromDetectors();
        }else
            System.out.println("list data");
        
    }
    
    private SimulationResult(PropertiesWrapper p) {
        this.prop = p.clone();
        this.name = prop.get(K_NAME);
        this.desc = prop.get(K_DESC);
        RIntv = prop.getInteger(K_RUNNING_INTV) == 0 ? Interval.getMinTMCInterval() : prop.getInteger(K_RUNNING_INTV);
        
        try{
            this.DataKey = prop.get(K_DATAKEY);
        }catch(Exception e){
            System.out.println("Not Found Data Key");
        }
        
        try{
            this.DataLength = prop.get(K_DATALENGTH);
        }catch(Exception e){
            System.out.println("Not Found Data Length");
        }
        
        Section s = new Section(prop.get(K_SECTION_NAME), prop.get(K_SECTION_DESC), prop.get(K_SECTION_CORRIDOR));
        s.setRnodeIds(prop.getStringList(K_SECTION_RNODES));///////////////////////////////
        this.section = s;
        this.period = new Period(prop.getDate(K_PERIOD_START), prop.getDate(K_PERIOD_END), prop.getInteger(K_PERIOD_INTERVAL));
        this.created = prop.getDate(K_CREATED);
    }       
    
    public int getRunningSeconds(){
            return this.RIntv;
    }
    public Interval getRunningInterval(){
            return Interval.get(RIntv);
    }
 
    public boolean IsListData(){
        if(this.DataKey != null && !this.isSubData)
            return true;
        else
            return false;
    }
    
    public int getSimulationLegnth(){
        if(this.DataLength == null){
            return 0;
        }else{
            return Integer.parseInt(this.DataLength);
        }
    }
    public String getDataKey(){
        return this.DataKey;
    }
    public void setSubData(boolean _f){
        this.isSubData = _f;
    }
    public void save() throws Exception {
        save(true);
    }
    
    public void save(boolean readTraffic) throws Exception {
        File MainDir = new File(this.SAVE_PROP_DIR);
        if (!MainDir.mkdir() && !MainDir.exists()) {
            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n" + MainDir);
            return;
        }        
        
        File savePropDir = new File(this.getPropDIR());
        if (!savePropDir.mkdir() && !savePropDir.exists()) {
            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n" + savePropDir);
            return;
        }        
        File saveDataDir = new File(this.getDataDIR());
        if (!saveDataDir.mkdir() && !saveDataDir.exists()) {
            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n" + saveDataDir);
            return;
        } 
        
        
        String eName = getCacheFileName(this.name);
        String propFileName = this.getPropDIR() + File.separator + eName;
        prop.save(propFileName, "Simulation Result Title : " + this.name);

        if(!this.IsListData()){
            if(readTraffic) readTrafficDataFromDetectors();

            String dataFileName = this.getDataDIR() + File.separator + eName;
            data.save(dataFileName, "Simulation Result Title : " + this.name);
        }
    }

    // add traffic data into property
    private void readTrafficDataFromDetectors() {
        for(RNode rn : section.getRNodesWithExitEntrance()) {
            for(Detector d : rn.getDetectors().values()) {
                addTrafficData(d);
            }
        }        
    }    
    
    private void addTrafficData(Detector d) {
        double[] k=d.getDensity(), q=d.getFlow(), u=d.getSpeed(), v=d.getVolume();
//        System.err.println("-"+d.getDensity().length+ ", "+d.getFlow().length+", "+d.getOccupancy().length
//                + ", "+d.getScan().length+ ", "+d.getSpeed().length+", "+d.getVolume().length);

        System.err.println("-"+d.getDensity().length+ ", "+d.getFlow().length+ ", "+d.getSpeed().length+", "+d.getVolume().length);

        data.put(getKey(d.getID(), TrafficType.DENSITY), getCsv(k));
        data.put(getKey(d.getID(), TrafficType.FLOW), getCsv(q));
        //data.put(getKey(d.getID(), TrafficType.OCCUPANCY), getCsv(o));
        //data.put(getKey(d.getId(), TrafficType.SCAN), getCsv(c));
        data.put(getKey(d.getID(), TrafficType.SPEED), getCsv(u));
        data.put(getKey(d.getID(), TrafficType.VOLUME), getCsv(v));        
    }
    
    public void setTrafficDataToDetectors(){
//        SimObjects simObjects = SimObjects.getInstance();
//        setTrafficDataToDetectors(simObjects);
    }
    
    public void setTrafficDataToDetectors(SimObjects sim) {
        if(!this.IsListData()){
            
            String dataFile = this.getDataDIR() + File.separator + SimulationResult.getCacheFileName(name);
            data = PropertiesWrapper.load(dataFile);        
            SimObjects simObjects = sim;
            List<String> detectorIds = prop.getStringList(K_SECTION_DETECTORS);
            for(String detector_id : detectorIds) {
                SimDetector sd = simObjects.getDetector(detector_id);
                sd.setTrafficData(TrafficType.DENSITY, data.getDoubleArray(getKey(detector_id, TrafficType.DENSITY)));
                sd.setTrafficData(TrafficType.FLOW, data.getDoubleArray(getKey(detector_id, TrafficType.FLOW)));
                sd.setTrafficData(TrafficType.OCCUPANCY, data.getDoubleArray(getKey(detector_id, TrafficType.OCCUPANCY)));
                //sd.setTrafficData(TrafficType.SCAN, data.getDoubleArray(getKey(detector_id, TrafficType.SCAN)));
                sd.setTrafficData(TrafficType.SPEED, data.getDoubleArray(getKey(detector_id, TrafficType.SPEED)));
                sd.setTrafficData(TrafficType.VOLUME, data.getDoubleArray(getKey(detector_id, TrafficType.VOLUME)));
            }
        }else{
            System.out.println("this is several Model");
        }
    }   
    
    public void AddTrafficDataToDetectors(){
//        SimObjects simObjects = SimObjects.getInstance();
//        AddTrafficDataToDetectors(simObjects);
    }
    
    public void AddTrafficDataToDetectors(SimObjects sim) {
        if(!this.IsListData()){
            String dataFile = this.getDataDIR() + File.separator + SimulationResult.getCacheFileName(name);
            data = PropertiesWrapper.load(dataFile);        
            SimObjects simObjects = sim;
            List<String> detectorIds = prop.getStringList(K_SECTION_DETECTORS);
            for(String detector_id : detectorIds) {
                SimDetector sd = simObjects.getDetector(detector_id);
                sd.addTrafficData(TrafficType.DENSITY, data.getDoubleArray(getKey(detector_id, TrafficType.DENSITY)));
                sd.addTrafficData(TrafficType.FLOW, data.getDoubleArray(getKey(detector_id, TrafficType.FLOW)));
                sd.addTrafficData(TrafficType.OCCUPANCY, data.getDoubleArray(getKey(detector_id, TrafficType.OCCUPANCY)));
//                sd.addTrafficData(TrafficType.SCAN, data.getDoubleArray(getKey(detector_id, TrafficType.SCAN)));
                sd.addTrafficData(TrafficType.SPEED, data.getDoubleArray(getKey(detector_id, TrafficType.SPEED)));
                sd.addTrafficData(TrafficType.VOLUME, data.getDoubleArray(getKey(detector_id, TrafficType.VOLUME)));
            }
        }else{
            System.out.println("this is several Model");
        }
    }

   
    /**
     * Load serialized simulation result
     * @return simulation result
     */
    public static SimulationResult load(File simResultFile)
    {
        return load(simResultFile.getAbsolutePath());
    }   
    
    public static SimulationResult load(String simResultPath)
    {
        try {            
            PropertiesWrapper prop = PropertiesWrapper.load(simResultPath);
            return new SimulationResult(prop);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }          
    }       
    

    /**
     * 
     * @param name simulation result name
     * @return hashed string with MD5
     */
    public static String getCacheFileName(String name)
    {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(name.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            return bigInt.toString(16);        
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }      

    public void update(String name, String desc) {
        try {            
            boolean samename = this.name.equals(name); 
            String originalName = this.name;
            File origin = new File(getPropPath(this.name));
            File target = new File(getPropPath(name));
            
            if(!name.equals(this.name) && target.exists()) {
                JOptionPane.showMessageDialog(null, "Use another name. (Already exists)");
                return;
            }
            if(name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name is required");
                return;            
            }

            prop.put(K_NAME, name);
            prop.put(K_DESC, desc);
            if(this.DataKey != null)
                prop.put(K_DATAKEY, this.DataKey);
            if(this.DataLength != null)
                prop.put(K_DATALENGTH, this.DataLength);
            
            this.name = name;
            this.desc = desc;

            this.save(false);            
            
            if(!samename) {
                File originDataFile = new File(this.getDataPath(originalName));
                FileHelper.copy(originDataFile, new File(getDataPath(name)));                
                originDataFile.delete();
                origin.delete();
            }
            
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fail");            
        }
    }

    private String getPropPath(String fileName) {
        return getPropDIR() + File.separator + getCacheFileName(fileName);
    }
    
    private String getDataPath(String fileName) {
        return getDataDIR() + File.separator + getCacheFileName(fileName);       
    }    
    private String getPropDIR(){
        if(this.isSubData)
            return SAVE_PROP_DIR_SUB;
        else
            return SAVE_PROP_DIR;
        
    }
    private String getDataDIR(){
        if(this.isSubData)
            return SAVE_DATA_DIR_SUB;
        else
            return SAVE_DATA_DIR;
        
    }
    private String getDetectorIds(Section section) {
        Vector<String> ids = new Vector<String>();
        for(RNode rn : section.getRNodesWithExitEntrance()) {
            for(Detector d : rn.getDetectors().values()) {
                ids.add(d.getID());
            }
        }                
        return getCsv(ids);
    }
    
    private String getKey(String did, TrafficType tType) {
        return did+"_"+tType.getTrafficTypeId();
    }
    
    private Vector<String> getVector(String str) {        
        String[] arr = str.split(",");
        if(arr.length == 0) return new Vector<String>();
        return new Vector<String>(Arrays.asList(arr));
    }
    
    private String getCsv(Vector<String> objs) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<objs.size(); i++) {
            if(i!=0) { sb.append(","); }
            sb.append(objs.get(i));
        }
        return sb.toString();        
    }    
    
    private String getCsv(double[] objs) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<objs.length; i++) {
            if(i!=0) { sb.append(","); }
            sb.append(objs[i]);
        }
        return sb.toString();        
    }

    @Override
    public int compareTo(Object o) {        
        SimulationResult r = (SimulationResult)o;        
//        return this.name.compareTo(r.name);
        Calendar current = Calendar.getInstance();
        Calendar com = Calendar.getInstance();
        current.setTime(this.getCreated());
        com.setTime(r.getCreated());
        
        if(current.getTimeInMillis() > com.getTimeInMillis())
            return -1;
        else
            return 1;
        
    }
    
    public Date getCreated() {
        return created;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public Period getPeriod() {
        return period.clone();
    }
    public Period getPeriod(int start_hour, int start_min){
        if( ( start_hour >= 0 && start_hour < 24) && ( start_min >= 0 && start_min < 60)) {
            Date sDate = period.startDate;
            Date eDate = period.endDate;
            int diff = (int)(eDate.getTime() - sDate.getTime());
            Calendar c;
            c = Calendar.getInstance();
            
            c.set(Calendar.HOUR_OF_DAY, start_hour);
            c.set(Calendar.MINUTE, start_min);
            c.set(Calendar.SECOND, 0);
            sDate = c.getTime();
            c.add(Calendar.MILLISECOND, diff);
            eDate = c.getTime();
            this.period = new Period(sDate, eDate, 30);
        }
        return period.clone();
    }

    public Section getSection() {
        return section;
    }
    public String getDESC(int key){
        if(this.desc == null)
            return null;
        String[] d = this.desc.split(",");
        
        if(key >= d.length)
            return null;
        
        return d[key];
    }
    @Override
    public String toString() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.getCreated());
        String created = String.format("%02d-%02d-%02d", c.get(Calendar.YEAR),  c.get(Calendar.MONTH)+1,  c.get(Calendar.DATE));
        String rseed = "";
        if(!this.IsListData()){
            rseed = "Single Data";
        }else{
            rseed = this.DataLength + " seeds";
        }
        return created + " - "+this.getSection()+" - "+this.name + " - (Random Seed info : "+rseed+")";
    }


}
