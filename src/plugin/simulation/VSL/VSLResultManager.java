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
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLVersion;
//import infra.Period;
//import infra.Section;
//import java.io.File;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Vector;
//import javax.swing.JOptionPane;
//import khta.SimulationResult;
//import util.PropertiesWrapper;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLResultManager {
//    final String NAME = "name";
//    final String DESC = "desc";
//    final String SECTION_NAME = "section.name";
//    final String SECTION_RNODES = "section.rnodes";
//    final String DATAKEY = "KEY";
//    final String CREATED = "created";
//    final String VSLVERSION = "vslversion";
//    
//    final String TYPE = "TYPE";
//    final String TYPE_ID = "TYPE_ID";
//    final String TYPE_STATION = "STATION";
//    final String TYPE_DMS = "DMS";
//    final String PERIOD_START = "PERIOD_START";
//    final String PERIOD_END = "PERIOD_END";
//    final String PERIOD_INTERVAL = "PERIOD_INTERVAL";
//    
//    public final static String SAVE_PROP_DIR = SimulationResult.SAVE_PROP_DIR;
//    public final static String SAVE_VSL_DIR = SAVE_PROP_DIR + File.separator + "VSL";
//    
//    private String name;
//    private String desc;
//    private Date created;
//    transient Section section;
//    private List<String> keys = new ArrayList<String>();
//    private VSLVersion vslversion;
//    private Period speriod;
//    
//    private PropertiesWrapper prop = new PropertiesWrapper();
//    
//    
//    
//    public VSLResultManager(String _name, String _desc, VSLResults vresult){
//        section = vresult.getSection();
//        name = _name;
//        desc = _desc;
//        keys = vresult.getMilePointstoString();
//        created = new Date();
//        vslversion = vresult.getVSLVersion();
//        speriod = vresult.getPeriod();
//        SaveData(keys,vresult);
//    }
//
//    private VSLResultManager(PropertiesWrapper _prop) {
//        prop = _prop.clone();
//        name = prop.get(NAME);
//        desc = prop.get(DESC);
//        created = prop.getDate(CREATED);
//        
//        try{
//            vslversion = VSLVersion.getVSLVersion(prop.getInteger(VSLVERSION));
//        }catch(Exception e){
//            vslversion = null;
//        }
//        
//        try{
//            speriod = new Period(prop.getDate(PERIOD_START), prop.getDate(PERIOD_END), prop.getInteger(PERIOD_INTERVAL));
//        }catch(Exception e){
//            speriod = null;
//        }
//        
//        
//        Section s = new Section(prop.get(SECTION_NAME),"");
//        s.setRnodeIds(prop.getStringList(SECTION_RNODES));
//        
//        keys = prop.getStringList(DATAKEY);
//    }
//    
//    public void save() throws Exception{
//        File MainDir = new File(SAVE_PROP_DIR);
//        if(!MainDir.mkdir() && !MainDir.exists()){
//            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n" + MainDir);
//            return;
//        }
//        
//        File saveDir = new File(SAVE_VSL_DIR);
//        if(!saveDir.mkdir() && !saveDir.exists()){
//            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n" + MainDir);
//            return;
//        }
//        
//        String eName = getCacheFileName(name);
//        String propFileName = SAVE_VSL_DIR + File.separator + eName;
//        prop.save(propFileName, "Simulation Result Title : " + this.name);
//    }
//    
//    /**
//     * 
//     * @param name simulation result name
//     * @return hashed string with MD5
//     */
//    public static String getCacheFileName(String name)
//    {
//        try {
//            MessageDigest m = MessageDigest.getInstance("MD5");
//            m.reset();
//            m.update(name.getBytes());
//            byte[] digest = m.digest();
//            BigInteger bigInt = new BigInteger(1,digest);
//            return bigInt.toString(16);        
//        } catch (NoSuchAlgorithmException ex) {
//            return null;
//        }
//    }
//
//
//    private void SaveData(List<String> keys1, VSLResults vresult) {
//        prop.put(NAME, name);
//        prop.put(DESC, desc);
//        prop.put(CREATED, created);
//        prop.put(VSLVERSION, vslversion.getSID());
//        
//        //Section Info
//        prop.put(SECTION_NAME, section.getName());
//        prop.put(SECTION_RNODES, section.getRNodeIds());
//        prop.put(PERIOD_START,speriod.startDate);
//        prop.put(PERIOD_END, speriod.endDate);
//        prop.put(PERIOD_INTERVAL, speriod.interval);
//        prop.put(DATAKEY, keys1);
//        vresult.saveConfig(prop);
//        saveMainData(vresult);
//    }
//
//    private void saveMainData(VSLResults vresult) {
//
//        //put Station
//        for(Double key : vresult.getStations().keySet()){
//            VSLResultStation rs = vresult.getStations().get(key);
//
//            //set Type
//            prop.put(setKey(key,TYPE), TYPE_STATION);
//            prop.put(setKey(key, TYPE_ID), rs.getID());
//
//            
//            double[] q = rs.getFlows(), qa = rs.getAverageFlows(), k=rs.getDensitys(), u=rs.getSpeeds(), 
//                    ru=rs.getRollingSpeeds(), acc=rs.getAcceleration(), v=rs.getTotalVolumes()
//                    ,bcnt = rs.getBottleneckCounts()
//                    ,cvss = rs.getCurrentVSStoDouble(), pvss = rs.getPreviousVSStoDouble();
//            int[] ca = rs.getControlThreshold();
//            
//            prop.put(setKey(key, VSLTrafficType.FLOW), getCsv(q));
//            prop.put(setKey(key, VSLTrafficType.AVERAGEFLOW), getCsv(qa));
//            prop.put(setKey(key, VSLTrafficType.DENSITY), getCsv(k));
//            prop.put(setKey(key, VSLTrafficType.SPEED), getCsv(u));
//            prop.put(setKey(key, VSLTrafficType.ROLLINGSPEED), getCsv(ru));
//            prop.put(setKey(key, VSLTrafficType.ACCELERATION), getCsv(acc));
//            prop.put(setKey(key, VSLTrafficType.VOLUME), getCsv(v));
//            prop.put(setKey(key, VSLTrafficType.BCNT), getCsv(bcnt));
//            prop.put(setKey(key, VSLTrafficType.CVSS), getCsv(cvss));
//            prop.put(setKey(key, VSLTrafficType.PVSS), getCsv(pvss));
//            prop.put(setKey(key, VSLTrafficType.CONTROLTHRESHOLD), getCsv(ca));
//        }
//        
//        //put DMS
//        for(Double key : vresult.getDMSs().keySet()){
//            VSLResultDMS rd = vresult.getDMSs().get(key);
//            
//            //set Type
//            prop.put(setKey(key,TYPE), TYPE_DMS);
//            prop.put(setKey(key,TYPE_ID), rd.getID());
//            
//            double[] slimit = rd.getSpeedLimit(), isstart = rd.getisStarted();
//            double[] aslimit = rd.getActualSpeedLimit();
//            int[] sta = rd.getSTA();
//            prop.put(setKey(key, VSLTrafficType.SPEEDLIMIT), slimit);
//            prop.put(setKey(key, VSLTrafficType.ISSTARTED), isstart);
//            prop.put(setKey(key, VSLTrafficType.ACTUALSPEEDLIMIT), aslimit);
//            prop.put(setKey(key, VSLTrafficType.SLOWTRAFFICAHEAD), getCsv(sta));
//            
//        }
//        
//    }
//    
//    public VSLResults LoadResults() {
//        VSLResults vresult = new VSLResults(name,created,vslversion);
//        vresult.setPeriod(speriod);
//        for(String key : keys){
//            String type = prop.get(setKey(key,TYPE));
//            
//            if(type.equals(TYPE_STATION)){
//                addStation(key,vresult);
//            }else if(type.equals(TYPE_DMS)){
//                addDMS(key,vresult);
//            }
//        }
//        vresult.loadConfig(prop);
//        return vresult;
//    }
//    
//    private void addStation(String key, VSLResults vresult) {
//        String did = prop.get(setKey(key,TYPE_ID));
//        Double dkey = Double.parseDouble(key);
//        VSLResultStation nstation = new VSLResultStation(dkey,did);
//        
//        //getData
//        double[] q = prop.getDoubleArray(setKey(dkey,VSLTrafficType.FLOW));
//        double[] qa = prop.getDoubleArray(setKey(dkey,VSLTrafficType.AVERAGEFLOW));
//        double[] k = prop.getDoubleArray(setKey(dkey,VSLTrafficType.DENSITY));
//        double[] u = prop.getDoubleArray(setKey(dkey,VSLTrafficType.SPEED));
//        double[] ru = prop.getDoubleArray(setKey(dkey,VSLTrafficType.ROLLINGSPEED));
//        double[] acc = prop.getDoubleArray(setKey(dkey,VSLTrafficType.ACCELERATION));
//        double[] v = prop.getDoubleArray(setKey(dkey,VSLTrafficType.VOLUME));
//        double[] bcnt = prop.getDoubleArray(setKey(dkey,VSLTrafficType.BCNT));
//        double[] cvss = prop.getDoubleArray(setKey(dkey,VSLTrafficType.CVSS));
//        double[] pvss = prop.getDoubleArray(setKey(dkey,VSLTrafficType.PVSS));
//        int[] ca = prop.getIntegerArray(setKey(dkey,VSLTrafficType.CONTROLTHRESHOLD));
//        nstation.addAllDatas(q,qa,k,u,ru,acc,v,bcnt,cvss,pvss,ca);
//        
//        vresult.addStation(dkey,nstation);
//    }
//    
//    private void addDMS(String key, VSLResults vresult) {
//        String did = prop.get(setKey(key,TYPE_ID));
//        Double dkey = Double.parseDouble(key);
//        VSLResultDMS ndms = new VSLResultDMS(dkey,did);
//        
//        //getData
//        double[] slimit = prop.getDoubleArray(setKey(dkey,VSLTrafficType.SPEEDLIMIT));
//        double[] isstarted = prop.getDoubleArray(setKey(dkey,VSLTrafficType.ISSTARTED));
//        double[] aslimit = prop.getDoubleArray(setKey(dkey,VSLTrafficType.ACTUALSPEEDLIMIT));
//        int[] sta = prop.getIntegerArray(setKey(dkey,VSLTrafficType.SLOWTRAFFICAHEAD));
//        ndms.addAllDatas(slimit,isstarted,aslimit,sta);
//        
//        vresult.addDMS(dkey, ndms);
//        
//    }
//    
//    private String getCsv(Vector<String> objs) {
//        StringBuilder sb = new StringBuilder();
//        for(int i=0; i<objs.size(); i++) {
//            if(i!=0) { sb.append(","); }
//            sb.append(objs.get(i));
//        }
//        return sb.toString();        
//    }    
//    
//    private String getCsv(double[] objs) {
//        StringBuilder sb = new StringBuilder();
//        for(int i=0; i<objs.length; i++) {
//            if(i!=0) { sb.append(","); }
//            sb.append(objs[i]);
//        }
//        return sb.toString();        
//    }
//    
//    private String getCsv(int[] objs) {
//        StringBuilder sb = new StringBuilder();
//        for(int i=0; i<objs.length; i++) {
//            if(i!=0) { sb.append(","); }
//            sb.append(objs[i]);
//        }
//        return sb.toString();        
//    }
//
//    private String setKey(Double key, String var) {
//        return String.valueOf(key) + "." + var;
//    }
//    private String setKey(Double key, VSLTrafficType v){
//        return String.valueOf(key) + "." +v.getID();
//    }
//    private String setKey(String key, String var){
//        return key + "." + var;
//    }
//    
//    enum VSLTrafficType {
//        FLOW(1),
//        AVERAGEFLOW(2),
//        DENSITY(3),
//        SPEED(4),
//        ROLLINGSPEED(5),
//        VOLUME(6),
//        BCNT(7),
//        PVSS(8),
//        CVSS(9),
//        ACCELERATION(10),
//        SPEEDLIMIT(11),
//        ISSTARTED(12),
//        CONTROLTHRESHOLD(13),
//        ACTUALSPEEDLIMIT(14),
//        SLOWTRAFFICAHEAD(15);
//        
//        int id;
//        VSLTrafficType(int n){
//            id = n;
//        }
//        
//        public String getID(){
//            return String.valueOf(id);
//        }
//    }
//    
//    public static VSLResultManager load(File ResultFile){
//        return load(ResultFile.getAbsolutePath());
//    }
//    
//    private static VSLResultManager load(String absolutePath) {
//        try{
//            PropertiesWrapper prop = PropertiesWrapper.load(absolutePath);
//            return new VSLResultManager(prop);
//        }catch(Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
