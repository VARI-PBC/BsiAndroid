package org.vai.vari.bsiandroid;

import android.graphics.Color;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Spinner _usernameSpinner;
    private EditText _passwordText;
    private EditText _databaseText;
    private Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        setFinishOnTouchOutside(false);

        _usernameSpinner = (Spinner)findViewById(R.id.input_username);
        _passwordText = (EditText)findViewById(R.id.input_password);

        _databaseText = (EditText)findViewById(R.id.input_database);
        _databaseText.setText(getResources().getString(R.string.default_database));
        _loginButton = (Button)findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.usernames_list));
        _usernameSpinner.setAdapter(adapter);
        _usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String username = (String)adapterView.getSelectedItem();
                String password = BsiConnector.getInstance().CachedPasswords.get(username);
                if (password != null)
                    _passwordText.setText(password);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                _passwordText.setText("");
            }
        });
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

        String username = _usernameSpinner.getSelectedItem().toString();
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
        String username = _usernameSpinner.getSelectedItem().toString();
        String password = _passwordText.getText().toString();
        BsiConnector.getInstance().CachedPasswords.put(username, password);
        String database = _databaseText.getText().toString();
        BsiConnector.Login(username, database, sessionId);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameSpinner.getSelectedItem().toString();
        String password = _passwordText.getText().toString();
        String database = _databaseText.getText().toString();

        TextView errorText = (TextView)_usernameSpinner.getSelectedView();
        if (username.isEmpty() || username.contains(".")) {
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.username_validation_error);
            valid = false;
        } else {
            errorText.setTextColor(Color.WHITE);
            errorText.setText(username);
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
