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
package plugin.simulation.VSL.algorithm;

import edu.umn.natsrl.infra.infraobjects.DMSImpl;
import java.util.TreeMap;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public interface VSLSTAImpl {
    public boolean findSAT(TreeMap<Double, DMSImpl> dmsMap, VSStationFinder cVSS_finder, boolean isFirst);
}
