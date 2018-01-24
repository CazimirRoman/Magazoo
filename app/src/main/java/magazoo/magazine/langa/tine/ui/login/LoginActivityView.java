package magazoo.magazine.langa.tine.ui.login;

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
import butterknife.OnClick;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.ui.OnFormValidatedListener;
import magazoo.magazine.langa.tine.ui.map.MapActivity;
import magazoo.magazine.langa.tine.ui.profile.ResetPasswordActivity;
import magazoo.magazine.langa.tine.utils.Util;
import magazoo.magazine.langa.tine.utils.UtilHelperClass;

public class LoginActivityView extends BaseActivity implements ILoginActivityView, OnFormValidatedListener {

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
    @BindView(R.id.btnAction)
    Button btnAction;
    @BindView(R.id.btnGoTo)
    Button btnGoTo;

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
        //getFacebookHash();
        checkIfAlreadyLoggedIn();
        initActionButtonText();
    }

    @OnClick({R.id.btnAction, R.id.btnFBLogin, R.id.btnForgotPassword, R.id.btnGoTo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAction:
                showProgressBar();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                UtilHelperClass.validateFormData(this, email, password, Constants.EMPTY_STRING_PLACEHOLDER);
                showProgressBar();
                break;
            case R.id.btnFBLogin:

                break;
            case R.id.btnForgotPassword:
                goToResetPasswordActivity();
                break;
            case R.id.btnGoTo:
                startRegisterActivity();
                break;
        }
    }

    private void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    private void initActionButtonText() {
        btnAction.setText(getString(R.string.btn_login));
    }

    private void checkIfAlreadyLoggedIn() {
        if (isLoggedInWithEmail() || isLoggedInWithFacebook()) {
            goToMap();
            finish();
        }
    }

    private void goToMap() {
        startActivity(new Intent(LoginActivityView.this, MapActivity.class));
    }

    private boolean isLoggedInWithEmail() {
        return mAuthManager.getCurrentUser() != null && mAuthManager.getCurrentUser().isEmailVerified();
    }

    private boolean isLoggedInWithFacebook() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    protected void initActionButtons() {

        btnSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMap();
                finish();
            }
        });
    }

    private void goToResetPasswordActivity() {
        startActivity(new Intent(LoginActivityView.this, ResetPasswordActivity.class));
    }

    private void startRegisterActivity() {
        startActivity(new Intent(LoginActivityView.this, RegisterActivityView.class));
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



        } else {
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            hideProgressBar();
        }
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

                            goToMap();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(mContext, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        getPresenter().performLogin(email, password);
    }

    @Override
    public void onValidateFail(String what) {

    }
}