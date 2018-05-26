package cazimir.com.magazoo.ui.reset;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.utils.UtilHelperClass;

public class ForgotPasswordActivityView extends BaseBackActivity implements IResetPasswordActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnForgotPassword)
    BootstrapButton btnForgotPassword;
    @BindView(R.id.progress)
    AVLoadingIndicatorView progress;

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

    @Override
    protected void setBackArrowColour() {
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
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
        progress.smoothToShow();
    }

    @Override
    public void hideProgress() {
        progress.smoothToHide();

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
