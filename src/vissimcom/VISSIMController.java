/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
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
package vissimcom;

import evaluation.Interval;
import infra.Infra;
import infra.infraobject.Detector;
import infra.simobjects.SimDetector;
import infra.simobjects.SimObjects;
import infra.type.LaneType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vissimcom.wrapper.IDataCollection;
import vissimcom.wrapper.IDataCollections;
import vissimcom.wrapper.IDesiredSpeedDecision;
import vissimcom.wrapper.IDesiredSpeedDecisions;
import vissimcom.wrapper.IDetector;
import vissimcom.wrapper.IDetectors;
import vissimcom.wrapper.IEvaluation;
import vissimcom.wrapper.INet;
import vissimcom.wrapper.ISignalController;
import vissimcom.wrapper.ISignalControllers;
import vissimcom.wrapper.ISignalGroup;
import vissimcom.wrapper.ISignalGroups;
import vissimcom.wrapper.ISimulation;
import vissimcom.wrapper.ITravelTime;
import vissimcom.wrapper.ITravelTimes;
import vissimcom.wrapper.IVissim;
import vissimcom.wrapper.Vissim;
import org.jawin.COMException;
import util.FileHelper;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */

public class VISSIMController {

    /** Parameter */
    private String caseFile;
    private int randomSeed;
    private int travelTimeInterval;
    private boolean useTravelTimeMeasuring = false;
    /** VISSIM objects */
    private IVissim vissim;
    private ISimulation simulation;
    private INet net;
    private ISignalControllers signalControllers;
    private ISignalController signalControllerForDetector;
    
    private int detectorLength = 0;
    private IDetectors detectors;
    private IDetector[] detectorList;
    
    private IDataCollections datacollections;
    private IDataCollection[] datacollectionList;
    
    private Detector[] queueDetectorList;
    
    private boolean[] detectorPulse;
    private int[] speedLimits;
    private IDesiredSpeedDecisions desiredSpeedDecisions;
    private HashMap<String, IDesiredSpeedDecision> desiredSpeedDecisionList = new HashMap<String, IDesiredSpeedDecision>();
    private ITravelTimes travelTimes;
    private ITravelTime[] travelTimeList;
    /** Simulation Setting */
    private final int DEFAULT_SC_FOR_DETECTOR = 1;
    //soobin Jeon modify
    private int RUNNING_STEP = Interval.getMinSimulationInterval(); // runStepListener for 30 second     
    private int FLOW_CONSTANT = 3600 / RUNNING_STEP;    // flow is calculated by volume for runningStep * flowConstant
    private final int DEFAULT_TIME_TRAVEL_INTERVAL = 5 * 60;    // 5min
    private int simResolution;
    private int simPeriod;
    private int simStep = 0; // this val keeps execution step
    private int totalExecutionStep; // how many step does simulation need to be executed?
    /** Information Storage */
    private HashMap<String, ISignalGroup> meterTable = new HashMap<String, ISignalGroup>();
    private DetectorData[] detectorData;
    // these arrays are used for notifing to listener temporarily
    String[] travelTimeNames;
    double[] travelTimeValues;
//    int[] detectorIds;
//    int[] detectorVolumes;
//    int[] detectorFlows;
//    float[] detectorSpeeds;
//    float[] detectorDensities;
    /** Event Listener */
    private Vector<ITrafficListener> readTrafficListeners = new Vector<ITrafficListener>();
    private Vector<ITravelTimeListener> readTravelTimeListeners = new Vector<ITravelTimeListener>();
    private HashMap<Integer, Vector<IStepListener>> vcStepListeners = new HashMap<Integer, Vector<IStepListener>>();
    private SimObjects simObjects;
    int threadStep = 10;
    private CollectDataThread[] collectThreads;

    public VISSIMController(SimObjects simObjects) {
        this.simObjects = simObjects;
    }

    // automatically runStepListener 
    public void autoRun() {
        while (run(RUNNING_STEP) > 0) {
            if (this.simStep >= this.totalExecutionStep) {
                break;
            }
        }
    }

    
    /**
     * runStepListener for given duration that should be multiple of 30
     * @param runTime time in second to runStepListener
     */
    public int run(int runTime) {
        // duration must be multiple of 30
        if (runTime <= 0 || runTime % RUNNING_STEP != 0) {
            return -1;
        }
        try {
            // simulation step that should be executed
            int runToThisStep = runTime * this.simResolution + this.simStep;
//            System.out.println("step : " + runToThisStep);
            if (runToThisStep > totalExecutionStep) {
                runToThisStep = totalExecutionStep;
            }
            float second = 0;
            int step;
            // loop for running
            for (step = simStep; step <= runToThisStep; step++, simStep++) {
                //sooba intend
                /*try {
                    int nDelayTime;
                       nDelayTime = 1 * 100; // 밀리초 단위에 맞도록 *1000을 해준다.
                       Robot tRobot = new Robot();
                      tRobot.delay(nDelayTime);
                      
                      for (int i = 0; i < this.detectorList.length; i++) {
                        // calculate traffic data
                        int detector_id = this.detectorList[i].getID();
                        int v = this.detectorData[i].getVolume();
                        //System.out.println("detector_id : " + detector_id + " volume : " + v);
                        int q = v * FLOW_CONSTANT;
                      }

                } catch (AWTException ex) {
                    Logger.getLogger(VISSIMController.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                second = (float) step / simResolution;
                // Second = step / resolution
                simulation.RunSingleStep();
                // every 30s routine
                if (second % RUNNING_STEP == 0) {
                    /**
                    * DataCollection Test
                    */
                    if(VISSIMHelper.detectorOption.isDATACOLLECTOR()){
                        for(int i=0;i<this.datacollectionList.length;i++){
                            IDataCollection d = datacollectionList[i];
                            double v = getDouble(d.GetResult("NVEHICLES", "SUM", 0));
                            double u = getDouble(d.GetResult("SPEED", "MEAN", 0));
                            double occ = -1;
                            if(queueDetectorList[i] != null){
                                occ = getDouble(d.GetResult("OCCUPANCYRATE", "SUM", 0));
                            }
//                            System.out.println("DCID("+d.getID()+") : "+v+"-"+u+"-"+occ+"-"+queueDetectorList[i]);
                            //AddDetectorData(d.getID(),(int)v,u,occ,speedLimits[i],queueDetectorList[i],VISSIMHelper.detectorOption);
                            AddDetectorData(d.getName(),(int)v,u,occ,speedLimits[i],queueDetectorList[i],VISSIMHelper.detectorOption);
//                          System.out.println("DCID("+d.getID()+") : "+getInt(d.getAttValue("ID")));
                        }
                    }
                    /********************************
                     * read traffic data
                     *********************************/
                    else if(VISSIMHelper.detectorOption.isDETECTOR()){
                        // for all detectors    
                        for (int i = 0; i < this.detectorList.length; i++) {
                            // calculate traffic data
                            String detector_name = this.detectorList[i].getName();
                            AddDetectorData(detector_name, detectorData[i].getVolume(),detectorData[i].getSpeed(),detectorData[i].getOccupancy(),
                                    speedLimits[i],queueDetectorList[i],VISSIMHelper.detectorOption);

                        }

                        // reset detector data of for past 30s
                        for (int i = 0; i < this.detectorList.length; i++) {
                            this.detectorData[i].reset();
                        }
                    }

                } // end of every 30s routine

                /************************************************
                 * read travel data every travel time interval
                 ************************************************/
                if (useTravelTimeMeasuring && !this.readTravelTimeListeners.isEmpty() && step % (travelTimeInterval * simResolution) == 0) {
                    // for all travel times                       
                    for (int i = 0; i < this.travelTimeList.length; i++) {
                        travelTimeNames[i] = this.travelTimeList[i].getName();
                        // get travel time from starting to this second
                        travelTimeValues[i] = getDouble(this.travelTimeList[i].GetResult(second, "TRAVELTIME", "", 0));
                        System.out.println("TravelTime : " + this.travelTimeList[i].getName() + " = " + getDouble(this.travelTimeList[i].GetResult(second, "TRAVELTIME", "", 0)));
                    }

                    // notify traffic data to listener
                    int cccount = 0;
                    for (ITravelTimeListener l : readTravelTimeListeners) {
                        if (l != null) {
                            l.readTravelTime(travelTimeNames, travelTimeValues);
                            cccount ++;
                        }
                    }
                }
                
                // Read data using threads (Java COM is very slow)
                //System.out.println("dLen=" + detectorList.length + " threadStep="+threadStep);
                if(VISSIMHelper.detectorOption.isDETECTOR()){
                    for (int i = 0, k=0; i < this.detectorList.length; i += threadStep, k++) {
                        int to = Math.min(i + threadStep - 1, this.detectorList.length - 1);
                        //System.out.println("i="+i+" k="+k + " to="+to);
                        collectThreads[k] = new CollectDataThread(i, to);
                        collectThreads[k].start();
                    }

                    for (int i = 0; i < collectThreads.length; i++) {
                        try {
                            collectThreads[i].join();
                        } catch (InterruptedException ex) {}
                    }
                }
                //System.out.println("  * All threads are joined");


                // execute all VSSIM step listeners
                for (int freq : this.vcStepListeners.keySet()) {
                    if (step % freq == 0) {
                        for (IStepListener l : vcStepListeners.get(freq)) {
                            l.runStepListener();
                        }
                    }
                }
            
            } // for : main loop
            

            return simStep;

        } catch (COMException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
    
    private void AddDetectorData(String detector_id, int volume, double speed, double occ, int speedLimit, Detector queuedetector, VISSIMDetector detectorOption) {
        int v = volume;

        int q = v * FLOW_CONSTANT;

        double u = 0;
        if(detectorOption.isDETECTOR()){
            u = (v == 0 ? speedLimit+5 : speed / v);
        }else if(detectorOption.isDATACOLLECTOR()){
            u = speed;
        }
        double k = (u <= 0 ? 0 : q / u);
//                        if(detector_id == 1020 || detector_id == 1021){
//                            System.out.println("DID("+detector_id+") : "+v+"-"+u);
//                        }
        double occupancy = -1;
//                        System.out.print("detector_id : " + detector_id + " -- ");
//                        System.out.println("v="+v+", u="+u+", k="+k+", occ=" + occupancy);
        if (queuedetector != null) {
//                            Detector d = TMO.getInstance().getInfra().getDetector("D" + this.detectorList[i].getID());
            if(detectorOption.isDETECTOR()){
            double total_occupancy = occ;
            occupancy = Math.min(total_occupancy / RUNNING_STEP * 100, 100);
//                            double scanData = occupancy * InfraConstants.MAX_SCANS / 100;
//                            double sk = occupancy * 5280 / d.getFieldLength() / 100;                            
//                            double su = q / sk;
            }else{
                occupancy = occ;
            }
        }

        if (k > 300) {
            System.out.println("WARNING!! VISSIMController > too high density : " + k);
        }
//                        SimDetector simDetector = simObjects.getDetector("D"+detector_id);
        SimDetector simDetector = simObjects.getDetector(""+detector_id);
        simDetector.addData(v, q, u, k, occupancy);
    }

        private void setFlowConstant(int RUNNING_STEP) {
                FLOW_CONSTANT = 3600 / RUNNING_STEP;
        }

    class CollectDataThread extends Thread {
        int fromIdx;
        int toIdx;
        String name;
        
        public CollectDataThread(int fromIdx, int toIdx) {
            this.fromIdx = fromIdx;
            this.toIdx = toIdx;
            this.name = fromIdx + " ~ " + toIdx;
        }

        public void run() {

            // read detector data from VISSIM every step 
            int count = 0;
            for (int i = fromIdx; i <= toIdx; i++) {
                count ++;
                try {

                    /*Modification 2012/1/21 //soobin Jeon 
                     * IMPULSE formula of jawin COM interface differ with jacozoom.
                     */
                    //System.out.println("dID = " + detectorList[0].getID() + "value("+count+") = "+getInt(detectorList[i].getAttValue("IMPULSE")));
                    
                    // if any vehicle was over the detector IMPULSE
                    int impulse = getInt(detectorList[i].getAttValue("IMPULSE"));
                    if (impulse == 1 && !detectorPulse[i]) {
                        /*if(detectorList[i].getID() == 1170 || detectorList[i].getID() == 1171){
                            System.out.println("dID = " + detectorList[i].getID() + " Impulse !!!");
                        }*/
                        detectorPulse[i] = true;
                        detectorData[i].addVolume();
                        detectorData[i].addSpeed(getDouble(detectorList[i].getAttValue("SPEED")));
                    if (queueDetectorList[i] != null) {
                        detectorData[i].addOccupancy(getDouble(detectorList[i].getAttValue("OCCUPANCY")));
                    }

                    }else if(impulse == 0 && detectorPulse[i]){
                        detectorPulse[i] = false;
                    }
                } catch (COMException ex) {
                }

            }
        }
    }

    /**
     * stop simulation and close vissim
     */
    public void stop() {
        try {
            this.simulation.Stop();
        } catch (COMException ex) {
        }

    }

    public void close() {
        try {
            this.vissim.Exit();
        } catch (COMException ex) {
        }

    }

    /**
     * Set light of meter to green or red
     */
    public boolean setMeterStatus(String meterName, MeterLight status) {
        try {
            ISignalGroup sg = meterTable.get(meterName);
            if (sg == null) {
                return false;
            }
            sg.setAttValue("STATE", status.id);
            return true;
        } catch (COMException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Set VSA to DesiredSpeedDecision */
    public void setVSA(String dmsName, VSA vsa) {
        try {
            if (vsa.getDSDNumber() < 0) {
                System.out.println("  - Warnning!! VISSIMController.setVSA() : can not find DesiredSpeedDistribution for VSA");
                return;
            }

            // desired speed decision name rule : L35WN27_1
            // alternative speed decision name rule : L35WN27_1A
            //   * alternative speed decision is only-in-simulation-DMS for 2-step vsa in simulation
            //     (assume : driver increase speed after passing DMS
            IDesiredSpeedDecision dsd = this.desiredSpeedDecisionList.get(dmsName);
            IDesiredSpeedDecision dsd_alt = this.desiredSpeedDecisionList.get(dmsName + "A");
//            System.out.println("set VSA : "+dmsName+" - "+vsa.speed);
            if (dsd == null) {
                System.out.println("  - Warnning!! VISSIMController.setVSA() : can not find DMS(" + dmsName + ")");
                return;
            }
//            System.out.println("  - " + dmsName + " : vsa=" + vsa.speed);

            for (VehicleClass v : VehicleClass.values()) {
                int classId = v.getVehicleClassId();
                dsd.setAttValue1("DESIREDSPEED", classId, vsa.getDSDNumber());
                if (dsd_alt != null) {
                    dsd_alt.setAttValue1("DESIREDSPEED", classId, vsa.getAltDSDNumber());
                }
            }
        } catch (COMException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * initialize with given case file
     * set signal controller for detector to DEFAULT_SC_FOR_DETECTOR(1) and
     * use random seed of case file
     */
    public int initialize(String casefile,VISSIMVersion v, int rinterval) {
        return initialize(casefile, this.DEFAULT_SC_FOR_DETECTOR, -1,v,rinterval);
    }

    /**
     * initialize with only given random seed
     * set signal controller for detector to DEFAULT_SC_FOR_DETECTOR(1)
     * @param randomSeed 
     */
    public int initialize(String casefile, int randomSeed, VISSIMVersion v, int rinterval) {
        return initialize(casefile, DEFAULT_SC_FOR_DETECTOR,randomSeed,v,rinterval);
    }

    /**
     * initialize with given signal control id for detector and random seed
     * @param signalControlIdForDetector
     * @param randomSeed 
     */
    public int initialize(String casefile, int signalControlIdForDetector, int randomSeed, 
            VISSIMVersion vversion,int rinterval) {
        try {
            this.RUNNING_STEP = rinterval;
            setFlowConstant(RUNNING_STEP);
            // set casefile
            this.caseFile = casefile;

            // get vissim
            
            vissim = new IVissim(vversion.getGUID());
            vissim.LoadNet(this.caseFile, 0);
            simulation = vissim.getSimulation();
            net = vissim.getNet();

            // get signal controllers
            signalControllers = net.getSignalControllers();
            signalControllerForDetector = signalControllers.GetSignalControllerByNumber(signalControlIdForDetector);

            // set resolution and period
            simResolution = simulation.getResolution();
            simPeriod = (int) simulation.getPeriod();
            
            if(VISSIMHelper.detectorOption.isDATACOLLECTOR()){
                simPeriod ++;
                simulation.setPeriod(simPeriod);
            }

            // how many execution steps do we need?
            totalExecutionStep = (int) simPeriod * simResolution;

            // set random seed
            if (randomSeed > 0) {
                simulation.setRandomSeed(randomSeed);
                this.randomSeed = randomSeed;
            } else {
                this.randomSeed = simulation.getRandomSeed();
            }

            // make desired speed decision list
            desiredSpeedDecisions = net.getDesiredSpeedDecisions();
            desiredSpeedDecisionList.clear();
            for (int i = 1; i <= desiredSpeedDecisions.getCount(); i++) {
                IDesiredSpeedDecision d = desiredSpeedDecisions.getItem(i);
                desiredSpeedDecisionList.put(d.getName(), d);
            }
            
            
            /**
             * set Lists
             */
            Infra infra = Infra.getInstance();
            if(VISSIMHelper.detectorOption.isDETECTOR()){
                // make detector list as hashmap
                /**
                 * Detector
                 */

                detectors = signalControllerForDetector.getDetectors();
                detectorList = new IDetector[detectors.getCount()];
                System.out.println("D Detector-"+detectors.getCount());
                
                this.detectorLength = detectorList.length;
                
                queueDetectorList = new Detector[detectorLength];
                detectorData = new DetectorData[detectorLength];
                speedLimits = new int[detectorLength];

                for (int i = 0; i < detectors.getCount(); i++) {
                    IDetector d = detectors.getItem(i+1);
                    detectorList[i] = d;
                    insertDetectorInfra(d.getID(),i,infra);
                }
                detectorPulse = new boolean[detectorList.length];
                int threadN = detectorLength / threadStep;
                if(detectorLength % threadStep != 0) threadN++;
                this.collectThreads = new CollectDataThread[threadN];
                
            }else if(VISSIMHelper.detectorOption.isDATACOLLECTOR()){
                /**
                * DataCollection
                */
               IEvaluation eval = vissim.getEvaluation();
               eval.setAttValue("DATACOLLECTION", true);
               this.datacollections = net.getDataCollections();
               datacollectionList = new IDataCollection[datacollections.getCount()];
               System.out.println("D Collector-"+datacollections.getCount());
            
                this.detectorLength = datacollectionList.length;
                
                queueDetectorList = new Detector[detectorLength];
                detectorData = new DetectorData[detectorLength];
                speedLimits = new int[detectorLength];

                for(int i=0;i<datacollections.getCount();i++){
                    IDataCollection d = datacollections.getItem(i+1);
                    datacollectionList[i] = d;
                    insertDetectorInfra(d.getID(),i,infra);
                }
            }else{
                return -1;
            }

//            System.out.println(datacollections.getCount()+ " : "+detectors.getCount());
            

            // make travel time list as hash map
            travelTimes = net.getTravelTimes();
            travelTimeList = new ITravelTime[travelTimes.getCount()];
            for (int i = 1; i <= travelTimes.getCount(); i++) {
                travelTimeList[i - 1] = travelTimes.getItem(i);
            }

            travelTimeNames = new String[this.travelTimeList.length];
            travelTimeValues = new double[this.travelTimeList.length];
//            detectorIds = new int[this.detectorList.length];
//            detectorVolumes = new int[this.detectorList.length];
//            detectorFlows = new int[this.detectorList.length];
//            detectorSpeeds = new float[this.detectorList.length];
//            detectorDensities = new float[this.detectorList.length];

            initializeFromCaseFile();

            simulation.RunSingleStep();
            simStep++;
            
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ComError.getError(ex.toString()).getErrorType();
        }
    }
    
    private void insertDetectorInfra(int Id,int cnt,Infra infra) {
        detectorData[cnt] = new DetectorData();
        Detector det = infra.getDetector(""+Id);
        if (det != null && det.getLaneType() == LaneType.QUEUE) {
            queueDetectorList[cnt] = det;
        } else {
            queueDetectorList[cnt] = null;
        }

        if (det != null && det.getStation() != null) {
            speedLimits[cnt] = (int) det.getStation().getSpeedLimit();
        } else {
            speedLimits[cnt] = 0;
        }
    }

    /**
     * make ramp meter table (signal group) 
     * @param signalControlStartNum signal control start number for metering
     */
    public void initializeMetering(int signalControlStartNum) {
        try {
            // make metering table
            //System.out.println("start num : " + signalControlStartNum + " / " + this.signalControllers.getCount());
            for (int i = signalControlStartNum; i < signalControlStartNum + this.signalControllers.getCount()-1; i++) {
                try {
                    ISignalController sc = signalControllers.GetSignalControllerByNumber(i);
                    if (sc == null) {
                        continue;
                    }
                    ISignalGroups sgs = sc.getSignalGroups();
                    for (int k = 1; k <= sgs.getCount(); k++) {
                        ISignalGroup sg = sgs.GetSignalGroupByNumber(k);
//                        System.err.println(sg.getName());
                        meterTable.put(sg.getName(), sg);
                        setMeterStatus(sg.getName(), MeterLight.GREEN);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * initialize for travel time measuring
     * with default interval (5min)
     */
    public boolean initializeTravelTimeMeasuring() {
        return initializeTravelTimeMeasuring(this.DEFAULT_TIME_TRAVEL_INTERVAL);
    }

    /**
     * initialize for travel time measuring
     * @param second travel time measuring interval
     */
    public boolean initializeTravelTimeMeasuring(int second) {
        if (second < 0 || second % RUNNING_STEP != 0) {
            return false;
        }
        this.travelTimeInterval = second;
        this.useTravelTimeMeasuring = true;
        return true;
    }

    /**
     * CAUTION!!
     * if VISSIM case file format is changed,
     * regular expression of this method must be changed.
     * @brief this method reads VISSIM case file and extract desired speed distribution and vehicle class information
     */
    private void initializeFromCaseFile() throws IOException {

        String text = FileHelper.readTextFile(this.caseFile);

        // VSA setting
        String regx = "DESIRED_SPEED (.*?)NAME \"(.*?)\"  (.*?)";
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            int did = Integer.parseInt(matcher.group(1).trim());
            String dname = matcher.group(2);
            VSA.setDSDNumber(dname, did);
        }

        // Vehicle Class setting
        regx = "VEHICLE_CLASS  ([0-9]+)\\r\\n     NAME          \"(.*?)\"(.*?)";
        p = Pattern.compile(regx);
        matcher = p.matcher(text);
        while (matcher.find()) {
            int vid = Integer.parseInt(matcher.group(1).trim());
            String vname = matcher.group(2);
            VehicleClass.setVehicleClassId(vname, vid);
        }
    }

//    /**
//     * @deprecated 
//     * @param resolution
//     * @throws COMException 
//     */
//    public void setResolution(int resolution) throws COMException {
//        // update resolution
//        float ratio = (float) resolution / this.simResolution;
//        this.simulation.setResolution(resolution);
//        this.totalExecutionStep = simPeriod * resolution;
//        simStep *= ratio;
//        this.simResolution = resolution;
//    }

    public int getResolution() {
        return this.simResolution;
    }

    /**
     * Show or hide vehicles and load on the VISSIM GUI
     */
    public void setVisible(boolean b) throws COMException {
       
        // hide or show vehicles
        this.vissim.getGraphics().setAttValue("VISUALIZATION", b);

        // hide or show road
        if (b) {
            this.vissim.getGraphics().setAttValue("DISPLAY", 0); // hide
        } else {
            this.vissim.getGraphics().setAttValue("DISPLAY", 2);   // show
        }
        this.vissim.getGraphics().Redraw();
    }

    /*********************************
     * Setter and Getter
     **********************************/
    public void addTrafficListener(ITrafficListener readTrafficListener) {
        this.readTrafficListeners.add(readTrafficListener);
    }

    public void addTravelTimeListener(ITravelTimeListener readTravelTimeListener) {
        this.readTravelTimeListeners.add(readTravelTimeListener);
    }

    /**
     * 
     * @param interval interval in step
     * @param stepListner IStepListener instance
     */
    public void addStepListener(int interval, IStepListener stepListner) {
        Vector<IStepListener> listeners = this.vcStepListeners.get(interval);
        if (listeners == null) {
            listeners = new Vector<IStepListener>();
            listeners.add(stepListner);
            this.vcStepListeners.put(interval, listeners);
        } else {
            listeners.add(stepListner);
        }
    }

    /**
     * Return case file
     * @return 
     */
    public String getCaseFile() {
        return caseFile;
    }

    public HashMap<String, ISignalGroup> getMeterTable() {
        return meterTable;
    }

    /**
     * @return random seed of simulation
     */
    public int getRandomSeed() {
        return randomSeed;
    }

    /**
     * @return simulation period in second
     */
    public int getSimPeriod() {
        return simPeriod;
    }

    /**
     * @return simulation resolution (time steps / simulation second)
     */
    public int getSimResolution() {
        return simResolution;
    }

    /**
     * @return current execution step (second * resolution)
     */
    public int getSimStep() {
        return simStep;
    }

    /**
     * @return total execution step (period * resolution)
     */
    public int getTotalExecutionStep() {
        return totalExecutionStep;
    }

    /**
     * @return interval to get travel time in second
     */
    public int getTravelTimeInterval() {
        return travelTimeInterval;
    }

    /**
     * Return current time in second
     * @return 
     */
    public float getCurrentTime() {
        return (float) this.simStep / this.simResolution;
    }

    /**
     * Class for saving detector data for 30s temporarily
     */
    private class DetectorData {

        private int volume = 0;
        private double speed = 0;
        private double occupancy = 0;
        private double pc = 0;  // previous occupancy

        public void addSpeed(double u) {
            if (u < 0.1 || u > 200) {
                u = 0;
            }
            this.speed += u;
        }

        public void addOccupancy(double c) {
            if (c == 0D) {
                this.occupancy += pc;
            }
            pc = c;
        }

        public void addVolume() {
            this.volume++;
        }

        public void reset() {
            this.volume = 0;
            this.speed = 0;
            this.occupancy = 0;
        }

        public double getSpeed() {
            return speed;
        }

        public int getVolume() {
            return volume;
        }

        public double getOccupancy() {
            return occupancy + pc;
        }
    }
    
    private double getDouble(Object res) {
        return Double.parseDouble(res.toString());
    }
    
    private float getFloat(Object res) {
        return Float.parseFloat(res.toString());
    }    
    
    private int getInt(Object res) {
        return Integer.parseInt(res.toString());
    }
}
