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
/*
 * SimulationExampleGUI.java
 *
 * Created on Apr 21, 2011, 3:45:47 PM
 */

package plugin.metering;

import edu.umn.natsrl.infra.section.SectionManager;
import edu.umn.natsrl.sfim.SectionInfoDialog;
import infra.Infra;
import infra.Period;
import infra.Section;
import infra.simulation.SimulationConfig;
import infra.simulation.SimulationUtil;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import plugin.PluginFrame;
import plugin.metering.Simulation.ISimEndSignal;
import util.FileHelper;
import util.PropertiesWrapper;
import vissimcom.ComError;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Chongmyung Park
 */
public class MeteringSimulationGUI extends javax.swing.JPanel implements ISimEndSignal {
//    private TMO tmo = TMO.getInstance();
    private Infra infra = Infra.getInstance();
    private Vector<Section> sections = new Vector<Section>();
    private PropertiesWrapper prop;
    private PluginFrame simFrame;
    private Simulation sim;
    private Date startTime;
    private PrintStream backupOut;
    private PrintStream backupErr;
    
    /** Creates new form SimulationExampleGUI */
    public MeteringSimulationGUI(PluginFrame parent) {
        initComponents();
        this.simFrame = parent;
        this.loadSection();
        
        //VissimVersion
        for(VISSIMVersion v : VISSIMVersion.values()){
            this.cbxVissimVersion.addItem(v);
        }
        
        MeteringConfig.loadConfig();
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(false);
        this.tbxCaseFile.setText(MeteringConfig.CASE_FILE);
        this.tbxRandom.setText(df.format(MeteringConfig.RANDOM_SEED));
        this.tbxKjam.setText(df.format(MeteringConfig.Kjam));
        this.tbxKc.setText(df.format(MeteringConfig.Kc));
        this.tbxKd_Rate.setText(df.format(MeteringConfig.Kd_Rate));
        this.tbxKb.setText(df.format(MeteringConfig.Kb));
        this.tbxKstop.setText(df.format(MeteringConfig.Kstop));
        this.tbxStopDuration.setText(String.format("%d", MeteringConfig.stopDuration));
        this.tbxStopTrend.setText(String.format("%d", MeteringConfig.stopBSTrend));
        this.tbxStopUpstreamCount.setText(String.format("%d", MeteringConfig.stopUpstreamCount));
        this.tbxAb.setText(String.format("%d", MeteringConfig.Ab));
        this.tbxMaxRedTime.setText(df.format(MeteringConfig.MAX_RED_TIME));
        this.tbxMaxWaittingTime.setText(df.format(MeteringConfig.MAX_WAIT_TIME_MINUTE));
        this.tbxMaxWaittingTimeF2F.setText(df.format(MeteringConfig.MAX_WAIT_TIME_MINUTE_F2F));        

        
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {}

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                sim.simulationStop();
            }
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
    
    private void runSimulation() {
        try {
            
            this.redirectOutput();            
            
            this.btnRun.setEnabled(false);
            startTime = new Date();
            
            MeteringConfig.CASE_FILE = this.tbxCaseFile.getText();
            MeteringConfig.RANDOM_SEED = Integer.parseInt(this.tbxRandom.getText());
            MeteringConfig.Kjam = Integer.parseInt(this.tbxKjam.getText());
            MeteringConfig.Kc = Integer.parseInt(this.tbxKc.getText());
            MeteringConfig.Kd_Rate = Double.parseDouble(this.tbxKd_Rate.getText());
            MeteringConfig.Kb = Double.parseDouble(this.tbxKb.getText());            
            MeteringConfig.Kstop = Double.parseDouble(this.tbxKstop.getText());            
            MeteringConfig.stopDuration = Integer.parseInt(this.tbxStopDuration.getText());
            MeteringConfig.stopBSTrend = Integer.parseInt(this.tbxStopTrend.getText());
            MeteringConfig.stopUpstreamCount = Integer.parseInt(this.tbxStopUpstreamCount.getText());
            MeteringConfig.Ab = Integer.parseInt(this.tbxAb.getText());                                   
            MeteringConfig.Kd = MeteringConfig.Kc * MeteringConfig.Kd_Rate;
            MeteringConfig.setMaxWaitTimeF2F(Integer.parseInt(this.tbxMaxWaittingTimeF2F.getText()));
            MeteringConfig.setMaxWaitTime(Integer.parseInt(this.tbxMaxWaittingTime.getText()));
            MeteringConfig.MAX_RED_TIME = Integer.parseInt(this.tbxMaxRedTime.getText());
            MeteringConfig.UseMetering = !this.chkNoMetering.isSelected();
            MeteringConfig.saveConfig();
            
            Section section = (Section)this.cbxSections.getSelectedItem();
            boolean noMetering = this.chkNoMetering.isSelected();
            sim = new Simulation(MeteringConfig.CASE_FILE, MeteringConfig.RANDOM_SEED, section, noMetering, (VISSIMVersion)this.cbxVissimVersion.getSelectedItem(),SimulationConfig.RunningInterval);
            sim.setSignalListener(this);
            sim.start();
            
            this.cbxSections.setEnabled(false);
            this.btnBrowse.setEnabled(false);
            this.tbxCaseFile.setEditable(false);
            this.tbxRandom.setEditable(false);
            this.chkNoMetering.setEnabled(false);
            this.tbxKc.setEditable(false);
            this.tbxKjam.setEditable(false);
            this.tbxKd_Rate.setEditable(false);
            this.tbxMaxWaittingTime.setEditable(false);
            this.tbxMaxWaittingTimeF2F.setEditable(false);
            this.tbxMaxRedTime.setEditable(false);
            this.tbxKb.setEditable(false);
            this.tbxKstop.setEditable(false);
            this.tbxStopDuration.setEditable(false);
            this.tbxStopTrend.setEditable(false);
            this.tbxStopUpstreamCount.setEditable(false);
            this.tbxAb.setEditable(false);
            
            System.out.println("[ Simulation Configurations ]");        
            System.out.println("  - Kjam = " + MeteringConfig.Kjam);
            System.out.println("  - Kc = " + MeteringConfig.Kc);        
            System.out.println("  - Kd Rate = " + MeteringConfig.Kd_Rate);
            System.out.println("  - Kd = " + MeteringConfig.Kd);
            System.out.println("  - Kb = " + MeteringConfig.Kb);
            System.out.println("  - Kstop = " + MeteringConfig.Kstop);
            System.out.println("  - StopDuration = " + MeteringConfig.stopDuration + " time steps");
            System.out.println("  - StopTrend = " + MeteringConfig.stopBSTrend + " time steps");
            System.out.println("  - StopUpstreamCount = " + MeteringConfig.stopUpstreamCount + " stations");
            System.out.println("  - Ab = " + MeteringConfig.Ab);
            System.out.println("  - Max Waiting Time = " + MeteringConfig.MAX_WAIT_TIME_MINUTE);
            System.out.println("  - Max Waiting Time (F2F) = " + MeteringConfig.MAX_WAIT_TIME_MINUTE_F2F);
            System.out.println("  - Max Red Time = " + MeteringConfig.MAX_RED_TIME);
            System.out.println("  - Use Metering = " + MeteringConfig.UseMetering);
            System.out.println("  - Use Coordination = " + MeteringConfig.UseCoordination);
            System.out.println("  - Case File = " + MeteringConfig.CASE_FILE);
            System.out.println("  - Random Seed = " + MeteringConfig.RANDOM_SEED);
            System.out.println("----------------------------------------------");
            
        } catch(Exception ex) {
            this.btnRun.setEnabled(true);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR : " + ex.getMessage());
        }
    }

    @Override
    public void signalEnd(int code) {
        if(code == -1) {
            this.chkShowVehicles.setEnabled(true);
            this.chkShowVehicles.setSelected(false);            
            setVissimVisible(false);
            return;
        }
        
        ComError ce = ComError.getErrorbyID(code);
        if(!ce.isCorrect()){
            JOptionPane.showMessageDialog(simFrame, ce.toString());
            this.restoreOutput();
            return;
        }
        
        int samples = sim.getSamples();
        if(samples < 5) {
            JOptionPane.showMessageDialog(simFrame, "Too short simulation");
            simFrame.afterSimulation(null, null);
            this.simFrame.setVisible(false);            
        }

        int duration = samples * 30;        

        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.set(Calendar.SECOND, 0);
        Date sTime = c.getTime();
        c.add(Calendar.SECOND, duration);
        Date eTime = c.getTime();
        simFrame.afterSimulation((Section)this.cbxSections.getSelectedItem(), new Period(sTime, eTime, 30));
        SimulationUtil.SaveSimulation((Section)this.cbxSections.getSelectedItem(),new Period(sTime, eTime, 30),simFrame);
        System.out.println("Restore output redirection ... ");
        this.restoreOutput();
    }
    
    
    private void setVissimVisible(boolean selected) {
        if(sim != null) {
            sim.setVissimVisible(selected);
        }
    }
    
    
    /**
     * Loads section information from TMO
     */
    private void loadSection() {
//        SectionManager sm = tmo.getSectionManager();
//        this.sections.clear();
//        sm.loadSections();
//        if(sm.getSections() == null){
//            return;
//        }
        Set<Section> tempSections = new HashSet<>();
        Collections.addAll(tempSections, infra.getSections());
        this.sections.addAll(tempSections);
        this.cbxSections.removeAllItems();

        for (Section s : this.sections) {
            this.cbxSections.addItem(s);
        }
    }    
    
    /**
     * Open section editor
     */
//    public void openSectionEditor() {
//        tmo.openSectionEditor(this.simFrame, true);
//        this.loadSection();
//    }    
    
    /**
     * Open section information dialog
     */
    private void openSectionInfoDialog() {
        Section section = (Section) this.cbxSections.getSelectedItem();
        if (section == null) {
            return;
        }
        SectionInfoDialog si = new SectionInfoDialog(section, null, true);
        si.setLocationRelativeTo(this);
        si.setVisible(true);
    }       

    /**
     * Redirect output into log box
     */
    public void redirectOutput() {
        backupOut = System.out;
        backupErr = System.err;
        // redirect System.out and System.err to log textbox
        StringOutputStream sos = new StringOutputStream(this.tbxLog);
        System.setOut(new PrintStream(sos));
        System.setErr(new PrintStream(sos));
    }

    /**
     * Resotre output
     */
    public void restoreOutput() {
        System.setOut(backupOut);
        System.setErr(backupErr);
    }
    
    
    /**
     * String Output Stream class for output redirection
     */
    public class StringOutputStream extends OutputStream {
        JTextArea logText;

        public StringOutputStream(JTextArea logText) {
            this.logText = logText;
        }
        
        @Override
        public void write(int b) throws IOException {
            updateLog(String.valueOf((char) b));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            updateLog(new String(b, off, len));
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        private synchronized void updateLog(final String text) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    logText.append(text);
                }
            });
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbxSections = new javax.swing.JComboBox();
        btnSectionInfo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        tbxCaseFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbxRandom = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkShowVehicles = new javax.swing.JCheckBox();
        chkNoMetering = new javax.swing.JCheckBox();
        cbxVissimVersion = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbxMaxWaittingTime = new javax.swing.JTextField();
        tbxKjam = new javax.swing.JTextField();
        tbxKc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tbxKd_Rate = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tbxMaxRedTime = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        tbxMaxWaittingTimeF2F = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbxKb = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        tbxAb = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        tbxKstop = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tbxStopDuration = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        tbxStopTrend = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        tbxStopUpstreamCount = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        btnRun = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbxLog = new javax.swing.JTextArea();
        btnClearLog = new javax.swing.JButton();
        btnSaveLog = new javax.swing.JButton();

        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });

        jSplitPane1.setDividerLocation(410);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Simulation Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 12))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel3.setText("Section");

        cbxSections.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        cbxSections.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxSectionsActionPerformed(evt);
            }
        });

        btnSectionInfo.setText("Info");
        btnSectionInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSectionInfoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel1.setText("Case File");

        btnBrowse.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        tbxCaseFile.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxCaseFile.setPreferredSize(new java.awt.Dimension(6, 25));

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel2.setText("Random Number");

        tbxRandom.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxRandom.setText("13");
        tbxRandom.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel4.setText("Option");

        chkShowVehicles.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        chkShowVehicles.setSelected(true);
        chkShowVehicles.setText("show vehicles and road");
        chkShowVehicles.setEnabled(false);
        chkShowVehicles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowVehiclesActionPerformed(evt);
            }
        });

        chkNoMetering.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        chkNoMetering.setText("no metering");

        cbxVissimVersion.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel13.setText("VISSIM Version");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnBrowse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbxCaseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cbxSections, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSectionInfo))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbxRandom, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbxVissimVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkShowVehicles))
                        .addGap(18, 18, 18)
                        .addComponent(chkNoMetering))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(49, 49, 49)
                        .addComponent(jLabel13)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxSections, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSectionInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(tbxCaseFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbxRandom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxVissimVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkShowVehicles)
                    .addComponent(chkNoMetering))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Metering Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 12))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel5.setText("Kjam");

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel6.setText("Kcrit");

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel7.setText("Max Wait Time1");

        tbxMaxWaittingTime.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxMaxWaittingTime.setText("4");

        tbxKjam.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxKjam.setText("180");

        tbxKc.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxKc.setText("40");

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel8.setText("min");

        jLabel9.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel9.setText("veh/hr");

        jLabel10.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel10.setText("veh/hr");

        jLabel11.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel11.setText("x Kcrit");

        jLabel12.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel12.setText("Kd");

        tbxKd_Rate.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxKd_Rate.setText("0.8");

        jLabel15.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel15.setText("Max Red Time");

        tbxMaxRedTime.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxMaxRedTime.setText("30");

        jLabel16.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel16.setText("seconds");

        jLabel17.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel17.setText("Max Wait Time2");

        tbxMaxWaittingTimeF2F.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxMaxWaittingTimeF2F.setText("2");

        jLabel18.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel18.setText("min");

        jLabel19.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        jLabel19.setText("(freeway to freeway)");

        jLabel20.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel20.setText("Kb");

        tbxKb.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxKb.setText("25");

        jLabel21.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel21.setText("veh/hr");

        jLabel22.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel22.setText("Ab");

        tbxAb.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxAb.setText("1000");

        jLabel23.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel23.setText("mile/hr^2");

        jLabel24.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel24.setText("Kstop");

        tbxKstop.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxKstop.setText("20");

        jLabel25.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel25.setText("veh/hr");

        jLabel26.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel26.setText("Stop Duration");

        tbxStopDuration.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxStopDuration.setText("10");

        jLabel27.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel27.setText("time steps");

        jLabel28.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel28.setText("Stop BS Trend");

        tbxStopTrend.setEditable(false);
        tbxStopTrend.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxStopTrend.setText("0");

        jLabel29.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel29.setText("time steps");

        jLabel30.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel30.setText("Stop Upstream #");

        tbxStopUpstreamCount.setEditable(false);
        tbxStopUpstreamCount.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxStopUpstreamCount.setText("0");

        jLabel31.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel31.setText("stations");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel12)
                            .addComponent(jLabel17)
                            .addComponent(jLabel20)
                            .addComponent(jLabel22)
                            .addComponent(jLabel24)
                            .addComponent(jLabel26)
                            .addComponent(jLabel28)
                            .addComponent(jLabel30))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbxMaxRedTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxKc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxKd_Rate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxMaxWaittingTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxMaxWaittingTimeF2F, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxKjam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbxKb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxAb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxKstop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxStopDuration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxStopTrend, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(tbxStopUpstreamCount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(103, 103, 103)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel8)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19))
                    .addComponent(jLabel21)
                    .addComponent(jLabel23)
                    .addComponent(jLabel25)
                    .addComponent(jLabel27)
                    .addComponent(jLabel29)
                    .addComponent(jLabel31))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tbxKjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbxKc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(tbxKd_Rate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(tbxKb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(tbxKstop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(tbxStopDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(tbxStopTrend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(tbxStopUpstreamCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(tbxAb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tbxMaxWaittingTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(tbxMaxWaittingTimeF2F, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(tbxMaxRedTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnRun.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        btnRun.setText("Run Simulation");
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnRun, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel3);

        tbxLog.setColumns(20);
        tbxLog.setEditable(false);
        tbxLog.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        tbxLog.setLineWrap(true);
        tbxLog.setRows(5);
        jScrollPane1.setViewportView(tbxLog);

        btnClearLog.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        btnClearLog.setText("Clear");
        btnClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLogActionPerformed(evt);
            }
        });

        btnSaveLog.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        btnSaveLog.setText("Save");
        btnSaveLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnClearLog, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
                        .addComponent(btnSaveLog, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveLog, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearLog, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 991, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        String path = ".";
        String prevPath = this.tbxCaseFile.getText();
        if(!prevPath.isEmpty()) path = new File(prevPath).getAbsolutePath();
        String caseFile = FileHelper.chooseFileToOpen(prevPath, "Select VISSIM Case File", FileHelper.FileFilterForVISSIM);
        if(caseFile != null) this.tbxCaseFile.setText(caseFile);
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        runSimulation();
    }//GEN-LAST:event_btnRunActionPerformed

    private void cbxSectionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxSectionsActionPerformed
        
}//GEN-LAST:event_cbxSectionsActionPerformed

    private void btnSectionInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSectionInfoActionPerformed
        this.openSectionInfoDialog();
}//GEN-LAST:event_btnSectionInfoActionPerformed

    private void chkShowVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowVehiclesActionPerformed
        setVissimVisible(this.chkShowVehicles.isSelected());
}//GEN-LAST:event_chkShowVehiclesActionPerformed

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
//        this.restoreOutput();
    }//GEN-LAST:event_formComponentRemoved

    private void btnClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLogActionPerformed
        this.tbxLog.setText("");
    }//GEN-LAST:event_btnClearLogActionPerformed

    private void btnSaveLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveLogActionPerformed
        String filepath = FileHelper.chooseFileToSave(".", "Select file to save", FileHelper.FileFilterForText);        
        if(filepath != null) {
            filepath = FileHelper.checkExtension(filepath, FileHelper.FileFilterForText);
            try {
                FileHelper.writeTextFile(this.tbxLog.getText(), filepath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnSaveLogActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSaveLog;
    private javax.swing.JButton btnSectionInfo;
    private javax.swing.JComboBox cbxSections;
    private javax.swing.JComboBox cbxVissimVersion;
    private javax.swing.JCheckBox chkNoMetering;
    private javax.swing.JCheckBox chkShowVehicles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField tbxAb;
    private javax.swing.JTextField tbxCaseFile;
    private javax.swing.JTextField tbxKb;
    private javax.swing.JTextField tbxKc;
    private javax.swing.JTextField tbxKd_Rate;
    private javax.swing.JTextField tbxKjam;
    private javax.swing.JTextField tbxKstop;
    private javax.swing.JTextArea tbxLog;
    private javax.swing.JTextField tbxMaxRedTime;
    private javax.swing.JTextField tbxMaxWaittingTime;
    private javax.swing.JTextField tbxMaxWaittingTimeF2F;
    private javax.swing.JTextField tbxRandom;
    private javax.swing.JTextField tbxStopDuration;
    private javax.swing.JTextField tbxStopTrend;
    private javax.swing.JTextField tbxStopUpstreamCount;
    // End of variables declaration//GEN-END:variables


}
