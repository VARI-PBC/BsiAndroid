package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

/**
 * Created by me on 12/21/16.
 */

public class LoginAsyncTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            if (!sessionID.isEmpty()) {
                client.call("common.logoff", sessionID);
            }
            sessionID = (String)client.call("common.logon", params[0], params[1], params[2]);
            return sessionID;
        } catch (XMLRPCException e) {
            e.printStackTrace();
        }
        return null;
    }
}
