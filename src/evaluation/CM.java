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
package evaluation;

import infra.Period;
import infra.Section;
import java.util.ArrayList;
import java.util.Vector;
import khta.KHTAOption;

/**
 *
 * @author Chongmyung Park
 */
public class CM extends Evaluation {
    protected Period period;
    protected Section section;
    protected String outputPath;
    // read as more data as original period + extend_hour

    public CM(Period p, Section s, String op, KHTAOption ko){
        this.period = p;
        this.section = s;
        this.outputPath = op;
        this.ko = ko;
        this.opts = ko.getEvaluationOption();
        init();
    }
    
    @Override
    protected void init() {
        this.name = "CM";
    }

    @Override
    public EvaluationResult process() {
        
        // caching, (if cached alread, just return)
        //if(!caching()) return;        
        
        ArrayList<EvaluationResult> ssResultList = new ArrayList();
        for(Period p : opts.getPeriods()){
        // get station speeds
            //Vector<EvaluationResult> stationSpeed = Evaluation.getResult(StationSpeed.class, this.opts);
            StationSpeed stationSpeed = new StationSpeed(p, opts.getSelectedSection(), ko.getOutputPath(), ko);
            ssResultList.add(stationSpeed.process());
        }
        
        //Period[] periods = this.opts.getPeriods();
        Period[] periods = new Period[this.opts.getPeriods().size()];
        this.opts.getPeriods().toArray(periods);
        int idx = 0;
        // for all results, calculate CM
        for(EvaluationResult result : ssResultList)
        {
            EvaluationResult res = EvaluationResult.copy(result);
                        
            if(printDebug && idx < periods.length) System.out.println("      - " + periods[idx++].getPeriodString());
            
            // skip average
            if(TITLE_AVERAGE.equals(res.getName())) continue;
            
            // adjust data
            preAdjustResult(res);

            // calculate CM
            calculateCM(res);
            
            // add result to result list
            this.results.add(res);                        
        }                
       
       makeTCMResult();

       return null;
    }
    
    /**
     * Congested Miles (Miles, %)
     * => CM(j): Sum of freeway segments whose speed values are less than or equal to 'User specified threshold'
     * Equation : CM(j) = SUM([L(i) | ui less than threshold])
     * 
     * Given EvaluationResult data format requirement :
     *   - no confidence column (if it is included, it is removed by 'preAdjustResult()')
     *   - accumulated distance required (if not included, it is created by 'preAdjustResult()')
     *   - virtual station required (if not included, it is created by 'preAdjustResult()')
     * 
     * @param res CM evaluation result
     */
    protected EvaluationResult calculateCM(EvaluationResult res)
    {       
        double cmValue = VIRTUAL_DISTANCE_IN_KM;
        
        int congestionThrosholdSpeed = this.opts.getCongestionThresholdSpeed();
        int dataStartCol = res.COL_DATA_START();
        int dataStartRow = res.ROW_DATA_START();
        
        // count value for data and station
        int dataCount = res.getRowSize(0);
        int stationCount = res.getColumnSize();
        
        // variable to store CM
        ArrayList<ArrayList> data = new ArrayList<ArrayList>();
        
        // add time line to CM data set
        ArrayList times = res.getColumn(res.COL_TIMELINE());
        data.add(times);       
        
        // add traffic data column to CM data set
        for(int c=res.COL_DATA_START(); c<stationCount; c++)
        {
            ArrayList stationData = new ArrayList();
            
            // add station name to station data to store CM
            stationData.add(res.get(c, res.ROW_TITLE()));
            
            // add distance to station data to store CM
            stationData.add(res.get(c, res.ROW_DISTANCE()));            
            
            // add station data to data set for CM
            data.add(stationData);
        }       

       // traffic data is from col 1, row 2
        double[] totalRow = new double[stationCount-1];
        double[] totalCol = new double[dataCount-1];
        
        for(int r=dataStartRow; r<dataCount; r++)
        {        
            // for all stations that from column 1
            //   : column 0 => time line
            for(int c=dataStartCol; c<stationCount; c++)
            {
                double speed = Double.parseDouble(res.get(c, r).toString());
                if(speed <= congestionThrosholdSpeed) {
                    totalRow[c-dataStartCol]++;
                    totalCol[r-dataStartRow]++;
                    data.get(c).add(VIRTUAL_DISTANCE_IN_KM);
                } else data.get(c).add(0);
                
            }
        }
        
        // Add TCM, TCM(%) and CM row
        data.get(0).add("TCM");
        data.get(0).add("TCM(%)");        
        
        for(int c=dataStartCol; c<stationCount; c++)
        {
            data.get(c).add(totalRow[c-dataStartCol]*cmValue);
            data.get(c).add(totalRow[c-dataStartCol]/(dataCount-dataStartRow)*100);
        }

        // Add TCM, TCM(%) and CM col
        ArrayList colTCM = new ArrayList();
        colTCM.add("TCM");
        colTCM.add(0);  // add distance row
        ArrayList colTCMPercentage = new ArrayList();
        colTCMPercentage.add("TCM(%)");
        colTCMPercentage.add(0);  // add distance row
        data.add(colTCM);
        data.add(colTCMPercentage);
        
        double totalTCM = 0.0f;
        double totalTCMP = 0.0f;
        for(int r=dataStartRow; r<dataCount; r++)
        {
            double tcm = totalCol[r-dataStartRow]*cmValue;
            double tcmp = totalCol[r-dataStartRow]/(stationCount-dataStartRow)*100;
            colTCM.add(tcm);
            colTCMPercentage.add(tcmp);
            totalTCM += tcm;
            totalTCMP += tcmp;
        }   
        
        colTCM.add(totalTCM);
        colTCM.add(-1);
        
        colTCMPercentage.add(-1);
        colTCMPercentage.add(totalTCMP/(dataCount-dataStartRow+1));
                    
        res.setData(data);
        
        return res;
    }

    /**
     * Adjust result to make required data
     */
    private EvaluationResult preAdjustResult(EvaluationResult res) {
        
        // remove confidence information from result
        this.removeConfidenceFromResult(res);

        // if res doesn't include distance, add it
        if(!res.useAccumulatedDistance()) this.addDistanceToResult(res);
        
        // if res doesn't include virtual station, add it
        if(!res.useVirtualStation()) this.addVirtualStationToResult(res);
                
        return res;
    }
    
    /**
     * Make TCM result
     */
    private void makeTCMResult() {

        ArrayList times = this.results.get(0).getColumn(0);
        
        // variable to save average
        EvaluationResult TCMResult = new EvaluationResult();
        
        // set statistic sheet
        TCMResult.setIsStatistic(true);
        
        // set result name
        TCMResult.setName("Congested Miles");
        
        // add time line at first column
        TCMResult.addColumn(times);
                
        // for all results
        for(EvaluationResult res : this.results)
        {     
            // add to total data set
            ArrayList lastData = (ArrayList)res.getColumn(res.getColumnSize()-2).clone();
            lastData.set(0, res.getName());
            TCMResult.addColumn(lastData);
        }
        
        ArrayList averageColumn = new ArrayList();
        averageColumn.add("Average");
        averageColumn.add(0);
        
        // make average column
        for(int r=2; r<TCMResult.getRowSize(0); r++)
        {
            double total = 0.0f;
            for(int c=1; c<TCMResult.getColumnSize(); c++)
            {
                total += Double.parseDouble(TCMResult.get(c, r).toString());
            }
            averageColumn.add(total/(TCMResult.getColumnSize()-1));
        }
        
        // add average column to average data set
        TCMResult.addColumn(averageColumn);
        
        // add average data at last element of results
        this.results.add(TCMResult);
        
    }


}
