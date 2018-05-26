package cazimir.com.magazoo.presenter.login;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.ui.login.ILoginActivityView;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailFinishedListener;

public class LoginPresenter implements ILoginPresenter, OnLoginWithEmailFinishedListener, OnLoginWithFacebookFinishedListener {

    private IGeneralView mView;
    private AuthPresenter mAuthPresenter;

    public LoginPresenter(IGeneralView view) {
        mView = view;
        mAuthPresenter = new AuthPresenter(mView);
    }

    public void performLoginWithEmail(String email, String password) {
        mAuthPresenter.login(this, email, password);
    }

    @Override
    public FacebookCallback<LoginResult> performLoginWithFacebook() {
        return mAuthPresenter.loginWithFacebook(this);
    }

    @Override
    public void onLoginWithEmailSuccess() {
        getLoginActivityView().goToMap();
        getLoginActivityView().hideProgressBar();
    }

    @Override
    public void onLoginWithEmailFailed(String error) {
        getLoginActivityView().hideProgressBar();
        getLoginActivityView().showToast(error);
    }

    @Override
    public void onLoginWithFacebookSuccess() {
        getLoginActivityView().hideProgressBar();
        getLoginActivityView().goToMap();
    }

    @Override
    public void onLoginWithFacebookFailed(String error) {
        getLoginActivityView().hideProgressBar();
        getLoginActivityView().showToast(error);
    }

    private ILoginActivityView getLoginActivityView() {
        return (ILoginActivityView) mView.getInstance();
    }
}