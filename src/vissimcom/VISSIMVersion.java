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

import org.jawin.GUID;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum VISSIMVersion {
    VISSIM540x64("{0157c47c-cb54-4788-9613-C444C7F0117C}"),
    VISSIM540x86("{cd13ec64-8a38-4363-8335-8BE904343F07}");
//    VISSIM540x86("{325df941-0b2d-40b1-B6C5-133E632203CD}");

    private GUID CLSID;
    VISSIMVersion(String guid){
        CLSID = new GUID(guid);
    }
    
    public GUID getGUID(){
        return CLSID;
    }
    
    public boolean is540x86(){
        return this == VISSIM540x86;
    }
    public boolean is540x64(){
        return this == VISSIM540x64;
    }
}
