package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class ReqTasksAsync extends AsyncTask<String, Integer, Map<String, Object>> {


    @Override
    protected Map<String, Object> doInBackground(String... params) {
        final String dateCutoff = params[0];
        final String taskType = params[1];
        Object[] criteria = new Object[2];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", taskType);
            put("operator", "equals");
            put("field", "req_tasks.req_task_type");
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", dateCutoff);
            put("operator", "greater or equals");
            put("field", "req_tasks.end_time");
        }};
        String[] display = new String[] {"req_tasks.requisition_id", "req_tasks.name", "req_task_template.label", "req_tasks.end_time", "+req_tasks.completed_by", "req_tasks.req_task_type"};
        String[] sort = new String[] {"-req_tasks.end_time", "req_tasks.requisition_id"};

        Map<String, Object> result;
        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, sort, 0, 1);
        } catch (XMLRPCException e) {
            e.printStackTrace();
            result = new ArrayMap<>();
            result.put("error", e.getMessage());
        }
        return result;
    }
}
