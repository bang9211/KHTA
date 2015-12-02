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

/**
 *
 * @author HanYoungTak
 */
public class StationSpeed extends BasicData {

    public StationSpeed(Period p, Section s, String op) {
        super(p, s, op);
    }

    @Override
    protected void init() {
        name = "speed";
    }

    @Override
    protected void setData() {
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
    }
}
