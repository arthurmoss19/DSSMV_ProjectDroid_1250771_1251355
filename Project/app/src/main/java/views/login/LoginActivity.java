package views.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.triptrail.BuildConfig;
import com.triptrail.R;
import api.ApiCallback;
import api.ApiClient;
import controllers.UserController;
import model.User;
import views.trips.TripListActivity;
import util.SessionManager;
import util.UiUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonGoToRegister;
    private ProgressBar progressLogin;
    private final UserController userController = new UserController();
    private boolean redirected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        bindViews();

        buttonLogin.setOnClickListener(view -> doLogin());
        buttonGoToRegister.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionManager.getInstance(this).isLoggedIn()) {
            goToTrips();
        }
    }
    private void bindViews() {
        editEmail = findViewById(R.id.editLoginEmail);
        editPassword = findViewById(R.id.editLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister);
        progressLogin = findViewById(R.id.progressLogin);
    }
    private void doLogin() {
        if (ApiClient.isKeyMissing(BuildConfig.RESTDB_KEY)) {
            UiUtils.toast(this, ApiClient.RESTDB_KEY_MISSING);
            return;
        }

        String email = valueOf(editEmail);
        String password = valueOf(editPassword);
        setLoading(true);
        userController.login(email, password, new ApiCallback<User>() {

            @Override
            public void onSuccess(User user) {
                setLoading(false);
                SessionManager.getInstance(LoginActivity.this).login(user);
                goToTrips();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.toast(LoginActivity.this, message);
            }
        });
    }
    private void goToTrips() {
        if (redirected) {
            return;
        }
        redirected = true;
        startActivity(new Intent(this, TripListActivity.class));
        finish();
    }
    private void setLoading(boolean loading) {
        buttonLogin.setEnabled(!loading);
        buttonGoToRegister.setEnabled(!loading);
        progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
    private String valueOf(EditText field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().toString().trim();
    }
}
