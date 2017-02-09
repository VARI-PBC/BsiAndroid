package org.vai.vari.bsiandroid;

import java.io.Serializable;
import java.util.Map;

class ReqTaskItem implements Serializable {
    String RequisitionId;
    String TaskId;
    String ReqInstructions;
    String TaskInstructions;
    String Notes;
    String TaskEndTime;
    String Technician;
    String TaskType;
    String VialCount;
    Map<String, Box> Boxes;
}

class Box implements Serializable {
    String Freezer;
    String Rack;
    String Box;
    String ContainerLabel;
    String Workbench;
    int RowFormat;
    int ColumnFormat;
    BsiConnector.ContainerType ContainerType;
    Map<String, Vial> Vials;

    static class Vial implements Serializable {
        String currentLabel;
        String bsiId;
        String workingId;
        String vialType;
        String comments;
        String row;
        String column;
    }
}