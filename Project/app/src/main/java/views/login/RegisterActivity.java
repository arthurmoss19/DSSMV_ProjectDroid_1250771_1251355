package views.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.triptrail.R;
import api.ApiCallback;
import model.User;
import controllers.UserController;
import util.SessionManager;
import util.UiUtils;

public class RegisterActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordConfirm;
    private Button buttonRegister;
    private ProgressBar progressRegister;
    private final UserController userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        findViewById(R.id.loginSection).setVisibility(View.GONE);
        findViewById(R.id.registerSection).setVisibility(View.VISIBLE);
        bindViews();
        findViewById(R.id.buttonBackToLogin).setOnClickListener(view -> finish());
        buttonRegister.setOnClickListener(view -> doRegister());
    }
    private void bindViews() {
        editName = findViewById(R.id.editRegisterName);
        editEmail = findViewById(R.id.editRegisterEmail);
        editPassword = findViewById(R.id.editRegisterPassword);
        editPasswordConfirm = findViewById(R.id.editRegisterPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressRegister = findViewById(R.id.progressRegister);
    }

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
    private String valueOf(EditText field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().toString().trim();
    }
}

