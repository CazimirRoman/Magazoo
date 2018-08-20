package cazimir.com.magazoo.presenter.reset;

import cazimir.com.magazoo.presenter.authentication.AuthenticationPresenter;
import cazimir.com.magazoo.ui.reset.ForgotPasswordActivityView;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsCallback;

public class ForgotPasswordPresenter implements IForgotPasswordPresenter {

    private ForgotPasswordActivityView mForgotPasswordActivityView;
    private AuthenticationPresenter mAuthenticationPresenter;

    public ForgotPasswordPresenter(ForgotPasswordActivityView forgotPasswordActivityView, AuthenticationPresenter mAuthenticationPresenter) {
        this.mForgotPasswordActivityView = forgotPasswordActivityView;
        this.mAuthenticationPresenter = mAuthenticationPresenter;
    }

    @Override
    public void sendResetInstructions(String email) {
        mAuthenticationPresenter.sendResetInstructions(new OnResetInstructionsCallback() {

            @Override
            public void onSuccess() {
                mForgotPasswordActivityView.hideProgress();
                mForgotPasswordActivityView.showEmailResetSentToastSuccess();
                mForgotPasswordActivityView.redirectToLogin();
            }

            @Override
            public void onFailed() {
                mForgotPasswordActivityView.hideProgress();
                mForgotPasswordActivityView.showEmailResetSentToastError();
            }
        }, email);
    }
}
