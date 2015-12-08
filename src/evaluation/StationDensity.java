/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import infra.infraobject.RNode;

/**
 *
 * @author HanYoungTak
 */
public class StationDensity extends BasicData{

    public StationDensity(Period p, Section s, String op) {
        super(p, s, op);
    }

    @Override
    protected void init() {
        name = "density";
    }

    @Override
    protected void addStationDataSet(RNode r) {
        for(double d : r.getDensity()){
            data.add(d);
        }
    }
    
}
