/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khta;

import evaluation.Interval;
import infra.Period;
import infra.Section;
import java.util.ArrayList;

/**
 *
 * @author HanYoungTak
 */
public class KHTAOption {
    
    private Section selectedSection;
    private ArrayList<Period>  periods;
    private Interval selectedInterval;
    private String outputPath;
    
    public KHTAOption(Section selectedSection, ArrayList<Period> periods,
            Interval selectedInterval, String outputPath){
        this.selectedSection = selectedSection;
        this.periods = periods;
        this.selectedInterval = selectedInterval;
        this.outputPath = outputPath;
    }
    
    public Section getSelectedSection(){
        return selectedSection;
    }
    
    public ArrayList<Period> getPeriods(){
        return periods;
    }
    
    public Interval getSeInterval(){
        return selectedInterval;
    }
    
    public String getOutputPath(){
        return outputPath;
    }
    
}
