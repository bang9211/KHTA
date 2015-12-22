/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author HanYoungTak
 */
public class EvaluationOption {
    
    private Section selectedSection;
    /** selected period, it may be multiple period because user can select multiple dates */
    private ArrayList<Period> periods = new ArrayList<Period>();
    
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
    
    private boolean ocaeCheck;
    private boolean imsdCheck;
    private boolean i0msdCheck;
        
    /** checked options */
//    private HashMap<String, OptionType> options = new HashMap<String, OptionType>();
    
    private transient HashMap<ContourType, ContourPanel> contourPanels = new HashMap<ContourType, ContourPanel>();    
    private HashMap<ContourType, ContourSetting> contourSettings = new HashMap<ContourType, ContourSetting>();    
    
    
    public EvaluationOption(){
        
    }
    
    public void setSelectedSection(Section selectedSection){
        this.selectedSection = selectedSection;
    }
    
    public Section getSelectedSection(){
        return this.selectedSection;
    }
    
    public void setPeriods(ArrayList<Period> periods){
        this.periods = periods;
    }
    
    public ArrayList<Period> getPeriods(){
        return this.periods;
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
    
    public void setOutputOption(boolean ocaeCheck, boolean imsdCheck, boolean i0msdCheck){
        this.ocaeCheck = ocaeCheck;
        this.imsdCheck = imsdCheck;
        this.i0msdCheck = i0msdCheck;
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
    
    public boolean getOCAE(){
        return ocaeCheck;
    }
    
    public boolean getIMSD(){
        return imsdCheck;
    }
    
    public boolean getI0MSD(){
        return i0msdCheck;
    }
    
    public void addContourPanel(ContourType cType, ContourPanel panel)
    {
        this.contourPanels.put(cType, panel);
        this.contourSettings.put(cType, panel.getContourSetting());
    }
    
    public ContourPanel getContourPanel(ContourType cType)
    {
        return this.contourPanels.get(cType);
    }
    
    public ContourSetting getContourSetting(ContourType cType)
    {
        return this.contourSettings.get(cType);
    }    
    
    /**
     * Check if it has option
     * @param optionType
     * @return true if it has given option, else false
     */
//    public boolean hasOption(OptionType optionType) {
//        return this.options.containsKey(optionType.name());
//    }  
}
