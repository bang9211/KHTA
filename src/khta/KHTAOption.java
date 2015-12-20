/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khta;

import evaluation.EvaluationOption;
import evaluation.Interval;
import infra.Period;
import infra.Section;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HanYoungTak
 */
public class KHTAOption {
    
    private boolean isLoaded;
    private int selectedSectionIndex = 0;
    private int duration = -1;
    private long timestamp = 0;
    
    private Interval selectedInterval;
    private String outputPath;
    private boolean excelCheck;
    private boolean csvCheck;
    private boolean contourCheck;
    
    private EvaluationOption evaluationOption = new EvaluationOption();
    
    public KHTAOption(){
        this.timestamp = new Date().getTime();
    }
    
    public void setOptions(int selectedSectionIndex, int duration,
            Interval selectedInterval, String outputPath, 
            boolean excelCheck, boolean csvCheck, boolean contourCheck){
        this.selectedSectionIndex = selectedSectionIndex;
        this.duration = duration;
        this.selectedInterval = selectedInterval;
        this.outputPath = outputPath;
        this.excelCheck = excelCheck;
        this.csvCheck = csvCheck;
        this.contourCheck = contourCheck;
    }
    
    
    /**
     * Saves option to file
     * @param opt
     * @param filename 
     */
    public static void save(KHTAOption opt, String filename) {
        FileOutputStream fileOut = null;
        boolean isLoadedBackup = opt.isLoaded;
        try {            
            opt.isLoaded = false;
            fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(opt);
            out.close();
            fileOut.close();
        } catch (Exception ex) {
            opt.isLoaded = isLoadedBackup;
        } finally {
            try {                
                fileOut.close();
            } catch (IOException ex) {
                Logger.getLogger(KHTAOption.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Load option from file
     * @param filename
     * @return 
     */
    public static KHTAOption load(String filename) {
        File optFile = new File(filename);
        if(!optFile.exists()) 
        {
            System.err.println("Option file does not be found");
            return new KHTAOption();
        }
        
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(optFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            KHTAOption khtaOption = (KHTAOption) in.readObject();
            EvaluationOption opt = khtaOption.getEvaluationOption();
            khtaOption.isLoaded = true;
            in.close();
            fileIn.close();
            return khtaOption;
        } catch (Exception ex) {
            ex.printStackTrace();
            optFile.delete();
        } finally {
            try {
                fileIn.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new KHTAOption();        
    }
        
    public void setSelectedSectionIndex(int selectedIndex) {
        this.selectedSectionIndex = selectedIndex;
    }

    public int getSeletedSectionIndex() {
        return selectedSectionIndex;
    }
    
    public void setSelectedInterval(Interval selectedInterval) {
        this.selectedInterval = selectedInterval;
    }
    
    public Interval getSelectedInterval() {
        return this.selectedInterval;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }
    
    public Interval getSeInterval(){
        return selectedInterval;
    }
    
    public String getOutputPath(){
        return outputPath;
    }
    
    boolean isLoaded() {
        return this.isLoaded;
    }
    
    public boolean getExcelCheck(){
        return excelCheck;
    }
    
    public boolean getCSVCheck(){
        return csvCheck;
    }
    
    public boolean getContourCheck(){
        return contourCheck;
    }
    
    public EvaluationOption getEvaluationOption()
    {        
        return this.evaluationOption;
    }
        
    public long getTimestamp() {
        return timestamp;
    }
}
