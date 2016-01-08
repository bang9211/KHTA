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

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

/**
 *
 * @author Chongmyung Park
 */
public class PluginInfoLoader {

    /** Directory path that includes plugins */
    String pluginsPath = "plugins";
    
    /** Plugin information list from plugin directory */
    Vector<PluginInfo> plugins = new Vector<PluginInfo>();
    
    public PluginInfoLoader() {}
    
    /**
     * Load plugin informations
     */
    public void load()
    {
        File pluginDir = new File(pluginsPath);
        if (!pluginDir.exists()) {
            pluginDir.mkdir();
        }
        File[] pluginPackages = pluginDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(pathname.getName().startsWith(".")) return false;
                else return true;
            }
        });
        for (File onePackage : pluginPackages) {
            if(onePackage.isFile()) continue;
            PluginInfo pi = loadPluginInfo(onePackage);
            if(pi != null) plugins.add(pi);
        }
    }
    
    /**
     * Returns plugin informations in plugins directory
     * @return plugin informations
     */
    public Vector<PluginInfo> getPlugins() {
        return plugins;
    }
    
    /**
     * Load one plugin information
     * @param pluginDir plugin directory path
     * @return plugin information
     */
    private PluginInfo loadPluginInfo(File pluginDir) {
        try {
            String pluginPath = pluginDir.getPath();
            PluginInfo info = new PluginInfo(pluginPath);            
            return info;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
