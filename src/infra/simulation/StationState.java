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
package infra.simulation;

import infra.*;
import infra.infraobject.*;
import infra.simobjects.*;
import infra.simulation.vsl.algorithm.VSLConfig;
import infra.type.TrafficType;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class StationState extends State {

    SimObjects simobjects;
    SimStation simstation;
    Section section;
    //EntranceState associatedEntrance;
//   int stationIdx = 0;
    double aggregatedDensity = 0;
    double aggregatedSpeed = 0;

    protected int MOVING_U_AVG_WINDOW = 2;
    protected int MOVING_K_AVG_WINDOW = 2;

    protected int MAX_SPEED_ALPHA = 10;
    protected int lastSpeedAggCount = 0;
    protected double NEARBY_DISTANCE = 2000;  // 2000 feet

    protected Vector<Double> RollingSpeed = new Vector<Double>();

    private StationState upstreamStationState;
    private StationState downstreamStationState;
    private double distanceToUpstreamStation = -1;
    private double distanceToDownstreamStation = -1;

    public StationState(Station s, Section _sec, SimObjects simObjects) {
        this(s, _sec, simObjects, null);
    }

    public StationState(Station s, Section _sec, SimObjects simObjects, SimInterval simIntv) {
        super(s.getID(), s, simIntv);
        section = _sec;
        System.out.println(s.getID() + ", " + s.getName() + ", " + s.getID());
        this.simstation = simObjects.getStation(s.getID());
        type = StateType.STATION;
        simobjects = simObjects;
    }

    /**
     * fix me -> for VSLStationState Default update every 30sec
     */
    public void updateState(SimulationGroup sg) {
        if (isStateTurn(sg)) {
            updateRollingSamples();
        }
        updateRollingSpeed(getSpeed(), sg);
    }

    public SimStation getSimStation() {
        return simstation;
    }

    public Station getStation() {
        return simstation.getStation();
    }

    public Section getSection() {
        return this.section;
    }

    public SimObjects getSimObject() {
        return this.simobjects;
    }

    /**
     * get Density by Interval
     *
     * @param sg Simulation Group
     * @param runTime Current Runtime
     * @return
     */
    public double getIntervalDensity(SimulationGroup sg) {
        return getIntervalValue(sg, TrafficType.DENSITY);
    }

    /**
     * @return
     */
    public double getDensity() {
        return this.simstation.getData(TrafficType.DENSITY);
//            return this.station.getDataForDebug(dc, TrafficType.DENSITY);
    }

    /**
     * get Aggregate Density by Interval
     *
     * @param sg
     * @param runTime
     * @return
     */
    public double getIntervalAggregatedDensity(SimulationGroup sg) {
        return getIntervalAggregatedDensity(sg, 0);
    }

    /**
     * get Aggregate Density by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @return
     */
    public double getIntervalAggregatedDensity(SimulationGroup sg, int prevStep) {
        return getIntervalAverageDensity(sg, prevStep, MOVING_K_AVG_WINDOW);
    }

    /**
     * get Average Density by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getIntervalAverageDensity(SimulationGroup sg, int prevStep, int howManySteps) {
        return getIntervalValue(sg, TrafficType.DENSITY, prevStep, howManySteps);
    }

    /**
     * Return aggregated density
     *
     * @return
     */
    public double getAggregatedDensity() {
        return getAggregatedDensity(0);
    }

    public double getAggregatedDensity(int prevStep) {
        return getAverageDensity(prevStep, MOVING_K_AVG_WINDOW);
    }

    /**
     * Returns aggregated density before given prevStep time step
     *
     * @return
     */
    public double getAverageDensity(int prevStep, int howManySteps) {
        return getAverageValue(prevStep, howManySteps, TrafficType.DENSITY);
    }

    /**
     * @return
     */
    public double getSpeed() {
        return this.simstation.getData(TrafficType.SPEED);
    }

    public double getIntervalSpeed(SimulationGroup sg) {
        return getIntervalValue(sg, TrafficType.SPEED);
    }

    /**
     * get Aggregate Speed by Interval
     *
     * @param sg
     * @param runTime
     * @return
     */
    public double getIntervalAggregatedSpeed(SimulationGroup sg) {
        return getIntervalAggregatedSpeed(sg, 0);
    }

    /**
     * get Aggregate Speed by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @return
     */
    public double getIntervalAggregatedSpeed(SimulationGroup sg, int prevStep) {
        return getIntervalAggregatedSpeed(sg, prevStep, MOVING_U_AVG_WINDOW);
    }

    /**
     * get Average Speed by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getIntervalAggregatedSpeed(SimulationGroup sg, int prevStep, int howManySteps) {
        return getIntervalValue(sg, TrafficType.SPEED, prevStep, howManySteps);
    }

    /**
     * get Aggregate Speed
     *
     * @return
     */
    public double getAggregatedSpeed() {
        return getAggregatedSpeed(0);
    }

    public double getAggregatedSpeed(int prevStep) {
        return getAverageSpeed(prevStep, MOVING_U_AVG_WINDOW);
    }

    public double getAverageSpeed(int prevStep, int howManySteps) {
        return getAverageValue(prevStep, howManySteps, TrafficType.SPEED);
    }

    public double getIntervalAverageLaneFlow(SimulationGroup sg) {
        return getIntervalValue(sg, TrafficType.AVERAGEFLOW);
    }

    /**
     * get Average Average Lane Flow by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getIntervalAggregatedAverageLaneFlow(SimulationGroup sg, int prevStep, int howManySteps) {
        return getIntervalValue(sg, TrafficType.AVERAGEFLOW, prevStep, howManySteps);
    }

    /**
     * get Average Lane Flow
     *
     * @return
     */
    public double getAverageLaneFlow() {
        return this.simstation.getData(TrafficType.AVERAGEFLOW);
    }

    /**
     * get Average Lane Flow
     *
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getAverageLaneFlow(int prevStep, int howManySteps) {
        return getAverageValue(prevStep, howManySteps, TrafficType.AVERAGEFLOW);
    }

    public double getIntervalFlow(SimulationGroup sg) {
        return getIntervalValue(sg, TrafficType.FLOW);
    }

    /**
     * get Average Flow by Interval
     *
     * @param sg
     * @param runTime
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getIntervalAggregatedFlow(SimulationGroup sg, int prevStep, int howManySteps) {
        return getIntervalValue(sg, TrafficType.FLOW, prevStep, howManySteps);
    }

    /**
     * get Total Flow
     *
     * @return
     */
    public double getFlow() {
        return this.simstation.getData(TrafficType.FLOW);
    }

    /**
     * get Total Flow
     *
     * @param prevStep
     * @param howManySteps
     * @return
     */
    public double getTotalFlow(int prevStep, int howManySteps) {
        return getAverageValue(prevStep, howManySteps, TrafficType.FLOW);
    }

    public double getIntervalVolume(SimulationGroup sg) {
        return getIntervalValue(sg, TrafficType.VOLUME);
    }

    public double getVolume() {
        return this.simstation.getData(TrafficType.VOLUME);
    }

    public double getTotalVolume(int prevStep, int howManySteps) {
        return getAverageValue(prevStep, howManySteps, TrafficType.VOLUME);
    }

    /**
     * Fix me : move to VSLStationState Rolling Speed for VSL
     */
    /**
     * get AggregateRollingSpeed
     *
     * @return
     */
    public double getAggregateRollingSpeed(SimulationGroup sg) {
        if (isSpeedValid()) {
            int n_samples = this.calculateRollingSamples();
            if (n_samples > 0) {
                if (VSLConfig.INTERVAL_ADJUST) {
                    n_samples = n_samples * (30 / stateInterval.getIntervalByGID(sg));
                }
                return getAverageRollingSpeed(sg, 0, n_samples);
            } else {
                return this.getStation().getSpeedLimit();
            }
        } else {
            return -1;
        }
    }

    public double getAverageRollingSpeed(SimulationGroup sg, int prevstep, int howmanystep) {
        double rInterval = getSimulationRunningInterval();
        double sInterval = this.getStateInterval(sg);
        int runTime = simInterval.getCurrentRunTime();
        if ((runTime / sInterval) < 1) {
            int size = RollingSpeed.size();
            return RollingSpeed.get(size - 1);
        }

        double t = runTime % sInterval;
        double a = t / rInterval;
        double b = sInterval / rInterval;
        double pStep = prevstep * b;
        double howmany = (howmanystep * b) + pStep;

        double sum = 0;
        int cnt = 0;
        for (int i = (int) pStep; i < (int) howmany; i++) {
            int idx = RollingSpeed.size() - (int) (i + a) - 1;
            if (idx >= 0) {
                double q = this.RollingSpeed.get(idx);
                if (q > 0) {
                    sum += q;
                }
                cnt++;
            }
        }
//           if(cnt == 0)
//                   System.out.println(this.getStation().getStationId()+" : cnt=0");
        return cnt == 0 ? -1 : (sum / cnt);
    }

    /**
     * get AggregateRollingSpeed
     *
     * @return
     */
//   public double getAggregateRollingSpeed(){
//       if(isSpeedValid()){
//           int n_samples = this.calculateRollingSamples();
//           if(n_samples > 0){
//               return getAverageRollingSpeed(0,n_samples);
//           }else{
//               return this.getStation().getSpeedLimit();
//           }
//       }else{
//           return -1;
//       }
//   }
//   public double getAverageRollingSpeed(int prevStep, int howManySteps){
//       double sum = 0;
//       int validCount = 0;
//       for(int i=0; i<howManySteps; i++) {
//           int idx = RollingSpeed.size()-(prevStep+i)-1;
//           double q = this.RollingSpeed.get(idx);
//           if(q > 0) {
//               sum += q;
//           }
//           validCount++;
//       }
//       if(validCount == 0 || sum < 0) return 0;
//       return sum/validCount;   
//   }
    /**
     * Samples used in previous time step
     */
    protected int rolling_samples = 0;

    /**
     * Update the rolling samples for previous time step
     */
    protected void updateRollingSamples() {
        rolling_samples = calculateRollingSamples(1);
    }

    /**
     * Calculate the number of samples for rolling average
     */
    protected int calculateRollingSamples() {
        return calculateRollingSamples(0);
    }

    protected int calculateRollingSamples(int i) {
        return Math.min(calculateMaxSamples(), rolling_samples + i);
    }

    /**
     * Calculate the maximum number of samples for rolling average
     */
    protected int calculateMaxSamples() {
        if (isSpeedTrending()) {
            return 2;
        } else {
//           return DensityRank.samples(getDensity());
            return DensityRank.samples(this.getIntervalDensity(SimulationGroup.VSL));
        }
    }

    protected boolean isSpeedTrending() {
        return isSpeedValid()
                && (isSpeedTrendingDownward() || isSpeedTrendingUpward());
    }

    /**
     * Is recent rolling speed data valid?
     */
    protected boolean isSpeedValid() {
        double turn = getStateIntervalTick(SimulationGroup.VSL);
        if (RollingSpeed.size() < 3 * turn) {
            return false;
        }
        return getAverageRollingSpeed(SimulationGroup.VSL, 0, 1) >= 0
                && getAverageRollingSpeed(SimulationGroup.VSL, 1, 1) >= 0
                && getAverageRollingSpeed(SimulationGroup.VSL, 2, 1) >= 0;
    }

    /**
     * Is the speed trending downward?
     */
    protected boolean isSpeedTrendingDownward() {
        return getAverageRollingSpeed(SimulationGroup.VSL, 0, 1) < getAverageRollingSpeed(SimulationGroup.VSL, 1, 1)
                && getAverageRollingSpeed(SimulationGroup.VSL, 1, 1) < getAverageRollingSpeed(SimulationGroup.VSL, 2, 1);
//           return RollingSpeed.get(0) < RollingSpeed.get(1) &&
//                  RollingSpeed.get(1) < RollingSpeed.get(2);
    }

    /**
     * Is the speed trending upward?
     */
    protected boolean isSpeedTrendingUpward() {
        return getAverageRollingSpeed(SimulationGroup.VSL, 0, 1) > getAverageRollingSpeed(SimulationGroup.VSL, 1, 1)
                && getAverageRollingSpeed(SimulationGroup.VSL, 1, 1) > getAverageRollingSpeed(SimulationGroup.VSL, 2, 1);
//           return RollingSpeed.get(0) > RollingSpeed.get(1) &&
//                  RollingSpeed.get(1) > RollingSpeed.get(2);
    }

    private void updateRollingSpeed(double speed, SimulationGroup sg) {
        this.RollingSpeed.add(Math.min(speed, this.getStation().getSpeedLimit() + 10));

        double value = getStateIntervalTick(sg);
        if (RollingSpeed.size() > (DensityRank.getMaxSamples() * value)) {
            RollingSpeed.remove(0);
        }

    }

    /**
     * get Value according to State Interval
     *
     * @param sg SimulationGroup ex)VSL, METERING, DEFAULT
     * @param runTime SIMULATION Run Time
     * @param traffictype TrafficType
     * @return
     */
    private double getIntervalValue(SimulationGroup sg, TrafficType traffictype) {
        return getIntervalValue(sg, traffictype, 0, 1);
    }

    /**
     * get Value according to State Interval
     *
     * @param sg SimulationGroup ex)VSL, METERING, DEFAULT
     * @param runTime SIMULATION Run Time
     * @param traffictype TrafficType
     * @param prevstep previous step
     * @param howmanystep how many step
     * @return
     */
    private double getIntervalValue(SimulationGroup sg, TrafficType traffictype, int prevstep, int howmanystep) {
        double rInterval = getSimulationRunningInterval();
        double sInterval = this.getStateInterval(sg);
        int runTime = simInterval.getCurrentRunTime();
        if ((runTime / sInterval) < 1) {
            return simstation.getData(traffictype);
        }

        double t = runTime % sInterval;
        double a = t / rInterval;
        double b = sInterval / rInterval;
        double pStep = prevstep * b;
        double howmany = (howmanystep * b) + pStep;

        double sum = 0;
        int cnt = 0;
        for (int i = (int) pStep; i < (int) howmany; i++) {
            double f = simstation.getData(traffictype, (int) (i + a));
            if (f > -1) {
                sum += f;
            }
            cnt++;
        }
        if (traffictype.isVolume()) {
            return sum;
        } else {
            return cnt == 0 ? -1 : (sum / cnt);
        }
    }

    public void setUpstreamStationState(StationState compareStation) {
        upstreamStationState = compareStation;
        if (upstreamStationState != null) {
            distanceToUpstreamStation = getStation().getDistanceToUpstreamStation(section.getName());
        }
    }

    public void setDownStreamStationState(StationState compareStation) {
        downstreamStationState = compareStation;
        if (downstreamStationState != null) {
            distanceToDownstreamStation = getStation().getDistanceToDownstreamStation(section.getName());
        }
    }

    public StationState getUpstreamStationState() {
        return upstreamStationState;
    }

    public StationState getDownStreamStationState() {
        return downstreamStationState;
    }

    public double getdistanceToUpstreamStationState() {
        if (upstreamStationState == null) {
            return -1;
        }
        return distanceToUpstreamStation;
    }

    public double getdistanceToDownstreamStationState() {
        if (downstreamStationState == null) {
            return -1;
        }
        return distanceToDownstreamStation;
    }

    @Override
    public boolean equals(Object o) {
        StationState com = (StationState) o;
        if (this == null || com == null || com.getID() == null || this.getID() == null) {
            return false;
        }
        return com.getID().equals(this.getID());
    }

    public Double calculateAcceleration(SimulationGroup sg) {
        return calculateAcceleration(sg, this.getUpstreamStationState(), this.getdistanceToUpstreamStationState());
    }

    public Double calculateAcceleration(SimulationGroup sg, StationState upstream, Double d) {
        if (upstream == null) {
            return null;
        }
        double u = this.getAggregateRollingSpeed(sg);
        double usu = upstream.getAggregateRollingSpeed(sg);
        return calculateAcceleration(u, usu, d);
    }
    
    /**
     * @see distance changed mile -> km
     * @param u
     * @param usu
     * @param d
     * @return 
     */
    private Double calculateAcceleration(double u, double usu, Double d) {
        assert d > 0;
        if (u > 0 && usu > 0) {
            return (u * u - usu * usu) / (2 * d);
        } else {
            return null;
        }
    }

    private double getAverageValue(int prevStep, int howManySteps, TrafficType ttype) {
        double sum = 0;
        int validCount = 0;
        for (int i = 0; i < howManySteps; i++) {
            double k = simstation.getData(ttype, prevStep + i);
                        //debug
            //                double k = station.getData(dc, TrafficType.DENSITY, prevStep+i);

            if (k > 0) {
                sum += k;
            }
            validCount++;
        }
        if (validCount == 0 || sum < 0) {
            return 0;
        }

        if (ttype == TrafficType.VOLUME) {
            return sum;
        } else {
            return sum / validCount;
        }
    }

    /**
     * StateInterval / runningInterval
     *
     * @param sg
     * @return StateInterval / runningInterval
     */
    private double getStateIntervalTick(SimulationGroup sg) {
        double rInterval = getSimulationRunningInterval();
        double sInterval = this.getStateInterval(sg);

        return sInterval / rInterval;
    }

    private boolean isStateTurn(SimulationGroup sg) {
        double sInterval = this.getStateInterval(sg);
        int runTime = simInterval.getCurrentRunTime();
        if (runTime % sInterval == 0) {
            return true;
        } else {
            return false;
        }
    }

}
