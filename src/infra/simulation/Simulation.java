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

import evaluation.Interval;
import infra.Section;
import infra.infraobject.Detector;
import infra.infraobject.Station;
import infra.simobjects.SimDMS;
import infra.simobjects.SimDetector;
import infra.simobjects.SimMeter;
import infra.simobjects.SimObjects;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JPanel;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.jawin.COMException;
import vissimcom.ComError;
import vissimcom.IStepListener;
import vissimcom.ITravelTimeListener;
import vissimcom.VISSIMController;
import vissimcom.VISSIMHelper;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class Simulation extends Thread implements IStepListener, ITravelTimeListener{
    protected Section section;
    protected String caseFile;
    protected int seed;
    protected VISSIMController vc;
    protected SimObjects simObjects;
    protected ArrayList<SimMeter> meters = new ArrayList<SimMeter>();
    protected ArrayList<SimDetector> detectors = new ArrayList<SimDetector>();
    protected HashMap<String, ArrayList<Double>> travelTimes = new HashMap<String, ArrayList<Double>>();
    protected ArrayList<SimDMS> simDMSs = new ArrayList<SimDMS>();
    
    protected ArrayList<StationState> stationStates;
    protected ArrayList<EntranceState> entrancestates;
    
    private ISimEndSignal signalListener;            
    protected int samples = 0;    
    
    private int simDuration = 0;
    
    protected int simcount=0;
    
    public SectionHelper sectionHelper;
    
    private VISSIMVersion version;
    
    private boolean isStop = false;
    
    private boolean isDebug_StationInfo = true;
    private boolean isDebug_EntranceInfo = true;
    protected int runInterval = Interval.I30SEC.getSecond();
    protected SimInterval simulationInterval;
    protected int DebugInterval = 10;
    //chart Option
    protected JPanel chartPanel = null;
    
    public Simulation(String caseFile, int seed, Section section, VISSIMVersion v, 
            SimInterval sInterval, SimObjects simObjects){
        try{
            runInterval = sInterval.getSimulationRunningInterval().getSecond();
            simulationInterval = sInterval;
//            simulationInterval.setSimulationGroup(simGroup);
            this.caseFile = caseFile;
            this.seed = seed;
            this.section = section;
            version = v;
            this.simObjects = simObjects;
//            loadSignalGroupFromCasefile(this.caseFile);
            detectors = loadDetectorFromCasefile(this.caseFile);
            checkDetector(detectors);
            this.simDuration = VISSIMHelper.loadSimulationDuration(this.caseFile);
            simDMSs = loadDMSsFromCasefile(this.caseFile);
            sectionHelper = new SectionHelper(section, detectors, meters, simDMSs, sInterval, simObjects);
            saveSimulationConfig();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void SimInit(){
        
    }
    
    @Override
    public void run(){
        isStop = false;
        System.out.print("Starting VISSIM Simulator......");
        vc = new VISSIMController(simObjects);
        ComError ce = ComError.getErrorbyID(vc.initialize(caseFile,seed,version,runInterval));
        if(!ce.isCorrect()){
            SimulationStop(ce.getErrorType());
            return;
        }
        
        if(!simulationInterval.isCorrect()){
                SimulationStop(-1);
                return;
        }
        
        System.out.println("Ok");
        
        System.out.print("VISSIM initializing.......");
        vc.initializeMetering(100);
        vc.initializeTravelTimeMeasuring();
        vc.addTravelTimeListener(this);
        vc.addStepListener(1, this);
        this.signalListener.signalEnd(-1);
        System.out.println("Ok");
        
         int totalExecutionStep = vc.getTotalExecutionStep();
        int totalSamples = totalExecutionStep / (runInterval * 10);
        System.out.println("total Running Time : " +totalExecutionStep+", "+totalSamples);
        samples = 0;
        long sTime = new Date().getTime();
        simcount=0; //30sec interval
        simulationInterval.setRunTime(simcount);
        
        /**
         * Running Initialize
         */
        RunningInitialize();
        
        while(true){
            if(isStop)
                break;
            
            ExecuteBeforeRun();
            vc.run(runInterval);
            simcount+=runInterval;
            simulationInterval.setRunTime(simcount);
            samples++;
            ExecuteAfterRun();
            
            if(isInterval(simcount)){
                System.out.println("\nData Log (Total Sec : "+simcount+")");
                DebugMassage();
            }
            
            if(samples >= totalSamples) {
//                metering.writeLog();
                break;
            }
            
        }
        ExecuteEnd();
        int elapsedSeconds = (int) ( ( new Date().getTime() - sTime ) / 1000 );
        int h = elapsedSeconds / 3600;
        int m = ( elapsedSeconds % 3600 ) / 60;
        int s = ( elapsedSeconds % 3600 ) % 60;
        System.out.println("Simulation has been done (run time="+String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s)+")");
//        writeTTLog();
        this.signalListener.signalEnd(0);
        
        this.vc.stop();
        this.vc.close();
    }
    
    public void simulationStop(){
        isStop = true;
        vc.stop();
        vc.close();
    }
    
    protected void RunningInitialize() {
        stationStates = sectionHelper.getStationStates();
        entrancestates = sectionHelper.getEntranceStates();
    }
    
    protected void ExecuteBeforeRun() {
        
    }

    protected void ExecuteAfterRun() {
        updateStates();
    }
    
    protected void DebugMassage(){
        DisplayStationState();
        DisplayMeterState(entrancestates);
    }
    
    protected void ExecuteEnd() {
        
    }
    
    private void loadSignalGroupFromCasefile(String casefile) throws IOException{
        System.out.println("asdf");
        ArrayList<String> sgs = VISSIMHelper.loadSignalGroupsFromCasefile(caseFile);
        System.out.println("zxcv");
        if(sgs == null){
            System.out.println("Cannot find signal head(meter) in case file");
            System.exit(-1);
        }
        
        for(String signal : sgs) {
            String name = signal;
            boolean isDual = false;
            if(name.contains("_L")) {
                name = name.split("_")[0];
                isDual = true;
            }
            if(name.contains("_R")) continue;            
            
            SimMeter sd = simObjects.getMeter(name);
            if(sd.getID() != null){
                if(isDual) sd.setMeterType(SimMeter.MeterType.DUAL);
                else sd.setMeterType(SimMeter.MeterType.SINGLE);

                meters.add(sd);
            }
        }
    }
    
    public ArrayList<SimMeter> getSimMeter(){
        return meters;
    }
    
    public void setVissimVisible(boolean b){
        try {
            vc.setVisible(b);
        } catch (COMException ex) {
            ex.printStackTrace();
        }
    }
    
    private void writeTTLog() {
        try {
            String prefix = "NEW_";
            WritableWorkbook workbook = Workbook.createWorkbook(new File(getFileName(prefix+"TT_LOG", "xls")));
            WritableSheet sheet = workbook.createSheet("tt", 0);
            Iterator<String> itr = this.travelTimes.keySet().iterator();
            int col = 0;
            while(itr.hasNext()) {
                String key = itr.next();
                sheet.addCell(new Label(col, 0, key));
                ArrayList<Double> data = this.travelTimes.get(key);
                int row = 1;
                for(Double d : data) {
                    sheet.addCell(new Number(col, row++, d));
                }
                col++;
            }
            
            
            WritableSheet configSheet = workbook.createSheet("configurations", 1);
            int r=0;
            configSheet.addCell(new Label(1, r++, "No Metering"));
            configSheet.addCell(new Label(0, r++, "Case File"));
            configSheet.addCell(new Label(0, r++, "Random Seed"));
            r = 1;                
            configSheet.addCell(new Label(1, r++, this.caseFile));
            configSheet.addCell(new Number(1, r++, this.seed));                
            
            workbook.write();
            workbook.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getFileName(String name, String ext) {
        String filepath = name + "." + ext;
        int count = 0;
        while (true) {
            File file = new File(filepath);
            if (!file.exists()) {
                break;
            }
            filepath = name + " (" + (++count) + ")" + "." + ext;
        }

        return filepath;
    }    

    @Override
    public void runStepListener() {
        for(SimMeter meter : meters){
            updateRampmeter(meter);
        }
    }
    
    /**
     * update lamp of ramp meter every simulation step
     * @param meter
     */
    private void updateRampmeter(SimMeter meter) {
        if (meter == null || !meter.isEnabled()){
            return; 
        }       
        meter.setCurrentSimTime(vc.getCurrentTime());
        meter.updateLamp(vc);
    }

    @Override
    public void readTravelTime(String[] travelTimeIds, double[] travelTimes) {
        for(int i=0; i<travelTimeIds.length; i++) {
            String ttid = travelTimeIds[i];
            double tt = travelTimes[i];
            
            ArrayList<Double> ttData = this.travelTimes.get(ttid);
            if(ttData == null) {
                ttData = new ArrayList<Double>();
                this.travelTimes.put(ttid, ttData);
            }
            //System.out.println("TravelTime : " + ttid + " = " + String.format("%.2f", tt));
            ttData.add(tt);
        }
    }
    
    public void setSignalListener(ISimEndSignal signalListener) {
        this.signalListener = signalListener;
    }

    public void setVISSIMVersion(VISSIMVersion selectedItem) {
        version = selectedItem;
    }

    private boolean isInterval(int count) {
        return count % DebugInterval == 0;
    }

    private void updateStates() {
        if(entrancestates.isEmpty())
            return;
        else{
            //Update Entrance
            for(EntranceState e : entrancestates){
                e.updateState();
            }
        }
        //Update Station
        for(StationState cs : stationStates){
            cs.updateState(SimulationGroup.DEFAULT);
        }
    }

    public void DisplayMeterState(ArrayList<EntranceState> enstates) {
        if(this.isDebug_EntranceInfo){
            System.err.println("clearlog");
            System.err.println("=====Metering State======================");
            for(int i=0;i<enstates.size();i++){
                EntranceState es = enstates.get(i);
                if(es.hasMeter())
                    System.err.println(es.meter.getID() + " : " + "Queue Demand="+es.getQueueVolume()+", Passage="+es.getPassageVolume()+", Rate="+es.getCurrentRate());
            }
        }
    }

    public void DisplayStationState() {
        if(this.isDebug_StationInfo){
            //for Station debuging
            for (int i = 0; i < stationStates.size(); i++) {
                System.out.println(stationStates.get(i).getID() + " : T_Q="+String.format("%.1f",stationStates.get(i).getFlow())
                        + " A_Q="+String.format("%.1f",stationStates.get(i).getIntervalAverageLaneFlow(SimulationGroup.DEFAULT))
                        + " k=" +String.format("%.1f", stationStates.get(i).getIntervalAggregatedDensity(SimulationGroup.DEFAULT, 1))
                        + " u=" + String.format("%.1f", stationStates.get(i).getIntervalAggregatedSpeed(SimulationGroup.DEFAULT, 1))
                        + " v=" + stationStates.get(i).getIntervalVolume(SimulationGroup.DEFAULT));
            }
        }
    }
    
    private ArrayList<SimDMS> loadDMSsFromCasefile(String casefile){
        ArrayList<SimDMS> simdms = new ArrayList<SimDMS>();
        try {
            ArrayList<String> dmss = VISSIMHelper.loadDMSsFromCasefile(this.caseFile);
            for(String d : dmss){
                simdms.add(simObjects.getDms(d));
            }
            return simdms;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * loadDetector from Casefile
     * @param caseFile
     * @return 
     */
    private ArrayList<SimDetector> loadDetectorFromCasefile(String caseFile) {
        ArrayList<SimDetector> simDets = new ArrayList<SimDetector>();
        ArrayList<String> dets = VISSIMHelper.loadDetectorsFromCasefile(caseFile);
        for(String det : dets) {
            simDets.add(simObjects.getDetector(det));
        }
        return simDets;
    }

    private void checkDetector(ArrayList<SimDetector> detectors) {
        System.out.println("Check Detector VISSIM to TICAS Section");
        for(Station s : section.getStations()){
            System.out.println("Check Station - "+s.getID());
            for(Detector d : s.getDetectorList()){
                boolean hasdec = false;
                for(SimDetector sd : detectors){
                    if(sd.getID().equals(d.getID()))
                        hasdec = true;
                }
                if(!hasdec){
                    System.out.println(d.getID()+ "-missing");
                }else
                    System.out.println(d.getID()+ "-ok");
                
            }
        }
    }

        private void saveSimulationConfig() {
                SimulationConfig.RunningInterval = runInterval;
                SimulationConfig.saveConfig();
        }

        private void SimulationStop(int errorType) {
                this.signalListener.signalEnd(errorType);
                this.vc.stop();     
                this.vc.close();
        }
    
    public static interface ISimEndSignal {
        public void signalEnd(int code);
    }
    
    public int getSamples() {
        return samples;
    }
    
    public int getDuration(){
        return this.simDuration;
    }
    
    public void setRandomSeed(int s){
        this.seed = s;
    }
    public void setDebugInterval(int t){
        this.DebugInterval = t;
    }
    
    public int getRunningInterval(){
            return runInterval;
    }
    public int getDebugIntervalIndex(){
        return this.DebugInterval / runInterval;
    }
    public void setDebugStationInfo(boolean is){
        this.isDebug_StationInfo = is;
    }
    public void setDebugEntranceInfo(boolean is){
        this.isDebug_EntranceInfo = is;
    }
    
    public void setChartPanel(JPanel PanelChart) {
        chartPanel = PanelChart;
    }
    
    public void setRunningTime(int rtime){
            runInterval = rtime;
    }
}
