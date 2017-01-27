package org.vai.vari.bsiandroid;

import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

class ReqTaskItem implements Serializable {
    String RequisitionId;
    String TaskName;
    String Instructions;
    String Notes;
    int NumVials;
    String TaskEndTime;
    String Technician;
    String TaskType;

    SparseArray<Box> Boxes;

    static class Box implements Serializable {
        String Freezer;
        String Rack;
        String BoxLabel;
        int RowFormat;
        int ColumnFormat;
        int ContainerType;

        ArrayMap<String, Vial> Vials;
    }

    static class Vial implements Serializable {
        String current_label;
        String workingId;
        String comments;
        String row;
        String column;
    }

}