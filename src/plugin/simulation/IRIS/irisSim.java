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

import infra.Section;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import plugin.metering.Metering;
import plugin.metering.MeteringConfig;
import plugin.metering.MeteringSectionHelper.EntranceState;
import plugin.simulation.VSL.VSLSim;
import plugin.simulation.VSL.algorithm.VSLVersion;
import util.FileHelper;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public class irisSim extends VSLSim{
    Metering metering;
    private HashMap<String, ArrayList<Double>> ReleaseRate = new HashMap<String, ArrayList<Double>>();
    
    public irisSim(String caseFile, int seed, Section section, VISSIMVersion v, VSLVersion _vv, SimInterval sinterval, SimObjects simObjects){
        super(caseFile,seed,section,v,_vv,sinterval, simObjects);
        metering = new Metering(section,meters,detectors,sinterval, simObjects);
    }
    
    @Override
    public void ExecuteAfterRun() {
        super.ExecuteAfterRun();
        RunMetering();
    }
    
    @Override
    public void DebugMassage() {
        super.DebugMassage();
        metering.DisplayStationState();
//        if(MeteringConfig.isMeteringStep(simcount))
                metering.printEntrance();
    }

        private void RunMetering() {
                metering.getSimulationInterval().setRunTime(simcount);
                metering.updateEntranceStates();
                if(MeteringConfig.isMeteringStep(simcount)){
                        metering.run(samples, simcount);
                        for (EntranceState e : metering.sectionHelper.getEntranceStates()) {
                                if (e.meter == null) {
                                    continue;
                                }
                                caculateRate(e.meter.getID(),e.meter.getReleaseRate());
                        }
                }
        }
        
        @Override
        public void ExecuteEnd(){
                writeDataLog("RT",30,ReleaseRate);
                writeDataLog("RT",300,ReleaseRate);
        }
        
        /**
     * caculate Release Rate
     * @param id
     * @param waittime 
     */
        private void caculateRate(String id, double rate){
                String ttid = id;
                double tt = rate;

                ArrayList<Double> ttData = this.ReleaseRate.get(ttid);
                if (ttData == null) {
                    ttData = new ArrayList<Double>();
                    this.ReleaseRate.put(ttid, ttData);
                }
                ttData.add(tt);
        }
        
        /**
     * write waitTime
     */
    private void writeDataLog(String type, int value, HashMap<String, ArrayList<Double>> datalog) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(FileHelper.getNumberedFileName("IRIS_"+value+type+"_LOG.xls")));
            WritableSheet sheet = workbook.createSheet("tt", 0);
            Iterator<String> itr = datalog.keySet().iterator();
            int col = 0;
            while (itr.hasNext()) {
                String key = itr.next();
                sheet.addCell(new Label(col, 0, key));
                ArrayList<Double> data = datalog.get(key);
                int row = 1;
                int cnt = 1;
                double R_data = 0;
                for (Double d : data) {
                    R_data += d;
                    if(cnt % (value/MeteringConfig.MeteringInterval) == 0){
                        double avg = R_data <= 0 ? 0 : R_data / (value/MeteringConfig.MeteringInterval);
                        sheet.addCell(new jxl.write.Number(col, row++, avg));
                        R_data = 0;
                    }
                    cnt ++;
                }
                col++;
            }
            workbook.write();
            workbook.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
