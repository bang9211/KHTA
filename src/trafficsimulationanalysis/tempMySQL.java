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
package trafficsimulationanalysis;

import infra.Direction;
import infra.InfraDatas;
import infra.type.RoadType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class tempMySQL {

    private int seed;
    private ArrayList<HashMap<InfraDatas, Object>> Corridors = new ArrayList();
    private ArrayList<HashMap<InfraDatas, Object>> stations = new ArrayList();
    Random randomGenerator = new Random();
    private int MAX_COR = 20;
    private int MAX_STATION = 4000;

    public tempMySQL(int _seed) {
        seed = _seed;
        randomGenerator.setSeed(seed);
    }

    public void setup() {
        System.out.println("Setting up the Corridors");
        setCorridor();
        System.out.println("Corridor Count : " + Corridors.size());
        setStation();
        System.out.println("Station Count : " + stations.size());
    }

    public ArrayList<HashMap<InfraDatas, Object>> getCorridors() {
        return Corridors;
    }

    public ArrayList<HashMap<InfraDatas, Object>> getStations(String Corid) {
        ArrayList<HashMap<InfraDatas, Object>> selStations = new ArrayList();
        for (HashMap<InfraDatas, Object> ss : stations) {
            String scid = (String) ss.get(InfraDatas.COR_ID);
            if (scid.equals(Corid)) {
                selStations.add(ss);
            }
        }
        return selStations;
    }

    private void setStation() {
        //int 
        int slength = 1000;
        for (HashMap<InfraDatas, Object> cor : Corridors) {
            String corid = (String) cor.get(InfraDatas.ID);
            Direction dr = (Direction) cor.get(InfraDatas.DIRECTION);
            int scnt = randomGenerator.nextInt(30);
            scnt = scnt < 10 ? 10 : scnt;
            int cnt = 0;
            while (cnt++ < scnt) {
                HashMap<InfraDatas, Object> station = new HashMap();
                int id = cnt - 1;
                String nid = String.valueOf(id);
                String name = "Name_" + nid + "_cor_" + corid;
                double eloc = cnt * slength;
                double loc = eloc - (slength / 2);
                double sloc = eloc - slength;

                station.put(InfraDatas.ID, nid);
                station.put(InfraDatas.NAME, name);
                station.put(InfraDatas.COR_ID, corid);
                station.put(InfraDatas.LOCATION, loc);
                station.put(InfraDatas.START_LOC, sloc);
                station.put(InfraDatas.END_LOC, eloc);
                station.put(InfraDatas.LENGTH, slength);
                station.put(InfraDatas.ORDER, cnt);
                station.put(InfraDatas.DIRECTION, dr);
                stations.add(station);
            }
        }
    }

    private void setCorridor() {
        int cnt = 0;

        while (cnt++ < MAX_COR) {
            int id = getID(MAX_COR, Corridors);
            String nid = String.valueOf(id);
            String name = "Name_" + nid;
            HashMap<InfraDatas, Object> corridor = new HashMap();
            corridor.put(InfraDatas.ID, String.valueOf(id));
            corridor.put(InfraDatas.NAME, name);
            corridor.put(InfraDatas.ROADTYPE, RoadType.HIGHWAY);
            corridor.put(InfraDatas.SHORTNAME, name + "_short");
            corridor.put(InfraDatas.STARTNAME, name + "_StartName");
            corridor.put(InfraDatas.ENDNAME, name + "_EndName");
            corridor.put(InfraDatas.STARTNODE, name + "_StartNode");
            corridor.put(InfraDatas.ENDNODE, name + "_EndNode");
            corridor.put(InfraDatas.FREESPEED, 80);
            corridor.put(InfraDatas.JAMSPEED, 40);
            corridor.put(InfraDatas.DIRECTION, getDirection());
            Corridors.add(corridor);
//            System.out.println("id : "+id + "\tCor length : "+Corridors.size());
        }

//        for(HashMap<InfraDatas, Object> cor : Corridors){
//            System.out.println("CID : "+(String)cor.get(InfraDatas.ID)+
//                    ", CNAME : "+ (String)cor.get(InfraDatas.NAME)+
//                    ", Direction : "+ ((Direction)cor.get(InfraDatas.DIRECTION)).toString());
//        }
    }

//    public ArrayList<HashMap<InfraDatas, Object>> getCorridor(){
//        
//    }
    private Direction getDirection() {
        int _id = randomGenerator.nextInt(4);
        switch (_id) {
            case 0:
                return Direction.EB;
            case 1:
                return Direction.WB;
            case 2:
                return Direction.NB;
            default:
                return Direction.SB;
        }
    }

    private int getID(int MAX, ArrayList<HashMap<InfraDatas, Object>> cors) {
        int id = -1;
        while (true) {
            id = randomGenerator.nextInt(MAX + 1);
            if (cors.size() == 0) {
                break;
            } else {
                boolean checked = false;
                for (HashMap<InfraDatas, Object> datas : cors) {
                    String d_id = (String) datas.get(InfraDatas.ID);
                    if (String.valueOf(id).equals(d_id)) {
                        checked = true;
                        break;
                    }
                }

                if (!checked) {
                    break;
                }
            }
        }
        return id;
    }
}
