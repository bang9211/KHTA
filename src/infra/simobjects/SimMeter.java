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

import infra.infraobject.RampMeter;
import java.util.ArrayList;
import vissimcom.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SimMeter extends SimObject {
    RampMeter meter;
    private boolean isRedtimeUpdated = false;
    private float nextRedTime = 0;
    private SimObjects simObjects;
    private MeterLight currentLight;
    int turn = 0;   // 0: left, 1: right
    String[] meterId;

    public enum MeterType {

        SINGLE, DUAL;

        private boolean isSingle() {
            return this == SINGLE;
        }

        private boolean isDual() {
            return this == DUAL;
        }
    };

    public enum MeterSide {

        LEFT, RIGHT
    };
    public final float GREEN_YELLOW_TIME = 2.0f;
    
    // single or double (2 meters in one ramp)
    private MeterType meterType;
    
    // is it in both VISSIM case file and IRIS ?
    // if yes, meter is enabled
    private boolean enabled = true;
    
    // rampmeter state values in real (or in IRIS)
    public byte status = SimConfig.METER_STATUS_FLASH;
    public byte currentRate = SimConfig.METER_RATE_FLASH;
    public byte greenCount30Sec = 0;
    public byte remoteRate = SimConfig.METER_RATE_FLASH;
    public byte policePanel = 0;
    public byte greenCount5Min = 0;
    
    // variables to change status
    private float currentTime = 0;
    private float redUpdatedTime = 0;
    private float redTime = 0;
    
    // red or green (3 or 2)
//    private MeterLight currentLeftLight = MeterLight.GREEN;
//    private MeterLight currentRightLight = MeterLight.GREEN;
    
    // it is updated when sending meter status to IRIS
    private int readStatusCount = 0;

    public SimMeter(RampMeter meter, SimObjects sobj) {
        super(meter.getID());
        this.meter = meter;
        this.type = SimDeviceType.METER;
        simObjects = sobj;
    }

    public MeterLight getMeterStatus() {
        return null;
    }

    public void updateLamp(VISSIMController updater) {
        try {
            // if not metering, set green
            if (this.status != SimConfig.METER_STATUS_CENTRAL) {
                // set lamp green
                if(currentLight == null || !currentLight.isGreen()){
                    for(String mid : this.meterId) {
                        boolean check = updater.setMeterStatus(mid, MeterLight.GREEN);
                        if(check){
                            currentLight = MeterLight.GREEN;
                        }
                    }
                }
                return;
            }

            updateMeterLight(updater);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateMeterLight(VISSIMController updater) {
        
        // to eliminate decimal point error
        // assume : time unit is "milisecond"
        int cycle = (int)((this.GREEN_YELLOW_TIME + redTime) * 10);
        int uTime = (int)(this.redUpdatedTime * 10);
        int cTime = (int)(this.currentTime * 10);                
        int cursor = (cTime - uTime) % cycle;
        
        // do not update in this case
        if (this.redTime == 0 && !isRedtimeUpdated) return;

        // set initial rate
        if (isRedtimeUpdated && redTime == 0) {
            turn = 0;
            this.redTime = this.nextRedTime;
            this.isRedtimeUpdated = false;
            this.redUpdatedTime = this.currentTime;
            updater.setMeterStatus(meterId[0], MeterLight.RED);
            if(meterType.isDual()) updater.setMeterStatus(meterId[1], MeterLight.RED);            
            this.currentLight = MeterLight.RED;
            System.out.println("  - " + this.getID() + "'s red time is set ("+meterType+") : " + redTime);
            return;
        }
        
        // switch meter to control
        if(cursor == 0) {
            // update rate
            if (isRedtimeUpdated) {
                System.out.println("  - " + this.getID() + "'s red time is updated("+meterType+") : " + redTime + " -> " + nextRedTime);
                this.redTime = this.nextRedTime;
                this.isRedtimeUpdated = false;
                this.redUpdatedTime = this.currentTime;
            }
            if(meterType.isDual()) {
                updater.setMeterStatus(meterId[turn], MeterLight.RED);    
                turn = (turn + 1) % 2;
                updater.setMeterStatus(meterId[turn], MeterLight.GREEN);    
                increaseCount();
                this.currentLight = MeterLight.GREEN;
                return;
            } else if(meterType.isSingle()) {
                updater.setMeterStatus(meterId[0], MeterLight.GREEN);
                increaseCount();
                this.currentLight = MeterLight.GREEN;
                return;
            }
        }   

        // set light       
        MeterLight light = MeterLight.RED;
        if (cursor < this.GREEN_YELLOW_TIME*10) {
            light = MeterLight.GREEN;
            increaseCount();
        }
        
        // apply light
        if(light != currentLight) 
            updater.setMeterStatus(meterId[turn], light);
        
        // save current light
        this.currentLight = light;
    }

    private void increaseCount() {
        greenCount30Sec++;
        greenCount5Min++;
    }

    /**
     * update green count value for 30 sec and 5 min
     */
    public void updateStatusCount() {
        this.greenCount30Sec = 0;
        this.readStatusCount++;

        // 10 = 5min / 30s (to check 5min)
        if (readStatusCount > 10) {
            this.readStatusCount = 1;
            this.greenCount5Min = 0;
        }

    }

    /**
     * set rate from IRIS
     * @param rate (FLASH_RATE=0, CENTRAL_RATE=1, TOD_RATE=2, FORCED_FLASH=7)
     */
    public void setRate(byte rate) {
        this.remoteRate = rate;
        this.currentRate = rate;
            
        // update ramp status according to rate
        if (rate == SimConfig.METER_RATE_CENTRAL) {
            this.status = SimConfig.METER_STATUS_CENTRAL;
        } else {
            this.status = SimConfig.METER_STATUS_FLASH;
            this.redTime = 0;
        }
    }

    /**
     * set red time of meter from IRIS
     * @param redTime red time (second)
     */
    public void setRedTime(float redTime) {
        this.isRedtimeUpdated = (redTime != this.redTime);
        this.nextRedTime = redTime;
        
        // in MnDot protocol, according to network or IRIS status
        // sometimes set-red-time message arrived faster than set-meter-rate
        if(this.status != SimConfig.METER_STATUS_CENTRAL) {
            setRate(SimConfig.METER_RATE_CENTRAL);
        }
    }
    
    /**
     * set current simulation time to meter
     * @param time
     */
    public void setCurrentSimTime(float time) {
        this.currentTime = time;
    }
    ArrayList<MeterState> lights = new ArrayList<MeterState>();

    /**
     * set meter type
     * @param meterType single? or pair?
     */
    public void setMeterType(MeterType meterType) {
        this.meterType = meterType;
        if(meterType.isDual()) {
            meterId = new String[]{this.getID() + "_L", this.getID() + "_R"};
        } else {
            meterId = new String[]{this.getID()};
        }
    }
    

    /**
     * get meter type
     * @return meterType single or pair
     */
    public MeterType getMeterType() {
        return this.meterType;       
    }

    public RampMeter getMeter() {
        return meter;
    }
    
    public String[] getMeterID(){
        return meterId;
    }

    /**
     * set enable status
     * @param v enabled? or not?
     */
    public void setEnabled(boolean v) {
        this.enabled = v;
        if (!v) {
            this.status = 0;
            this.currentRate = 0;
            this.remoteRate = 0;
        }
    }

    /**
     * is this meter enabled?
     * @return true or false
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @see 
     * @return 
     */
    public SimDetector getGreen() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return simObjects.getDetector(meter.getGreen());
    }

    /**
     * @see 
     * @return 
     */
    public SimDetector getPassage() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return simObjects.getDetector(meter.getPassage());
    }

    /**
     * @see 
     * @return 
     */
    public SimDetector getMerge() {
        throw new UnsupportedOperationException("Not supported yet.");        
        //return simObjects.getDetector(meter.getMerge());
    }

    /**
     * @see 
     * @return 
     */
    public SimDetector getByPass() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return simObjects.getDetector(meter.getByPass());
    }

    public SimDetector[] getQueue() {
        throw new UnsupportedOperationException("Not supported yet.");
//        String[] queues = meter.getQueue();
//        if (queues == null || queues.length == 0) {
//            return null;
//        }
//        SimDetector[] dets = new SimDetector[queues.length];
//        for (int i = 0; i < queues.length; i++) {
//            dets[i] = simObjects.getDetector(queues[i]);
//        }
//        return dets;
    }

    public float getRedTime() {
        return redTime;
    }

    public float getReleaseRate() {
        if (this.redTime == 0) {
            return -1f;
        }
        return 3600 / (this.redTime + this.GREEN_YELLOW_TIME);
    }

    /**
     *
     * @param num
     * @return rounded number (x.x)
     */
    public float round1(float num) {
        float result = num * 10;
        result = Math.round(result);
        result = result / 10;
        return result;
    }

    @Override
    public void reset() {
        status = SimConfig.METER_STATUS_FLASH;
        currentRate = SimConfig.METER_RATE_FLASH;
        greenCount30Sec = 0;
        remoteRate = SimConfig.METER_RATE_FLASH;
        policePanel = 0;
        greenCount5Min = 0;
        currentTime = 0;
        redUpdatedTime = 0;
        redTime = 0;
        this.currentLight = MeterLight.GREEN;
        readStatusCount = 0;
        isRedtimeUpdated = false;
        nextRedTime = 0;
    }

    class MeterState {

        String id;
        MeterSide meterSide;
        MeterLight meterLight;

        public MeterState(String id) {
            this.id = id;
        }
    }
}
