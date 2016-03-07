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
package evaluation;

import infra.Section;
import infra.infraobject.Station;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import khta.KHTAOption;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class Evaluation {
    
    protected Vector<EvaluationResult> results = new Vector<EvaluationResult>();
    protected String name;
    protected KHTAOption ko;
    protected EvaluationOption opts;
    protected boolean printDebug = true;
    protected boolean simulationMode = false;
    
    protected final int SECONDS_PER_HOUR = 3600;
    protected final String NO_STATION = "-";
    protected final String TITLE_AVERAGE = "Average";
    protected final String TITLE_TOTAL = "Total";
    protected final String TITLE_DISTANCE = "Accumulated Distance";
    protected final String TITLE_CONFIDENCE = "Confidence Level (%)";
    
    protected final double VIRTUAL_DISTANCE_IN_KM = 0.1;  // 0.1 mile
    
    protected abstract void init();
    
    protected abstract void process();
            
    protected boolean checkExcelDataLength() {

        int maxColumn = 256;
        int maxRow = 65536;
        int colSize = 0;
        int rowSize = 0;
        
//        EvaluationResult ev = null;
//        for(int i=0;i<results.size();i++){
//            if(results.get(i) != null){
//                ev = results.get(i);
//                break;
//            }
//        }
//        if(ev == null){
//            colSize = 0;
//            rowSize = 0;
//        }
        
        if (this.ko.getOutputDirection().isToRight()) {
            colSize = this.results.get(0).getRowSize(0);
            rowSize = this.results.get(0).getColumnSize();
        } else {
            colSize = this.results.get(0).getColumnSize();
            rowSize = this.results.get(0).getRowSize(0);
        }
        
        return (maxColumn > colSize) && (maxRow > rowSize);
    }
    
    /**
     * Save to excel file
     * @param outputpath
     * @throws Exception 
     */
    public void saveExcel(String outputpath) throws Exception {
        String workbookFile = getFileName(outputpath, name, "xls");
        //System.out.println("Save to Excel : " + workbookFile);

        if (!checkExcelDataLength()) {
            JOptionPane.showMessageDialog(null, this.name + " : Too many data.. \nTry it again after changeing col-row mode or use csv mode.\nIf it happen again, reduce period.");
            return;
        }

        WritableWorkbook workbook = Workbook.createWorkbook(new File(workbookFile));

        int sheet_count = 0;
        
        Vector<EvaluationResult> cResults = (Vector<EvaluationResult>)this.results.clone();
        
        for (EvaluationResult res : cResults) {
            WritableSheet sheet = workbook.createSheet(res.getName(), sheet_count++);

            ArrayList<ArrayList> data = res.getData();

            if (this.ko.getOutputDirection().isToRight()) {
                data = res.getTransposedData();
            }

            int cols = data.size();
            int rows = data.get(0).size();

            for (int c = 0; c < cols; c++) {                
                for (int r = 0, rr=0; r < rows; r++) {
                    
                    Object o = data.get(c).get(r);
                    try {
                        if (isDouble(o)) {
                            sheet.addCell(new Number(c, rr, (Double) o));
                        } else if (isInteger(o)) {
                            sheet.addCell(new Number(c, rr, (Integer) o));
                        } else {
                            sheet.addCell(new Label(c, rr, (String) o));
                        }
                        rr++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        System.out.println("END WRITE");
        workbook.write();
        workbook.close();

    }
    
    /**
     * Save to CSV file
     * @throws Exception 
     */
    public void saveCsv(String outputpath) throws Exception {
        
        Vector<EvaluationResult> cResults = (Vector<EvaluationResult>)this.results.clone();
        
        for (EvaluationResult res : cResults) {
            
            //FileWriter fw = new FileWriter(getFileName(outputpath, res.getName(), "csv"), false);
            FileOutputStream fos = new FileOutputStream(getFileName(outputpath, res.getName(), "csv"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "EUC-KR");
            BufferedWriter bw = new BufferedWriter(osw);

            ArrayList<ArrayList> data = res.getData();
            
            if (this.ko.getOutputDirection().isToRight()) {
                data = res.getTransposedData();
            }

            int cols = data.size();
            int rows = data.get(0).size();

            StringBuilder contents = new StringBuilder(2048);
            String d = null;
            
            for (int r = 0; r < rows; r++) {

                for (int c = 0; c < cols; c++) {

                    Object o = data.get(c).get(r);

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
                contents.append(System.getProperty("line.separator"));
            }
            bw.write(contents.toString());
            bw.close();

        }
    }
    
    /**
     * Adds accumulated distance to result
     * @param res Evaluation Result
     */
//    protected EvaluationResult addDistanceToResult(EvaluationResult res) {
//        if (res.useAccumulatedDistance()) {
//            return res;
//        }
//
//        // where should we insert distance information into data?
//        int distanceRow = res.ROW_DISTANCE();
//
//        Station[] stations = this.opts.getSection().getStations(this.detectorChecker);
//
//        // add label to time line
//        ArrayList times = res.getColumn(res.COL_TIMELINE());
//        times.add(distanceRow, TITLE_DISTANCE);
//
//        // for all stations 
//        double distance = 0.1;
//        if (!res.useVirtualStation()) {
//            distance = 0;
//        }
//        for (int col = res.COL_DATA_START(); col < res.getColumnSize(); col++) {
//            if (res.useVirtualStation()) {
//                res.insert(col, distanceRow, distance);
//                distance += 0.1;
//            } else {
//                res.insert(col, distanceRow, distance);
//                if (col < res.getColumnSize() - 1) {
//                    distance += EvalHelper.getDistance(this.opts.getSection().getName(), stations[col - 1]); //roundUp((double)stations[col-1].getDistanceToDownstreamStation()/FEET_PER_MILE, 1);
//                }
//            }
//        }
//        res.setUseAccumulatedDistance(true);
//        return res;
//    }
    
     /**
     * Removes accumulated distance from result
     * @param res evaluation result
     * @return evaluation result that accumulated distance is removed
     */
    protected EvaluationResult removeDistanceFromResult(EvaluationResult res) {
        if (!res.useAccumulatedDistance()) {
            return res;
        }

        int distanceRow = res.ROW_DISTANCE();

        for (int c = 0; c < res.getColumnSize(); c++) {
            res.remove(c, distanceRow);
        }
        res.setUseAccumulatedDistance(false);
        return res;
    }
    
    /**
     * Returns this name
     * @return 
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns results
     */
    public Vector<EvaluationResult> getResult() {
        return this.results;
    }
    
     /**
     * Is it integer ?
     * @param data string
     * @return true if it is interger format, else false
     */
    protected boolean isInteger(Object data) {
        return (data instanceof Integer);
        //return !NO_STATION.equals(data) && data.matches("^-?[0-9]+$") || data.matches("^[0]+(.)[0]+$");
    }
    
    /**
     * Is it double ?
     * @param data string
     * @return true if it is double format, else false
     */
    protected boolean isDouble(Object data) {
        return (data instanceof Double);
    }
    
    /**
     * Get file name to save in order to overwrite
     * @param name option name for txt file (txt file should be made multiple file)
     * @param ext extension
     * @return filepath string
     */
    private String getFileName(String outputPath, String name, String ext) {
        String nameOption = (name == null ? "" : "_" + name);
        String filepath = outputPath + File.separator + this.name + nameOption + "." + ext;
        int count = 0;
        while (true) {
            File file = new File(filepath);
            if (!file.exists()) {
                break;
            }
            filepath = outputPath + File.separator + this.name + nameOption + " (" + (++count) + ")" + "." + ext;
        }

        return filepath;
    }
    
    /**
     * Interpolate missing station data
     * @param res 
     */
    public void fixMissingStation(EvaluationResult res) {
        Section section = this.opts.getSelectedSection();
        Station[] stations = section.getStations();
        
        double value, upstationValue, downstationValue;
        int colStart = res.COL_DATA_START();
        int rowStart = res.ROW_DATA_START();
        int colEnd = res.getColumnSize();
        int rowEnd = res.getRowSize(0);
        int upstationIdx = -1, downstationIdx = -1;
        double distance, distanceToDownstation, distanceToUpstation;

        // for all stations
        for (int c = colStart; c < colEnd; c++) {
            // for all data (row)
            for (int r = rowStart; r < rowEnd; r++) {
                // retrieve data
                value = Double.parseDouble(res.get(c, r).toString());
                // if missing data
                if (value < 0) {
                    upstationValue = 0;
                    downstationValue = 0;
                    upstationIdx = -1;
                    downstationIdx = -1;

                    // find upstation value and index
                    if (c > colStart) {
                        for (int i = c - 1; i > colStart; i--) {
                            upstationValue = Double.parseDouble(res.get(i, r).toString());
                            if (upstationValue > 0) {
                                upstationIdx = i - colStart;
                                break;
                            }
                        }
                    }

                    // find downstation value and index
                    if (c < colEnd - 1) {
                        for (int i = c + 1; i < colEnd; i++) {
                            downstationValue = Double.parseDouble(res.get(i, r).toString());
                            if (downstationValue > 0) {
                                downstationIdx = i - colStart;
                                break;
                            }
                        }
                    }

                    // calculate interpolated value                    
                    // if it's not first and last station
                    if (upstationIdx > 0 && downstationIdx > 0) {
                        distance = section.getFeetInSection(stations[upstationIdx], stations[downstationIdx]);
                        double oneThirdDistance = distance / 3.0;
                        distanceToUpstation = section.getFeetInSection(stations[upstationIdx], stations[c - colStart]); 
                        distanceToDownstation = section.getFeetInSection(stations[c - colStart], stations[downstationIdx]);

                        if (distanceToUpstation <= oneThirdDistance) {
                            value = upstationValue;
                        } else if (distanceToDownstation <= oneThirdDistance) {
                            value = downstationValue;
                        } else {
                            value = (upstationValue + downstationValue) / 2;
                        }

                    } else if (downstationIdx > 0) {
                        // if it's first station                        
                        value = downstationValue;
                    } else if (upstationIdx > 0) {
                        // if it's last station
                        value = upstationValue;
                    }

                    // update data
                    res.set(c, r, value);
                }
            }
        }
    }
    
    protected void fixZeroMissingStation(EvaluationResult res) {
        Section section = this.opts.getSelectedSection();
        Station[] stations = section.getStations();
        
        double value, upstationValue, downstationValue;
        int colStart = res.COL_DATA_START();
        int rowStart = res.ROW_DATA_START();
        int colEnd = res.getColumnSize();
        int rowEnd = res.getRowSize(0);
        int upstationIdx = -1, downstationIdx = -1;
        double distance, distanceToDownstation, distanceToUpstation;

        // for all stations
        for (int c = colStart; c < colEnd; c++) {
            // for all data (row)
            for (int r = rowStart; r < rowEnd; r++) {
                // retrieve data
                value = Double.parseDouble(res.get(c, r).toString());
                // if missing data
                if (value < 0) {
                    res.set(c, r, 0);
                }
            }
        }
    }
}
