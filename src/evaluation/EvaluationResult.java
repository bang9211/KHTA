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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class EvaluationResult implements Serializable {

    private String name;
    private boolean useConfidence = false;
    private boolean useAccumulatedDistance = false;
    private boolean useVirtualStation = false;
    private boolean useTotalColumn = false;
    private boolean isStatistic = false;
    private int setDataStartCol = -1;
    private int setDataStartRow = -1;
    /*
     * Result Data Structure Description
     * 
     *                             col 0     |  col 1           |   col 2
     *                             timeline  |  data start col  |   
     * ------------------------------------------------------------------------
     * row 0 (station name)    |             |  S1              |    S2  ...
     * row 1 (confidence)      | Confidence  |                  |
     * row 2 (distance)        |   Distance  |                  |
     * row 3 (data start row)  |   07:00:30  |                  |
     *                         |   07:01:00  |                  |
     *                         |   07:01:30  |                  |
     *                         |   07:00:00  |                  |
     *                         |    ....     |                  |
     * 
     * confidence and distance row should be made by calling
     *      Evaluation.addConfidenceToResult()
     *      Evaluateion.addDistanceToResult()
     * 
     * And if you call Evaluation.addVirtualStationToResult(),
     * data structure is changed like this :
     * ------------------------------------------------------------------------
     *             |  S1  |  -  |  -  |  -  |  S2 |  -  |  -  | S3  | ...
     * Confidence  |      |     |     |     |     |     |     |     |
     *   Distance  |      |     |     |     |     |     |     |     |
     *   07:00:30  |      |     |     |     |     |     |     |     |
     *   07:01:00  |      |     |     |     |     |     |     |     |
     *   07:01:30  |      |     |     |     |     |     |     |     |
     *   07:00:00  |      |     |     |     |     |     |     |     |
     *       ....  |      |     |     |     |     |     |     |     |
     * ------------------------------------------------------------------------
     * 
     */
    private ArrayList<ArrayList> data;

    public EvaluationResult() {
        data = new ArrayList<ArrayList>();
    }
    
    public EvaluationResult(ArrayList<ArrayList> data) {
        this.data = (ArrayList<ArrayList>) data.clone();
    }    

    static public EvaluationResult copy(EvaluationResult original) {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            output = new ObjectOutputStream(byteOutput);
            output.writeObject(original);
            output.flush();
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(byteOutput.toByteArray());
            input = new ObjectInputStream(bin);

            return (EvaluationResult) input.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }
    
    public int ROW_TITLE() {
        return 0;
    }

    public int ROW_CONFIDENCE() {
        return 1;
    }

    public int ROW_DISTANCE() {
        return (this.useConfidence ? 2 : 1);
    }

    public int ROW_DATA_START() {
        if (this.setDataStartRow > 0) {
            return this.setDataStartRow;
        }
        int dsr = 1;
        if (this.useConfidence) {
            dsr++;
        }
        if (this.useAccumulatedDistance) {
            dsr++;
        }
        return dsr;
    }

    public int COL_TIMELINE() {
        return 0;
    }

    public int COL_DATA_START() {
        if (this.setDataStartCol > 0) {
            return this.setDataStartCol;
        }
        return 1;
    }

    public void add(int col, Object o) {
        ensureCapacity(col);
        while (col >= getColumnSize()) {
            this.data.add(new ArrayList());
        }
        this.data.get(col).add(o);
    }

    public void addAll(int col, Object[] o) {
        ensureCapacity(col);
        while (col >= getColumnSize()) {
            this.data.add(new ArrayList());
        }
        this.data.get(col).addAll(Arrays.asList(o));
    }

    public void addAll(int col, ArrayList o) {
        ensureCapacity(col);
        while (col >= getColumnSize()) {
            this.data.add(new ArrayList());
        }
        this.data.get(col).addAll(o);
    }

    public void addColumn(ArrayList column) {
        addAll(this.data.size(), column);
    }

    public void insert(int col, int row, Object o) {
        this.data.get(col).add(row, o);
    }

    public void insertAll(int col, int row, Object[] os) {
        for (Object o : os) {
            this.data.get(col).add(row++, o);
        }
    }

    public void insertAll(int col, int row, ArrayList os) {
        for (Object o : os) {
            this.data.get(col).add(row++, o);
        }
    }

    public void set(int col, int row, Object data) {
        //this.ensureCapacity(col, row);        
        this.data.get(col).set(row, data);
    }

    public Object get(int col, int row) {
        return data.get(col).get(row);
    }

    public void remove(int col, int row) {
        data.get(col).remove(row);
    }

    public ArrayList getColumn(int col) {
        this.ensureCapacity(col);
        return data.get(col);
    }

    public ArrayList getRow(int row) {
        ArrayList array = new ArrayList();
        for (ArrayList col : data) {
            if (col.size() <= row) {
                array.add(null);
            } else {
                array.add(col.get(row));
            }
        }
        return array;
    }

    public boolean contains(Object data) {
        for (int i = 0; i < this.data.size(); i++) {
            if (this.data.get(i).contains(data)) {
                return true;
            }
        }
        return false;
    }

    public int getColumnSize() {
        return data.size();
    }

    public int getRowSize(int col) {
        return data.get(col).size();
    }

    public ArrayList<ArrayList> getTransposedData() {
        int rows = data.get(0).size();
        ArrayList<ArrayList> array = new ArrayList<ArrayList>();
        for (int r = 0; r < rows; ++r) {
            ArrayList column = new ArrayList();
            column.addAll(this.getRow(r));
            array.add(column);
        }
        return array;
    }

    /**
     * ensures a minimum capacity of num cols. Note that this does not guarantee
     * that there are that many cols.
     * 
     * @param num
     */
    private void ensureCapacity(int num) {
        data.ensureCapacity(num);
    }

    /**
     * Ensures that the given col has at least the given capacity. Note that
     * this method will also ensure that getNumcols() >= col
     * 
     * @param col
     * @param num
     */
    private void ensureCapacity(int col, int num) {
        ensureCapacity(col);
        while (col < getColumnSize()) {
            data.add(new ArrayList());
        }
        data.get(col).ensureCapacity(num);
    }

    public ArrayList<ArrayList> getData() {
        return this.data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean useAccumulatedDistance() {
        return useAccumulatedDistance;
    }

    public void setUseAccumulatedDistance(boolean useAccumulatedDistance) {
        this.useAccumulatedDistance = useAccumulatedDistance;
    }

    public boolean useConfidence() {
        return useConfidence;
    }

    public void setUseConfidence(boolean useConfidence) {
        this.useConfidence = useConfidence;
    }

    public boolean useVirtualStation() {
        return useVirtualStation;
    }

    public void setUseVirtualStation(boolean useVirtualStation) {
        this.useVirtualStation = useVirtualStation;
    }

    public void setData(ArrayList<ArrayList> data) {
        this.data = data;
    }

    public boolean useTotalColumn() {
        return useTotalColumn;
    }

    public void setUseTotalColumn(boolean useTotalColumn) {
        this.useTotalColumn = useTotalColumn;
    }

    public void setDataStartCol(int setDataStartCol) {
        this.setDataStartCol = setDataStartCol;
    }

    public void setDataStartRow(int setDataStartRow) {
        this.setDataStartRow = setDataStartRow;
    }

    public boolean isStatistic() {
        return isStatistic;
    }

    public void setIsStatistic(boolean isStatistic) {
        this.isStatistic = isStatistic;
    }

}
