package magazoo.magazine.langa.tine.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.utils.Util;

public class RegisterActivityView extends LoginActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnFBLogin)
    LoginButton btnFBLogin;
    @BindView(R.id.btnForgotPassword)
    Button btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;
    private FirebaseAuth mAuthManager;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mCallbackManager = CallbackManager.Factory.create();
        mAuthManager = FirebaseAuth.getInstance();
        initActionButtons();
    }

    @Override
    protected void initActionButtons() {
        super.initActionButtons();

        Button register = findViewById(R.id.btnAction);
        register.setText(getString(R.string.btn_register));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.setVisibility(View.VISIBLE);

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (isFormDataValid(email, password)) {
                    registerUser(email, password);
                }else{
                    progress.setVisibility(View.GONE);
                }
            }
        });

        Button btnGoToLogin = findViewById(R.id.btnGoTo);
        btnGoToLogin.setText(getString(R.string.btn_link_to_login));

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser(String email, String password) {

        if (Util.isInternetAvailable(getContext())) {

            mAuthManager.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivityView.this, "AuthenticationPresenter failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        final FirebaseUser user = mAuthManager.getCurrentUser();
                        if (user != null && !user.isEmailVerified()) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivityView.this,
                                                        "Verification email sent to " + user.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivityView.this, LoginActivityView.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivityView.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            startActivity(new Intent(RegisterActivityView.this, LoginActivityView.class));
                            finish();
                        }
                    }

                    progress.setVisibility(View.GONE);
                }
            });

        } else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.setVisibility(View.GONE);
    }
}
