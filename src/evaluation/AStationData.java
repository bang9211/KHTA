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
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public abstract class AStationData extends Evaluation {

    @Override
    abstract protected void init();
    abstract protected double[] getTrafficData(Station s);
    
    @Override
    protected final EvaluationResult process() throws OutOfMemoryError {        
      
//        // caching, (if cached alread, just return)
//        if(!caching()) return;
//        
//        // get section that selected from TICAS GUI
//        Section section = this.opts.getSection();
//        
//        // get stations including the section
//        Station[] stations = section.getStations(this.detectorChecker);
//        
//        // for all periods
//        for (Period period : this.opts.getPeriods()) 
//        {            
//            if(printDebug) 
//                System.out.println("      - LoadAStationData..." + period.getPeriodString());
//            
//            // data load from all detectors in the section
//            section.loadData(period, this.dataLoadOption);                 
//            
//            // variable to save results
//            EvaluationResult res = new EvaluationResult();
//            
//            // result name that is used sheet name in excel or a part of file name in csv
//            res.setName(period.getPeriodString());
//            
//            // make time line list (at first column)
//            ArrayList times = new ArrayList();
//            
//            // time line's first element is "", 
//            // because first row is for station name, not a data
//            times.add("");
//            times.addAll(Arrays.asList(this.opts.getPeriods()[0].getTimeline()));
//
//            // add time line to first column
//            res.addAll(res.COL_TIMELINE(), times);
//                        
//            // for all stations
//            for (int col = 0; col < stations.length; col++) 
//            {
//                // retrieve station
//                Station s = stations[col];
//                // get data from station 
//                // this 'getTrafficData' method is implemented child-class
//                double[] data = getTrafficData(s);
//                                
//                // variable to save traffic data
//                ArrayList stationData = new ArrayList();
//                
//                // add station name at first element
//                stationData.add(EvalHelper.getStationLabel(stations[col], detectorChecker));
//                
//                // for all time series traffic data
//                for (int row = 0; row < data.length; row++) 
//                {
//                    // add data to station data according to data type
//                    // in order to save excel file as correct data type
//                    stationData.add(data[row]);
//
//                } // <!- end of loop for all time series data
//                
//                // add station data to result
//                res.addColumn(stationData);
//                
//            } // <!- end of loop for all stations
//            
//            this.results.add(res);            
//            
//        } // <!- end of loop for all periods
//                
//        for(EvaluationResult res : this.results)
//        {
//            this.addConfidenceToResult(res);
//            this.addDistanceToResult(res);
////            this.printEvaluation(res);
//            this.addVirtualStationToResult(res);
//        }
//        
//        // add average to results
//        makeAverage();
//        
//        hasResult = true;
        return null;
    }    
   
}
