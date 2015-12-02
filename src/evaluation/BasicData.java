/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import infra.Period;
import infra.Section;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    protected abstract void setData();
    
    protected BasicData(Period p, Section s, String op){
        this.period = p;
        this.section = s;
        this.outputPath = op;
        init();
        process();
    }
    
    protected void setTimeLine(){
        timeLine.add("timeline");
        timeLine.add("");
        timeLine.add("Confidence");
        timeLine.add("Distance");
        //타임 넣기
        timeLine.addAll(Arrays.asList(period.getTimelineJustTime()));
        erData.add(timeLine);
    }
    
    @Override
    protected void process() {
        //EvaluationResult 형식에 맞추어 헤더 작성
        //0번째는 문자열 입력(colHeader)
        setTimeLine();
        setData();
        
        //speed를 EvaluationResult로 만들어 results에 넣어야 함
        EvaluationResult er = new EvaluationResult(erData);
        er.setName(period.getPeriodString());
        results.add(er);
        
        //엑셀 저장
        if(this.checkExcelDataLength()){
            try {
                //this.saveExcel(outputPath + "speed_" + section.getName() + period.getPeriodString());
                this.saveExcel(outputPath);
            } catch (Exception ex) {
                Logger.getLogger(StationSpeed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
