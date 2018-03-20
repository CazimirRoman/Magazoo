package magazoo.magazine.langa.tine.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.OnClick;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.presenter.common.LoginPresenter;
import magazoo.magazine.langa.tine.ui.OnFormValidatedListener;
import magazoo.magazine.langa.tine.ui.map.MapActivityView;
import magazoo.magazine.langa.tine.ui.profile.ForgotPasswordActivityView;
import magazoo.magazine.langa.tine.ui.register.RegisterActivityView;
import magazoo.magazine.langa.tine.utils.UtilHelperClass;

public class LoginActivityView extends BaseActivity implements ILoginActivityView, OnFormValidatedListener {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnFBLogin)
    LoginButton btnFacebook;
    @BindView(R.id.login_button_dummy)
    TextView btnDummyLoginButton;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btnLoginWithEmail)
    TextView btnLoginWithEmail;
    @BindView(R.id.btnGoToRegister)
    TextView btnGoToRegister;

    private CallbackManager mFacebookCallbackManager;
    private LoginPresenter mLoginPresenter;
    private AuthPresenter mAuthPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenter(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mAuthPresenter = new AuthPresenter(this);
        redirectToMapScreenIfLoggedIn();
        configureFacebookLogin();
    }

    private void redirectToMapScreenIfLoggedIn() {
        mAuthPresenter.checkIfUserLoggedInAndRedirectToMap();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.login;
    }

    @OnClick({R.id.btnLoginWithEmail, R.id.btnForgotPassword, R.id.btnGoToRegister, R.id.login_button_dummy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginWithEmail:
                showProgressBar();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                UtilHelperClass.validateFormData(this, email, password, Constants.PASSWORD_MATCH_NA);
                break;
            case R.id.login_button_dummy:
                btnFacebook.performClick();
                showProgressBar();
                break;
            case R.id.btnForgotPassword:
                goToResetPasswordActivity();
                break;
            case R.id.btnGoToRegister:
                startRegisterActivity();
                break;
        }
    }

    public void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    public void goToMap() {
        startActivity(new Intent(LoginActivityView.this, MapActivityView.class));
        finish();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToResetPasswordActivity() {
        startActivity(new Intent(LoginActivityView.this, ForgotPasswordActivityView.class));
    }

    private void startRegisterActivity() {
        startActivity(new Intent(LoginActivityView.this, RegisterActivityView.class));
    }

    protected void configureFacebookLogin() {
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(mFacebookCallbackManager, mLoginPresenter.performLoginWithFacebook());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        mLoginPresenter.performLoginWithEmail(email, password);
    }

    @Override
    public void onValidateFail(String what) {

        hideProgressBar();

        switch (what) {
            case Constants.EMAIL_EMPTY:
                setEmailError(getString(R.string.email_missing));
                break;

            case Constants.EMAIL_INVALID:
                setEmailError(getString(R.string.email_invalid));
                break;

            case Constants.PASSWORD_EMPTY:
                setPasswordError(getString(R.string.password_missing));
                break;

            case Constants.PASSWORD_INVALID:
                setPasswordError(getString(R.string.password_minimum));
                break;
        }
    }

    private void setPasswordError(String error) {
        etPassword.setError(error);
    }

    private void setEmailError(String error) {
        etEmail.setError(error);
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}