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

package plugin;

/**
 *
 * @author Chongmyung Park
 */
public enum PluginType {
    
    SIMULATION("simulation"), 
    TOOL("tool");
    
    String id;

    private PluginType() {
        this.id = "";
    }    

    private PluginType(String id) {
        this.id = id;
    }
    
    /**
     * Returns PluginType by id
     * @param id PluginType id ("simulation" or "tool")
     * @return PluginType
     */
    public static PluginType getPluginType(String id)
    {
        for(PluginType pt : PluginType.values())
        {
            if(pt.id.equals(id)) return pt;
        }
        return null;                
    }

    /**
     * Retruns true if it is PluginType.SIMULATION, else false
     * @return true if it is PluginType.SIMULATION, else false
     */
    public boolean isSimulationPlugin() {
        return ( this == SIMULATION);
    }
    
    /**
     * Retruns true if it is PluginType.TOOL, else false
     * @return true if it is PluginType.TOOL, else false
     */
    public boolean isToolPlugin() {
        return ( this == TOOL);
    }    
      
}
