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

import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.util.Arrays;
import javax.swing.JColorChooser;

/**
 *
 * @author Chongmyung Park
 */
public class ContourPanel extends javax.swing.JPanel {

    protected JButton[] buttons;
    protected JTextField[] rangeStarts;
    protected JTextField[] rangeEnds;
    protected Color[] colors;
    protected float maxValue = 0;
    protected int steps = 0;
    protected boolean isReverse = false;
    private int selectCount = 0;
    private int count = 0;
    
    public ContourPanel(int interval, float maxValue, boolean isreverse) {
        initComponents();
        if (interval < 3) {
            interval = 3;
        }
        if (interval > 12) {
            interval = 12;
        }
        this.steps = interval;
        this.maxValue = maxValue;
        
        this.cbxIntervals.setSelectedIndex(interval - 3);
        
        this.isReverse = isreverse;
              
        init();
    }
    
    public Color[] getColors() {
        return colors;
    }    
    
    public float[] getStepValues()
    {
        float[] stepValues = new float[steps];
        for(int i=0; i<steps; i++)
        {
            stepValues[i] = Float.parseFloat(rangeEnds[i].getText());
        }
        return stepValues;
    }
    
    public int getSteps()
    {
        return steps;
    }
    
    public void setIsReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }
    
    public void setUnit(String unit) {
        this.lbUnit.setText(unit);
    }
    
    public String getUnit() {
        return this.lbUnit.getText();
    }
    
    public int getStepBox(){
        return cbxIntervals.getSelectedIndex();
    }
    
    private void init() {
        
        buttons = new JButton[]{btnColor01, btnColor02, btnColor03, btnColor04, btnColor05, btnColor06, btnColor07, btnColor08, btnColor09, btnColor10, btnColor11, btnColor12, btnColor13};
        rangeStarts = new JTextField[]{tbxRangeStart01, tbxRangeStart02, tbxRangeStart03, tbxRangeStart04, tbxRangeStart05, tbxRangeStart06, tbxRangeStart07, tbxRangeStart08, tbxRangeStart09, tbxRangeStart10, tbxRangeStart11, tbxRangeStart12, tbxRangeStart13};
        rangeEnds = new JTextField[]{tbxRangeEnd01, tbxRangeEnd02, tbxRangeEnd03, tbxRangeEnd04, tbxRangeEnd05, tbxRangeEnd06, tbxRangeEnd07, tbxRangeEnd08, tbxRangeEnd09, tbxRangeEnd10, tbxRangeEnd11, tbxRangeEnd12, tbxRangeEnd13};
        colors = new Color[13];
        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
          public void mouseClicked(MouseEvent e) {
            JButton btn = (JButton) e.getSource();
            Color background = JColorChooser.showDialog(null, "Change Color", btn.getBackground());
            if (background != null) {
              btn.setBackground(background);
              updateColor();
            }
          }
        };        

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addMouseListener(mouseAdapter);
        }
        initSetting();        
    }

    private void initSetting() {
        this.steps = Integer.parseInt((String) this.cbxIntervals.getSelectedItem());
        initColors();
        initValues();
        //System.out.println("init Settings");        
    }
    
    protected void initColors() {
        int step = this.steps;

        Arrays.fill(colors, new Color(-1250856));
        colors[0] = new Color(-1250856);
        for (int i = 0; i < step; i++) {
            colors[i+1] = new Color(-1250856);
//          colors[i+1]=Color.getHSBColor((float)(300-(300/(step-1))*i)/360, 1f, 1f);
            colors[i+1]=Color.getHSBColor((float)(60+(i*30))/360, 1f, 1f);
        }
        colors[0] = Color.getHSBColor(0, 0, 1.0f);
        if(this.isReverse) reverse();
        
        //버튼색 설정
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(colors[i]);
        }
    }

    protected void initValues() {
        int offset = Math.round(this.maxValue / this.steps);

        for (int i = 1; i < this.rangeStarts.length; i++) {
            this.rangeStarts[i].setText("");
            this.rangeEnds[i].setText("");
            this.rangeStarts[i].setEnabled(false);
            this.rangeEnds[i].setEnabled(false);
            this.buttons[i].setEnabled(false);
        }

        this.rangeStarts[0].setText("0");
        for (int i = 1; i < this.steps + 1; i++) {
            String value = String.format("%d", offset * i);
            this.rangeEnds[i - 1].setText(value);
            this.rangeStarts[i].setText(value);
            this.rangeStarts[i].setEnabled(true);
            this.rangeEnds[i - 1].setEnabled(true);
            this.buttons[i].setEnabled(true);
        }
        this.rangeEnds[this.steps].setText("+++");
    }
    
    public ContourSetting getContourSetting()
    {
        ContourSetting setting = new ContourSetting();        
        setting.setContourColors(colors);
        setting.setContourStepValues(getStepValues());
        setting.setContourStep(steps);
        setting.setContourUnit(getUnit());
        setting.setIsReverse(isReverse);
        setting.setMaxValue(this.maxValue);
       
        return setting;
    }
    
    //Load 성공 후에 실행되는 함수
    //Load된 데이터를 가져와 저장하고, GUI에 설정 및 갱신
    public void setContourSetting(ContourSetting setting)
    {
        this.colors = setting.getContourColors();
        this.steps = setting.getContourStep();
        this.lbUnit.setText(setting.getContourUnit());
        this.isReverse = setting.isReverse();
        this.maxValue = setting.getMaxValue();
        float[] sv = setting.getContourStepValues();
        
        applySetting(sv);
    }

    private void applySetting(float[] sv){
        //Load한 Interval 입력
        cbxIntervals.setSelectedIndex(steps - 3);
        
        setColors();
        setValues(sv);
        
        //System.out.println("apply Settings");        
    }

    protected void setColors(){
        //버튼색 설정
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(colors[i]);
        }
    }
    
    protected void setValues(float[] sv){
        //Load한 value값 입력
        for(int i=0; i<sv.length; i++)
        {
            String v = String.format("%.0f", sv[i]);
            rangeEnds[i].setText(v);
            rangeStarts[i+1].setText(v);
            buttons[i].setBackground(colors[i]);
            
            this.rangeStarts[i].setEnabled(true);
            this.rangeEnds[i].setEnabled(true);
            
        }
        rangeStarts[0].setText("0");
        rangeStarts[steps].setEnabled(true);
        rangeEnds[steps].setText("+++");
        rangeEnds[steps].disable();
        for(int i=steps+1; i<12; i++)
        {
            rangeStarts[i].setText("");
            rangeStarts[i].disable();
            rangeEnds[i].setText("");
            rangeEnds[i].disable();
        }
    }

    private void updateColor() {
        for (int i = 0; i < this.buttons.length; i++) {
            this.colors[i] = this.buttons[i].getBackground();
        }
    }
    
    protected void reverse()
    {
        for (int l=0, r=steps; l<r; l++, r--) {
            Color temp = colors[l]; 
            colors[l]  = colors[r]; 
            colors[r] = temp;
        }        
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbxIntervals = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        btnColor01 = new javax.swing.JButton();
        tbxRangeStart01 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbxRangeEnd01 = new javax.swing.JTextField();
        btnColor02 = new javax.swing.JButton();
        tbxRangeStart02 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbxRangeEnd02 = new javax.swing.JTextField();
        tbxRangeEnd04 = new javax.swing.JTextField();
        tbxRangeStart04 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnColor04 = new javax.swing.JButton();
        btnColor03 = new javax.swing.JButton();
        tbxRangeStart03 = new javax.swing.JTextField();
        tbxRangeEnd03 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbxRangeStart07 = new javax.swing.JTextField();
        btnColor08 = new javax.swing.JButton();
        tbxRangeEnd07 = new javax.swing.JTextField();
        btnColor07 = new javax.swing.JButton();
        tbxRangeEnd08 = new javax.swing.JTextField();
        tbxRangeStart08 = new javax.swing.JTextField();
        btnColor06 = new javax.swing.JButton();
        btnColor05 = new javax.swing.JButton();
        tbxRangeStart05 = new javax.swing.JTextField();
        tbxRangeStart06 = new javax.swing.JTextField();
        tbxRangeEnd06 = new javax.swing.JTextField();
        tbxRangeEnd05 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnColor09 = new javax.swing.JButton();
        btnColor10 = new javax.swing.JButton();
        btnColor11 = new javax.swing.JButton();
        btnColor12 = new javax.swing.JButton();
        tbxRangeStart11 = new javax.swing.JTextField();
        tbxRangeStart10 = new javax.swing.JTextField();
        tbxRangeStart09 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbxRangeEnd09 = new javax.swing.JTextField();
        tbxRangeEnd10 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tbxRangeEnd11 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tbxRangeStart12 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbxRangeEnd12 = new javax.swing.JTextField();
        btnColor13 = new javax.swing.JButton();
        tbxRangeStart13 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tbxRangeEnd13 = new javax.swing.JTextField();
        lbUnit = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Verdana 12", 1, 12)); // NOI18N
        jLabel1.setText("Number of Contour Intervals");

        cbxIntervals.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        cbxIntervals.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxIntervalsItemStateChanged(evt);
            }
        });
        cbxIntervals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxIntervalsActionPerformed(evt);
            }
        });

        tbxRangeStart01.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart01.setText("0");

        jLabel2.setText("-");

        tbxRangeEnd01.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd01.setText("0");

        tbxRangeStart02.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart02.setText("0");

        jLabel3.setText("-");

        tbxRangeEnd02.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd02.setText("0");

        tbxRangeEnd04.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd04.setText("0");

        tbxRangeStart04.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart04.setText("0");

        jLabel4.setText("-");

        tbxRangeStart03.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart03.setText("0");

        tbxRangeEnd03.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd03.setText("0");

        jLabel5.setText("-");

        jLabel6.setText("-");

        jLabel7.setText("-");

        tbxRangeStart07.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart07.setText("0");

        tbxRangeEnd07.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd07.setText("0");

        tbxRangeEnd08.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd08.setText("0");

        tbxRangeStart08.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart08.setText("0");

        tbxRangeStart05.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart05.setText("0");

        tbxRangeStart06.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart06.setText("0");

        tbxRangeEnd06.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd06.setText("0");

        tbxRangeEnd05.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd05.setText("0");

        jLabel8.setText("-");

        jLabel9.setText("-");

        tbxRangeStart11.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart11.setText("0");

        tbxRangeStart10.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart10.setText("0");

        tbxRangeStart09.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart09.setText("0");

        jLabel10.setText("-");

        tbxRangeEnd09.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd09.setText("0");

        tbxRangeEnd10.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd10.setText("0");

        jLabel11.setText("-");

        tbxRangeEnd11.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd11.setText("0");

        jLabel13.setText("-");

        tbxRangeStart12.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeStart12.setText("0");

        jLabel12.setText("-");

        tbxRangeEnd12.setFont(new java.awt.Font("Verdana 12", 0, 12)); // NOI18N
        tbxRangeEnd12.setText("0");

        tbxRangeStart13.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxRangeStart13.setText("0");

        jLabel14.setText("-");

        tbxRangeEnd13.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxRangeEnd13.setText("0");

        lbUnit.setFont(new java.awt.Font("Verdana 12", 1, 12)); // NOI18N
        lbUnit.setText("[UNIT]");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnColor01, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart01, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd01, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbUnit))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnColor02, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart02, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd02, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor03, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart03, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd03, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnColor04, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart04, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd04, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor05, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart05, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd05, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor06, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart06, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd06, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor07, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart07, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd07, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor08, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart08, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd08, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor09, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart09, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd09, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor10, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart10, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd10, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnColor11, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor12, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart12, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd12, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnColor13, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeStart13, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxRangeEnd13, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lbUnit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor01, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd01)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart01)
                        .addComponent(jLabel2)))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor02, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd02)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart02)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor03, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd03)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart03)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor04, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd04)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart04)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor05, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd05)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart05)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor06, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd06)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart06)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor07, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd07)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart07)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor08, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd08)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart08)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor09, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd09)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart09)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd10)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart10)
                        .addComponent(jLabel11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd11)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart11)
                        .addComponent(jLabel13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd12)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart12)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnColor13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbxRangeEnd13)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbxRangeStart13)
                        .addComponent(jLabel14)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addComponent(cbxIntervals, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel1))
                    .addComponent(cbxIntervals, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbxIntervalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxIntervalsActionPerformed
        //System.out.println("Action Performed");        
//        if((colors != null)) initSetting();        
}//GEN-LAST:event_cbxIntervalsActionPerformed

    private void cbxIntervalsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxIntervalsItemStateChanged
        count++;
        //System.out.println(count);
        
        if(steps == 7)
            selectCount = 2;
        else
            selectCount = 4;
        if(count > selectCount && evt.getStateChange() == ItemEvent.SELECTED){
            //System.out.println("State Changed");
            initSetting();
        }
    }//GEN-LAST:event_cbxIntervalsItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnColor01;
    private javax.swing.JButton btnColor02;
    private javax.swing.JButton btnColor03;
    private javax.swing.JButton btnColor04;
    private javax.swing.JButton btnColor05;
    private javax.swing.JButton btnColor06;
    private javax.swing.JButton btnColor07;
    private javax.swing.JButton btnColor08;
    private javax.swing.JButton btnColor09;
    private javax.swing.JButton btnColor10;
    private javax.swing.JButton btnColor11;
    private javax.swing.JButton btnColor12;
    private javax.swing.JButton btnColor13;
    private javax.swing.JComboBox cbxIntervals;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbUnit;
    private javax.swing.JTextField tbxRangeEnd01;
    private javax.swing.JTextField tbxRangeEnd02;
    private javax.swing.JTextField tbxRangeEnd03;
    private javax.swing.JTextField tbxRangeEnd04;
    private javax.swing.JTextField tbxRangeEnd05;
    private javax.swing.JTextField tbxRangeEnd06;
    private javax.swing.JTextField tbxRangeEnd07;
    private javax.swing.JTextField tbxRangeEnd08;
    private javax.swing.JTextField tbxRangeEnd09;
    private javax.swing.JTextField tbxRangeEnd10;
    private javax.swing.JTextField tbxRangeEnd11;
    private javax.swing.JTextField tbxRangeEnd12;
    private javax.swing.JTextField tbxRangeEnd13;
    private javax.swing.JTextField tbxRangeStart01;
    private javax.swing.JTextField tbxRangeStart02;
    private javax.swing.JTextField tbxRangeStart03;
    private javax.swing.JTextField tbxRangeStart04;
    private javax.swing.JTextField tbxRangeStart05;
    private javax.swing.JTextField tbxRangeStart06;
    private javax.swing.JTextField tbxRangeStart07;
    private javax.swing.JTextField tbxRangeStart08;
    private javax.swing.JTextField tbxRangeStart09;
    private javax.swing.JTextField tbxRangeStart10;
    private javax.swing.JTextField tbxRangeStart11;
    private javax.swing.JTextField tbxRangeStart12;
    private javax.swing.JTextField tbxRangeStart13;
    // End of variables declaration//GEN-END:variables

}
