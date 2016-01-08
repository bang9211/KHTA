/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth, US) and
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

import java.util.HashMap;

/**
 *
 * @author Chongmyung Park
 */
public enum VehicleClass {
    CAR("Car", 10),
    HGV("HGV", 20),
    BUS("Bus", 30),    
    TRAM("Tram", 40),
    Pedestrian("Pedestrian", 50),
    Bike("Bike", 60)
    ;
    
    private static HashMap<VehicleClass, Integer> vehicleClassIds = new HashMap<VehicleClass, Integer>();
    private final String vehicleClassName;
    private final int vehicleClassId;

    private VehicleClass(String vehicleClassName, int defaultId) {
        this.vehicleClassName = vehicleClassName;
        this.vehicleClassId = defaultId;
    }
    
    private static VehicleClass getVehicleClassByName(String name)
    {
        for(VehicleClass v : VehicleClass.values()) {
            if(name.equals(v.vehicleClassName)) return v;
        }
        return null;
    }
    
    protected static void setVehicleClassId(String name, int num)
    {
        VehicleClass v = VehicleClass.getVehicleClassByName(name);
        if(v == null) return;
        else {
            //System.out.println("VISSIMController> VehicleClass found : " + name + "("+num+")");
            VehicleClass.vehicleClassIds.put(v, num);
        }
    }
    
    public int getVehicleClassId()
    {
        Integer n = VehicleClass.vehicleClassIds.get(this);
        return (n != null ? n : this.vehicleClassId);
    }
}
