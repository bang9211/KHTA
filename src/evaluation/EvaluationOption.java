/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author HanYoungTak
 */
public class EvaluationOption implements Serializable{
    
    private Section selectedSection;
    /** selected period, it may be multiple period because user can select multiple dates */
    private ArrayList<Period> periods = new ArrayList<Period>();
    
    private boolean speedCheck;
    private boolean densityCheck;
    private boolean totalFlowCheck;
    private boolean averageLaneFlowCheck;
    private boolean accelerationCheck;
    
    private boolean vmtCheck;        //Vehicle Miles Traveled
    private boolean lvmtCheck;       //Lost VMT for congestion
    private boolean vhtCheck;        //Vehicle Hour Traveled
    private boolean dvhCheck;        //Delayed Vehicle Hours
    private boolean flowRatesCheck;  //Mainlane and Ramp Flow Rates
    private boolean ttCheck;         //Travel Time
    private boolean svCheck;         //Speed Variations
    private boolean cmCheck;         //Congested Miles
    
    private double cts = 0;         //Congestion Threshold Speed(km)
    private double cd = 0;          //Critical Density(veh/km)
    private double dlc = 0;         //Default Lane Capacity(veh/hr)
    
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
    
    public void setTrafficFlowMeasurements(boolean vmtCheck, boolean lvmtCheck, boolean vhtCheck,
            boolean dvhCheck, boolean flowRatesCheck, boolean ttCheck, boolean svCheck, boolean cmCheck, 
            double ctsCheck, double cdCheck, double dlcCheck){
        this.vmtCheck = vmtCheck;
        this.lvmtCheck = lvmtCheck;
        this.vhtCheck = vhtCheck;
        this.dvhCheck = dvhCheck;
        this.flowRatesCheck = flowRatesCheck;
        this.ttCheck = ttCheck;
        this.svCheck = svCheck;
        this.cmCheck = cmCheck;
        this.cts = ctsCheck;
        this.cd = cdCheck;
        this.dlc = dlcCheck;
    }
    
    public boolean isSelectedAnything(){
        if((speedCheck == true) ||
                (densityCheck == true) ||
                (totalFlowCheck == true) ||
                (averageLaneFlowCheck == true) ||
                (accelerationCheck == true) ||
                (vmtCheck == true) ||
                (lvmtCheck == true) ||
                (vhtCheck == true) ||
                (dvhCheck == true) ||
                (flowRatesCheck == true) ||
                (ttCheck == true) ||
                (svCheck == true) ||
                (cmCheck == true)){
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
        return vmtCheck;
    }
    
    public boolean getLVMT(){
        return lvmtCheck;
    }
    
    public boolean getVHT(){
        return vhtCheck;
    }
    
    public boolean getDVH(){
        return dvhCheck;
    }
    
    public boolean getFlowRates(){
        return flowRatesCheck;
    }
    
    public boolean getTT(){
        return ttCheck;
    }
    
    public boolean getSV(){
        return svCheck;
    }
    
    public boolean getCM(){
        return cmCheck;
    }
    
    public double getCTS(){
        return cts;
    }
    
    public double getCD(){
        return cd;
    }
    
    public double getDLC(){
        return dlc;
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
