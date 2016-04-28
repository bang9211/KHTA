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
//import infra.Period;
//import java.io.File;
//import java.util.Calendar;
//import java.util.Date;
//import jxl.Workbook;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.Number;
//import jxl.write.Label;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public class VSLResultExtractor {
//    VSLResults vslresult;
//    
//    public VSLResultExtractor(VSLResults res) {
//        vslresult = res;
//    }
//    
//    public void run(){
//        int sheet_count = 0;
//        int idx = 0;
//        try{
//            String workbookFile = getFileName("VSLResult("+vslresult.getName()+")","xls");
//            WritableWorkbook workbook = Workbook.createWorkbook(new File(workbookFile));
//            
//            writeStationSpeed(workbook,sheet_count++);
//            writeStationAcc(workbook,sheet_count++);
//            writeStationVSS(workbook,sheet_count++);
//            writeDatasByDistance(workbook,sheet_count++,RESULTTYPE.STATION);
//            writeDatasByDistance(workbook,sheet_count++,RESULTTYPE.StationcACC);
//            writeDatasByDistance(workbook,sheet_count++,RESULTTYPE.StationVSS);
//            writeDatasByDistance(workbook,sheet_count++,RESULTTYPE.DMS);
//            writeDatasByDistance(workbook,sheet_count++,RESULTTYPE.ALL);
//            
//            
//            workbook.write();
//            workbook.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    
//    private String getFileName(String name, String ext) {
//        String[] filter_word = {"\\\"","\\/","\\\\","\\:","\\*","\\?","\\<","\\>","\\|"};
//        for(int i=0;i<filter_word.length;i++){
//            name = name.replaceAll(filter_word[i], "-");
//        }
//        
//        String filepath = name + "." + ext;        
//        int count = 0;
//        while (true) {
//            File file = new File(filepath);
//            if (!file.exists()) {
//                break;
//            }
//            filepath = name + " (" + (++count) + ")" + "." + ext;
//        }
//        
//        return filepath;
//    }
//    
//    private void writeDatasByDistance(WritableWorkbook workbook, int scout, RESULTTYPE rtype) {
//        WritableSheet sheet = workbook.createSheet(rtype.toString(), scout);
//        
//        int col = 0;
//        double INC = 0.1;
//        Double colMile = 0.0;
//        
//        WritePeriod(sheet,col++,2,vslresult.getDataLength());
//        
//        for(Double d : vslresult.getMilePoints()){
//            
//            while(true){
//                if(d < (colMile + 0.05)){
//                    addNumberData(sheet,col,0,colMile);
//                    
//                    VSLResultInfra vinfra = vslresult.getDataByKey(d);
//                    
//                    addResultDataRows(sheet, col, 1, vinfra, rtype);
//                    colMile += INC;
//                    col++;
//                    break;
//                }else{
//                    addNumberData(sheet,col,0,colMile);
//                    colMile += INC;
//                    col++;
//                }
//                
//            }
//        }
//    }
//    
//    private void addResultDataRows(WritableSheet sheet, int Startcol, int StartRow, VSLResultInfra vinfra, RESULTTYPE rtype) {
//        int row = StartRow;
//        
//        if(vslresult.isStationByKey(vinfra.getMilePoint()) && (rtype.isStation() || rtype.isStationACC() || rtype.isStationVSS())){
//            addStringData(sheet,Startcol,row++,vinfra.getID());
//            VSLResultStation rs = (VSLResultStation)vinfra;
//            if(rtype.isStation()){
//                addDataRows(sheet,Startcol,row,rs.getRollingSpeeds());
//            }else if(rtype.isStationVSS()){
//                addDataRows(sheet,Startcol,row,rs.getCurrentVSS());
//            }else if(rtype.isStationACC()){
//                addDataRows(sheet,Startcol,row,rs.getAcceleration());
//            }
//            return;
//        }
//        
//        if(vslresult.isDMSByKey(vinfra.getMilePoint()) && rtype.isDMS()){
//            addStringData(sheet,Startcol,row++,vinfra.getID());
//            VSLResultDMS rdms = (VSLResultDMS)vinfra;
//            addDataRows(sheet,Startcol,row,rdms.getSpeedLimit());
//            return;
//        }
//    }
//
//    private void writeStationSpeed(WritableWorkbook workbook, int scount) {
//        WritableSheet sheet = workbook.createSheet("Speed", scount);
//        
//        int col = 0;
//        WritePeriod(sheet,col++,1,vslresult.getDataLength());
//        for(VSLResultStation rs : vslresult.getStations().values()){
//            addStringData(sheet,col,0,rs.getID());
//            addDataRows(sheet,col++,1,rs.getRollingSpeeds());
//        }
//    }
//    
//    private void writeStationAcc(WritableWorkbook workbook, int scount) {
//        WritableSheet sheet = workbook.createSheet("Acceleration", scount);
//        
//        int col = 0;
//        
//        WritePeriod(sheet,col++,1,vslresult.getDataLength());
//        for(VSLResultStation rs : vslresult.getStations().values()){
//            addStringData(sheet,col,0,rs.getID());
//            addDataRows(sheet,col++,1,rs.getAcceleration());
//        }
//    }
//    
//    private void writeStationVSS(WritableWorkbook workbook, int scount) {
//        WritableSheet sheet = workbook.createSheet("VSS", scount);
//        
//        int col = 0;
//        
//        WritePeriod(sheet,col++,1,vslresult.getDataLength());
//        for(VSLResultStation rs : vslresult.getStations().values()){
//            addStringData(sheet,col,0,rs.getID());
//            addDataRows(sheet,col++,1,rs.getCurrentVSS());
//        }
//    }
//
//    private void addDataRows(WritableSheet sheet, int Startcol, int StartRow, double[] rollingSpeeds) {
//        int col = Startcol;
//        int row = StartRow;
//        
//        for(double d : rollingSpeeds){
//            addNumberData(sheet,col, row++, d);
//        }
//    }
//    
//    private void addDataRows(WritableSheet sheet, int Startcol, int StartRow, String[] data) {
//        int col = Startcol;
//        int row = StartRow;
//        
//        for(String d : data){
//            addStringData(sheet,col, row++, d);
//        }
//    }
//    
//    private void addDataRows(WritableSheet sheet, int Startcol, int StartRow, boolean[] rollingSpeeds) {
//        int col = Startcol;
//        int row = StartRow;
//        
//        for(boolean d : rollingSpeeds){
//            if(d){
//                addNumberData(sheet,col, row++, 60);
//            }else{
//                row++;
//            }
//        }
//    }
//    
//    private void addNumberData(WritableSheet sheet, int col, int row, double data){
//        try{
//            sheet.addCell(new Number(col, row, data));
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    
//    private void addStringData(WritableSheet sheet, int col, int row, String data){
//        try{
//            sheet.addCell(new Label(col, row, data));
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void WritePeriod(WritableSheet sheet, int col, int row, int length) {
//        Date d = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(d);
//        c.set(Calendar.HOUR, 0);
//        c.set(Calendar.MINUTE,0);
//        c.set(Calendar.SECOND, 0);
//        Date sTime = c.getTime();
//        c.add(Calendar.SECOND, length*30);
//        Date eTime = c.getTime();
//        Period p = new Period(sTime, eTime, 30);
//        
//        addDataRows(sheet,col,row,p.getTimeline());
//    }
//    
//    enum RESULTTYPE {
//        STATION,
//        DMS,
//        ALL,
//        StationVSS,
//        StationcACC;
//        
//        public boolean isStation(){return this == STATION || this == ALL;}
//        public boolean isDMS(){return this == DMS || this == ALL;}
//        public boolean isStationVSS(){return this == StationVSS;}
//        public boolean isStationACC(){return this == StationcACC;}
//    }
//}
