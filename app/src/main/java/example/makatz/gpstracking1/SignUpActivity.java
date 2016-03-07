package example.makatz.gpstracking1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private Button btnSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordRetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setUp();
    }

    protected void setUp() {
        btnSignUp = (Button) findViewById(R.id.sign_up_btn);
        edtEmail = (EditText) findViewById(R.id.sign_up_email);
        edtPassword = (EditText) findViewById(R.id.sign_up_password);
        edtPasswordRetype = (EditText) findViewById(R.id.sign_up_password_retype);

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up_btn) {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            String passwordRetype = edtPasswordRetype.getText().toString();
            boolean hasError = false;

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError(getResources().getString(R.string.err_email_missing));
                hasError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError(getResources().getString(R.string.err_email_invalid));
                hasError = true;
            } else if (password.length() < 6) {
                edtPassword.setError(getResources().getString(R.string.sign_up_err_pass_invalid));
                hasError = true;
            } else if (!password.equals(passwordRetype)) {
                edtPasswordRetype.setError(getResources().getString(R.string.sign_up_err_pass_not_match));
                hasError = true;
            }

            if (!hasError) {
                createUser();
            }
        }
    }

    // CREATE USER (STORE USER DATA TO FIREBASE)
    private void createUser() {
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();
        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Log.d(TAG, "Successfully created user account with uid: " + result.toString());
                Toast.makeText(SignUpActivity.this, "Account created!", Toast.LENGTH_SHORT).show();

                logInAfterSignUp(email, password);
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d(TAG, "Error occured: " + firebaseError.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(firebaseError.getMessage())
                        .setTitle(R.string.sign_up_err_dialog_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void logInAfterSignUp(String email, String password) {

        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                Log.d(TAG, authData.getUid());
                Log.d(TAG, authData.getToken());
                Intent returnToMain = new Intent(SignUpActivity.this, MainActivity.class);
                returnToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                returnToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(returnToMain);
                finish();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError

            }
        };

        ref.authWithPassword(email, password, authResultHandler);
    }
}
