/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infra.infraobject;

import evaluation.DataLoadOption;
import exception.DBException;
import infra.Period;
import infra.simobjects.SimObjects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobisooba
 */
public class DetectorThread extends Thread{
    private Detector detector = null;
    private Period p;
    private DataLoadOption dopt = null;
    private SimObjects sobj = null;
    
    private boolean isLoaded = false;
    public DetectorThread(Detector d, Period period, DataLoadOption dopt, SimObjects sobj){
        detector = d;
        p = period;
        this.dopt = dopt;
        this.sobj = sobj;
    }
    
    @Override
    public void run(){
        try {
            if (sobj == null) {
                detector.loadData(p, dopt);
            } else {
                detector.loadData(p, dopt, sobj);
            }
            isLoaded = true;
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isLoaded(){
        return isLoaded;
    }
}
