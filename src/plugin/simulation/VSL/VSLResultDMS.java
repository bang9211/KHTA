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
//import edu.umn.natsrl.infra.infraobjects.DMSImpl;
//import java.util.ArrayList;
//
///**
// *
// * @author Soobin Jeon <j.soobin@gmail.com>
// */
//class VSLResultDMS extends VSLResultInfra{
//    ArrayList<Boolean> isStarted = new ArrayList<Boolean>();
//    ArrayList<Double> speedlimit = new ArrayList<Double>();
//    ArrayList<Double> actualSpeedLimit = new ArrayList<Double>();
//    ArrayList<Integer> SLOWTRAFFICAHEAD = new ArrayList<Integer>();
//    
//    VSLResultDMS(Double _milePoint, String _id) {
//        super(_id,_milePoint);
//        clear();
//    }
//
//    public void addData(DMSImpl d) {
//        addData(d.isStarted(),d.getSpeedLimit(),d.getActualSpeedLimit(),d.getSTA());
//    }
//    
//    public void addData(boolean isStart, double _speed, double _aspeed, int _sta){
//        isStarted.add(isStart);
//        speedlimit.add(_speed);
//        actualSpeedLimit.add(_aspeed);
//        SLOWTRAFFICAHEAD.add(_sta);
//    }
//    
//    void addAllDatas(double[] slimit, double[] started, double[] aslimit, int[] _sta) {
//        addAllDatas(slimit, speedlimit);
//        addAllDatas(convertDoubletoBooleanArray(started),isStarted);
//        addAllDatas(aslimit,actualSpeedLimit);
//        addAllDatas(_sta,SLOWTRAFFICAHEAD);
//    }
//    
//    private void clear() {
//        isStarted.clear();
//        speedlimit.clear();
//        actualSpeedLimit.clear();
//        SLOWTRAFFICAHEAD.clear();
//    }
//    
//    public double[] getSpeedLimit(){
//        return getArraytoDouble(speedlimit);
//    }
//    
//    public double[] getisStarted(){
//        return convertBooleantoDoubleArray(getArraytoBoolean(isStarted));
//    }
//    
//    public double[] getActualSpeedLimit(){
//        return getArraytoDouble(actualSpeedLimit);
//    }
//    
//    public int[] getSTA(){
//        return getArraytoInt(SLOWTRAFFICAHEAD);
//    }
//
//    
//}
