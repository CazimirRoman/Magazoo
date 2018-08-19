package cazimir.com.magazoo.presenter.login;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.authentication.IAuthPresenter;
import cazimir.com.magazoo.ui.login.ILoginActivityView;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailCallback;

public class LoginPresenter implements ILoginPresenter {

    private ILoginActivityView mLoginActivityView;
    private IAuthPresenter mAuth;

    public LoginPresenter(ILoginActivityView view, IAuthPresenter authPresenter) {
        mLoginActivityView = view;
        mAuth = authPresenter;
    }

    public void performLoginWithEmail(String email, String password) {
        mAuth.login(new OnLoginWithEmailCallback() {
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
        return mAuth.loginWithFacebook(new OnLoginWithFacebookCallback() {
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