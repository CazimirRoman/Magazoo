package magazoo.magazine.langa.tine.presenter.common;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

interface ILoginPresenter {
    void performLoginWithEmail(String email, String password);
    FacebookCallback<LoginResult> performLoginWithFacebook();
}
