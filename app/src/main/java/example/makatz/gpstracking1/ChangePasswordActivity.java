package example.makatz.gpstracking1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by makatz on 3/12/2016.
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText edtOldPassword, edtNewPassword, edtNewPasswordRetype;
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setUp();
    }

    private void setUp() {
        edtNewPassword = (EditText) findViewById(R.id.change_pw_new_password);
        edtOldPassword = (EditText) findViewById(R.id.change_pw_old_password);
        edtNewPasswordRetype = (EditText) findViewById(R.id.change_pw_new_password_retype);
        btnChangePassword = (Button) findViewById(R.id.change_pw_btn);
        btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_pw_btn) {
            if (checkChangePasswordForm()) {
                changePassword();
            }
        }
    }


    private boolean checkChangePasswordForm() {
        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String newPasswordRetype = edtNewPasswordRetype.getText().toString();

        boolean hasError = false;

        if (TextUtils.isEmpty(oldPassword)) {
            edtOldPassword.setError(getString(R.string.change_pw_err_missing_old_password));
            hasError = true;
        } else if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError(getString(R.string.change_pw_err_missing_new_password));
            hasError = true;
        } else if (!newPassword.equals(newPasswordRetype)) {
            edtNewPasswordRetype.setError(getString(R.string.change_pw_err_new_password_mismatch));
            hasError = true;
        }

        return !hasError;
    }


    private void changePassword() {
        String email = ref.getAuth().getProviderData().get("email").toString();
        Log.d(TAG, email);
        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        btnChangePassword.setText(getString(R.string.change_pw_status_updating));
        ref.changePassword(email, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Intent returnToMain = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(returnToMain);
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                btnChangePassword.setText(getString(R.string.change_pw_button_label));
                showError(firebaseError.getMessage());
            }
        });
    }

    private void showError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(R.string.change_pw_err_dialog_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
