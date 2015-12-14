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

package evaluation;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Chongmyung Park
 */
public class ContourSetting implements Serializable {

    private ContourType cType;
    private float[] stepValues;
    private Color[] colors;
    private int step;
    private String unit;
    private boolean isReverse;
    private float maxValue;

    public ContourType getcType() {
        return cType;
    }

    public void setcType(ContourType cType) {
        this.cType = cType;
    }    
    
    public Color[] getContourColors() {
        return colors;
    }

    public void setContourColors(Color[] contourColors) {
        this.colors = contourColors;
    }

    public float[] getContourStepValues() {
        return stepValues;
    }

    public void setContourStepValues(float[] contourLevelValues) {
        this.stepValues = contourLevelValues;
    }

    public int getContourStep() {
        return step;
    }

    public void setContourStep(int contourStep) {
        this.step = contourStep;
    }

    public String getContourUnit() {
        return unit;
    }

    public void setContourUnit(String contourUnit) {
        this.unit = contourUnit;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setIsReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
   
}
