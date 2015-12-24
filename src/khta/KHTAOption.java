/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khta;

import evaluation.EvaluationOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HanYoungTak
 */
public class KHTAOption implements Serializable{
    private boolean isLoaded;
    
    private int sectionIndex = 0;
    private int duration = 0;
    private int intervalIndex = 0;
    private String outputPath = "";
    private boolean excelCheck = false;
    private boolean csvCheck = false;
    private boolean contourCheck = false;
        
    private EvaluationOption evaluationOption = new EvaluationOption();
    
    public KHTAOption(){
    }
    
    public void setOptions(int sectionIndex, int duration,
            int intervalIndex, String outputPath, 
            boolean excelCheck, boolean csvCheck, boolean contourCheck){
        this.sectionIndex = sectionIndex;
        this.duration = duration;
        this.intervalIndex = intervalIndex;
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
    public static void save(KHTAOption opt, String path) {
        FileOutputStream fileOut = null;
        boolean isLoadedBackup = opt.isLoaded;
        try {            
            opt.isLoaded = false;
            fileOut = new FileOutputStream(path);
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
    public static KHTAOption load(String path) {
        File optFile = new File(path);
                
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
        
    public void setSectionIndex(int selectedIndex) {
        this.sectionIndex = selectedIndex;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }
    
    public void setIntervalIndex(int interval) {
        this.intervalIndex = interval;
    }
    
    public int getIntervalIndex() {
        return this.intervalIndex;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
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
        
}
