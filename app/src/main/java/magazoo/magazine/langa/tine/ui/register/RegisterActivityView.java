package magazoo.magazine.langa.tine.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.OnClick;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseBackActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.presenter.RegisterPresenter;
import magazoo.magazine.langa.tine.ui.OnFormValidatedListener;
import magazoo.magazine.langa.tine.ui.login.LoginActivityView;
import magazoo.magazine.langa.tine.utils.UtilHelperClass;

public class RegisterActivityView extends BaseBackActivity implements IRegisterActivityView, OnFormValidatedListener {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btnRegisterWithEmail)
    BootstrapButton btnRegisterWithEmail;
    @BindView(R.id.progress)
    ProgressBar progress;

    private RegisterPresenter mRegisterPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterPresenter = new RegisterPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.register;
    }

    @OnClick({R.id.btnRegisterWithEmail})
    public void onViewClicked(View view) {
        showProgressBar();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();
        UtilHelperClass.validateFormData(this, email, password, passwordConfirm);
    }

    public void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void redirectToLoginPage() {
        startActivity(new Intent(RegisterActivityView.this, LoginActivityView.class));
        finish();
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        mRegisterPresenter.performRegisterWithEmail(email, password);
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

            case Constants.PASSWORD_MATCH_ERROR:
                setPasswordConfirmError(getString(R.string.password_not_matching));
                break;
        }
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