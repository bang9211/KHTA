/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import infra.infraobject.RNode;
import khta.KHTAOption;

/**
 *
 * @author HanYoungTak
 */
public class StationAcceleration extends BasicData{

    public StationAcceleration(Period p, Section s, String op, KHTAOption opts) {
        super(p, s, op, opts);
    }

    @Override
    protected void init() {
        name = "acceleration";
    }

    @Override
    protected void addStationDataSet(RNode r) {
        
    }
}
