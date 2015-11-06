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
package infra;

import java.util.HashMap;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class InfraObject {
    private final String id;
    private final String name;
    private HashMap<InfraDatas, Object> properties = new HashMap();
    
    public InfraObject(HashMap<InfraDatas, Object> _pro){
        properties = _pro;
        id = (String)getProperty(InfraDatas.ID);
        name = (String)getProperty(InfraDatas.NAME);
    }
    
    //get Property from properties
    protected Object getProperty(InfraDatas key){
        return properties.get(key);
    }
    
    //get ID
    public String getID(){
        return id;
    }
    
    //get NAME
    public String getName(){
        return name;
    }
}
