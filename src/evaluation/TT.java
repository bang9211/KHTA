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
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Chongmyung Park
 */
public class TT extends Evaluation {
    
    // read as more data as original period + extend_hour
    private int EXTENDED_HOUR = 2;

    @Override
    protected void init() {
        this.name = "TT";
    }

    @Override
    protected void process() {
        
//        // caching, (if cached alread, just return)
//        if(!caching()) return;
        
        if(!this.simulationMode) {
            for(Period p : opts.getPeriods()) {
                p.addEndHour(this.EXTENDED_HOUR);
                this.opts.setStartEndTime(p.start_hour, p.start_min, p.end_hour, p.end_min);
            }
        }
                
        // get station speeds
        Vector<EvaluationResult> stationSpeed = Evaluation.getResult(StationSpeed.class, this.opts);

        Period[] periods = new Period[this.opts.getPeriods().size()];
        this.opts.getPeriods().toArray(periods);
        int idx = 0;
        
        // for all results, calculate TT
        for (EvaluationResult result : stationSpeed) {
            
            EvaluationResult res = EvaluationResult.copy(result);
            
            if(printDebug && idx < periods.length) 
                System.out.println("      - " + periods[idx++].getPeriodString());
            
            // skip average
            if (TITLE_AVERAGE.equals(res.getName())) {
                continue;
            }

            // adjust data
            preAdjustResult(res);           
            
            // calculate TT
            calculateTT(res);
            
            // add result to result list
            this.results.add(res);
        }
        
        if(!this.simulationMode) {
            for(Period p : opts.getPeriods()) {
                p.addEndHour(-1 * this.EXTENDED_HOUR);
                this.opts.setStartEndTime(p.start_hour, p.start_min, p.end_hour, p.end_min);                
            }        
            
            periods = new Period[this.opts.getPeriods().size()];
            this.opts.getPeriods().toArray(periods);
            for(int i=0; i<this.results.size();i++) {
                EvaluationResult res = this.results.get(i);
                Period p = periods[i];
                res.setName(p.getPeriodString());
            }
        }
       
        makeTravelTimeAverage();

    }

    /**
     * Travel Time
     * Equation : TT(A and B) = SUM(Li/Ui)
     *   - There are i virtual stations between station A and B
     * 
     * Given EvaluationResult data format requirement :
     *   - no confidence column (if it is included, it is removed by 'preAdjustResult()')
     *   - accumulated distance required (if not included, it is created by 'preAdjustResult()')
     *   - virtual station required (if not included, it is created by 'preAdjustResult()')
     * 
     * @param res TT evaluation result
     */
    protected EvaluationResult calculateTT(EvaluationResult res) {
        
        boolean debug = false;
        
        // count value for data and station
        int dataCount = res.getRowSize(0) - (this.EXTENDED_HOUR * this.SECONDS_PER_HOUR / this.opts.getInterval().second);
        if(this.simulationMode) {
            dataCount = res.getRowSize(0);
        }
        
        int stationCount = res.getColumnSize();

        // variable to store TT
        ArrayList<ArrayList> data = new ArrayList<ArrayList>();

        // add time line to TT data set
        ArrayList times = res.getColumn(res.COL_TIMELINE());
        
        //System.out.println("RowSize="+res.getRow(0).size()+", DataCount=" + dataCount+", TimesCount="+times.size());
        
        if(!this.simulationMode) {
            while(times.size() > dataCount)
            {
                times.remove(times.size()-1);
            }
        }
        
        //times.add("Average");
        data.add(times);

        // add traffic data column to TT data set
        for (int c = res.COL_DATA_START(); c < stationCount; c++) {
            ArrayList stationData = new ArrayList();

            // add station name to station data to store TT
            stationData.add(res.get(c, res.ROW_TITLE()));

            // add distance to station data to store TT
            stationData.add(res.get(c, res.ROW_DISTANCE()));

            // add station data to data set for TT
            data.add(stationData);
        }

        // traffic data is from col 1, row 2
        double[] totalCol = new double[stationCount - 1];
        double[] validCount = new double[stationCount - 1];
        
        int interval = this.opts.getInterval().second;
        
        // TT of first station
        for(int r=res.ROW_DATA_START(); r<dataCount; r++)
        {
            data.get(res.COL_DATA_START()).add(0.0); // in minute
        }
        
        
        int rowStart = res.ROW_DATA_START();
        int colStart = res.COL_DATA_START();
                
        
        // for all data
        for (int r = res.ROW_DATA_START(); r < dataCount; r++) 
        {            
            int timeStepIndex = 1;
            int row = r;
            double TT = 0.0;
            
            for(int c=res.COL_DATA_START()+1; c<stationCount; c++)
            {

                boolean doPrint = ( debug && r == rowStart+103 );

                if(this.simulationMode && row >= dataCount) {
                    int fillcount = dataCount - data.get(c).size();
                    for(int i=0; i<fillcount; i++) {
                        data.get(c).add(-1);
                    }
                    continue;
                }                
                
                // current speed
                double u = Double.parseDouble(res.get(c-1, row).toString());

                // time (in second) to travel between two station (0.1mile)
                double ttSec = VIRTUAL_DISTANCE_IN_KM / u * SECONDS_PER_HOUR + TT;          

                
                // next time interval
                int nextTimeStep = interval * timeStepIndex;

                if(doPrint) {
                    System.out.print("["+(c-colStart)+"] U("+(c-1)+", " + row + ") = "+EvalHelper.roundUp(u,2)+", Intervals = " + nextTimeStep + ", prevTT="+EvalHelper.roundUp(TT,2)+"("+EvalHelper.roundUp(TT/60,3)+")");
                }

                if( ttSec == nextTimeStep ) {
                    // if vehicles arrived at next station(or virtual station) on next time step
                    TT = ttSec;
                    row++; timeStepIndex++;                    
                
                } else if( ttSec < nextTimeStep ) {
                    // vehicles arrived at next station(or virtual station) within next time step
                    TT = ttSec;                    
                    
                } else if( ttSec > nextTimeStep ) {
                    
                    // if vehicles didn't arrive at next station(or virtual station) by next time step
                    row++; timeStepIndex++;
                    
                    if(row < dataCount || !this.simulationMode) {
                        
                        // remained time to next time step (TT = previous travel time)
                        double tt = nextTimeStep - TT;
                        
                        // distance that can be reached with the previous speed (speed in previous row of speed evaulation result)
                        double distanceWithPrevU = u * tt / SECONDS_PER_HOUR; // mile
                        
                        // distance th next station = 0.1 mile - distance with previous speed
                        double distanceToNextStation = VIRTUAL_DISTANCE_IN_KM - distanceWithPrevU; // mile

                        
                        // speed next step
                        double nextStepSpeed = Double.parseDouble(res.get(c-1, row).toString());  // mph
                        
                        // distance that can be reached with the speed of next time step
                        double distanceWithNextU = nextStepSpeed * interval / SECONDS_PER_HOUR;

                        // it cannot be reached at next station even if using next speed
                        // it means this section is congested (too slow speed)
                        if(distanceWithPrevU + distanceWithNextU < VIRTUAL_DISTANCE_IN_KM) {
                            if(doPrint) {
                                System.out.println();
                                System.out.println("    - TooSlow : intervals-prevTT="+EvalHelper.roundUp(tt,2)+", distanceWithPrev(a)="+EvalHelper.roundUp(distanceWithPrevU,2)+", distanceWithNextU(b)="+EvalHelper.roundUp(distanceWithNextU,2)+", a+b=" + EvalHelper.roundUp(distanceWithPrevU + distanceWithNextU, 2));                            
                                System.out.println("    - d : " + EvalHelper.roundUp(distanceWithPrevU,4) + ", with u("+(c-1)+", "+(row-1)+")=" + EvalHelper.roundUp(u,3) + ", t=" + EvalHelper.roundUp(distanceWithPrevU/u*3600, 2));                            
                            }

                            double d = distanceWithPrevU;
                            
                            // calculate travel time until reaching the next station
                            while(d < VIRTUAL_DISTANCE_IN_KM) {                                

                                // missing data area
                                if(d < 0) {
                                    tt = -1 * (TT+1);
                                    break;
                                }
                                
                                try {
                                    nextStepSpeed = Double.parseDouble(res.get(c-1, row).toString());
                                } catch(Exception ex) {
                                    System.out.println("try to get("+(c-1)+", "+row+") : colSize="+res.getColumnSize() + ", rowSize="+res.getRowSize(1));
                                    ex.printStackTrace();
                                }
                                double dWithNext = nextStepSpeed * interval / SECONDS_PER_HOUR;
                                double tDistance = d + dWithNext;

                                if(tDistance == VIRTUAL_DISTANCE_IN_KM) {
                                    // arraived at the next station exactly
                                    tt += interval;
                                    if(doPrint) System.out.println("    - d : " + EvalHelper.roundUp(dWithNext,4) + ", with u("+(c-1)+", "+row+")=" + nextStepSpeed + ", t=" + EvalHelper.roundUp(dWithNext/nextStepSpeed*3600, 2));
                                    break;
                                }
                                else if (tDistance > VIRTUAL_DISTANCE_IN_KM) {
                                    // passed the next station
                                    // travel time between just two stations is calculated
                                    tt = tt + (VIRTUAL_DISTANCE_IN_KM - d)/nextStepSpeed*3600;
                                    if(doPrint) System.out.println("    - d : " + EvalHelper.roundUp(VIRTUAL_DISTANCE_IN_KM-d,4) + ", with u("+(c-1)+", "+row+")=" + nextStepSpeed + ", t=" + EvalHelper.roundUp((VIRTUAL_DISTANCE_IN_KM-d)/nextStepSpeed*3600, 2));                                
                                    break;
                                } else {
                                    // not reached to the next station
                                    tt += interval;
                                    d = tDistance;
                                    if(doPrint) System.out.println("    - d : " + EvalHelper.roundUp(dWithNext,4) + ", with u("+(c-1)+", "+row+")=" + nextStepSpeed + ", t=" + EvalHelper.roundUp(dWithNext/nextStepSpeed*3600, 2));                                                                
                                }              
                                row++; timeStepIndex++;
                            }   // while

                        } else {
                            
                        }
                    
                        // not too slow
                        if(distanceWithPrevU + distanceWithNextU >= VIRTUAL_DISTANCE_IN_KM) {

                            TT = nextTimeStep + (distanceToNextStation / nextStepSpeed * SECONDS_PER_HOUR);
                        }    // second
                        else {
                            // too slow
                            if(doPrint) System.out.println("    - adding : " + EvalHelper.roundUp(tt,3) +"seconds");
                            TT += tt;
                        }
                    } else {
                        TT = -1;
                    }

                }


                if(TT > 0) {
                    totalCol[c-res.COL_DATA_START()] += TT;
                    validCount[c-res.COL_DATA_START()]++;                   
                    data.get(c).add(TT/60); // in minute
                }
                else  {
                    data.get(c).add(-1); // in minute
                }

                if(doPrint) System.out.println(", curTT="+EvalHelper.roundUp(TT,2)+"("+EvalHelper.roundUp(TT/60,3)+")");
            } // for
            
            
        } // for

        
        // Add average row to times and data
        data.get(0).add("Average");
        for(int c=1; c<stationCount; c++)
        {
            if(totalCol[c-1] > 0 && validCount[c-1] > 0) {
                data.get(c).add(totalCol[c-1]/validCount[c-1]/60);
            } else if(totalCol[c-1] == 0) {
                data.get(c).add(0);
            } else {
                data.get(c).add(-1);
            }
        }
        
        res.setData(data);
                
        return res;
    }

    
    private TTResult getTTBetweenTwoStations(EvaluationResult res, int col, int row, double psec)
    {
        int interval = this.opts.getInterval().second;
        
        double u = Double.parseDouble(res.get(col, row).toString());
        double d = u * psec/SECONDS_PER_HOUR;
        double tt = psec;
        
        if(d > VIRTUAL_DISTANCE_IN_KM)
        {
            return new TTResult(row, tt + (VIRTUAL_DISTANCE_IN_KM - d)/u);
        }
        
        while(d < VIRTUAL_DISTANCE_IN_KM)
        {
            u = Double.parseDouble(res.get(col, ++row).toString());
            double tDistance = d + u * interval / SECONDS_PER_HOUR;
            if(tDistance == VIRTUAL_DISTANCE_IN_KM) {
                return new TTResult(row+1, tt+interval);
            }
            else if (tDistance > VIRTUAL_DISTANCE_IN_KM) {
                return new TTResult(row, tt + (VIRTUAL_DISTANCE_IN_KM - d)/u);
            } else {
                tt += interval;
                row++;
                d = tDistance;
                System.out.println("  - d = " + d);
            }
        }
        return new TTResult(-1, -1);
    }
    
    class TTResult {
        int row;
        double tt;

        public TTResult(int row, double tt) {
            this.row = row;
            this.tt = tt;
        }
        
    }
    
    
    /**
     * Adjust result to make required data
     */
     private EvaluationResult preAdjustResult(EvaluationResult res) {

        // remove confidence information from result
        this.removeConfidenceFromResult(res);

        // if res doesn't include distance, add it
        if (!res.useAccumulatedDistance()) {
            this.addDistanceToResult(res);
        }

        // if res doesn't include virtual station, add it
        if (!res.useVirtualStation()) {
            this.addVirtualStationToResult(res);
        }

        return res;
    }

    /**
     * Make TT average result
     */
    private void makeTravelTimeAverage() {

        ArrayList times = this.results.get(0).getColumn(0);

        // variable to save average
        EvaluationResult average = new EvaluationResult();

        // set statistic sheet
        average.setIsStatistic(true);
        
        // set result name
        average.setName("Travel Time");

        // add time line at first column
        average.addColumn((ArrayList) times.clone());

        // for all results
        for (EvaluationResult res : this.results) {            
//            this.printEvaluation(res);
            // add to total data set
            ArrayList lastData = (ArrayList) res.getColumn(res.getColumnSize() - 1).clone();
            lastData.set(0, res.getName());
            average.addColumn(lastData);
        } 

        ArrayList averageColumn = new ArrayList();
        averageColumn.add("Average");
        averageColumn.add(0);

        // make average column
        for (int r = 2; r < average.getRowSize(0); r++) {
            double total = 0.0f;
            for (int c = 1; c < average.getColumnSize(); c++) {
                total += Double.parseDouble(average.get(c, r).toString());
            }
            averageColumn.add(total / (average.getColumnSize() - 1));
        }

        // add average column to average data set
        average.addColumn(averageColumn);

        // add average data at last element of results
        this.results.add(average);

    }

}
