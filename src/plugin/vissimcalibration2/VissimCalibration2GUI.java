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

package plugin.vissimcalibration2;

//import edu.umn.natsrl.infra.TMO;
//import edu.umn.natsrl.sfim.SectionInfoDialog;
import evaluation.Interval;
import infra.Infra;
import infra.Period;
import infra.Section;
import infra.simobjects.RandomSeed;
import infra.simobjects.RandomSeeds;
import infra.simobjects.SimObjects;
import infra.simulation.SimInterval;
import infra.simulation.Simulation.ISimEndSignal;
import infra.simulation.SimulationConfig;
import infra.simulation.SimulationUtil;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import plugin.PluginFrame;
import util.FileHelper;
import util.PropertiesWrapper;
import vissimcom.ComError;
import vissimcom.VISSIMVersion;

/**
 *
 * @author Chongmyung Park
 */
public class VissimCalibration2GUI extends javax.swing.JPanel implements ISimEndSignal {
    //private TMO tmo = TMO.getInstance();
    private Vector<Section> sections = new Vector<Section>();
    private PropertiesWrapper prop;
    private PluginFrame simFrame;
//    private Simulation sim;
    private BasicSimulation sim;
    private Date startTime;
    private PrintStream backupOut;
    private PrintStream backupErr;
    private SimObjects simObjects;
    
    //modify for Random 02/22/2012
    private RandomSeeds randomseeds;
    private RandomSeed currentrseed = new RandomSeed(null);
    private RandomSeed SimRandom = RandomSeed.getInstance();
    private Infra infra = Infra.getInstance();
    
    /** Creates new form SimulationExampleGUI */
    public VissimCalibration2GUI(PluginFrame parent) {
        initComponents();
        this.simFrame = parent;
        this.loadSection();
        
        //VissimVersion
        for(VISSIMVersion v : VISSIMVersion.values()){
            this.cbxVissimVersion.addItem(v);
        }
        
        loadSimulationInterval();
        
        SimulationConfig.loadConfig();
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(false);
        this.tbxCaseFile.setText(SimulationConfig.CASE_FILE);
        this.tbxRandom.setText(df.format(SimulationConfig.RANDOM_SEED));
        /*this.tbxKjam.setText(df.format(MeteringConfig.Kjam));
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
        this.tbxMaxWaittingTimeF2F.setText(df.format(MeteringConfig.MAX_WAIT_TIME_MINUTE_F2F));        */
        
        //modify for Random 02/22/2012
        this.randomseeds = RandomSeeds.load(RandomSeeds.SAVE_PROP_NAME);
        this.ReflashGroupList();
        
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {}

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if(sim != null)
                    sim.simulationStop();
            }
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
    
    private void loadSimulationInterval() {
                for(Interval i : Interval.values()){
                        this.cbxSimulationInterval.addItem(i);
                }
                
                this.cbxSimulationInterval.setSelectedItem(Interval.get(SimulationConfig.RunningInterval));
        }
    
    private void runSimulation() {
        try {
            if(!this.simFrame.isVisible())
                this.dispose();
                        
            //simobjects 만들기
            simObjects = new SimObjects();
            //레절트에 넘기기
            
            this.cbxTimeInterval.setEnabled(false);
            this.btnRun.setEnabled(false);
            this.cbxRandomSeedList.setEnabled(false);
            this.chxRunwithsingleRandom.setEnabled(false);
            startTime = new Date();
            
            SimulationConfig.CASE_FILE = this.tbxCaseFile.getText();
            SimulationConfig.RANDOM_SEED = this.SimRandom.NextSeed();
            SimulationConfig.logtimeinterval = cbxTimeInterval.getSelectedIndex();
            //MeteringConfig.Kjam = Integer.parseInt(this.tbxKjam.getText());
            //MeteringConfig.Kc = Integer.parseInt(this.tbxKc.getText());
            //MeteringConfig.Kd_Rate = Double.parseDouble(this.tbxKd_Rate.getText());
            //MeteringConfig.Kb = Double.parseDouble(this.tbxKb.getText());            
            //MeteringConfig.Kstop = Double.parseDouble(this.tbxKstop.getText());            
            //MeteringConfig.stopDuration = Integer.parseInt(this.tbxStopDuration.getText());
            //M/eteringConfig.stopBSTrend = Integer.parseInt(this.tbxStopTrend.getText());
            //MeteringConfig.stopUpstreamCount = Integer.parseInt(this.tbxStopUpstreamCount.getText());
            //MeteringConfig.Ab = Integer.parseInt(this.tbxAb.getText());                                   
            //MeteringConfig.Kd = MeteringConfig.Kc * MeteringConfig.Kd_Rate;
            //MeteringConfig.setMaxWaitTimeF2F(Integer.parseInt(this.tbxMaxWaittingTimeF2F.getText()));
            //MeteringConfig.setMaxWaitTime(Integer.parseInt(this.tbxMaxWaittingTime.getText()));
            //MeteringConfig.MAX_RED_TIME = Integer.parseInt(this.tbxMaxRedTime.getText());
            SimulationConfig.UseMetering = false;
            SimulationConfig.saveConfig();
            
            Section section = (Section)this.cbxSections.getSelectedItem();
            Interval simIntv = (Interval)this.cbxSimulationInterval.getSelectedItem();
            SimInterval sintv = new SimInterval(section, Interval.getDefaultSimulationInterval(), simIntv);
//            Interval simRunningIntv = (Interval)this.cbxSimulationInterval.getSelectedItem();
//            this.simulationInterval.setSimulationRunningInterval(simRunningIntv);
            sim = new BasicSimulation(SimulationConfig.CASE_FILE, SimulationConfig.RANDOM_SEED, section, 
                    (VISSIMVersion)this.cbxVissimVersion.getSelectedItem(), sintv, simObjects);
            sim.setDebugInterval(SimulationConfig.getInterval());
            sim.setSignalListener(this);
            sim.start();
            
            this.cbxSections.setEnabled(false);
            this.btnBrowse.setEnabled(false);
            this.tbxCaseFile.setEditable(false);
            this.tbxRandom.setEditable(false);
            
            System.out.println("[ Simulation Configurations ]");        
            System.out.println("  - Case File = " + SimulationConfig.CASE_FILE);
            System.out.println("  - Random Seed = " + SimulationConfig.RANDOM_SEED);
            System.out.println("  - DataLog Time Interval = " + SimulationConfig.getInterval() + "sec");
            System.out.println("----------------------------------------------");
            
        } catch(Exception ex) {
            this.btnRun.setEnabled(true);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR : " + ex.getMessage());
        }
    }

    public void signalEnd(int code) {
        if(code == -1) {
            this.chkShowVehicles.setEnabled(true);
            this.chkShowVehicles.setSelected(false);            
            setVissimVisible(false);
            return;
        }
        int rinterval = 0;
        ComError ce = ComError.getErrorbyID(code);
        if(!ce.isCorrect()){
            JOptionPane.showMessageDialog(simFrame, ce.toString());
            this.restoreOutput();
            return;
        }
        
        int samples = sim.getSamples();
        rinterval = sim.getRunningInterval();
        if(samples < 5) {
            JOptionPane.showMessageDialog(simFrame, "Too short simulation");
            simFrame.afterSimulation(null, null);
//            this.simFrame.setVisible(false);            
        }

        int duration = samples * rinterval;        

        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.set(Calendar.SECOND, 0);
        Date sTime = c.getTime();
        c.add(Calendar.SECOND, duration);
        Date eTime = c.getTime();
        
        if(this.SimRandom.isSingle()){
            simFrame.afterSimulation((Section)this.cbxSections.getSelectedItem(), new Period(sTime, eTime, rinterval));
        }else{
            //아직 구현안함
            System.out.println("아직 싱글밖에 구현안함");
//            System.out.println("Save Simulation File("+SimulationConfig.RANDOM_SEED+")");
//            SimulationSeveralResult ssr = new SimulationSeveralResult((Section)this.cbxSections.getSelectedItem(), new Period(sTime, eTime, rinterval));
//            ssr.saveResult();
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Random Seed - " + SimulationConfig.RANDOM_SEED + "Simulation Complete.");
            System.out.println("------------------------------------------------------------------------");
        }
        
        if(!this.SimRandom.IsEndSeed()){
//            System.out.println();
//            SimObjects.getInstance().reset();
            runSimulation();
        }else{
            simFrame.afterSimulation((Section)this.cbxSections.getSelectedItem(), new Period(sTime, eTime, rinterval));
            System.out.println("Restore output redirection ... ");
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Simulation Complete.");
            System.out.println("------------------------------------------------------------------------");
            this.restoreOutput();
            SimulationUtil.SaveSimulation((Section)this.cbxSections.getSelectedItem(),new Period(sTime, eTime, rinterval),simFrame,simObjects);
        }
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
        this.cbxSections.removeAllItems();
        for (Section s : infra.getSections()) {
            this.cbxSections.addItem(s);
        }
        
//        SectionManager sm = tmo.getSectionManager();
//        this.sections.clear();
//        sm.loadSections();
//        if(sm.getSections() == null){
//            return;
//        }
//        this.sections.addAll(sm.getSections());
//
//        this.cbxSections.removeAllItems();
//
//        for (Section s : this.sections) {
//            this.cbxSections.addItem(s);
//        }
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
//    private void openSectionInfoDialog() {
//        Section section = (Section) this.cbxSections.getSelectedItem();
//        if (section == null) {
//            return;
//        }
//        SectionInfoDialog si = new SectionInfoDialog(section, null, true);
//        si.setLocationRelativeTo(this);
//        si.setVisible(true);
//    }       

    /**
     * Redirect output into log box
     */
    public void redirectOutput() {
//        backupOut = System.out;
//        backupErr = System.err;
        // redirect System.out and System.err to log textbox
        StringOutputStream sos = new StringOutputStream(this.tbxLog);
//        System.setOut(new PrintStream(sos));
//        System.setErr(new PrintStream(sos));
    }

    /**
     * Resotre output
     */
    public void restoreOutput() {
        if(backupOut != null){
            System.setOut(backupOut);
            System.setErr(backupErr);
        }
    }
    
    public void dispose(){
        System.out.println("Restore output redirection ... ");
        this.restoreOutput();
        System.out.println("final");
    }
    @Override
    public void finalize() throws Throwable{
        
        
        this.simFrame.finalize();
    }

    private void CleanSeedList(){
        this.currentrseed.Clear();
        this.txtGname.setText("");
        ReflashSeedList();
    }
    private void CleanGroupList(){
        ReflashGroupList();
    }
    private void ReflashSeedList(){
        ReflashList(this.lSeedList,this.currentrseed.getSeedList());
    }
    private void ReflashGroupList(){
        ReflashList(this.lGroupList,this.randomseeds.getRandomSeedNameList());
        ReflashComboBox(this.cbxRandomSeedList,this.randomseeds.getRandomSeedNameList());
    }
    private void ReflashList(JList _list, List<String> _data) {
        DefaultListModel dm = new DefaultListModel();
        for(String s : _data){
            dm.addElement(s);
        }
        _list.setModel(dm);
    }
    private void ReflashComboBox(JComboBox _box, List<String> _data){
        DefaultComboBoxModel dm = new DefaultComboBoxModel();
        dm.addElement("none");
        for(String s : _data){
            dm.addElement(s);
        }
        _box.setModel(dm);
    }

    private boolean runinitRandomSeed() {
        //initialize random seed
            if(this.SimRandom.isSingle()){
                try{
                this.SimRandom.Clear();
                this.SimRandom.AddSeed(Integer.parseInt(this.tbxRandom.getText()));
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(simFrame, "Please insert random seed");
//                    e.printStackTrace();
                    return false;
                }
            }else{
                if(this.cbxRandomSeedList.getSelectedIndex() == 0){
                    JOptionPane.showMessageDialog(simFrame, "Please select random seed list");
                    return false;
                }
                String rseedlist = this.cbxRandomSeedList.getSelectedItem().toString();
                this.SimRandom.Clear();
                this.SimRandom.setInstance(this.randomseeds.getRandomSeedInstance(rseedlist));
                this.SimRandom.MakeSimulationKey();
//                System.out.println(this.SimRandom.getName());
//                for(String s : this.SimRandom.getSeedList())
//                    System.out.println("Seed :" + s);
            }
            return true;

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
        jLabel1 = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        tbxCaseFile = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkShowVehicles = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        cbxTimeInterval = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cbxVissimVersion = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        cbxSimulationInterval = new javax.swing.JComboBox();
        btnRun = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cbxRandomSeedList = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        tbxRandom = new javax.swing.JTextField();
        chxRunwithsingleRandom = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtRnumber = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lSeedList = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        txtGname = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lGroupList = new javax.swing.JList();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
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

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel5.setText("Simulation DataLog Time Interval");

        cbxTimeInterval.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30 second", "1 min", "2 min", "3 min", "4 min", "5 min", "10 min", "15 min", "20 min", "30 min", "1 hour" }));

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel6.setText("VISSIM Version");

        cbxVissimVersion.setEditable(true);
        cbxVissimVersion.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jLabel48.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel48.setText("Simulation Interval");

        cbxSimulationInterval.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btnBrowse)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(tbxCaseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(cbxSections, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5)
                    .addComponent(cbxTimeInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(chkShowVehicles)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(cbxVissimVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48)
                            .addComponent(cbxSimulationInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSections, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(tbxCaseFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbxTimeInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkShowVehicles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxVissimVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxSimulationInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        btnRun.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        btnRun.setText("Run Simulation");
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Seed List"));

        cbxRandomSeedList.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        cbxRandomSeedList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none" }));
        cbxRandomSeedList.setPreferredSize(new java.awt.Dimension(150, 25));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(cbxRandomSeedList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(202, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(cbxRandomSeedList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Number"));

        tbxRandom.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tbxRandom.setText("13");
        tbxRandom.setEnabled(false);
        tbxRandom.setPreferredSize(new java.awt.Dimension(59, 25));

        chxRunwithsingleRandom.setText("Run with Single random seed");
        chxRunwithsingleRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chxRunwithsingleRandomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chxRunwithsingleRandom))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(tbxRandom, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(183, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(chxRunwithsingleRandom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbxRandom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Random Seed List", jPanel2);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Number"));

        txtRnumber.setMinimumSize(new java.awt.Dimension(6, 25));
        txtRnumber.setPreferredSize(new java.awt.Dimension(59, 25));
        txtRnumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRnumberKeyPressed(evt);
            }
        });

        lSeedList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lSeedListKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(lSeedList);

        jButton1.setText("▼");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("▲");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRnumber, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtRnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
        );

        jButton1.getAccessibleContext().setAccessibleName("");

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Group Name"));

        txtGname.setPreferredSize(new java.awt.Dimension(100, 25));

        jButton3.setText("Update →");
        jButton3.setPreferredSize(new java.awt.Dimension(73, 35));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("← Modified");
        jButton4.setPreferredSize(new java.awt.Dimension(73, 35));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("◀ Clear");
        jButton5.setPreferredSize(new java.awt.Dimension(73, 35));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
            .addComponent(txtGname, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(txtGname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Group List"));

        lGroupList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lGroupListKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(lGroupList);

        jButton6.setText("▲Delete Item");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("CLEAR");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(53, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Manager", jPanel7);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel3);

        tbxLog.setEditable(false);
        tbxLog.setColumns(20);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnClearLog, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                        .addComponent(btnSaveLog, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jSplitPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        if(JOptionPane.showConfirmDialog(simFrame, "Do you want to clear group list?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }
        this.randomseeds.clearGroup();
        this.ReflashGroupList();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if(JOptionPane.showConfirmDialog(simFrame, "Do you want to delete the selected group?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }
        try{
            RandomSeed gseed = this.randomseeds.getRandomSeedInstance(this.lGroupList.getSelectedValue().toString());
            if(gseed != null)
            this.randomseeds.RemoveRandomSeed(gseed);
            else
                System.out.println("null!!!");

            this.ReflashGroupList();
        }catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void lGroupListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lGroupListKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            jButton6ActionPerformed(null);
        }
    }//GEN-LAST:event_lGroupListKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        CleanSeedList();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        try{
            RandomSeed gseed = this.randomseeds.getRandomSeedInstance(this.lGroupList.getSelectedValue().toString());
            if(gseed != null){
                this.currentrseed.Clear();
                this.currentrseed.setInstance(gseed);
                this.txtGname.setText(currentrseed.getName());
            }
            else
            System.out.println("null!!!");
            this.ReflashSeedList();
        }catch(Exception e){
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(this.txtGname.getText().equals("")){
            JOptionPane.showMessageDialog(simFrame, "Set Group Name");
            return;
        }

        //add Name
        this.currentrseed.setName(this.txtGname.getText());

        if(this.currentrseed.IsEmpty()){
            JOptionPane.showMessageDialog(simFrame, "Add Seed List");
            return;
        }
        if(this.randomseeds.IsRandomSeed(this.txtGname.getText())){
            if(JOptionPane.showConfirmDialog(simFrame, "Do you want to rewrite this group(\""+this.txtGname.getText()+"\")?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }

        this.randomseeds.UpdateRandomSeed(currentrseed);

        this.CleanSeedList();
        this.ReflashGroupList();
        this.txtGname.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try{
            this.currentrseed.RemoveSeed(this.lSeedList.getSelectedIndex());
            ReflashSeedList();
        }catch(Exception e){
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int rseed = Integer.parseInt(this.txtRnumber.getText());
        if(rseed > 9999){
            JOptionPane.showMessageDialog(simFrame, "Random seed should be less than 9999");
            return;
        }
        this.currentrseed.AddSeed(rseed);
        ReflashSeedList();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lSeedListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lSeedListKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_DELETE){
            jButton2ActionPerformed(null);
        }
    }//GEN-LAST:event_lSeedListKeyPressed

    private void txtRnumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRnumberKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            jButton1ActionPerformed(null);
            this.txtRnumber.setText("");
        }
    }//GEN-LAST:event_txtRnumberKeyPressed

    private void chxRunwithsingleRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chxRunwithsingleRandomActionPerformed
        // TODO add your handling code here:
        if(this.chxRunwithsingleRandom.isSelected()){
            this.cbxRandomSeedList.setEnabled(false);
            this.tbxRandom.setEnabled(true);
            this.SimRandom.setSingle(true);
        }else{
            this.cbxRandomSeedList.setEnabled(true);
            this.tbxRandom.setEnabled(false);
            this.SimRandom.setSingle(false);
        }
    }//GEN-LAST:event_chxRunwithsingleRandomActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        if(this.cbxRandomSeedList.getSelectedIndex() == 0 && !this.chxRunwithsingleRandom.isSelected()){
            JOptionPane.showMessageDialog(this, "Select Random option");
            return;
        }
        this.redirectOutput();

        if(runinitRandomSeed()){
            System.out.println("[ Simulation ]");
            System.out.println("  - Selected Random Seed List = " + this.cbxRandomSeedList.getSelectedItem().toString());
            System.out.print("  - Random Seed List = ");
            for(String value : SimRandom.getSeedList())
            System.out.print(value+" ");
            System.out.println();
            System.out.println("  - DataLog Time Interval = " + cbxTimeInterval.getSelectedItem().toString());
            System.out.println("----------------------------------------------");
            runSimulation();
        }
    }//GEN-LAST:event_btnRunActionPerformed

    private void chkShowVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowVehiclesActionPerformed
        setVissimVisible(this.chkShowVehicles.isSelected());
    }//GEN-LAST:event_chkShowVehiclesActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        String path = ".";
        String prevPath = this.tbxCaseFile.getText();
        if(!prevPath.isEmpty()) path = new File(prevPath).getAbsolutePath();
        String caseFile = FileHelper.chooseFileToOpen(prevPath, "Select VISSIM Case File", FileHelper.FileFilterForVISSIM);
        if(caseFile != null) this.tbxCaseFile.setText(caseFile);
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void cbxSectionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxSectionsActionPerformed

    }//GEN-LAST:event_cbxSectionsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSaveLog;
    private javax.swing.JComboBox cbxRandomSeedList;
    private javax.swing.JComboBox cbxSections;
    private javax.swing.JComboBox cbxSimulationInterval;
    private javax.swing.JComboBox cbxTimeInterval;
    private javax.swing.JComboBox cbxVissimVersion;
    private javax.swing.JCheckBox chkShowVehicles;
    private javax.swing.JCheckBox chxRunwithsingleRandom;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList lGroupList;
    private javax.swing.JList lSeedList;
    private javax.swing.JTextField tbxCaseFile;
    private javax.swing.JTextArea tbxLog;
    private javax.swing.JTextField tbxRandom;
    private javax.swing.JTextField txtGname;
    private javax.swing.JTextField txtRnumber;
    // End of variables declaration//GEN-END:variables


}
