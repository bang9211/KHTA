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
package evaluation;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class DataLoadOption {

    private boolean SimulationMode;
    private Interval SimInterval;

    public DataLoadOption(boolean smode, Interval sitv) {
        SimulationMode = smode;
        SimInterval = sitv;
    }

    public static DataLoadOption setSimulationMode(Interval sitv) {
        return new DataLoadOption(true, sitv);
    }

    public static DataLoadOption setEvaluationMode() {
        return new DataLoadOption(false, null);
    }

    public boolean isSimulationMode() {
        return SimulationMode;
    }

    public Interval getSimulationInterval() {
        return SimInterval;
    }
}
