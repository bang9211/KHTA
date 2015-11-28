/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package khta;

import evaluation.Interval;
import infra.Period;
import infra.Section;
import infra.infraobject.RNode;
import infra.infraobject.Station;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class khtaTest {
    public khtaTest(){
        
    }
    
    public void test(Section s){
        String stime = "20151001063000";
        String etime = "20151001073000";
        DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
        Date sdate = null;
        Date edate = null;
        try {
            sdate = formatter.parse(stime);
            edate = formatter.parse(etime);
        } catch (ParseException ex) {
            
        }
        if(sdate == null || edate == null)
            return;
        System.out.println("Start Date : "+sdate.toString());
        System.out.println("End Date : "+edate.toString());
        Period p = new Period(sdate, edate, Interval.I5MIN.getSecond());
        System.out.println("test sdate : "+p.getStartDateString());
        System.out.println("test edate : "+p.getEndDateString());
        
        s.loadData(p, null);
        
        for(RNode r : s.getRNodes()) {
            Station st = (Station) r;
            System.out.println(st.getName());
            
            int cnt = 0;
            System.out.println("Volume Data");
            for (double v : st.getVolume()) {
                System.out.println("[" + cnt + "] Volume : " + v);
                cnt++;
            }
            
            cnt = 0;
            System.out.println("Speed Data");
            for (double v : st.getSpeed()) {
                System.out.println("[" + cnt + "] Speed : " + v);
                cnt++;
            }
            
            cnt = 0;
            System.out.println("Flow Data");
            for (double v : st.getAverageLaneFlow()) {
                System.out.println("[" + cnt + "] alf : " + v);
                cnt++;
            }
            
            cnt = 0;
            System.out.println("Density Data");
            for (double v : st.getDensity()) {
                System.out.println("[" + cnt + "] den : " + v);
                cnt++;
            }

        }
    }
}
