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
package evaluation;

import error.StringErrorStream;
import infra.Period;
import infra.Section;
import infra.simobjects.SimObjects;
import infra.simobjects.SimulationSeveralResult;
import infra.simulation.SectionHelper;
import infra.simulation.StationState;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import khta.KHTAOption;
import khta.RunningDialog;
import khta.SimulationResult;
import util.FileHelper;


/**
 *
 * @author soobin Jeon
 */
public class EvaluationForCalibration extends TimerTask {
    KHTAOption selectedOption;
    EvaluationOption option;
//    OptionType eot = null;
//    OptionType[] options;
    RunningDialog rd;
    
    Section SectionD;
    SectionHelper SectionH;
//    ArrayList<CStation> StationStateSim;
    ArrayList<CStation> StationStateReal;
    ArrayList<ArrayList> StationStateSims;
    
    ArrayList<CStation> CumSimStations;
    CStation CumStationReal;
    CStation CumStationSim;
    
    //ArrayList<SimObjects> SimList = SimObjects.getArrayListInstance();
    //SimObjects simObject = SimObjects.getInstance();
    SimObjects simObjects;
    SimulationResult selectedSimulationResult;
    private ArrayList<SimulationResult> simResultList;
    
    private String SimulationName;
    
    //for File Create
    private boolean IsRunSheet = false;
    private String RunFname;
    
    public EvaluationForCalibration(KHTAOption selectedOption, RunningDialog rd, SimulationResult sr){
        rd.showLog();
        this.selectedOption = selectedOption;
        this.option = selectedOption.getEvaluationOption();
        //this.options = opt;
        this.rd = rd;
//        SectionD = (Section)option.getSection().clone();
        SectionD = (Section)option.getSelectedSection();
        SectionH = new SectionHelper(SectionD);
//        StationStateSim = SectionHSim.getStationStates();
//        StationStateSim = new ArrayList<CStation>();
        StationStateReal = new ArrayList<CStation>();
        StationStateSims = new ArrayList<ArrayList>();
        CumSimStations = new ArrayList<CStation>();
        this.selectedSimulationResult = sr;
        this.IsRunSheet = false;
        this.RunFname = null;
        this.SimulationName = selectedSimulationResult.getName();
    }
    
    @Override
    public void run(){
        StringErrorStream sos = new StringErrorStream(rd);
        
        try {
            RealDataLoad();
//            realSection.loadData(option.getPeriods()[0],false);
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintStream(sos));
            sos.getMessage("Calibration Fail","Data Load Failed, Check Data File");
            ex.printStackTrace();
            return;
        }
        
        /**
         * Data Period Correcting check
         */
        if(StationStateReal.get(0) != null && StationStateSims.get(0).get(0) != null){
            CStation sims = (CStation)StationStateSims.get(0).get(0);
            System.err.println(sims.getDataLength() + " = "+StationStateReal.get(0).getDataLength());
            if(StationStateReal.get(0).getDataLength() != sims.getDataLength()){
                if(option.getPeriods().isEmpty()){
                    sos.getMessage("Calibration Fail\n - There is no Period","");
                    return;
                }
                
                Period p = option.getPeriods().get(0);
                double shour = sims.getDataLength() * p.interval / 3600;
                double rhour = StationStateReal.get(0).getDataLength() * p.interval / 3600;
                sos.getMessage("Calibration Fail\n - Time Period is not correct.","Selected Simulation Period : "+shour+" hours"
                        +"\n  - Selected RealData Period : "+rhour + " hours"
                        +"\n  - Please revise the Start and End Time of \'Real Data\' at \"Date and Time\" Tab");
                return;
            }
        }
        
        try {
            
            if (selectedOption.getExcelCheck()) {
                CumSimStations = new ArrayList<CStation>();
                Evaluation();
            }
            if (selectedOption.getCSVCheck()) {
                CumSimStations = new ArrayList<CStation>();
                EvaluationCSV();
                
            }
            
            /*
             * modify soobin Jeon 02/27/12
             */
            if(selectedOption.getContourCheck()){
                if(selectedSimulationResult.IsListData()){
                    //Avg Data
                    //SimObjects.getInstance().reset();  
                    simObjects = new SimObjects();
                    for(SimulationResult _sr : this.simResultList){
                        System.out.println("Load Simulation Result Seed : " + _sr.getName());
                        this.ReadAvgData(_sr);
                    }
                    RunContourData();
                }else{
                    RunContourData();
                }

//                //Each Data
//                for(SimulationResult _sr : this.simResultList){
//                    System.out.println("Load Simulation Result Seed : " + _sr.getName());
//                    this.ReadSingleData(_sr);
//                    RunData();
//                }
//                this.RunContourData();
            }
            
//            realSection.loadData(option.getPeriods()[0],false);
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintStream(sos));
            sos.getMessage("Calibration Fail","Evaluation Failed");
            ex.printStackTrace();
            return;
        }
        
        
        
//        for(StationState s : StationStateSim){
//            System.out.println(s.getRNode().getLabel());
//        }
        rd.close(1.5);
    }
    
    private void RealDataLoad(){
        System.out.println("Loading Simulation Datas..");

        //Data Read to SimObject
        if(selectedSimulationResult.IsListData()){
            //SimObject
            SimulationSeveralResult ssr = new SimulationSeveralResult(SectionD,option.getPeriods().get(0));
            ssr.LoadResult(selectedSimulationResult);
            
            /*
             * modify soobin Jeon 02/27/12
             */
            //ContourMode
            if(selectedOption.getContourCheck())
                this.simResultList = ssr.getSimulationResults(selectedSimulationResult);
            
        }else{
            //SimObjects.getInstance().reset();        
            simObjects = new SimObjects();
            selectedSimulationResult.setTrafficDataToDetectors(simObjects);
        }
        
        System.out.println("Reading Datas(Period : " + option.getPeriods().get(0).getPeriodString() + ")");
        //simulation
//        System.out.println("Simulation Data File Count :" + this.SimList.size());
        int listcount = 0;
//        if(this.SimList.size() > 0){
//            for(SimObjects sobj : this.SimList){
////                SimObjects.getInstance() = sobj;
//                this.ReadSimData(sobj);
//            }
//        }else{
            this.ReadSimData(null);
//        }
        
        //real data load
        SectionD.loadData(option.getPeriods().get(0),DataLoadOption.setEvaluationMode());
        
        for(StationState s : SectionH.getStationStates()){
            //StationStateReal.add(new CStation(s.getRNode().getTotalFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values));
            StationStateReal.add(new CStation(s.getRNode().getFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values));
        }
        

        
        
    }
    
    private void ReadSimData(SimObjects sobj){
        SectionD.loadData(option.getPeriods().get(0), DataLoadOption.setSimulationMode(option.getSimulationInterval()),sobj);
        ArrayList<CStation> realstation = new ArrayList<CStation>();
        
//        try{
//            SimDetector sd = SimObjects.getInstance().getDetector("D1119");
//            System.out.print("data : ");
//            for(double ddd : sd.getFlow()){
//                System.out.print(ddd + ",");
//            }
//            System.out.println();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
        
        for(StationState s : SectionH.getStationStates()){
//            if(sobj != null)
            if(sobj != null)
                //realstation.add(new CStation(s.getRNode().getTotalFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values,sobj.getRandomSeed()));
                realstation.add(new CStation(s.getRNode().getFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values,sobj.getRandomSeed()));
            else
                //realstation.add(new CStation(s.getRNode().getTotalFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values));
                realstation.add(new CStation(s.getRNode().getFlow(),s.getRNode().getSpeed(),CalibrationPrintType.Values));
        }
        
//        for(double dd : realstation.get(0).getTotalFlow())
//            System.out.print(dd + ",");
//        System.out.println();
        
        this.StationStateSims.add(realstation);
    }
    
    private void ReadSingleData(SimulationResult _sr){
        //SimObjects.getInstance().reset();        
        simObjects = new SimObjects();
        _sr.setTrafficDataToDetectors(simObjects);
    }

    /*
     * modify soobin Jeon 02/27/12
     */
    private void ReadAvgData(SimulationResult _sr){
        _sr.AddTrafficDataToDetectors();
    }
        
    private void EvaluationCSV() throws Exception{
        
        
        int Col = 0;
        int colspace = 3;
        ArrayList<ArrayList> DataCol = new ArrayList<ArrayList>();
        
        DataCol.add(AddTimeArray(Col++,new String[]{String.valueOf(this.StationStateSims.size()),String.valueOf(this.StationStateSims.get(0).size()),""},option.getPeriods().get(0).getTimeline()));
        
        //Evaluate of each Station
        if(this.StationStateSims.size() > 0){
            for(int i=0;i<StationStateSims.get(0).size();i++){
                String RNodeLabel = SectionH.getStationStates().get(i).getRNode().getName()+"("+SectionH.getStationStates().get(i).getRNode().getID()+")";
                System.out.println("Calibration Station("+RNodeLabel + "<->" + RNodeLabel + ")");

//                Col = AddStation(StationStateReal.get(i),StationStateSim.get(i),Col,sheet,RNodeLabel)+3;
//                Col = AddStations(i,StationStateReal.get(i),Col,sheet,RNodeLabel)+3;
                  AddData(i,StationStateReal.get(i),Col,DataCol,RNodeLabel);
                  
                  //3 space
                  for(int k=0;k<colspace;k++)
                    DataCol.add(DataColAddRow(new String[]{"","",""},getDataByVType(VariableType.flow,null)));
                  
                //Set Cumulative Data
                if(i == 0){
                    CumStationReal = new CStation(StationStateReal.get(i).getTotalFlow(),StationStateReal.get(i).getSpeed(),CalibrationPrintType.Average);
                    CumStationSim = new CStation(this.CumSimStations.get(i).getTotalFlowWithType(),this.CumSimStations.get(i).getSpeedWithType(),CalibrationPrintType.Average);
                }else{
                    CumStationReal.AddTotalFlow(StationStateReal.get(i).getTotalFlow());
                    CumStationReal.AddSpeed(StationStateReal.get(i).getSpeed());
                    CumStationSim.AddTotalFlow(this.CumSimStations.get(i).getTotalFlowWithType());
                    CumStationSim.AddSpeed(this.CumSimStations.get(i).getSpeedWithType());
                }

            }
            
            System.out.println("Calibration Station(Total Data)");
            DataCol.add(DataColAddRow(new String[]{"Total Average Data","Flow","Sim"},getDataByVType(VariableType.flow,CumStationSim)));
            DataCol.add(DataColAddRow(new String[]{"","","Real"},getDataByVType(VariableType.flow,CumStationReal)));
            DataCol.add(DataColAddRow(new String[]{"","Speed","Sim"},getDataByVType(VariableType.speed,CumStationSim)));
            DataCol.add(DataColAddRow(new String[]{"","","Real"},getDataByVType(VariableType.speed,CumStationReal)));
        }
        
        this.saveCsv(DataCol);
        
    }
    private void Evaluation() throws Exception{
        String FileName = getFileName("xls");
        System.out.println("Making File.."+FileName);
        
        WritableWorkbook workbook = Workbook.createWorkbook(new File(FileName));
        String[] temparr = FileName.split("\\.")[0].split("\\\\");
        WritableSheet sheet = workbook.createSheet(temparr[temparr.length-1], 1);
        int Col = 0;
        
        //Add Time Info
        AddTimes(sheet,Col++,new String[]{String.valueOf(this.StationStateSims.size()),String.valueOf(this.StationStateSims.get(0).size()),""},option.getPeriods().get(0).getTimeline());
        
        //Evaluate of each Station
        if(this.StationStateSims.size() > 0){
            for(int i=0;i<StationStateSims.get(0).size();i++){
                //Excel Max Col Count = 234
                String RNodeLabel = SectionH.getStationStates().get(i).getRNode().getName()+"("+SectionH.getStationStates().get(i).getRNode().getID()+")";
                System.out.println("Calibration Station("+RNodeLabel + "<->" + RNodeLabel + ")");

//                Col = AddStation(StationStateReal.get(i),StationStateSim.get(i),Col,sheet,RNodeLabel)+3;
                Col = AddStations(i,StationStateReal.get(i),Col,sheet,RNodeLabel)+3;
                
                //Set Cumulative Data
                if(i == 0){
                    CumStationReal = new CStation(StationStateReal.get(i).getTotalFlow(),StationStateReal.get(i).getSpeed(),CalibrationPrintType.Average);
                    CumStationSim = new CStation(this.CumSimStations.get(i).getTotalFlowWithType(),this.CumSimStations.get(i).getSpeedWithType(),CalibrationPrintType.Average);
                }else{
                    CumStationReal.AddTotalFlow(StationStateReal.get(i).getTotalFlow());
                    CumStationReal.AddSpeed(StationStateReal.get(i).getSpeed());
                    CumStationSim.AddTotalFlow(this.CumSimStations.get(i).getTotalFlowWithType());
                    CumStationSim.AddSpeed(this.CumSimStations.get(i).getSpeedWithType());
                }

            }
        }
        
         String RNodeLabel = "Total Average Data";
        System.out.println("Calibration Station(Total Data)");

        Col = AddStation(CumStationReal,CumStationSim,Col,sheet,RNodeLabel)+1;
        
        System.out.println("Writing Excel File..");
        if(Col > 234){
            StringErrorStream sos = new StringErrorStream(rd);
            sos.getMessage("Calibration Fail\nError Message\n  - Too many data.","Use csv mode.\n  - Change Output Format with 'CSV' in Real Data Extraction Tab.\n    If it happen again, reduce Random Seed.");
            workbook.close();
            File f = new File(FileName);
            f.delete();
        }else{
            workbook.write();
            workbook.close();
        }
        
        //Desktop.getDesktop().open(new File(FileName));
        Desktop.getDesktop().open(new File(selectedOption.getOutputPath()));
    }
    private int AddStations(int StationNum,CStation real, int Col, WritableSheet sheet, String RNodeLabel) throws Exception{
        int rSeedCount = this.StationStateSims.size();
        int CellRange = rSeedCount+2;
        int CalibrationStartIndex = 0;
        int CalibrationStartCol = 0;
        
        setCumulative(StationNum,this.CumSimStations);
        
        sheet.addCell(new Label(Col, CalibrationStartIndex++, RNodeLabel));
        sheet.addCell(new Label(Col + 0, CalibrationStartIndex, "Flow"));
        sheet.addCell(new Label(Col+ CellRange, CalibrationStartIndex++, "Speed"));
        
        //
        for(int i=0;i<rSeedCount;i++){
            CStation s = (CStation)this.StationStateSims.get(i).get(StationNum);
            int rs = s.getRandomSeed();
            if(rs != -1){
                sheet.addCell(new Label(Col + i, CalibrationStartIndex, "R"+rs));
                sheet.addCell(new Label(Col + i + CellRange, CalibrationStartIndex, "R"+rs));
            }else{
                sheet.addCell(new Label(Col + i, CalibrationStartIndex, "Sim"));
                sheet.addCell(new Label(Col + i + CellRange, CalibrationStartIndex, "Sim"));
            }
                
        }
        sheet.addCell(new Label(Col + rSeedCount, CalibrationStartIndex, "Avg"));
        sheet.addCell(new Label(Col + rSeedCount+1, CalibrationStartIndex, "Real"));
        sheet.addCell(new Label(Col+ (CellRange*2)-2, CalibrationStartIndex, "Avg"));
        sheet.addCell(new Label(Col+ (CellRange*2)-1, CalibrationStartIndex++, "Real"));
        
//        sheet.mergeCells(Col, CalibrationStartIndex-3, Col+(CellRange*2)-1, CalibrationStartIndex-3); //First Rows Merge
//        sheet.mergeCells(Col, CalibrationStartIndex-2, Col+CellRange-1, CalibrationStartIndex-2); //First Rows Merge
//        sheet.mergeCells(Col+CellRange, CalibrationStartIndex-2, Col+(CellRange*2)-1, CalibrationStartIndex-2); //First Rows Merge
        
        //write simData
        int initIndex = CalibrationStartIndex;
        for(int i=0;i<rSeedCount;i++){
            CStation s = (CStation)this.StationStateSims.get(i).get(StationNum);
            for(int z=0;z<s.getTotalFlow().length;z++){
                sheet.addCell(new Number(Col+i, z+CalibrationStartIndex, s.getTotalFlowWithType()[z]));
                sheet.addCell(new Number(Col+i+CellRange, z+CalibrationStartIndex, s.getSpeedWithType()[z]));
                if(i == (rSeedCount-1)){
                    sheet.addCell(new Number(Col+i+1, z+CalibrationStartIndex, this.CumSimStations.get(StationNum).getAvgTotalFlow()[z]));
                    sheet.addCell(new Number(Col+i+CellRange+1, z+CalibrationStartIndex, this.CumSimStations.get(StationNum).getAvgSpeed()[z]));
                    sheet.addCell(new Number(Col+i+2, z+CalibrationStartIndex, real.getTotalFlowWithType()[z]));
                    sheet.addCell(new Number(Col+i+CellRange+2, z+CalibrationStartIndex, real.getSpeedWithType()[z]));
                }
                    
            }
            //write MAE
            sheet.addCell(new Number(Col + i, s.getTotalFlow().length + CalibrationStartIndex, getMAE(real.getTotalFlowWithType(),s.getTotalFlowWithType())));
            sheet.addCell(new Number(Col + i + CellRange, s.getSpeedWithType().length + CalibrationStartIndex, getMAE(real.getSpeedWithType(),s.getSpeedWithType())));
            
            //write avg MAE
            sheet.addCell(new Number(Col + i+1, s.getTotalFlow().length + CalibrationStartIndex, getMAE(real.getTotalFlowWithType(),CumSimStations.get(StationNum).getTotalFlowWithType())));
            sheet.addCell(new Number(Col + i + CellRange+1, s.getSpeedWithType().length + CalibrationStartIndex, getMAE(real.getSpeedWithType(),CumSimStations.get(StationNum).getSpeedWithType())));
        }
        
        
        
        
        return Col + (CellRange*2);
    }
    private int AddStation(CStation real, CStation sim, int Col, WritableSheet sheet,String RNodeLabel) throws Exception{
//        CStation real = StationStateReal.get(Sposition);
//        CStation sim = StationStateSim.get(Sposition);
        int CalibrationStartIndex = 0;
        int CalibrationStartCol = 0;
        //Write Index
        
        sheet.addCell(new Label(Col, CalibrationStartIndex++, RNodeLabel));
        CalibrationStartIndex++;
        sheet.addCell(new Label(Col + (CalibrationStartCol++), CalibrationStartIndex, "Sim(flow)"));
        sheet.addCell(new Label(Col+ (CalibrationStartCol++), CalibrationStartIndex, "Real(flow)"));
        sheet.addCell(new Label(Col + (CalibrationStartCol++), CalibrationStartIndex, "Sim(Speed)"));
        sheet.addCell(new Label(Col+ (CalibrationStartCol++), CalibrationStartIndex++, "Real(Speed)"));
        sheet.mergeCells(Col, CalibrationStartIndex-2, Col+CalibrationStartCol-1, CalibrationStartIndex-2); //First Rows Merge
        
        
        //Write Values
        for(int i = 0;i<sim.getTotalFlow().length;i++){
            sheet.addCell(new Number(Col, i+CalibrationStartIndex, sim.getTotalFlowWithType()[i]));
            sheet.addCell(new Number(Col+1, i+CalibrationStartIndex, real.getTotalFlowWithType()[i]));
            sheet.addCell(new Number(Col+2, i+CalibrationStartIndex, sim.getSpeedWithType()[i]));
            sheet.addCell(new Number(Col+3, i+CalibrationStartIndex, real.getSpeedWithType()[i]));
        }
        
        //write MAE
        sheet.addCell(new Label(Col, sim.getTotalFlowWithType().length + CalibrationStartIndex, "MAE(Flow)"));
        sheet.addCell(new Number(Col + 1, sim.getTotalFlowWithType().length + CalibrationStartIndex, getMAE(real.getTotalFlowWithType(),sim.getTotalFlowWithType())));
        sheet.addCell(new Label(Col + 2, sim.getSpeedWithType().length + CalibrationStartIndex, "MAE(Speed)"));
        sheet.addCell(new Number(Col + 3, sim.getSpeedWithType().length + CalibrationStartIndex, getMAE(real.getSpeed(),sim.getSpeed())));
        
        
        
        return Col + CalibrationStartCol;
    }
    
    private double getMAE(double[] real, double[] sim){
        double k = (double)1/(double)sim.length;
        double Sigma = 0;
        
//        System.out.println(sim.length);
        for(int i = 0;i<sim.length;i++){
            double abs = Math.abs(real[i]-sim[i]);
            Sigma += abs == 0 ? 0 : abs/real[i]*100;
        }
//        System.out.println(k);
        return k * Sigma;
    }
    
    private void AddTimes(WritableSheet sheet, int column, String[] labels, String[] data)
    {
        try {
            int row = 0;
            for(int r=0; r<labels.length; r++) {
                sheet.addCell(new Label(column, row++, labels[r]));
            }
            for(int r=0; r<data.length+1; r++)
            {
                if(r == data.length)
                    sheet.addCell(new Label(column, row++, "MAE"));
                else
                    sheet.addCell(new Label(column, row++, data[r]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setCumulative(int i, ArrayList<CStation> CumSimStations) {
        int rSeedCount = this.StationStateSims.size();
        for(int z=0;z<rSeedCount;z++){
            CStation s = (CStation)this.StationStateSims.get(z).get(i);
            if(z == 0){
                CumSimStations.add(new CStation(s.getTotalFlowWithType(),s.getSpeedWithType(),CalibrationPrintType.Average));
            }else{
                CumSimStations.get(i).AddTotalFlow(s.getTotalFlowWithType());
                CumSimStations.get(i).AddSpeed(s.getSpeedWithType());
            }
                
        }
    }

    private ArrayList<String> AddTimeArray(int column, String[] labels, String[] data) {
        ArrayList<String> Tdata = new ArrayList<String>();
        try {
            int row = 0;
            for(int r=0; r<labels.length; r++) {
                Tdata.add(labels[r]);
//                sheet.addCell(new Label(column, row++, labels[r]));
            }
            for(int r=0; r<data.length; r++)
            {
                Tdata.add(data[r]);
//                sheet.addCell(new Label(column, row++, data[r]));
            }
            Tdata.add("MAE");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Tdata;
    }
    
    public void saveCsv(ArrayList<ArrayList> res) throws Exception {
        String FileName = this.getFileName("csv");
        System.out.println("Making File.."+FileName);
        
        FileWriter fw = new FileWriter(FileName, false);
        BufferedWriter bw = new BufferedWriter(fw);

        ArrayList<ArrayList> data = res;

        int cols = data.size();
        int rows = data.get(0).size();

        StringBuilder contents = new StringBuilder(2048);
        String d = null;

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols; c++) {
                if(r < data.get(c).size()){
                    Object o = data.get(c).get(r);
                    addCSVData(o,contents,c,cols);
                }
                else{
                    Object o = null;
                    addCSVData(o,contents,c,cols);
                }
            }
            contents.append(System.getProperty("line.separator"));
        }
        bw.write(contents.toString());
        bw.close();
        Desktop.getDesktop().open(new File(selectedOption.getOutputPath()));
    }

    private void AddData(int StationNum,CStation real, int Col, ArrayList<ArrayList> DataCol, String RNodeLabel) {
        int rSeedCount = this.StationStateSims.size();
        int AddData = 2;
        int CellRange = rSeedCount+AddData;
        int CalibrationStartIndex = 0;
        int CalibrationStartCol = 0;
        
        setCumulative(StationNum,this.CumSimStations);
        
        //setFlow
        AddStationCol(DataCol,StationNum,rSeedCount,this.StationStateSims,RNodeLabel,this.CumSimStations.get(StationNum),real,VariableType.flow);
        AddStationCol(DataCol,StationNum,rSeedCount,this.StationStateSims,RNodeLabel,this.CumSimStations.get(StationNum),real,VariableType.speed);
    }

    private void AddStationCol(ArrayList<ArrayList> DataCol,int StationNum, int rSeedCount, ArrayList<ArrayList> stationstatesim, String RNodeLabel,CStation CumSimStations,CStation real, VariableType vtype) {
        for(int i=0;i<rSeedCount;i++){
            String RTitle = "";
            String variable = "";
            String ColTitle = "";
            CStation s = (CStation)stationstatesim.get(i).get(StationNum);

            
            if(i==0){
                if(vtype.IsFlow())
                    RTitle = RNodeLabel;
                variable = vtype.getType();
            }
 
            ColTitle = "Rseed("+String.valueOf(s.RANDOMSEED)+")"+s.getTotalFlow().length;
            
            DataCol.add(DataColAddRow(new String[]{RTitle,variable,ColTitle},getDataByVType(vtype,s)));
            DataCol.get(DataCol.size()-1).add(String.valueOf(getMAE(getDataByVType(vtype,real),getDataByVType(vtype,s))));
        }
        
        //avg
        DataCol.add(DataColAddRow(new String[]{"","","Avg"},getDataByVType(vtype,CumSimStations)));
        DataCol.get(DataCol.size()-1).add(String.valueOf(getMAE(getDataByVType(vtype,real),getDataByVType(vtype,CumSimStations))));
        //real data
        DataCol.add(DataColAddRow(new String[]{"","","Real"},getDataByVType(vtype,real)));
    }

    private ArrayList DataColAddRow(String[] string, double[] b_data) {
        ArrayList<String> Tdata = new ArrayList<String>();
        try {
            int row = 0;
            for(int r=0; r<string.length; r++) {
                Tdata.add(string[r]);
//                sheet.addCell(new Label(column, row++, labels[r]));
            }
            if(b_data != null){
                for(int r=0; r<b_data.length; r++)
                {
                    Tdata.add(String.valueOf(b_data[r]));
    //                sheet.addCell(new Label(column, row++, data[r]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Tdata;
    }

    private double[] getDataByVType(VariableType variableType, CStation vtype) {
        double[] b_data=null;
        if(vtype != null){
            if(variableType.IsFlow()){
                b_data = vtype.getTotalFlowWithType();
            }else if(variableType.IsSpeed()){
                b_data = vtype.getSpeedWithType();
            }
        }
        return b_data;
    }

    private void addCSVData(Object o, StringBuilder contents,int c, int cols) {
        String d = null;
        if (o == null) {
            d = "";
        } else {
            d = o.toString();
        }
        if (c >= cols - 1) {
            contents.append(d);
        } else {
            contents.append(d + ", ");
        }
    }
    
    /*
     * modify soobin Jeon 02/27/12
     */
    private void saveContour(Evaluation ev, KHTAOption selectedOption, EvaluationOption opts, ContourType cType) {
        ContourPlotter cp = new ContourPlotter(opts.getSelectedSection(), opts.getContourSetting(cType), ev, selectedOption.getOutputPath());
        String Cname = cType.toString();
        cp.saveImage(opts.getOCAE(), this.getFileName(Cname,"jpg"));        
    }
    
    /*
     * modify soobin Jeon 02/27/12
     */
    private void RunContourData() throws IOException {
//        Evaluation.clearCache();
////        JOptionPane.showMessageDialog(rd, options.length);
//        for (OptionType ot : options) {
//            if(!runEvaluate(ot, selectedOption));
//        }
    }
    
    /*
     * modify soobin Jeon 02/27/12
     */
//    private boolean runEvaluate(OptionType ot, KHTAOption selectedOption) {
//        EvaluationOption opts = selectedOption.getEvaluationOption();
//        Evaluation ev = Evaluation.createEvaluate(ot, selectedOption.getEvaluationOption());
//        if (ev != null) {
//            System.out.println("    - Evaluating " + ev.getName() + " ... ");
//            ev.setPrintDebug(true);
//            boolean v = ev.doEvaluate();
//            try {
//                // save Contour
//                if (opts.hasOption(OptionType.OUT_CONTOUR)) {
//                    if (ot.equals(OptionType.STATION_SPEED)) {
//                        saveContour(ev, selectedOption, opts, ContourType.SPEED);
//                    } else if (ot.equals(OptionType.STATION_TOTAL_FLOW)) {
//                        saveContour(ev, selectedOption, opts, ContourType.TOTAL_FLOW);
//                    } else if (ot.equals(OptionType.STATION_OCCUPANCY)) {
//                        saveContour(ev, selectedOption, opts, ContourType.OCCUPANCY);
//                    } else if (ot.equals(OptionType.STATION_DENSITY)) {
//                        saveContour(ev, selectedOption, opts, ContourType.DENSITY);
//                    } else if (ot.equals(OptionType.EVAL_TT)){
//                            System.out.println("EVALTT");
//                            saveContour(ev, selectedOption, opts, ContourType.TT);
//                    } else if (ot.equals(OptionType.EVAL_TT_REALTIME)){
//                            saveContour(ev, selectedOption, opts, ContourType.STT);
//                    }
//                }
//                Desktop.getDesktop().open(new File(selectedOption.getOutputPath()));
//            } catch(Exception ex) {
//                JOptionPane.showMessageDialog(null, "Fail to save result : " + ev.getName());
//                ex.printStackTrace();                
//            }
//            return v;
//        }
//        return true;
//    }

    /*
     * modify soobin Jeon 02/27/12
     */
    private String getFileName(String ext) {
        return getFileName(null,ext);
    }
    private String getFileName(String name, String ext){
//        String Fname = "Calibration_" + option.getSection().getName();
        String Fname = this.SimulationName;
        
        //if Contour with CSV or EXEL
        if(ext.equals("jpg") && IsRunSheet){
            Fname = RunFname;
        }
        
        if(name != null)
            Fname += "_"+name;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyHHmmss");
        String FileName = FileHelper.getNumberedFileName(this.selectedOption.getOutputPath() + "\\" + Fname  + "."+ext);
        
        if(ext.equals("csv") || ext.equals("xls")){
            this.IsRunSheet = true;
            String[] temparr = FileName.split("\\.")[0].split("\\\\");
            this.RunFname = temparr[temparr.length-1];
        }
        return FileName;
    }
    
    enum VariableType{
        flow("Flow"),
        speed("Speed");
        
        String type = "";
        
        VariableType(String data){
            type = data;
        }
        
        public boolean IsFlow(){
            return (this == flow);
        }
        public boolean IsSpeed(){
            return (this == speed);
        }
        public String getType(){
            return type;
        }
    }
    
    public boolean cancel(){
            return super.cancel();
        }
    
    class CStation{
        double[] TotalFlow;
        double[] Speed;
        int FlowAddCount = 0;
        int SpeedAddCount = 0;
        int type = CalibrationPrintType.Values;
        int RANDOMSEED = -1;
        
        public CStation(double[] flow, double[] spd, int type, int rseed){
            this.RANDOMSEED = rseed;
            initStation(flow,spd,type);
        }
        public CStation(double[] flow, double[] spd, int type){
            initStation(flow,spd,type);
        }
        
        public void initStation(double[] flow, double[] spd, int type){
            SetTotalFlow(flow);
            SetSpeed(spd);
            FlowAddCount ++;
            SpeedAddCount++;
            this.type = type;
        }
        
        public void setType(int type){
            this.type = type;
        }
        public void SetTotalFlow(double[] data){
            TotalFlow = new double[data.length];
            for(int i=0;i<data.length;i++)
                TotalFlow[i] = data[i];
        }
        
        public void SetSpeed(double[] data){
            Speed = new double[data.length];
            for(int i=0;i<data.length;i++)
                Speed[i] = data[i];
        }
        
        public double[] getTotalFlow(){
            return TotalFlow;
        }
        
        public double[] getSpeed(){
            return Speed;
        }
        
        public int getRandomSeed(){
            return this.RANDOMSEED;
        }
        
        public void AddTotalFlow(double[] data){
            for(int i=0;i<data.length;i++)
                TotalFlow[i] += data[i];
            
            FlowAddCount++;
        }
        
        public void AddSpeed(double[] data){
            for(int i=0;i<data.length;i++)
                Speed[i] += data[i];

            SpeedAddCount++;
        }
        
        public double[] getAvgTotalFlow(){
            double[] tempFlow = new double[TotalFlow.length];
            for(int i = 0;i<TotalFlow.length;i++)
                tempFlow[i] = TotalFlow[i] == 0 ? 0 : TotalFlow[i] / FlowAddCount;
            
            return tempFlow;
        }
        
        public double[] getAvgSpeed(){
            double[] tempFlow = new double[Speed.length];
            for(int i = 0;i<Speed.length;i++)
                tempFlow[i] = Speed[i] == 0 ? 0 : Speed[i] / SpeedAddCount;
            
            return tempFlow;
        }
        
        public double[] getTotalFlowWithType(){
            switch(type){
                case CalibrationPrintType.Values :
                    return getTotalFlow();
                case CalibrationPrintType.Average:
                    return getAvgTotalFlow();
                default:
                    return getTotalFlow();
            }
        }
        
        public double[] getSpeedWithType(){
            switch(type){
                case CalibrationPrintType.Values :
                    return getSpeed();
                case CalibrationPrintType.Average:
                    return getAvgSpeed();
                default:
                    return getSpeed();
            }
        }
        
        public int getDataLength(){
        return this.TotalFlow.length;
    }
        
    }
    
    class CalibrationPrintType{
        static final public int Values = 1;
        static final public int Average = 2;
    }
}
