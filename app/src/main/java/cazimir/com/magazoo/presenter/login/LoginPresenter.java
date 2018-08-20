package cazimir.com.magazoo.presenter.login;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.magazoo.presenter.authentication.IAuthenticationPresenter;
import cazimir.com.magazoo.ui.login.ILoginActivityView;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailCallback;

public class LoginPresenter implements ILoginPresenter {

    private ILoginActivityView mLoginActivityView;
    private IAuthenticationPresenter mAuthenticationPresenter;

    public LoginPresenter(ILoginActivityView view, IAuthenticationPresenter authPresenter) {
        mLoginActivityView = view;
        mAuthenticationPresenter = authPresenter;
    }

    public void performLoginWithEmail(String email, String password) {
        mAuthenticationPresenter.login(new OnLoginWithEmailCallback() {
            @Override
            public void onSuccess() {
                mLoginActivityView.hideProgressBar();
                mLoginActivityView.checkIfOnboardingNeeded();
            }

            @Override
            public void onFailed(String error) {
                mLoginActivityView.hideProgressBar();
                mLoginActivityView.showToast(error);
            }
        }, email, password);
    }

    @Override
    public FacebookCallback<LoginResult> performLoginWithFacebook() {
        return mAuthenticationPresenter.loginWithFacebook(new OnLoginWithFacebookCallback() {
            @Override
            public void onSuccess() {
                mLoginActivityView.hideProgressBar();
                mLoginActivityView.checkIfOnboardingNeeded();
            }

            @Override
            public void onFailed(String error) {
                mLoginActivityView.hideProgressBar();
                mLoginActivityView.showToast(error);
            }
        });
    }
}