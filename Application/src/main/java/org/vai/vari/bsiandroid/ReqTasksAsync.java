package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class ReqTasksAsync extends AsyncTask<String, Integer, List<ReqTaskItem>> {

    Exception ex;

    @Override
    protected List<ReqTaskItem> doInBackground(String... params) {
        final String dateCutoff = params[0];
        final String taskType = params[1];
        Object[] criteria = new Object[3];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", taskType);
            put("operator", "equals");
            put("field", "req_tasks.req_task_type");
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", "req_tasks.end_time");
        }};
        criteria[2] = new HashMap<String, Object>() {{
            put("value", "2");
            put("operator", "equals");
            put("field", "req_repository.req_status");
        }};
        String[] display = new String[] {"req_tasks.requisition_id", "req_tasks.name", "req_task_template.label", "req_tasks.end_time", "+req_tasks.completed_by", "req_tasks.req_task_type"};
        String[] sort = new String[] {"-req_tasks.end_time", "req_tasks.requisition_id"};

        ArrayList<ReqTaskItem> tasks = new ArrayList<>();
        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, sort, 0, 1);
            Object[] rows = (Object[])result.get("rows");
            for (Object row : rows) {
                Object[] values = (Object[])row;
                ReqTaskItem item = new ReqTaskItem();
                item.RequisitionId = (String)values[0];
                item.TaskName = (String)values[1];
                //if (!hasLocations(item.RequisitionId, item.TaskName)) continue;
                item.TemplateLabel = (String)values[2];
                item.TaskEndTime = (String)values[3];
                item.Technician = (String)values[4];
                item.TaskType = (String)values[5];
                tasks.add(item);
            }
        } catch (XMLRPCException e) {
            e.printStackTrace();
            ex = e;
        }
        return tasks;
    }

    private boolean hasLocations(final String requisitionId, final String taskName) throws XMLRPCException {
        Object[] criteria = new Object[5];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", requisitionId);
            put("operator", "equals");
            put("field", "req_vial_tasks.requisition_id");
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", taskName);
            put("operator", "equals");
            put("field", "req_tasks.name");
        }};
        criteria[2] = new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", "location.freezer");
        }};
        criteria[3] = new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", "location.rack");
        }};
        criteria[4] = new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", "location.box");
        }};
        String[] display = new String[] {"vial.current_label"};

        XMLRPCClient client = BsiConnector.getInstance().Client;
        String sessionID = BsiConnector.getInstance().SessionID;
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, new String[] {}, 1, 1);
        Object[] rows = (Object[])result.get("rows");
        return rows.length > 0;
    }
}
