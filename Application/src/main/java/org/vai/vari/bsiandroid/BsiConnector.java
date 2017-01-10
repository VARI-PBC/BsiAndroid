package org.vai.vari.bsiandroid;

import java.net.URL;
import de.timroes.axmlrpc.XMLRPCClient;


public class BsiConnector {
    private static BsiConnector mInstance = null;
    private static String WEBSVC = "https://websvc.bsisystems.com/bsi/xmlrpc";

    XMLRPCClient Client = null;
    String Username = "";
    String Database = "";
    String SessionID = "";

    OnLoginListener mCallback;

    private BsiConnector() {
        try {
            URL url = new URL(WEBSVC);
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
}
