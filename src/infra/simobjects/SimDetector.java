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
package infra.simobjects;

import infra.infraobject.Detector;
import infra.type.TrafficType;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimDetector extends SimObject{
    private String detectorId;
    private Detector detector;
    private Vector<Double> volume = new Vector<Double>();
    private Vector<Double> speed = new Vector<Double>();
    private Vector<Double> flow = new Vector<Double>();
    private Vector<Double> density = new Vector<Double>();
    private Vector<Double> occupancy = new Vector<Double>();
    
    //for AVG
    private int avgCnt = 0;
    private Date startTime;
    
    
    public SimDetector(Detector detector) {
        super(detector.getID());
        System.out.println("did : "+detector.getID());
        this.detector = detector;
        this.detectorId = detector.getID();
        this.type = SimDeviceType.DETECTOR;
        this.avgCnt = 0;
    }
    public Detector getDetector(){
        return detector;
    }
    
    public String getDetectorId() {
        return detectorId;
    }
    
    private int getAvg(){
        return avgCnt == 0 ? 1 : avgCnt;
    }
    
    public Vector<Double> getSpeed() {
        if(avgCnt == 0)
            return this.speed;
        else{
            return getAvgData(this.speed);
        }
    }

    public Vector<Double> getDensity() {        
        if(avgCnt == 0)
            return this.density;
        else{
            return getAvgData(this.density);
        }
    }

    public Vector<Double> getFlow() {
        if(avgCnt == 0)
            return this.flow;
        else{
            return getAvgData(this.flow);
        }
    }

    public Vector<Double> getVolume() {
        if(avgCnt == 0)
            return this.volume;
        else{
            return getAvgData(this.volume);
        }
    }
    
    public Vector<Double> getOccupancy() {
        if(avgCnt == 0)
            return this.occupancy;
        else{
            return getAvgData(this.occupancy);
        }
    }
    
    public double getData(TrafficType trafficType) {
        return getData(trafficType, 0);
    }
    
    /**
     * Returns data before given prevStep time step
     */    
    public double getData(TrafficType trafficType, int prevStep) {
        Vector<Double> data = dispatchData(trafficType);
        int idx = data.size()-prevStep-1;
        if(idx < 0 || idx > data.size()-1) return -1;
        return data.get(idx);
    }
    
    private Vector<Double> dispatchData(TrafficType trafficType) {
        if (trafficType.isSpeed()) {
            return this.getSpeed();
        }
        if (trafficType.isDensity()) {
            return this.getDensity();
        }
        if (trafficType.isFlow() || trafficType.isAverageFlow()) {
            return this.getFlow();
        }
        if (trafficType.isVolume()) {
            return this.getVolume();
        }
        if (trafficType.isOccupancy()) {
            return this.getOccupancy();
        }
        return null;
    }
    
    /**
     * Simulation Result에서 데이터를 가져올 때 사용
     * @param trafficType
     * @param values 
     */
    public void setTrafficData(TrafficType trafficType, double[] values) {
        Vector<Double> target = null;
        if(trafficType.isDensity()) target = this.density;
        else if(trafficType.isFlow()) target = this.flow;
        else if(trafficType.isSpeed()) target = this.speed;
        else if(trafficType.isVolume()) target = this.volume;
        else if(trafficType.isOccupancy()) target = this.occupancy;
        
        target.clear();
        for(double v : values) target.add(v);
    }
    
    public void addTrafficData(TrafficType trafficType, double[] values) {
        Vector<Double> target = null;
        if(trafficType.isDensity()) target = this.density;
        else if(trafficType.isFlow()) target = this.flow;
        else if(trafficType.isSpeed()) target = this.speed;
        else if(trafficType.isVolume()) target = this.volume;
        else if(trafficType.isOccupancy()) target = this.occupancy;
        
//        System.err.println(this.id+" - vcnt : "+values.length);
        int cnt = 0;
        for(double v : values){
            if((trafficType.isDensity()&&avgCnt==0) || (!trafficType.isDensity()&&avgCnt==1))
                target.add(v);
            else{
                double temp = target.get(cnt);
                target.set(cnt, v+temp);
                cnt ++;
            }
        }
        if(trafficType.isDensity())
            avgCnt ++;
    }
    
    /**
     * 시뮬레이션 과정에서 데이터를 넣을 때 쓰는 메소드
     * @param v
     * @param q
     * @param u
     * @param k
     * @param occupancy 
     */
    public void addData(double v, double q, double u, double k, double occupancy) {
        this.volume.add(v);
        this.flow.add(q);
        this.speed.add(u);
        this.density.add(k);
        this.occupancy.add(occupancy);
    }
    
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    private Vector<Double> getAvgData(Vector<Double> data) {
//        System.out.println("avgcnt : "+getAvg());
        Vector<Double> tmpv = new Vector<Double>();
        for(int i=0;i<data.size();i++){
            double temp = data.get(i);
            tmpv.add((double)Math.round(temp/getAvg()));
        }
//        System.err.println(this.id+ " : orgin-"+data.size()+", re-"+tmpv.size());
        return tmpv;
    }

    @Override
    public void reset() {
        volume.clear();
        speed.clear();
        flow.clear();
        density.clear();
        occupancy.clear();
        avgCnt = 0;
    }
    
}
