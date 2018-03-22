package magazoo.magazine.langa.tine.presenter.common;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.presenter.authentication.OnLoginWithFacebookFinishedListener;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public class LoginPresenter implements ILoginPresenter, OnLoginWithFacebookFinishedListener {

    private IGeneralView mView;
    private AuthPresenter mAuthPresenter;

    public LoginPresenter(IGeneralView view) {
        mView = view;
        mAuthPresenter = new AuthPresenter(mView);
    }

    public void performLoginWithEmail(String email, String password) {
        mAuthPresenter.login(new OnLoginWithEmailFinishedListener() {
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
        }, email, password);
    }

    @Override
    public FacebookCallback<LoginResult> performLoginWithFacebook() {
        return mAuthPresenter.loginWithFacebook(this);
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