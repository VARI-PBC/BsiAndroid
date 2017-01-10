package org.vai.vari.bsiandroid;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class ReqTaskVialsAsync extends AsyncTask<String, Integer, Map<String, Object>> {


    @Override
    protected Map<String, Object> doInBackground(String... params) {
        final String requisitionId = params[0];
        Object[] criteria = new Object[2];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", "Completed");
            put("operator", "equals");
            put("field", "req_vial_tasks.req_vial_task_status");
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", "U;F");
            put("operator", "equals");
            put("field", "req_tasks.req_task_type");
        }};
        criteria[2] = new HashMap<String, Object>() {{
            put("value", requisitionId);
            put("operator", "equals");
            put("field", "req_vial_tasks.requisition_id");
        }};
        String[] display = new String[] {"vial.current_label","vial.field_347","vial.field_188","location.freezer","location.rack","location.box","vial_location.col","req_vial_tasks.date_completed"};
        String[] sort = new String[] {"req_vial_tasks.date_completed", "location.freezer","location.rack","location.box","vial_location.col"};

        Map<String, Object> result;
        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, sort, 0, 1);
        } catch (XMLRPCException e) {
            e.printStackTrace();
            result = new HashMap<>();
            result.put("error", e.getMessage());
        }
        return result;
    }
}
