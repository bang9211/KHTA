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
package infra.simulation;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum DensityRank {
    First(55, 6),	// 55+ vpm => 6 samples (3 minutes)
    Second(40, 4),	// 40-55 vpm => 4 samples (2 minutes)
    Third(25, 3),	// 25-40 vpm => 3 samples (1.5 minutes)
    Fourth(15, 4),	// 15-25 vpm => 4 samples (2 minutes)
    Fifth(10, 6),	// 10-15 vpm => 6 samples (3 minutes)
    Last(0, 0);	// less than 10 vpm => 0 samples
    protected final double density;
    protected final int samples;
    private DensityRank(double k, int n_smp) {
            density = k;
            samples = n_smp;
    }
    /** Get the number of rolling samples for the given density */
    static protected int samples(double k) {
            for(DensityRank dr: values()) {
                    if(k > dr.density)
                            return dr.samples;
            }
            return Last.samples;
    }
    /** Get the maximum number of samples in any density rank */
    static protected int getMaxSamples() {
            int s = 0;
            for(DensityRank dr: values())
                    s = Math.max(s, dr.samples);
            return s;
    }
}
