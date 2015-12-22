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
public class StationTotalFlow extends BasicData{

    public StationTotalFlow(Period p, Section s, String op, EvaluationOption opts) {
        super(p, s, op, opts);
    }

    @Override
    protected void init() {
        name = "totalFlow";
    }

    @Override
    protected void addStationDataSet(RNode r) {
        for(double d : r.getFlow()){
            data.add(d);
        }
    }
}
