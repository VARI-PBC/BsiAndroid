package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class ReqTasksQueryAsync extends AsyncTask<Map, Integer, List<ReqTaskItem>> {
    Exception ex;

    static final String REQUISITION_ID = "req_tasks.requisition_id";
    static final String TASK_ID = "req_tasks.task_id";
    static final String TASK_TYPE = "req_tasks.req_task_type";
    static final String TASK_NAME = "req_tasks.name";
    static final String REQ_INSTRUCTIONS = "requisition.instructions";
    static final String TASK_INSTRUCTIONS = "req_tasks.instructions";
    static final String NOTES = "requisition.notes";
    static final String END_TIME = "req_tasks.end_time";
    static final String COMPLETED_BY = "+req_tasks.completed_by";
    static final String BSI_ID = "req_vial_tasks.bsi_id";

    @Override
    protected List<ReqTaskItem> doInBackground(Map... criteria) {

        try {
           return getTasks(criteria);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        }
        return null;
    }

    private List<ReqTaskItem> getTasks(Map[] criteria) throws XMLRPCException {
        List<ReqTaskItem> tasks = new ArrayList<>();
        ArrayList<String> display = new ArrayList<>(Arrays.asList(REQUISITION_ID, TASK_ID, TASK_NAME, TASK_TYPE, REQ_INSTRUCTIONS, TASK_INSTRUCTIONS, END_TIME, COMPLETED_BY, NOTES));

        XMLRPCClient client = BsiConnector.getInstance().Client;
        String sessionID = BsiConnector.getInstance().SessionID;
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, new String[0], 0, BsiConnector.USER_DEFINED_FREQUENCY);
        Object[] rows = (Object[]) result.get("rows");
        for (Object row : rows) {
            Object[] values = (Object[]) row;
            String requisitionId = (String) values[display.indexOf(REQUISITION_ID)];
            String taskId = (String) values[display.indexOf(TASK_ID)];

            ReqTaskItem task = new ReqTaskItem();
            tasks.add(task);

            task.RequisitionId = requisitionId;
            task.TaskId = taskId;
            task.ReqInstructions = (String) values[display.indexOf(REQ_INSTRUCTIONS)];
            task.TaskInstructions = (String) values[display.indexOf(TASK_INSTRUCTIONS)];
            task.Notes = (String) values[display.indexOf(NOTES)];
            task.TaskEndTime = (String) values[display.indexOf(END_TIME)];
            task.Technician = (String) values[display.indexOf(COMPLETED_BY)];
            task.TaskType = (String) values[display.indexOf(TASK_TYPE)];
            task.VialCount = (String)values[values.length-1];
        }
        return tasks;
    }
}
