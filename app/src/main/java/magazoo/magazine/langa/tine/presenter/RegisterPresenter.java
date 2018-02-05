package magazoo.magazine.langa.tine.presenter;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.presenter.authentication.OnLoginWithFacebookFinishedListener;
import magazoo.magazine.langa.tine.ui.register.IRegisterActivityView;
import magazoo.magazine.langa.tine.ui.register.OnRegisterWithEmailFinishedListener;

/**
 * Handles the registration process
 */
public class RegisterPresenter implements IRegisterPresenter, OnRegisterWithEmailFinishedListener {

    private IGeneralView mView;
    private AuthenticationPresenter mAuthPresenter;

    public RegisterPresenter(IGeneralView view) {
        mView = view;
        mAuthPresenter = new AuthenticationPresenter(mView);
    }

    public void performRegisterWithEmail(String email, String password) {
        mAuthPresenter.register(this, email, password);
    }

    @Override
    public void onRegisterWithEmailSuccess(String email) {
        getRegisterActivityView().showToast("Ti-am trimis un email de verificare la adresa: " + email);
        getRegisterActivityView().redirectToLoginPage();
        getRegisterActivityView().hideProgressBar();
    }

    @Override
    public void onRegisterWithEmailFailed(String error) {
        getRegisterActivityView().hideProgressBar();
        getRegisterActivityView().showToast(error);
    }

    private IRegisterActivityView getRegisterActivityView() {
        return (IRegisterActivityView) mView.getInstance();
    }
}
