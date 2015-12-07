/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infra.infraobject;

import evaluation.DataLoadOption;
import infra.Period;
import infra.simobjects.SimObjects;

/**
 *
 * @author soobisooba
 */
public class RNodeThread extends Thread{
    private RNode rnode;
    private Period p;
    private DataLoadOption dopt = null;
    private SimObjects sobj = null;
    private boolean isLoaded = false;
    
    public static interface Callback{
        public void IsLoaded(RNode r);
    }
    
    private Callback mcallback;
    
    public RNodeThread(RNode r, Period period, DataLoadOption dopt, SimObjects sobj){
        rnode = r;
        p = period;
        this.dopt = dopt;
        this.sobj = sobj;
    }
    
    @Override
    public void run(){
        rnode.loadData(p, dopt,sobj);
        isLoaded = true;
        mcallback.IsLoaded(rnode);
    }
    
    public boolean isLoaded(){
        return isLoaded;
    }
    
    public RNode getRNode(){
        return rnode;
    }
    
    public void setCallback(Callback msg){
        mcallback = msg;
    }
}
