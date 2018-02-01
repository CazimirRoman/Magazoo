package magazoo.magazine.langa.tine.presenter.common;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.presenter.authentication.OnLoginWithFacebookFinishedListener;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

interface ILoginPresenter {
    void performLoginWithEmail(String email, String password);
    FacebookCallback<LoginResult> performLoginWithFacebook();
}
