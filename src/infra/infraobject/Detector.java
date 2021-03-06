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
package infra.infraobject;

import evaluation.DataLoadOption;
import evaluation.Interval;
import infra.DetectorDataReader;
import infra.InfraConstants;
import infra.InfraDatas;
import infra.InfraObject;
import infra.Period;
import infra.simobjects.SimObjects;
import infra.type.*;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import exception.DBException;
import infra.simobjects.SimDetector;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Detector extends InfraObject {

    private String detector_id;
    private LaneType lanetype = LaneType.None;
    transient private Period period;
    transient private int MinInterval;
    private RNode r_node;
    private Station station = null;
    private double confidence = -1;

    transient private Vector<Double> volume = new Vector<Double>();
    transient private Vector<Double> speed = new Vector<Double>();
    transient private Vector<Double> flow = new Vector<Double>();
    transient private Vector<Double> density = new Vector<Double>();

    public Detector(HashMap<InfraDatas, Object> datas, LaneType ltype, RNode r_node) {
        super(datas);
        detector_id = this.getID();
        lanetype = ltype;
        setRNode(r_node);
    }

    /**
     * Data Load
     *
     * @param period
     */
    void loadData(Period p, DataLoadOption dopt) throws DBException {
        loadData(p, dopt, null);
    }

    public void loadData(Period p, DataLoadOption dopt, SimObjects sobj) throws DBException {
        this.clear();

        this.period = p;

        //for Simulation
        if (sobj != null && dopt != null && dopt.isSimulationMode()) {
            MinInterval = dopt.getSimulationInterval().getSecond();
            System.err.println("Simulation Interval : " + MinInterval);
            this.fillSimulationData(sobj);
            return;
        }
        
        MinInterval = Interval.getMinInterval(); //fix me

        if (p.interval < Interval.getMinInterval()) {
            JOptionPane.showMessageDialog(null, "Interval of real traffic data must be over the 30 sec");
        }
        if (p.interval % Interval.getMinTMCInterval() != 0) {
            JOptionPane.showMessageDialog(null, "Interval must be the multiples of 5Min");
        }

        DetectorDataReader ddr = new DetectorDataReader(this, period);
        ddr.load();
        adjustTrafficData(ddr.read(TrafficType.VOLUME), ddr.read(TrafficType.SPEED));
    }

    public void fillSimulationData(SimObjects sobj) {
        clear();
        SimDetector d;
        d = sobj.getDetector(getID());

        int samples = period.getTimeline().length * (period.interval / this.MinInterval);
//        System.err.println(this.id + ".fillSimulationData() : size=" + d.getSpeed().size() + ", times=" + samples);
        if (d.getSpeed().size() == 0) {
            Double[] dummy = new Double[samples];
            Arrays.fill(dummy, -1D);
            List dummyList = Arrays.asList(dummy);
            this.speed.addAll(dummyList);
            this.flow.addAll(dummyList);
            this.density.addAll(dummyList);
            this.volume.addAll(dummyList);
            return;
        }

//        for(double dd : d.getSpeed()){
//            System.out.print(dd+",");
//        }
//        System.out.println();
//        addData(speed, d.getSpeed(), samples);
//        addData(flow, d.getFlow(), samples);
//        addData(occupancy, d.getOccupancy(), samples);
//        addData(density, d.getDensity(), samples);
//        addData(volume, d.getVolume(), samples);
//        addData(scan, d.getScan(), samples);
        System.err.println(this.getID() + ", " + d.getSpeed().size() + ", " + d.getFlow().size() + ", " + d.getOccupancy().size()
                + ", " + d.getDensity().size() + ", " + d.getVolume().size());
        addData(speed, d.getSpeed());
        addData(flow, d.getFlow());
        addData(density, d.getDensity());
        addData(volume, d.getVolume());
    }

    private void addData(Vector<Double> dst, Vector<Double> src) {
//        System.out.println("Add data : size="+src.size()+", samples="+samples);
        for (int i = 0; i < src.size(); i++) {
            dst.add(src.get(i));
//            else
//                dst.add((double)0);
        }
    }

    /**
     * Make Data file from Volume and speed
     *
     * @param volume
     * @param speed
     */
    private void adjustTrafficData(double[] volume, double[] speed) {
        for (int i = 0; i < volume.length; i++) {
            double q = volume[i] * InfraConstants.SAMPLES_PER_HOUR;

            double u = speed[i];

            double k = q / u;

            if (q <= 0) {
                q = k = u = InfraConstants.MISSING_DATA;
            }

            this.volume.add(volume[i]);
            this.speed.add(u);
            this.flow.add(q);
            this.density.add(k);
        }
    }

    /**
     * Correction Data by Interval
     *
     * @param data
     * @param atype
     * @return
     */
    private Vector<Double> adjustInterval(Vector<Double> data, AdjustType atype) {
        if (data == null) {
            return null;
        }
        int interval = this.period.interval / MinInterval;
//        System.err.println(this.getID() + " - adjustInterval : "+interval + ", dsize : "+data.size());
        Vector<Double> aData = new Vector<Double>();

        for (int i = 0; i < data.size(); i += interval) {
            double sum = 0.0;
            double validCount = 0;
            int nextItv = (i + interval) > data.size() ? data.size() : (i + interval);
            for (int j = i; j < nextItv; j++) {
                double v = data.get(j);
                if (v > 0) {
                    sum += v;
                    validCount++;
                } else if (atype.IsDensityWithStation()) {
                    validCount++;
                }
            }

            /*
             * modify soobin Jeon 02/14/2012
             */
            if (validCount > 0) {
                if (atype.IsAverage() || atype.IsDensityWithStation()) {
                    aData.add(sum / validCount);
                } else if (atype.IsCumulative()) {
                    aData.add(sum);
                } else if (atype.IsFlow()) {
                    aData.add(sum / interval);
                }
            } else {
                aData.add(-1.0);
            }
        }
        return aData;
    }

    /*
     * modify soobin Jeon 02/13/2012
     */
    public double[] getDensity() {
        return toDoubleArray(adjustInterval(density, AdjustType.DensityWithStation));
    }

    /*
     * modify soobin Jeon 02/13/2012
     */
    public double[] getFlow() {
        return toDoubleArray(adjustInterval(flow, AdjustType.Flow));
    }

    /*
     * modify soobin Jeon 02/13/2012
     */
    public double[] getSpeed() {
        return toDoubleArray(adjustInterval(speed, AdjustType.Average));
    }

    /*
     * modify soobin Jeon 02/13/2012
     */
    public double[] getVolume() {
        return toDoubleArray(adjustInterval(volume, AdjustType.Cumulative));
    }

    /**
     * get Data by traffic Type
     *
     * @param trafficType
     * @return
     */
    public double[] getData(TrafficType trafficType) {
        return toDoubleArray(dispatchData(trafficType));
    }

    /**
     * get Data Vector by traffic type
     *
     * @param trafficType
     * @return
     */
    public Vector<Double> getDataVector(TrafficType trafficType) {
        return (Vector<Double>) dispatchData(trafficType).clone();
    }

    /*
     * modify soobin Jeon 02/13/2012
     */
    private Vector<Double> dispatchData(TrafficType trafficType) {
        if (trafficType.isSpeed()) {
            return adjustInterval(speed, AdjustType.Average);
        }
//        if (trafficType.isSppedForStation()){
//            return speed;//adjustInterval(speed,AdjustType.SpeedWithStation);
//        }
        if (trafficType.isDensity()) {
            return adjustInterval(density, AdjustType.DensityWithStation);
        }
        if (trafficType.isFlow() || trafficType.isAverageFlow()) { //modify soobin Jeon 02/15/2012
            return adjustInterval(flow, AdjustType.Flow);
        }
        if (trafficType.isVolume()) {
            return adjustInterval(volume, AdjustType.Cumulative);
        }
        return null;
    }

    public double getConfidence() {
        if (this.confidence < 0) {
            int validCount = 0;
            for (double u : this.speed) {
                if (u > 0) {
                    validCount++;
                }
            }
            this.confidence = (double) validCount / this.speed.size() * 100;
        }
        return this.roundUp(this.confidence, 1);
    }

    public boolean isMissing() {
        this.confidence = getConfidence();
        if (confidence < 50) {
//            System.out.println("confidence : " + confidence);
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        if (this.speed == null) {
            this.volume = new Vector<Double>();
            this.speed = new Vector<Double>();
            this.density = new Vector<Double>();
            this.flow = new Vector<Double>();
        }
        this.volume.clear();
        this.speed.clear();
        this.density.clear();
        this.flow.clear();
    }

    /**
     * set RNode
     *
     * @param rnode
     */
    private void setRNode(RNode rnode) {
        r_node = rnode;
        if (r_node.nodetype.isStation()) {
            station = (Station) r_node;
        }
    }

    /**
     * get LaneType
     *
     * @return
     */
    public LaneType getLaneType() {
        return lanetype;
    }

    /**
     * get RNode
     *
     * @return
     */
    public RNode getRNode() {
        return r_node;
    }

    public Station getStation() {
        return station;
    }

    /**
     * get Detector ID for Database
     *
     * @return
     */
    public String getIDforDB() {
        String _id = getID().split(this.lanetype.dbsuffix)[1];
        return _id == null ? getID() : _id;
    }
}
