/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import infra.infraobject.RNode;
import infra.infraobject.Station;
import infra.type.RnodeType;
import java.util.ArrayList;
import java.util.Arrays;
import khta.KHTAOption;

/**
 *
 * @author HanYoungTak
 */
public abstract class BasicData extends Evaluation{

    protected Period period;
    protected Section section;
    protected String outputPath;
    protected ArrayList< ArrayList > erData = new ArrayList();
    protected ArrayList timeLine = new ArrayList();
    protected ArrayList data;
    
    protected abstract void addStationDataSet(RNode r);
    
    protected BasicData(Period p, Section s, String op, KHTAOption ko){
        this.period = p;
        this.section = s;
        this.outputPath = op;
        this.ko = ko;
        this.opts = ko.getEvaluationOption();
        init();
    }
    
    protected void setTimeLine(){
        timeLine.add("timeline");
        timeLine.add("Confidence");
        timeLine.add("Distance");
        //타임 넣기
        timeLine.addAll(Arrays.asList(period.getTimelineJustTime()));
        erData.add(timeLine);
    }
    
    @Override
    public EvaluationResult process() {
        //EvaluationResult 형식에 맞추어 헤더 작성
        setTimeLine();
        double distance = 0;
        //해당 데이터를 results에 저장
        for (RNode r : section.getRNodes()) {
            if(r.getNodeType() == RnodeType.STATION){
                Station s = (Station)r;
                //매 스테이션마다 데이터의 헤더를 추가해야함
                data = new ArrayList();
                data.add(s.getName() + " - " + s.getID());
                data.add(s.getConfidence());
                data.add(distance);
                addStationDataSet(r);
                erData.add(data);
                distance += EvalHelper.getDistance(section.getName(), s);
            }
        }
        
        //speed를 EvaluationResult로 만들어 results에 넣어야 함
        EvaluationResult er = new EvaluationResult(erData);
        er.setName(period.getPeriodString());
        
        
        if(opts.getIMSD())
             fixMissingStation(er);
        else if(opts.getI0MSD())
            fixZeroMissingStation(er);

        results.add(er);
 
            this.addConfidenceToResult(er);
            this.addDistanceToResult(er);
//            this.printEvaluation(res);
            this.addVirtualStationToResult(er);
        
        // add average to results
        makeAverage();
        
        hasResult = true;
        
        return er;
    }
}
