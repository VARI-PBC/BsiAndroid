package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";

    private TextView _usernameText;
    private EditText _oldPasswordText;
    private EditText _newPasswordText;
    private EditText _confirmPasswordText;
    private Button _submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);

        _usernameText = (TextView)findViewById(R.id.username);
        _usernameText.setText(BsiConnector.getInstance().Username);
        _oldPasswordText = (EditText)findViewById(R.id.old_password);
        _newPasswordText = (EditText)findViewById(R.id.new_password);
        _confirmPasswordText = (EditText)findViewById(R.id.confirm_password);
        _submitButton = (Button)findViewById(R.id.btn_submit);
        _submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }


    public void submit() {
        Log.d(TAG, "Submit");

        if (!validate()) {
            onChangePasswordFailed();
            return;
        }

        _submitButton.setEnabled(false);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        String username = _usernameText.getText().toString();
        String oldPassword = _oldPasswordText.getText().toString();
        String newPassword = _newPasswordText.getText().toString();
        new ChangePasswordAsyncTask() {

            @Override
            protected void onPostExecute(Boolean success) {
                // On complete call either onLoginSuccess or onLoginFailed
                if (!success) {
                    onChangePasswordFailed();
                }
                else {
                    onChangePasswordSuccess();
                }
                progressBar.setVisibility(View.GONE);
            }
        }.execute(username, oldPassword, newPassword);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
    }

    public void onChangePasswordSuccess() {
        Toast.makeText(getBaseContext(), "Change password succeeded", Toast.LENGTH_LONG).show();

        finish();
    }

    public void onChangePasswordFailed() {
        Toast.makeText(getBaseContext(), "Change password failed", Toast.LENGTH_LONG).show();

        _submitButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String newPassword = _newPasswordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();

        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 64) {
            _newPasswordText.setError("between 4 and 64 alphanumeric characters");
            valid = false;
        } else {
            _newPasswordText.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            _confirmPasswordText.setError("passwords do not match");
            valid = false;
        } else {
            _confirmPasswordText.setError(null);
        }

        return valid;
    }
}
