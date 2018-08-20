package cazimir.com.magazoo.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.presenter.authentication.AuthenticationPresenter;
import cazimir.com.magazoo.presenter.register.RegisterPresenter;
import cazimir.com.magazoo.utils.OnFormValidatedCallback;
import cazimir.com.magazoo.ui.login.LoginActivityView;

import static cazimir.com.magazoo.utils.UtilHelperClass.validateFormData;

public class RegisterActivityView extends BaseBackActivity implements IRegisterActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btnRegisterWithEmail)
    BootstrapButton btnRegisterWithEmail;
    @BindView(R.id.progress)
    AVLoadingIndicatorView progress;

    private RegisterPresenter mPresenter;
    private AuthenticationPresenter mAuthenticationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthenticationPresenter = new AuthenticationPresenter(this);
        mPresenter = new RegisterPresenter(this, mAuthenticationPresenter);
        btnRegisterWithEmail.setBootstrapBrand(getLoginRegisterbrand());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLogoLanguageForRomanian();
    }

    @Override
    protected void setBackArrowColour() {
        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_arrow_back, null);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.nothing;
    }

    @OnClick({R.id.btnRegisterWithEmail})
    public void onViewClicked(View view) {
        showProgressBar();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();
        validateFormData(new OnFormValidatedCallback() {
            @Override
            public void onSuccess(String email, String password) {
                mPresenter.performRegisterWithEmail(email, password);
            }

            @Override
            public void onFailed(String what) {
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

                    case Constants.PASSWORD_MATCH_ERROR:
                        setPasswordConfirmError(getString(R.string.password_not_matching));
                        break;
                }
            }
        }, email, password, passwordConfirm);
    }

    public void showProgressBar() {
        progress.smoothToShow();
    }

    public void hideProgressBar() {
        progress.smoothToHide();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRegistrationConfirmationToast(String email) {
        showToast(getString(R.string.email_sent) + email);
    }

    @Override
    public void redirectToLoginPage() {
        startActivity(new Intent(RegisterActivityView.this, LoginActivityView.class));
        finish();
    }

    private void setPasswordError(String error) {
        etPassword.setError(error);
    }

    private void setPasswordConfirmError(String error) {
        etPasswordConfirm.setError(error);
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