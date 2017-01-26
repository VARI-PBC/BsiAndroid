package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.timroes.axmlrpc.XMLRPCClient;

class ReqTaskDetailAsync extends AsyncTask<ReqTaskItem, Integer, ReqTaskItem> {

    Exception ex;

    @Override
    protected ReqTaskItem doInBackground(ReqTaskItem... params) {
        final ReqTaskItem task = params[0];
        Object[] criteria = new Object[2];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", task.RequisitionId);
            put("operator", "equals");
            put("field", "req_vial_tasks.requisition_id");
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", task.TaskName);
            put("operator", "equals");
            put("field", "req_tasks.name");
        }};
        String[] display = new String[] {"vial.current_label","vial.field_347","vial.field_188","location.freezer","location.rack","location.box","lkup_container_type.number_of_rows","lkup_container_type.number_of_columns","vial_location.row","vial_location.col","req_vial_tasks.date_completed","location.row_format","location.column_format"};
        String[] sort = new String[] {"location.freezer","location.rack","location.box","vial_location.col"};

        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display, sort, 0, 1);
            Object[] rows = (Object[])result.get("rows");
            ReqTaskItem.Box box = null;
            if (task.Boxes == null) {
                task.Boxes = new ArrayList<>();
            }
            for (Object row : rows) {
                Object[] values = (Object[])row;
                if (values[3].equals("") && values[4].equals("") && values[5].equals("")) continue;
                if (box == null || !(values[3].equals(box.Freezer) && values[4].equals(box.Rack) && values[5].equals(box.BoxLabel))) {
                    box = new ReqTaskItem.Box();
                    box.Freezer = (String)values[3];
                    box.Rack = (String)values[4];
                    box.BoxLabel = (String)values[5];
                    box.NumRows = Integer.parseInt((String)values[6]);
                    box.NumColumns = Integer.parseInt((String)values[7]);
                    box.RowFormat = Integer.parseInt((String)values[11]);
                    box.ColumnFormat = Integer.parseInt((String)values[12]);
                    box.Vials = new ArrayMap<>();
                    task.Boxes.add(box);
                }
                ReqTaskItem.Vial vial = new ReqTaskItem.Vial();
                vial.current_label = (String)values[0];
                vial.workingId = (String)values[1];
                vial.comments = (String)values[2];
                vial.row = (String)values[8];
                vial.column = (String)values[9];
                vial.date_completed = (String)values[10];
                String key = box.NumRows == 1 ? vial.column : vial.row + "-" + vial.column;
                box.Vials.put(key, vial);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        }
        return task;
    }
}
