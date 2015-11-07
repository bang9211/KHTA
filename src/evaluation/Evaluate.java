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

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Evaluate {
    /**
     * Save to excel file
     * @throws Exception 
     */
//    public void saveExcel(String outputpath) throws Exception {
//        String workbookFile = getFileName(outputpath, null, "xls");
//        //System.out.println("Save to Excel : " + workbookFile);
//
//        if (!checkExcelDataLength()) {
//            JOptionPane.showMessageDialog(null, this.name + " : Too many data.. \nTry it again after changeing col-row mode or use csv mode.\nIf it happen again, reduce period.");
//            return;
//        }
//
//        WritableWorkbook workbook = Workbook.createWorkbook(new File(workbookFile));
//
//        int sheet_count = 0;
//        
//        Vector<EvaluationResult> cResults = (Vector<EvaluationResult>)this.results.clone();
//        
//        for (EvaluationResult res : cResults) {
//            if(this.opts.hasOption(OptionType.WITHOUT_VIRTUAL_STATIONS)) {
//                res = this.removeVirtualStationFromResult(res);
//            }
//            WritableSheet sheet = workbook.createSheet(res.getName(), sheet_count++);
//
//            ArrayList<ArrayList> data = res.getData();
//
//            if (this.opts.getOutputDirection().isToRight()) {
//                data = res.getTransposedData();
//            }
//
//            int cols = data.size();
//            int rows = data.get(0).size();
//
//            for (int c = 0; c < cols; c++) {                
//                for (int r = 0, rr=0; r < rows; r++) {
//                    
//                    Object o = data.get(c).get(r);
//                    try {
//                        if (isDouble(o)) {
//                            sheet.addCell(new Number(c, rr, (Double) o));
//                        } else if (isInteger(o)) {
//                            sheet.addCell(new Number(c, rr, (Integer) o));
//                        } else {
//                            sheet.addCell(new Label(c, rr, (String) o));
//                        }
//                        rr++;
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        workbook.write();
//        workbook.close();
//
//    }
}
