package org.vai.vari.bsiandroid;

import android.util.SparseArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;


class BsiConnector {
    private static BsiConnector mInstance = null;
    static final String CONTAINER_TYPE_VALUE = "lkup_container_type.value";
    static final String DESCRIPTION = "lkup_container_type.description";
    static final String DISPLAY_NAME = "lkup_container_type.display_name";
    static final String NUM_ROWS = "lkup_container_type.number_of_rows";
    static final String NUM_COLUMNS = "lkup_container_type.number_of_columns";

    XMLRPCClient Client = null;
    String Username = "";
    String Database = "";
    String SessionID = "";
    SparseArray<ContainerType> ContainerTypes = null;

    OnLoginListener mCallback;

    private BsiConnector() {
        try {
            URL url = new URL("https://websvc.bsisystems.com/bsi/xmlrpc");
            Client = new XMLRPCClient(url, XMLRPCClient.FLAGS_NIL); // | XMLRPCClient.FLAGS_DEBUG);
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }

    static BsiConnector getInstance() {
        if(mInstance == null)
        {
            mInstance = new BsiConnector();
        }
        return mInstance;
    }

    interface OnLoginListener {
        void OnLogin(String username, String database, String sessionID);
    }

    static void Login(String username, String database, String sessionID) {
        BsiConnector instance = getInstance();
        instance.Username = username;
        instance.Database = database;
        instance.SessionID = sessionID;
        instance.UpdateUI();
    }

    void UpdateUI() {
        mCallback.OnLogin(Username, Database, SessionID);
    }

    static class ContainerType {
        String Description;
        String DisplayName;
        int NumRows;
        int NumColumns;
    }

    static ContainerType getContainerType(int containerTypeValue) {
        BsiConnector instance = getInstance();
        if (instance.ContainerTypes == null) {
            instance.ContainerTypes = new SparseArray<>();
            Object[] criteria = new Object[1];
            criteria[0] = new HashMap<String, Object>() {{
                put("value", "@@Missing");
                put("operator", "not equals");
                put("field", CONTAINER_TYPE_VALUE);
            }};
            ArrayList<String> display = new ArrayList<>(Arrays.asList(CONTAINER_TYPE_VALUE, DESCRIPTION, DISPLAY_NAME, NUM_ROWS, NUM_COLUMNS));
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) instance.Client.call("report.execute", instance.SessionID, criteria, display.toArray(), new String[]{}, 0, 1);
                Object[] rows = (Object[]) result.get("rows");
                for (Object row : rows) {
                    Object[] values = (Object[]) row;
                    ContainerType type = new ContainerType();
                    type.Description = (String)values[display.indexOf(DESCRIPTION)];
                    type.DisplayName = (String)values[display.indexOf(DISPLAY_NAME)];
                    type.NumRows = Integer.parseInt((String)values[display.indexOf(NUM_ROWS)]);
                    type.NumColumns = Integer.parseInt((String)values[display.indexOf(NUM_COLUMNS)]);
                    int key = Integer.parseInt((String)values[display.indexOf(CONTAINER_TYPE_VALUE)]);
                    instance.ContainerTypes.put(key, type);
                }
            } catch (XMLRPCException e) {
                e.printStackTrace();
            }
        }
        return instance.ContainerTypes.get(containerTypeValue);
    }
}
