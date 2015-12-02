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
public class StationAverageLaneFlow extends Evaluation{
    private Period period;
    private Section section;
    private String outputPath;

    public StationAverageLaneFlow(Period p, Section s, String op) {
        this.period = p;
        this.section = s;
        this.outputPath = op;
    }

    @Override
    protected void init() {

    }

    @Override
    //make excel
    protected void process() {
        for (RNode r : section.getRNodes()) {
            //evaluationResult

        }
    }
}
