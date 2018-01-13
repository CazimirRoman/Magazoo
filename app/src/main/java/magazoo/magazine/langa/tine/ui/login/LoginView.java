package magazoo.magazine.langa.tine.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.utils.Util;
import magazoo.magazine.langa.tine.base.BaseActivity;
import magazoo.magazine.langa.tine.ui.map.MainActivity;
import magazoo.magazine.langa.tine.ui.profile.ResetPasswordActivity;

public class LoginView extends BaseActivity {

    protected Context mContext;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnFBLogin)
    LoginButton btnFBLogin;
    @BindView(R.id.btnForgotPassword)
    Button btnForgotPassword;
    @BindView(R.id.btnSkip)
    Button btnSkip;
    @BindView(R.id.progress)
    ProgressBar progress;

    private FirebaseAuth mAuthManager;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initActionButtons();
        mCallbackManager = CallbackManager.Factory.create();
        mAuthManager = FirebaseAuth.getInstance();
        configureFacebookLogin();
        mContext = getApplicationContext();
        //getFacebookHash();

        if (mAuthManager.getCurrentUser() != null && mAuthManager.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(LoginView.this, MainActivity.class));
            finish();
        }
    }

    protected void initActionButtons() {

        Button login = findViewById(R.id.btnAction);
        login.setText(getString(R.string.btn_login));
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setVisibility(View.VISIBLE);

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (isFormDataValid(email, password)) {
                    logInUser(email, password);
                }

                progress.setVisibility(View.GONE);
            }
        });

        Button btnGoToRegister = findViewById(R.id.btnGoTo);

        btnGoToRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginView.this, RegisterView.class));
            }
        });

        btnForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginView.this, ResetPasswordActivity.class));
            }
        });

        btnSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginView.this, MainActivity.class));
                finish();
            }
        });
    }

    protected boolean isFormDataValid(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (!Util.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.password_missing));
            return false;
        } else {
            if (password.length() < 6) {
                etPassword.setError(getString(R.string.minimum_password));
                return false;
            }
        }

        return true;
    }

    private void logInUser(String email, String password) {

        if (Util.isInternetAvailable(mContext)) {

            mAuthManager.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginView.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progress.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, R.string.login_failed, Toast.LENGTH_LONG).show();
                            } else {
                                if (mAuthManager.getCurrentUser().isEmailVerified()) {
                                    Intent intent = new Intent(LoginView.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(mContext, getString(R.string.check_email), Toast.LENGTH_LONG).show();

                                }

                            }
                        }
                    });

        } else {
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
        }

        progress.setVisibility(View.GONE);
    }

    protected void configureFacebookLogin() {

        btnFBLogin.setReadPermissions("email", "public_profile");
        btnFBLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                if (!Util.isInternetAvailable(mContext)) {
                    Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void handleFacebookAccessToken(AccessToken token) {

        btnFBLogin.setVisibility(View.INVISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuthManager.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(mContext, R.string.authentication_success,
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginView.this, MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(mContext, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void getFacebookHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "magazoo.magazine.langa.tine",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


    }
}