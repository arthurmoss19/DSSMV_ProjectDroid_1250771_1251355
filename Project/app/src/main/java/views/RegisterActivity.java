package views;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.triptrail.R;
import api.ApiCallback;
import model.User;
import controllers.UserController;
import util.PasswordUtils;
import util.SessionManager;
import util.UiUtils;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editName;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editPasswordConfirm;
    private MaterialButton buttonRegister;
    private ProgressBar progressRegister;

    private final UserController userController = new UserController();

    private void doRegister() {
        String name = valueOf(editName);
        String email = valueOf(editEmail);
        String password = valueOf(editPassword);
        String confirmation = valueOf(editPasswordConfirm);

        setLoading(true);
        userController.register(name, email, password, new ApiCallback<User>() {

            @Override
            public void onSuccess(User user) {
                setLoading(false);
                SessionManager.getInstance(RegisterActivity.this).login(user);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.toast(RegisterActivity.this, message);
            }
        });
    }

    private void setLoading(boolean loading) {
        buttonRegister.setEnabled(!loading);
        progressRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String valueOf(TextInputEditText field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().toString().trim();
    }
}

