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
import infra.infraobject.Station;
import java.util.ArrayList;
import java.util.Vector;
import khta.KHTAOption;

/**
 *
 * @author Chongmyung Park
 */
public class LVMT extends Evaluation {
    protected Period period;
    protected Section section;
    protected String outputPath;
    
    private int congestionThresholdSpeed;
    private int laneCapacity;
    private int criticalDensity;
    
    public LVMT(Period p, Section s, String op, KHTAOption ko){
        this.period = p;
        this.section = s;
        this.outputPath = op;
        this.ko = ko;
        this.opts = ko.getEvaluationOption();
        init();
    }
    
    @Override
    protected void init() {
        this.name = "LVMT";
        congestionThresholdSpeed = this.opts.getCongestionThresholdSpeed();
        criticalDensity = this.opts.getCriticalDensity();
        laneCapacity = this.opts.getLaneCapacity();
    }

    @Override
    protected EvaluationResult process() {
        
        // caching, (if cached alread, just return)
        //if(!caching()) return;
        
        // get station total flow
        Vector<EvaluationResult> stationFlows = Evaluation.getResult(StationTotalFlow.class, this.opts);
        
        // get station density
        Vector<EvaluationResult> stationDensities = Evaluation.getResult(StationDensity.class, this.opts);
        
        //Period[] periods = this.opts.getPeriods();
        Period[] periods = new Period[this.opts.getPeriods().size()];
        this.opts.getPeriods().toArray(periods);
        int idx = 0;
        int densityidx = 0;
        if(stationDensities.size() > 1)
            densityidx = 1;
        // for all results, calculate VMT
        for(int i=0; i<stationFlows.size(); i++)
        {
            if(printDebug && idx < periods.length) System.out.println("      - " + periods[idx++].getPeriodString());
            
            EvaluationResult res = EvaluationResult.copy(stationDensities.get(i+densityidx)) ;
            EvaluationResult flowResult = EvaluationResult.copy(stationFlows.get(i));            
            
            // skip average
            if(TITLE_AVERAGE.equals(res.getName())) continue;
           
            // adjust data
            preAdjustResult(res);
            preAdjustResult(flowResult);
                        
            // calculate LVMT
            res = calculateLVMT(res, flowResult);
            // add result to result list
            this.results.add(res);                                    
        }
        
        this.makeTotal();

        return null;
    }
    /**
     * Lost VMT for congestion
     * Equation :  
     *     capacity balance = lane capacity (v/h) * lane - total flow of station (v/h)
     *     LVMT = capacity balance (v/h) * interval(hour) * 0.1(distance in mile)
     *          - calculates if capacity balance > 0
     * 
     * Given EvaluationResult data format requirement :
     *   - no confidence column (if it is included, it is removed by 'preAdjustResult()')
     *   - accumulated distance required (if not include, it is created by 'preAdjustResult()')
     *   - virtual station required (if not included, it is created by 'preAdjustResult()')
     * 
     * @param res station density evaluation result
     * @param flows station total flow evaluation result
     */
    protected EvaluationResult calculateLVMT(EvaluationResult res, EvaluationResult flows)
    {             
        int interval = this.opts.getInterval().second;        
        
        int dataCount = res.getRowSize(0);
        int stationCount = res.getColumnSize();
        
        // make lane counter according to detector checker
        Station[] stations = this.opts.getSelectedSection().getStations();
        int[] lanes = new int[stations.length];
        for(int i=0; i<stations.length; i++)
        {
            lanes[i] = stations[i].getLanes(detectorChecker);
        }
                
        // variable to store LVMT
        ArrayList<ArrayList> data = new ArrayList<ArrayList>();

        // add time line to LVMT data set        
        data.add(res.getColumn(0));

        // add traffic data column to LVMT data set
        for(int c=1; c<stationCount; c++)
        {
            ArrayList stationData = new ArrayList();
            
            // add station name to station data to store LVMT
            stationData.add(res.get(c, res.ROW_TITLE()));
            
            // add distance to station data to store LVMT
            stationData.add(res.get(c, res.ROW_DISTANCE()));            
            
            // add station data to data set for LVMT
            data.add(stationData);
        }
        
        // add sum column to data
        ArrayList totalLVMTData = new ArrayList();        
        // add station name as 'Total'
        totalLVMTData.add("Total");
        // display distance as 0
        totalLVMTData.add(0);  
        // add to data
        data.add(totalLVMTData);
                
       // traffic data is from row 2
        //   : row(0) = station name
        //   : row(1) = distance
        int lane = 0;
        for(int r=2; r<dataCount; r++)
        {        
            double totalLVMT = 0.0f;

            // for all stations that from column 1
            //   : column 0 : time line
            for(int c=1; c<stationCount; c++)
            {
                // get lane from lanes data made previously with column title
                if(!NO_STATION.equals(res.get(c, 0)))
                {             
                    String sname = EvalHelper.getStationNameFromTitle(res.get(c, 0).toString());
                    lane = EvalHelper.getLanes(sname, stations, detectorChecker);
                }
                
                // LVMT equation
                if (this.criticalDensity < (Double)(res.get(c, r))) {
                    double lvmt = this.laneCapacity * lane - Double.parseDouble(flows.get(c, r).toString());
                    if(lvmt < 0) lvmt = 0;
                    lvmt = (lvmt * interval / SECONDS_PER_HOUR * VIRTUAL_DISTANCE_IN_KM);
                    data.get(c).add(lvmt);                    
                    totalLVMT += lvmt;                
                } else {
                    data.get(c).add(0);
                }
            }
            totalLVMTData.add(totalLVMT);
        }
        
        res.setData(data);
        res.setUseTotalColumn(true);
        
        return res;
    }
    
    /**
     * Returns lane number of station
     * @param columnTitle column title, e.g. S910 (Crystal Lake Rd)
     * @param lanes data including lane number
     * @return lane number
     */
    protected int getLanes(String columnTitle, int[] lanes)
    {
        Station[] stations = this.opts.getSelectedSection().getStations();
        for(int i=0; i<stations.length; i++)
        {
            if(EvalHelper.getStationLabel(stations[i], detectorChecker).equals(columnTitle)) {
                return lanes[i];
            }
        }
        return 0;        
    }
    
    /**
     * adjust result to make required data
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
       
}
