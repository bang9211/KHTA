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

//import edu.umn.natsrl.infra.Section;
//import edu.umn.natsrl.infra.infraobjects.Station;
import infra.Section;
import infra.infraobject.Station;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GradientPaint;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.contour.NonGridContourDataset;

/**
 *
 * @author Chongmyung Park
 */
public final class ContourPlotter {

    private ValueAxis xAxis = null;
    private NumberAxis yAxis = null;
    private ColorBar zColorBar = null;
    private final int numX = 40;
    private final int numY = 40;
    private final double ratio = 1;
    private final int power = 8;
    private String[] streetNames = new String[numY];
    private String[] plotTimes = new String[20];
    private Section section;
    private Evaluation eval;
    private ArrayList<Double> xIndex = new ArrayList();
    private ArrayList<Double> yIndex = new ArrayList();
    private ArrayList<Double> zValue = new ArrayList();
    private JFreeChart chart;
    private float[] cv;
    private int ncv = 0;
    private Color[] colors;
    private String unit;
    private String outputPath;

    public ContourPlotter(Section section, ContourSetting setting, Evaluation evaluation, String outputPath) {

        this.section = section;
        this.eval = evaluation;
        this.outputPath = outputPath;

        setStreetNames();
        setTimes();
        setData();

        colors = setting.getContourColors();
        ncv = setting.getContourStep();
        cv = setting.getContourStepValues();
        unit = setting.getContourUnit();

        createContourPlot();

    }

    public void saveImage(boolean viewImage) {
        if (!createContourPlot()) {
            return;
        }

        try {
            File img = new File(getFileName(null, "jpg"));
            ChartUtilities.saveChartAsJPEG(img, chart, 1000, 800);
            try {
                if (viewImage) {
                    Desktop.getDesktop().open(img);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * modifiy soobin Jeon 02/27/2012
     * add saveImage function with f_name
     */
    public void saveImage(boolean viewImage,String f_name) {
        if (!createContourPlot()) {
            return;
        }

        try {
            File img = new File(f_name);
            ChartUtilities.saveChartAsJPEG(img, chart, 1000, 800);
            try {
                if (viewImage) {
//                    Desktop.getDesktop().open(img);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean createContourPlot() {

        if (chart != null) {
            return true;
        }

        if (this.cv == null || this.ncv == 0 || this.colors == null) {
            return false;
        }

        String date = null;
        Vector<EvaluationResult> results = (Vector<EvaluationResult>) this.eval.getResult().clone();

        if (results.size() > 2) {
            date = "  [average of " + (results.size() - 1) + " days]";
        } else {
            date = "  [" + results.get(0).getName() + "]";
        }

        final String title = section.getName() + date;
        final String xAxisLabel = "";
        final String yAxisLabel = "";
        final String zAxisLabel = "";

        this.xAxis = new NumberAxis(xAxisLabel);
        this.yAxis = new NumberAxis(yAxisLabel);
        this.zColorBar = new ColorBar(zAxisLabel);

        if (this.xAxis instanceof NumberAxis) {
            ((NumberAxis) this.xAxis).setAutoRangeIncludesZero(false);
        }

        this.yAxis.setAutoRangeIncludesZero(false);

        this.yAxis.setStreetName(this.streetNames);
        ((NumberAxis) this.xAxis).setTime(this.plotTimes);
        ((NumberAxis) this.zColorBar.getAxis()).setColor(cv, unit);
        ((NumberAxis) this.xAxis).setLowerMargin(0.0);
        ((NumberAxis) this.xAxis).setUpperMargin(0.0);


        this.yAxis.setLowerMargin(0.0);
        this.yAxis.setUpperMargin(0.0);
        this.xAxis.setRange(0, numX - 1);
        this.yAxis.setRange(0, numY - 1);
        this.zColorBar.getAxis().setTickMarksVisible(true);


        final ContourDataset data = createDataset();
//        for(Number n : data.getZValues()) {
//            System.out.println("Data=" + n);
//        }
        final ContourPlot plot = new ContourPlot(data, this.xAxis, this.yAxis, this.zColorBar, ncv);

        plot.SetContourValue(cv, ncv, colors, numX, numY);
        plot.setDataAreaRatio(ratio);


        chart = new JFreeChart(title, null, plot, false);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.white));

        return true;
    }

    private ContourDataset createDataset() {
//        final Double[] oDoubleX = this.xIndex.toArray(new Double[this.xIndex.size()]);
//        final Double[] oDoubleY = this.yIndex.toArray(new Double[this.yIndex.size()]);
//        final Double[] oDoubleZ = this.zValue.toArray(new Double[this.zValue.size()]);

        Double[] oDoubleX = new Double[xIndex.size()];
        Double[] oDoubleY = new Double[yIndex.size()];
        Double[] oDoubleZ = new Double[zValue.size()];

        for (int i = 0; i < oDoubleX.length; i++) {
            oDoubleX[i] = xIndex.get(i);
            oDoubleY[i] = yIndex.get(i);
            oDoubleZ[i] = zValue.get(i);
        }

//        for(int i=0; i<oDoubleX.length; i++) {
//         double x= oDoubleX[i];
//         double y= oDoubleY[i];
//         double z= oDoubleZ[i];
//            System.out.println("x="+x+", y="+y+", z="+z);
//        }
        ContourDataset data = null;
//        System.out.println("size=("+oDoubleX.length+", "+oDoubleY.length+", "+oDoubleZ.length+")");
        data = new NonGridContourDataset("Contouring", oDoubleX, oDoubleY, oDoubleZ, numY, numX, power);

//        int idx = 1;
//        for(Number n : data.getZValues()) {
//            System.out.println("["+idx+"] Data=" + n.doubleValue());
//            idx++;
//        }

//        System.out.println("Z min="+data.getMinZValue()+", max="+data.getMaxZValue());        
        return data;
    }

    private void setStreetNames() {

        Arrays.fill(streetNames, "");

        int Dis = 0;
        String[] stationNames = this.getStationNames();
        double[] stDis = this.getDistances();
        double totalDistance = stDis[stDis.length - 1];
        for (int i = 0; i < stDis.length; i++) {
            Dis = (int) (stDis[i] / totalDistance * (numY - 1));
            if (streetNames[Dis].equals("")) {
                streetNames[Dis] = stationNames[i];
            }
        }
    }

    private void setTimes() {

        Arrays.fill(plotTimes, "");
        String[] times = this.getTimes();
        int timeCount = times.length;

        int num = 0;
        for (int i = 0; i < 20; i++) {
            if (times.length <= 20) {
                if (i % 4 == 0) {
                    this.plotTimes[i] = times[num];
                }
            } else {
                num = (int) (i * (timeCount / 20.0d));
                this.plotTimes[i] = times[num].substring(0, 5);
            }
        }
    }

    private void setData() {
        // it should be average or a day data
        // see Evaluation.java
        Vector<EvaluationResult> results = (Vector<EvaluationResult>) this.eval.getResult().clone();
        EvaluationResult result = results.get(0);
        result.setUseAccumulatedDistance(true);
        result.setUseConfidence(true);

        // x => index of time line
        // y => index of stations with virtual stations
        // z => value
        int colDataStartPoint = result.COL_DATA_START();
        int rowDataStartPoint = result.ROW_DATA_START();

        int xSize = result.getRowSize(1) - rowDataStartPoint;
        int ySize = result.getColumnSize() - colDataStartPoint;

        // for all data according to time series
        for (int x = rowDataStartPoint; x < xSize; x++) {
            // for all station data
            for (int y = colDataStartPoint; y < ySize; y++) {
                //System.out.println("x="+x+", y="+y+", z="+Double.parseDouble(result.get(y+colDataStartPoint, x+rowDataStartPoint).toString()));
                xIndex.add((double) x);
                yIndex.add((double) y);
                zValue.add(Double.parseDouble(result.get(y + colDataStartPoint, x + rowDataStartPoint).toString()));
            }
        }
    }

    private String[] getStationNames() {
        Station[] stations = section.getStations();
        String[] names = new String[stations.length];
        for (int i = 0; i < stations.length; i++) {
            names[i] = stations[i].toString();
            System.out.println("=> " + names[i]);
        }
        return names;
    }

    private double[] getDistances() {
        Station[] stations = section.getStations();
        double[] dis = new double[stations.length];
        double prevDis = 0;
        dis[0] = 0;
        for (int i = 1; i < stations.length; i++) {
            dis[i] = (EvalHelper.getDistance(this.section.getName(), stations[i - 1])*0.62) * 10 + prevDis;
            prevDis = dis[i];
        }
        return dis;
    }

    private String[] getTimes() {
        Vector<EvaluationResult> results = (Vector<EvaluationResult>) this.eval.getResult().clone();
        ArrayList<String> times = results.get(0).getColumn(0);

        // remove not time format (hh:mm:ss)
        for (int i = 0; i < times.size(); i++) {
            if (!times.get(i).matches("[0-9]+:[0-9]+:[0-9]+")) {
                times.remove(i--);
            }
        }

        return times.toArray(new String[times.size()]);
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public void setSteps(int steps) {
        this.ncv = steps;
    }

    public void setStepValues(float[] values) {
        this.cv = values;
    }

    private String getFileName(String name, String ext) {
        String nameOption = (name == null ? "" : "_" + name);
        String filepath = this.outputPath + File.separator + this.eval.getName() + nameOption + "." + ext;
        int count = 0;
        while (true) {
            File file = new File(filepath);
            if (!file.exists()) {
                break;
            }
            filepath = this.outputPath + File.separator + this.eval.getName() + nameOption + " (" + (++count) + ")" + "." + ext;
        }

        return filepath;
    }
}
