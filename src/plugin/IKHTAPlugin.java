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

import net.xeoh.plugins.base.Plugin;

/**
 * Interface for tool and simulation plugin in TICAS
 * @author Chongmyung Park
 */

public interface IKHTAPlugin extends Plugin {
    /**
     * Initialize plugin     
     * <pre>
     * In this method, you should implement :
     * 
     *      + adding your GUI component to frame and set frame properties (size, title ...)
     *         [Example]
     *         public void init(JDialog dialog, TMO tmo) {
     *              // set dialog properties
     *              dialog.setSize(800, 550);       
     *              dialog.setResizable(false);
     *              dialog.setLocationRelativeTo(dialog.getParent());
     *              // add GUI to dialog
     *              Container container = dialog.getContentPane();
     *              container.setLayout(new BorderLayout());    
     *              container.add(new YourPluginPanel(tmo)); // give tmo object to use Infra objects in your plugin
     *         }
     *      + setting given tmo to your TMO object to use infra (corridor, station, detector ...)
     * </pre>
     * @param container container component to show plugin gui
     * @param tmo TMO object used in TICAS     
     */
    public void init(PluginFrame frame);
}
