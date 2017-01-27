package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.serializer.StringSerializer;

class ReqTasksAsync extends AsyncTask<String, Integer, Map<String, ReqTaskItem>> {

    XMLRPCClient client;
    String sessionId;
    Exception ex;

    static final String REQUISITION_ID = "req_tasks.requisition_id";
    static final String TASK_ID = "req_tasks.task_id";
    static final String TASK_TYPE = "req_tasks.req_task_type";
    static final String TASK_NAME = "req_tasks.name";
    static final String INSTRUCTIONS = "requisition.instructions";
    static final String NOTES = "requisition.notes";
    static final String NUM_VIALS = "requisition.num_vials";
    static final String END_TIME = "req_tasks.end_time";
    static final String COMPLETED_BY = "req_tasks.completed_by";
    static final String CURRENT_LABEL = "vial.current_label";
    static final String WORKING_ID = "vial.field_347";
    static final String COMMENTS = "vial.field_188";
    static final String FREEZER = "location.freezer";
    static final String RACK = "location.rack";
    static final String BOX = "location.box";
    static final String ROW_FORMAT = "location.row_format";
    static final String COLUMN_FORMAT = "location.column_format";
    static final String CONTAINER_TYPE = "location.container_type";
    static final String ROW = "vial_location.row";
    static final String COL = "vial_location.col";
    static final String LOCATION_ID = "vial_location.location_id";

    @Override
    protected Map<String, ReqTaskItem> doInBackground(String... params) {
        final String dateCutoff = params[0];
        final String taskType = params[1];

        client = BsiConnector.getInstance().Client;
        sessionId = BsiConnector.getInstance().SessionID;
        Map<String, ReqTaskItem> tasks = null;
        try {
            // Combine the results of 2 separate queries due to limitations in the BSI query API.
            // Base the return value on the vial results since the vial query is more selective.
            tasks = getVialDetails(taskType, dateCutoff);
            addTasksDetails(tasks, taskType, dateCutoff);
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
            return tasks;
        }
    }

    private void addTasksDetails(Map<String, ReqTaskItem> tasks, final String taskType, final String dateCutoff) throws XMLRPCException {

        Object[] criteria = new Object[2];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", taskType);
            put("operator", "equals");
            put("field", TASK_TYPE);
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", dateCutoff);
            put("operator", "greater or equals");
            put("field", END_TIME);
        }};
        ArrayList<String> display = new ArrayList<>(Arrays.asList(REQUISITION_ID, TASK_ID, TASK_NAME, INSTRUCTIONS, END_TIME, "+" + COMPLETED_BY, NUM_VIALS, NOTES));
        String[] sort = new String[]{"-" + END_TIME, REQUISITION_ID};

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionId, criteria, display, sort, 0, 1);
        Object[] rows = (Object[]) result.get("rows");
        for (Object row : rows) {
            Object[] values = (Object[]) row;
            String requisitionId = (String) values[display.indexOf(REQUISITION_ID)];
            String taskId = (String) values[display.indexOf(TASK_ID)];
            ReqTaskItem task = tasks.get(requisitionId + "|" + taskId);
            if (task == null) continue;
            task.RequisitionId = requisitionId;
            task.TaskName = taskId;
            task.Instructions = (String) values[display.indexOf(INSTRUCTIONS)];
            task.Notes = (String) values[display.indexOf(NOTES)];
            task.NumVials = Integer.parseInt((String) values[display.indexOf(NUM_VIALS)]);
            task.TaskEndTime = (String) values[display.indexOf(END_TIME)];
            task.Technician = (String) values[display.indexOf("+" + COMPLETED_BY)];
            task.TaskType = taskType;
        }
    }

    private Map<String, ReqTaskItem> getVialDetails(final String taskType, final String dateCutoff) throws XMLRPCException {

        Map<String, ReqTaskItem> results = new HashMap<>();
        Object[] criteria = new Object[3];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", taskType);
            put("operator", "equals");
            put("field", TASK_TYPE);
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", dateCutoff);
            put("operator", "greater or equals");
            put("field", END_TIME);
        }};
        criteria[2] = new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", LOCATION_ID);
        }};
        ArrayList<String> display = new ArrayList<>(Arrays.asList(REQUISITION_ID,TASK_ID,CURRENT_LABEL,WORKING_ID,COMMENTS,FREEZER,RACK,BOX,ROW_FORMAT,COLUMN_FORMAT,CONTAINER_TYPE,COL,ROW,LOCATION_ID));
        String[] sort = new String[] {FREEZER,RACK,BOX,ROW,COL};


        XMLRPCClient client = BsiConnector.getInstance().Client;
        String sessionID = BsiConnector.getInstance().SessionID;
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display.toArray(), sort, 0, 1);
        Object[] rows = (Object[])result.get("rows");
        for (Object row : rows) {
            Object[] values = (Object[])row;
            String taskKey = values[display.indexOf(REQUISITION_ID)] + "|" + values[display.indexOf(TASK_ID)];
            ReqTaskItem task = results.get(taskKey);
            if (task == null) {
                task = new ReqTaskItem();
                task.Boxes = new SparseArray<>();
            }
            int locationId = Integer.parseInt((String)values[display.indexOf(LOCATION_ID)]);
            ReqTaskItem.Box box = task.Boxes.get(locationId);
            if (box == null) {
                box = new ReqTaskItem.Box();
                box.Freezer = (String)values[display.indexOf(FREEZER)];
                box.Rack = (String)values[display.indexOf(RACK)];
                box.BoxLabel = (String)values[display.indexOf(BOX)];
                box.RowFormat = Integer.parseInt((String)values[display.indexOf(ROW_FORMAT)]);
                box.ColumnFormat = Integer.parseInt((String)values[display.indexOf(COLUMN_FORMAT)]);
                box.ContainerType = Integer.parseInt((String)values[display.indexOf(CONTAINER_TYPE)]);
                box.Vials = new ArrayMap<>();
                task.Boxes.put(locationId, box);
            }
            ReqTaskItem.Vial vial = new ReqTaskItem.Vial();
            vial.current_label = (String)values[display.indexOf(CURRENT_LABEL)];
            vial.workingId = (String)values[display.indexOf(WORKING_ID)];
            vial.comments = (String)values[display.indexOf(COMMENTS)];
            vial.column = (String)values[display.indexOf(COL)];
            vial.row = (String)values[display.indexOf(ROW)];
            int numRows = BsiConnector.getContainerType(box.ContainerType).NumRows;
            String key =  numRows == 1 ? vial.column : vial.row + "-" + vial.column;
            box.Vials.put(key, vial);
            results.put(taskKey, task);
        }
        return results;
    }
}
