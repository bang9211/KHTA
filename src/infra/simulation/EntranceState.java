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

import infra.infraobject.*;
import infra.simobjects.*;
import infra.type.TrafficType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class EntranceState extends State {
   Entrance entrance;
   SimMeter meter;

   ArrayList<Double> cumulativeDemand = new ArrayList<Double>();
   ArrayList<Double> cumulativeMergingVolume = new ArrayList<Double>();
   ArrayList<Double> rateHistory = new ArrayList<Double>();        
   HashMap<Integer, Double> segmentDensityHistory = new HashMap<Integer, Double>(); // <dataCount, K>
   SimObjects simObjects;
   double lastDemand = 0;
   double lastVolumeOfRamp = 0;

   public EntranceState(Entrance e, SimObjects _simObject, SimInterval sitv) {
       super(e.getID(), e,sitv);
       this.entrance = e;
       type = StateType.ENTRANCE;
       simObjects = _simObject;
   }
   
   public EntranceState(Entrance e, SimObjects _simObject) {
           this(e,_simObject,null);
   }

   /**
    * Return output in this time interval
    */        
   public double getPassageVolume() {
       return this.lastVolumeOfRamp;
   }

   /**
    * Return output before 'prevStep' time step
    */        
   public double getMergingVolume(int prevStep) {
       int nIdx = cumulativeMergingVolume.size() - prevStep - 1;
       int pIdx = cumulativeMergingVolume.size() - prevStep - 2;
       //System.out.println(cumulativeFlow.size() + ", " + prevStep + ", " + pIdx + ", " + nIdx);
       return cumulativeMergingVolume.get(nIdx) - cumulativeMergingVolume.get(pIdx);
   }        

   /**
    * Return demand in this time interval
    */
   public double getQueueVolume() {
       return this.lastDemand;
   }

   public double getCumulativeDemand(){
       return this.cumulativeDemand.get(cumulativeDemand.size()-1);
   }
   public double getCumulativePassage(){
       return this.cumulativeMergingVolume.get(cumulativeMergingVolume.size()-1);
   }

   /**
    * Calculate demand and output
    */
   public void updateState() {
       if(this.meter == null) return;


       double p_volume = calculateRampVolume();
       double demand = calculateRampDemand();

       double prevCd = 0;
       double prevCq = 0;

       if(this.cumulativeDemand.size()>0) prevCd = this.cumulativeDemand.get(this.cumulativeDemand.size()-1);
       if(this.cumulativeMergingVolume.size()>0) prevCq = this.cumulativeMergingVolume.get(this.cumulativeMergingVolume.size()-1);

       this.cumulativeDemand.add(prevCd + demand);
       this.cumulativeMergingVolume.add(prevCq + p_volume);

       this.lastDemand = demand;                
       this.lastVolumeOfRamp = p_volume;
   }

   /**
    * Return ramp demand
    * @return 
    */
   private double calculateRampDemand() {

       if(this.meter == null) return 0;

       SimDetector[] qDets = this.meter.getQueue();           

       double demand = 0;
       double p_flow = calculateRampVolume();

       // queue detector is ok
       if(qDets != null) {
           for(int i=0; i<qDets.length; i++) {
               double d = (int)simObjects.getDetector(qDets[i].getID()).getData(TrafficType.VOLUME);
               if(d > 0) demand += d;
           }

           return demand;
       }

       return p_flow;                                    
   }

   /**
    * Return ramp flow now
    * @return ramp flow
    */
   private double calculateRampVolume() {
       return calculateRampVolume(0);
   }

   /**
    * Return ramp flow before given prevStep intervals
    * @param prevStep
    * @return ramp flow
    */
   private double calculateRampVolume(int prevStep) {
       if(this.meter == null) return 0;
       
       SimDetector[] pDets = this.meter.getPassage();
       
       double p_volume = 0;           
       
       if(pDets != null){
           for(int i=0;i<pDets.length;i++){
               double d = simObjects.getDetector(pDets[i].getID()).getData(TrafficType.VOLUME, prevStep);
               if(d > 0) p_volume += d;
           }
           
           return p_volume;
       }
       
       return 0;
   }

   public void saveSegmentDensityHistory(int dataCount, double Kt) {
       this.segmentDensityHistory.put(dataCount, Kt);
   }

   public Double getSegmentDensity(int dataCount)
   {
       return segmentDensityHistory.get(dataCount);            
   }

   public String getID(){
       if(hasMeter())
           return meter.getID();
       else
           return null;
   }

   public String getLabel(){
       if(hasMeter())
           return meter.getMeter().getName();
       else return null;
   }

   public boolean hasMeter(){
       if(meter != null)
           return true;
       else
           return false;
   }

   public double getCurrentRate(){
       if(meter != null)
           return 0;
       else
           return meter.currentRate;
   }

   public SimMeter getSimMeter(){
       return this.meter;
   }
}
