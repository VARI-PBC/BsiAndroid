package org.vai.vari.bsiandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String SAVED_USER_NAMES = "SavedUserNames";
    private static final String SAVED_DATABASE = "SavedDatabase";

    private InstantAutoComplete _usernameText;
    private EditText _passwordText;
    private EditText _databaseText;
    private Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        setFinishOnTouchOutside(false);

        _usernameText = (InstantAutoComplete) findViewById(R.id.input_username);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _databaseText = (EditText)findViewById(R.id.input_database);
        _loginButton = (Button)findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        String[] savedUserNames = pref.getStringSet(SAVED_USER_NAMES, new ArraySet<String>()).toArray(new String[0]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedUserNames);
        _usernameText.setAdapter(adapter);
        _usernameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = ((TextView)view).getText().toString();
                String password = pref.getString(username, null);
                if (password != null)
                    _passwordText.setText(password);
            }
        });

        String database = pref.getString(SAVED_DATABASE, "");
        _databaseText.setText(database);
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String database = _databaseText.getText().toString();

        new LoginAsyncTask() {

            @Override
            protected void onPostExecute(String sessionId) {
                // On complete call either onLoginSuccess or onLoginFailed
                if (sessionId == null) {
                    onLoginFailed();
                }
                else {
                    onLoginSuccess(sessionId);
                }
                progressBar.setVisibility(View.GONE);
            }
        }.execute(username, password, database);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
    }

    public void onLoginSuccess(String sessionId) {
        _loginButton.setEnabled(true);
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String database = _databaseText.getText().toString();
        BsiConnector.getInstance().Login(username, database, sessionId);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        Set<String> savedUserNames = pref.getStringSet(SAVED_USER_NAMES, new ArraySet<String>());
        SharedPreferences.Editor editor = pref.edit();
        if (!savedUserNames.contains(username)) {
            savedUserNames.add(username);
            editor.putStringSet(SAVED_USER_NAMES, savedUserNames);
            editor.putString(username, password);
        }

        String savedDatabase = pref.getString(SAVED_DATABASE, null);
        if (savedDatabase == null || !savedDatabase.equals(database)) {
            editor.putString(SAVED_DATABASE, database);
        }
        editor.apply();

        setResult(AppCompatActivity.RESULT_OK, null);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String database = _databaseText.getText().toString();

        if (username.isEmpty() || username.contains(".")) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 64) {
            _passwordText.setError("between 4 and 64 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (database.isEmpty()) {
            _databaseText.setError("enter a valid database (e.g. \"VARI\")");
            valid = false;
        } else {
            _databaseText.setError(null);
        }

        return valid;
    }
}
