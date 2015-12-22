/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khta;

import evaluation.EvaluationOption;
import evaluation.Interval;
import static infra.Section.getCacheFileName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.KHTAParam;
import util.PropertiesWrapper;

/**
 *
 * @author HanYoungTak
 */
public class KHTAOption {
    public static final String OPTION_SECTION_INDEX = "option.sectionIndex";
    public static final String OPTION_DURATION = "option.duration";
    public static final String OPTION_INTERVAL = "option.interval";
    public static final String OPTION_OUTPUT_PATH = "option.outputPath";
    public static final String OPTION_EXCEL_CHECK = "option.excelCheck";
    public static final String OPTION_CSV_CHECK = "option.csvCheck";
    public static final String OPTION_CONTOUR_CHECK = "option.contourCheck";
    
    private boolean isLoaded;
    private static int sectionIndex = 0;
    private static int duration = -1;
    
    private Interval interval;
    private String outputPath = "";
    private boolean excelCheck;
    private boolean csvCheck;
    private boolean contourCheck;
    
    private static String savePath = KHTAParam.CONFIG_DIR + File.separator;
    private static PropertiesWrapper prop;
    
    private EvaluationOption evaluationOption = new EvaluationOption();
    
    public KHTAOption(){
        prop = new PropertiesWrapper();
    }
    
    public void setOptions(int sectionIndex, int duration,
            Interval interval, String outputPath, 
            boolean excelCheck, boolean csvCheck, boolean contourCheck){
        this.sectionIndex = sectionIndex;
        this.duration = duration;
        this.interval = interval;
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
        File configChacheDir = new File(KHTAParam.CONFIG_DIR);
        if(!configChacheDir.mkdir() && !configChacheDir.exists()){
            JOptionPane.showMessageDialog(null, "Fail to create cache folder\n"+configChacheDir);
        }
        
        prop.put(savePath, OPTION_SECTION_INDEX);
        
        prop.put(filename, Boolean.TRUE);
        
        savePath += filename;
        prop.save(savePath, filename);
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
        
    public void setSectionIndex(int selectedIndex) {
        this.sectionIndex = selectedIndex;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }
    
    public void setInterval(Interval selectedInterval) {
        this.interval = selectedInterval;
    }
    
    public Interval getInterval() {
        return this.interval;
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
