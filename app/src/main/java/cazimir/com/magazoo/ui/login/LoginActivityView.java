package cazimir.com.magazoo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.magazoo.BuildConfig;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.login.LoginPresenter;
import cazimir.com.magazoo.utils.OnFormValidatedListener;
import cazimir.com.magazoo.ui.map.MapActivityView;
import cazimir.com.magazoo.ui.reset.ForgotPasswordActivityView;
import cazimir.com.magazoo.ui.register.RegisterActivityView;
import cazimir.com.magazoo.utils.UtilHelperClass;
import io.fabric.sdk.android.Fabric;

public class LoginActivityView extends BaseActivity implements ILoginActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLoginWithFacebook)
    LoginButton btnFacebook;
    @BindView(R.id.login_button_dummy)
    AwesomeTextView btnDummyLoginButton;
    @BindView(R.id.btnForgotPassword)
    BootstrapButton btnForgotPassword;
    @BindView(R.id.progress)
    FrameLayout progress;
    @BindView(R.id.btnLoginWithEmail)
    BootstrapButton btnLoginWithEmail;
    @BindView(R.id.btnGoToRegister)
    BootstrapButton btnGoToRegister;
    @BindView(R.id.expandableLayout)
    ExpandableRelativeLayout expandableLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private CallbackManager mFacebookCallbackManager;
    private LoginPresenter mLoginPresenter;
    private AuthPresenter mAuthPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenter(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mAuthPresenter = new AuthPresenter(this);
        initUI();
        redirectToMapScreenIfLoggedIn();
        configureFacebookLogin();
    }

    private void initUI() {
        btnLoginWithEmail.setBootstrapBrand(getLoginRegisterbrand());
        btnGoToRegister.setBootstrapBrand(getLoginRegisterbrand());
        btnLoginWithEmail.setBootstrapBrand(getLoginRegisterbrand());
        btnForgotPassword.setBootstrapBrand(getLoginRegisterbrand());
        expandableLayout.collapse();
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
        return R.string.nothing;
    }

    @OnClick({R.id.btnLoginWithEmail, R.id.btnForgotPassword, R.id.btnGoToRegister, R.id.login_button_dummy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginWithEmail:
                if (expandableLayout.isExpanded()) {
                    showProgressBar();
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    UtilHelperClass.validateFormData(new OnFormValidatedListener() {
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
                    }, email, password, Constants.PASSWORD_MATCH_NA);
                } else {
                    expandableLayout.expand();
                }

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
        progress.setVisibility(View.INVISIBLE);
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
        if(resultCode == 0){
            hideProgressBar();
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