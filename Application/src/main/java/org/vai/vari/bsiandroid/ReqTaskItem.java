package org.vai.vari.bsiandroid;

import android.support.v4.util.ArrayMap;
import java.io.Serializable;
import java.util.List;

class ReqTaskItem implements Serializable {
    String RequisitionId;
    String TaskName;
    String TemplateLabel;
    String TaskEndTime;
    String Technician;
    String TaskType;

    List<Box> Boxes;

    static class Box implements Serializable {
        String Freezer;
        String Rack;
        String BoxLabel;
        int NumRows;
        int NumColumns;
        int RowFormat;
        int ColumnFormat;

        ArrayMap<String, Vial> Vials;
    }

    static class Vial implements Serializable {
        String current_label;
        String workingId;
        String comments;
        String row;
        String column;
        String date_completed;
    }

}