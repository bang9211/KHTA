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

import java.awt.Color;
import java.util.Arrays;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class ContourPanel_Speed extends ContourPanel{

    public ContourPanel_Speed(int interval, float maxValue, boolean isreverse) {
        super(interval, maxValue, isreverse);
    }
    
    @Override
    protected void setColors() {
//        Arrays.fill(colors, new Color(-1250856));
//        
//        colors[7]=Color.getHSBColor(0.008f, 0.60f, 0.97f);
//        colors[6]=Color.getHSBColor(0.991f, 0.42f, 1f);
//        colors[5]=Color.getHSBColor(0.663f, 0.40f, 1f);
//        colors[4]=Color.getHSBColor(0.627f, 0.22f, 0.99f);
//        colors[3]=Color.getHSBColor(0.45f, 0.87f, 0.51f);
//        colors[2]=Color.getHSBColor(0.33f, 0.59f, 0.99f);
//        colors[1]=Color.getHSBColor(0.32f, 0.20f, 1.0f);
//        colors[0] = Color.getHSBColor(0, 0, 1.0f);
//        if(this.isReverse) reverse();
    }
    
    @Override
    protected void setValues(float[] sv) {
//        int offset = 20;
//
//        for (int i = 1; i < this.rangeStarts.length; i++) {
//            this.rangeStarts[i].setText("");
//            this.rangeEnds[i].setText("");
//            this.rangeStarts[i].setEnabled(false);
//            this.rangeEnds[i].setEnabled(false);
//            this.buttons[i].setEnabled(false);
//        }
//
//        this.rangeStarts[0].setText("0");
//        for (int i = 1; i < this.steps + 1; i++) {
//            if(i != 1)
//                offset += 10;
//            String value = String.format("%d", offset);
//            this.rangeEnds[i - 1].setText(value);
//            this.rangeStarts[i].setText(value);
//            this.rangeStarts[i].setEnabled(true);
//            this.rangeEnds[i - 1].setEnabled(true);
//            this.buttons[i].setEnabled(true);
//        }
//        this.rangeEnds[this.steps].setText("+++");
    }
}
