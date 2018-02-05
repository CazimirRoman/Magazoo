package magazoo.magazine.langa.tine.presenter.common;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.presenter.authentication.OnLoginWithFacebookFinishedListener;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public class LoginPresenter implements ILoginPresenter, OnLoginWithEmailFinishedListener, OnLoginWithFacebookFinishedListener {

    private IGeneralView mView;
    private AuthenticationPresenter mAuthPresenter;

    public LoginPresenter(IGeneralView view) {
        mView = view;
        mAuthPresenter = new AuthenticationPresenter(mView);
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
        getLoginActivityView().hideProgressBar();
        getLoginActivityView().goToMap();
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