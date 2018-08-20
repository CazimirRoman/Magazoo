package cazimir.com.magazoo.ui.reset;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.reset.ForgotPasswordPresenter;
import cazimir.com.magazoo.utils.OnFormValidatedCallback;
import cazimir.com.magazoo.utils.UtilHelperClass;

import static cazimir.com.magazoo.utils.UtilHelperClass.validateFormData;

public class ForgotPasswordActivityView extends BaseBackActivity implements IForgotPasswordActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnForgotPassword)
    BootstrapButton btnForgotPassword;
    @BindView(R.id.progress)
    FrameLayout progress;

    private AuthPresenter mAuthPresenter;
    private ForgotPasswordPresenter mForgotPasswordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthPresenter = new AuthPresenter(this);
        mForgotPasswordPresenter = new ForgotPasswordPresenter(this, mAuthPresenter);
        btnForgotPassword.setBootstrapBrand(getLoginRegisterbrand());
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
        return R.layout.activity_reset_password;
    }

    @OnClick(R.id.btnForgotPassword)
    public void onViewClicked() {
        String email = etEmail.getText().toString().trim();

        showProgress();

        validateFormData(new OnFormValidatedCallback() {
            @Override
            public void onSuccess(String email, String password) {
                mForgotPasswordPresenter.sendResetInstructions(email);
            }

            @Override
            public void onFailed(String what) {
                hideProgress();
                etEmail.setError(getString(R.string.email_missing));
            }
        }, email, Constants.PASSWORD_NA, Constants.PASSWORD_MATCH_NA);
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.nothing;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmailResetSentToastSuccess() {
        showToast(getString(R.string.email_reset_sent));
    }

    @Override
    public void showEmailResetSentToastError() {
        showToast(getString(R.string.email_reset_sent_error));
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.INVISIBLE);

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
