package magazoo.magazine.langa.tine.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import magazoo.magazine.langa.tine.presenter.common.LoginPresenter;
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
    LoginButton btnFacebook;
    @BindView(R.id.login_button_dummy)
    TextView btnDummyLoginButton;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btnAction)
    TextView btnAction;
    @BindView(R.id.btnGoTo)
    TextView btnGoTo;

    private CallbackManager mFacebookCallbackManager;
    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionButtons();
        mLoginPresenter = new LoginPresenter(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        configureFacebookLogin();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.login;
    }

    @OnClick({R.id.btnAction, R.id.btnFBLogin, R.id.btnForgotPassword, R.id.btnGoTo, R.id.login_button_dummy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAction:
                showProgressBar();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                UtilHelperClass.validateFormData(this, email, password, Constants.EMPTY_STRING_PLACEHOLDER);
                showProgressBar();
                break;
            case R.id.login_button_dummy:
                    btnFacebook.performClick();
                    btnDummyLoginButton.setVisibility(View.GONE);
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

    public void goToMap() {
        startActivity(new Intent(LoginActivityView.this, MapActivity.class));
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void initActionButtons() {
        btnAction.setText(getString(R.string.btn_login));
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