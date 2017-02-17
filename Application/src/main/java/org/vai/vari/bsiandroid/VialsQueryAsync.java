package org.vai.vari.bsiandroid;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class VialsQueryAsync extends AsyncTask<ReqTaskItem, Integer, List<Box>> {
    Exception ex;

    static final String REQUISITION_ID = "req_vial_tasks.requisition_id";
    static final String TASK_ID = "req_vial_tasks.task_id";
    static final String BSI_ID = "req_vial_tasks.bsi_id";
    static final String CURRENT_LABEL = "vial.current_label";
    static final String WORKING_ID = "vial.field_347";
    static final String VIAL_TYPE = "+vial.vial_type";
    static final String COMMENTS = "vial.field_188";
    static final String LOCATION_ID = "vial_location.location_id";
    static final String ROW = "vial_location.row";
    static final String COL = "vial_location.col";
    static final String FREEZER = "location.freezer";
    static final String RACK = "location.rack";
    static final String BOX = "location.box";
    static final String CONTAINER_LABEL = "location.label";
    static final String WORKBENCH = "location.workbench";
    static final String ROW_FORMAT = "location.row_format";
    static final String COLUMN_FORMAT = "location.column_format";
    static final String CONTAINER_TYPE = "location.container_type";

    @Override
    protected List<Box> doInBackground(ReqTaskItem... params) {
        final ReqTaskItem task = params[0];

        try {
            Map<String, Box> boxes = getVialDetails(task);
            return new ArrayList<>(boxes.values());
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
            return new ArrayList<>();
        }
    }

    private Map<String, Box> getVialDetails(final ReqTaskItem task) throws XMLRPCException {

        Object[] criteria = new Object[2];
        criteria[0] = new HashMap<String, Object>() {{
            put("value", task.RequisitionId);
            put("operator", "equals");
            put("field", REQUISITION_ID);
        }};
        criteria[1] = new HashMap<String, Object>() {{
            put("value", task.TaskId);
            put("operator", "equals");
            put("field", TASK_ID);
        }};
        ArrayList<String> display = new ArrayList<>(Arrays.asList(BSI_ID,CURRENT_LABEL,WORKING_ID,VIAL_TYPE,COMMENTS,LOCATION_ID,ROW,COL,FREEZER,RACK,BOX,CONTAINER_LABEL,WORKBENCH,ROW_FORMAT,COLUMN_FORMAT,CONTAINER_TYPE));
        String[] sort = new String[] {LOCATION_ID,ROW,COL};
        Map<String, Box> boxes = new HashMap<>();
        XMLRPCClient client = BsiConnector.getInstance().Client;
        String sessionID = BsiConnector.getInstance().SessionID;
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) client.call("report.execute", sessionID, criteria, display.toArray(), sort, 0, BsiConnector.USER_DEFINED_LISTING);
        Object[] rows = (Object[])result.get("rows");
        for (Object row : rows) {
            Object[] values = (Object[])row;
            String locationId = (String)values[display.indexOf(LOCATION_ID)];
            Box box = boxes.get(locationId);
            if (box == null) {
                box = new Box();
                box.Freezer = (String)values[display.indexOf(FREEZER)];
                box.Rack = (String)values[display.indexOf(RACK)];
                box.Box = (String)values[display.indexOf(BOX)];
                box.Workbench = (String)values[display.indexOf(WORKBENCH)];
                if (locationId.isEmpty()) {
                    box.ContainerLabel = "No location";
                    box.RowFormat = -1;
                    box.ColumnFormat = -1;
                    box.ContainerType = null;
                }
                else {
                    box.ContainerLabel = (String)values[display.indexOf(CONTAINER_LABEL)];
                    box.RowFormat = Integer.parseInt((String) values[display.indexOf(ROW_FORMAT)]);
                    box.ColumnFormat = Integer.parseInt((String) values[display.indexOf(COLUMN_FORMAT)]);
                    int containerType = Integer.parseInt((String) values[display.indexOf(CONTAINER_TYPE)]);
                    box.ContainerType = BsiConnector.getContainerType(containerType);
                }
                box.Vials = new HashMap<>();
                boxes.put(locationId, box);
            }
            Box.Vial vial = new Box.Vial();
            vial.currentLabel = (String)values[display.indexOf(CURRENT_LABEL)];
            vial.bsiId = (String)values[display.indexOf(BSI_ID)];
            vial.workingId = (String)values[display.indexOf(WORKING_ID)];
            vial.vialType = (String)values[display.indexOf(VIAL_TYPE)];
            vial.comments = (String)values[display.indexOf(COMMENTS)];
            vial.column = (String)values[display.indexOf(COL)];
            vial.row = (String)values[display.indexOf(ROW)];
            if (locationId.isEmpty()) {
                box.Vials.put(vial.bsiId, vial);
            } else {
                String key = vial.row + "-" + vial.column;
                box.Vials.put(key, vial);
            }
        }
        return boxes;
    }
}
