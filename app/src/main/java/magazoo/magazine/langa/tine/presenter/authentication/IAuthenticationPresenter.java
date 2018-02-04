package magazoo.magazine.langa.tine.presenter.authentication;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public interface IAuthenticationPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void checkIfLoggedIn();
    FacebookCallback<LoginResult> loginWithFacebook(OnLoginWithFacebookFinishedListener listener);
}
