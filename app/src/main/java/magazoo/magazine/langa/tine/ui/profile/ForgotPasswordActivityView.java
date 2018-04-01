package magazoo.magazine.langa.tine.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.OnClick;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseBackActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.utils.UtilHelperClass;

public class ForgotPasswordActivityView extends BaseBackActivity implements IResetPasswordActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnForgotPassword)
    BootstrapButton btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;

    private AuthPresenter mAuthPresenter;

    @OnClick(R.id.btnForgotPassword)
    public void onViewClicked() {
                if(isFormDataValid()){
                    String email = etEmail.getText().toString().trim();

                    if(isFormDataValid()){
                        mAuthPresenter.sendResetInstructions(new OnResetInstructionsSent() {
                            @Override
                            public void onResetInstructionsSentSuccess() {
                                showToast(getString(R.string.email_reset_sent));
                                finish();
                            }

                            @Override
                            public void onResetInstructionsSentFailed() {
                                showToast(getString(R.string.email_reset_sent_error));
                            }
                        }, email);
                    }
                }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthPresenter = new AuthPresenter(this);
        btnForgotPassword.setBootstrapBrand(getBootrapBrand());
    }

    private boolean isFormDataValid() {

        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (!UtilHelperClass.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }

        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.resetPassword;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);

    }

    @Override
    public void redirectToLogin() {
        finish();
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
