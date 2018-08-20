package cazimir.com.magazoo.presenter.reset;

import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.ui.reset.ForgotPasswordActivityView;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsCallback;

public class ForgotPasswordPresenter implements IForgotPasswordPresenter {

    private ForgotPasswordActivityView mForgotPasswordActivityView;
    private AuthPresenter mAuthPresenter;

    public ForgotPasswordPresenter(ForgotPasswordActivityView forgotPasswordActivityView, AuthPresenter mAuthPresenter) {
        this.mForgotPasswordActivityView = forgotPasswordActivityView;
        this.mAuthPresenter = mAuthPresenter;
    }

    @Override
    public void sendResetInstructions(String email) {
        mAuthPresenter.sendResetInstructions(new OnResetInstructionsCallback() {

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
