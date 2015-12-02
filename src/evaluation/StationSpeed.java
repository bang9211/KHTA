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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HanYoungTak
 */
public class StationSpeed extends Evaluation {

    private Period period;
    private Section section;
    private String outputPath;

    public StationSpeed(Period p, Section s, String op) {
        name = "speed";
        this.period = p;
        this.section = s;
        this.outputPath = op;
        init();
        process();
    }

    @Override
    protected void init() {
        
    }

    @Override
    //make excel
    protected void process() {
        //EvaluationResult 형식에 맞추어 헤더 작성
        //0번째는 문자열 입력(colHeader)
        ArrayList< ArrayList > erData = new ArrayList();
        ArrayList timeLine = new ArrayList();
        ArrayList data;
        
        timeLine.add("timeline");
        timeLine.add("");
        timeLine.add("Confidence");
        timeLine.add("Distance");
        //타임 넣기
        for(String p : period.getTimelineJustTime()){
            timeLine.add(p);
        }
        
        erData.add(timeLine);
        //해당 데이터를 results에 저장
        for (RNode r : section.getRNodes()) {
            if(r.getNodeType() == RnodeType.STATION){
                Station s = (Station)r;
                //매 스테이션마다 데이터의 헤더를 추가해야함
                data = new ArrayList();
                data.add("");
                data.add(s.getName());
                data.add(100);
                data.add(s.getLocation());
                for(double speedData : r.getSpeed()){
                    data.add(speedData);
                }
                erData.add(data);
            }
        }
        
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
