/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
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
package vissimcom;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum VISSIMDetector {
    DETECTOR(1,"Detector"),
    DATACOLLECTOR(2, "DATA COLLECTOR"),
    NULL(3, "NULL");
    
    int srl;
    String str;
    
    VISSIMDetector(int k, String str){
        srl = k;
        this.str = str;
    }
    
    @Override
    public String toString(){
        return str;
    }
    
    public boolean isDETECTOR(){return this==DETECTOR;}
    public boolean isDATACOLLECTOR(){return this==DATACOLLECTOR;}
    public boolean isNULL(){return this==NULL;}
}
