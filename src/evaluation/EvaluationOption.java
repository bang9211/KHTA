/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

/**
 *
 * @author HanYoungTak
 */
public class EvaluationOption {
    private boolean speedCheck;
    private boolean densityCheck;
    private boolean totalFlowCheck;
    private boolean averageLaneFlowCheck;
    private boolean accelerationCheck;
    
    private boolean vmt;        //Vehicle Miles Traveled
    private boolean lvmt;       //Lost VMT for congestion
    private boolean vht;        //Vehicle Hour Traveled
    private boolean dvh;        //Delayed Vehicle Hours
    private boolean flowRates;  //Mainlane and Ramp Flow Rates
    private boolean tt;         //Travel Time
    private boolean sv;         //Speed Variations
    private boolean cm;         //Congested Miles
    
    private double cts;         //Congestion Threshold Speed(km)
    private double cd;          //Critical Density(veh/km)
    private double dlc;         //Default Lane Capacity(veh/hr)
        
    public EvaluationOption(){
        
    }
    
    public void setStationData(boolean speedCheck, boolean densityCheck, 
            boolean totalFlowCheck, boolean averageLaneFlowCheck, boolean accelerationCheck){
        this.speedCheck = speedCheck;
        this.densityCheck = densityCheck;
        this.totalFlowCheck = totalFlowCheck;
        this.averageLaneFlowCheck = averageLaneFlowCheck;
        this.accelerationCheck = accelerationCheck;
    }
    
    public void setTrafficFlowMeasurements(boolean vmt, boolean lvmt, boolean vht,
            boolean dvh, boolean flowRates, boolean tt, boolean sv, boolean cm, 
            double cts, double cd, double dlc){
        this.vmt = vmt;
        this.lvmt = lvmt;
        this.vht = vht;
        this.dvh = dvh;
        this.flowRates = flowRates;
        this.tt = tt;
        this.sv = sv;
        this.cm = cm;
        this.cts = cts;
        this.cd = cd;
        this.dlc = dlc;
    }
    
    public boolean isSelectedAnything(){
        if((speedCheck == true) ||
                (densityCheck == true) ||
                (totalFlowCheck == true) ||
                (averageLaneFlowCheck == true) ||
                (accelerationCheck == true) ||
                (vmt == true) ||
                (lvmt == true) ||
                (vht == true) ||
                (dvh == true) ||
                (flowRates == true) ||
                (tt == true) ||
                (sv == true) ||
                (cm == true)){
            return true;
        }
        else
            return false;
    }
    
    public boolean getSpeedCheck(){
        return speedCheck;
    }
    
    public boolean getDensityCheck(){
        return densityCheck;
    }
    
    public boolean getTotalFlowCheck(){
        return totalFlowCheck;
    }
    
    public boolean getAverageLaneFlowCheck(){
        return averageLaneFlowCheck;
    }
    
    public boolean getAccelerationCheck(){
        return accelerationCheck;
    }
    
    public boolean getVMT(){
        return speedCheck;
    }
    
    public boolean getLVMT(){
        return speedCheck;
    }
    
    public boolean getVHT(){
        return speedCheck;
    }
    
    public boolean getDVH(){
        return speedCheck;
    }
    
    public boolean getFlowRates(){
        return speedCheck;
    }
    
    public boolean getTT(){
        return speedCheck;
    }
    
    public boolean getSV(){
        return speedCheck;
    }
    
    public boolean getCM(){
        return speedCheck;
    }
    
    public boolean getCTS(){
        return speedCheck;
    }
    
    public boolean getCD(){
        return speedCheck;
    }
    
    public boolean getDLC(){
        return speedCheck;
    }
}
