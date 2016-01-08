///*
// * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
// * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package plugin.simulation.VSL;
//
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLSTAImpl;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLSTAOption1;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLSTAOption2;
//import edu.umn.natsrl.ticas.plugin.simulation.VSL.algorithm.VSLStationState;
//import infra.Section;
//import java.util.TreeMap;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//public enum VSLSTAType {
//    OPTION1(0,"option1"),
//    OPTION2(1,"option2");
//    
//    int src;
//    String name;
//    VSLSTAType(int _src, String _name){
//        src = _src;
//        name = _name;
//    }
//    
//    public VSLSTAImpl getVSLType(Section s, TreeMap<Double, VSLStationState> smap){
//        if(isOption1()){
//            return new VSLSTAOption1(s);
//        }else if(isOption2()){
//            return new VSLSTAOption2(s,smap);
//        }else
//            return new VSLSTAOption1(s);
//    }
//    
//    public boolean isOption1(){return this==OPTION1;}
//    public boolean isOption2(){return this==OPTION2;}
//}
