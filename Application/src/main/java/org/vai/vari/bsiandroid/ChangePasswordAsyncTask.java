package org.vai.vari.bsiandroid;

import android.os.AsyncTask;
import android.util.ArrayMap;

import java.util.Map;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

class ChangePasswordAsyncTask extends AsyncTask<String, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            XMLRPCClient client = BsiConnector.getInstance().Client;
            String sessionID = BsiConnector.getInstance().SessionID;
            String username = params[0];
            String oldPassword = params[1];
            String newPassword = params[2];

            boolean isValid = (boolean)client.call("user.verifyUsernameAndPassword", sessionID, username, oldPassword);
            if (!isValid) return false;

            @SuppressWarnings("unchecked")
            Map<String, Object> props = (Map<String, Object>)client.call("user.getCurrentUserInfo", sessionID);
            int uid = Integer.parseInt((String)props.get("users.user_id"));

            props = new ArrayMap<>();
            props.put("users.password", newPassword);
            client.call("user.update", sessionID, uid, props);
            return true;
        } catch (XMLRPCException e) {
            e.printStackTrace();
        }
        return false;
    }
}
