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
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author Chongmyung Park
 */

/**
 * 
 * Read plugin information xml file (plugin.xml)
 * XML file example
 * <pre>
 * <plugin>
 *  <package>edu.umn.natsrl.apps.plugin.something</package>
 *  <jarfile></jarfile>
 *  <name>Something Plugin</name>
 *  <description>This plugin does....</description>
 *  <entrypoint>StartClassName (without package)</entrypoint>
 * </plugin>
 * <pre>
 */
public class PluginInfo {

    private String path;
    private String name;
    private String description;
    private String jarfile;
    private PluginType type;
    private Class pluginClass;
    

    public PluginInfo(String name, PluginType type, Class pluginClass) {
        this.name = name;
        this.type = type;
        this.pluginClass = pluginClass;
    }

    public PluginInfo(String pluginPath) throws ParserConfigurationException, SAXException, IOException {
        String infoFile = pluginPath + File.separator + "plugin.xml";
        File xml = new File(infoFile);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xml);
        this.path = pluginPath;
        this.name = doc.getElementsByTagName("name").item(0).getTextContent();
        this.type = PluginType.getPluginType(doc.getElementsByTagName("type").item(0).getTextContent());
        this.jarfile = this.path + File.separator + doc.getElementsByTagName("jarfile").item(0).getTextContent();
        this.description = doc.getElementsByTagName("description").item(0).getTextContent();
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(Class pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getJarfilePath() {
        return jarfile;
    }

    public PluginType getType() {
        return type;
    }   

    @Override
    public String toString() {
        return this.name;
    }
}