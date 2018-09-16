package cazimir.com.magazoo.presenter.authentication;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.magazoo.presenter.login.OnLoginWithFacebookCallback;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailCallback;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsCallback;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailCallback;

public interface IAuthenticationPresenter {
    void login (OnLoginWithEmailCallback listener, String email, String password);
    void register (OnRegisterWithEmailCallback listener, String email, String password);
    boolean isLoggedIn();
    String getUserEmail();
    String getUserId();
    FacebookCallback<LoginResult> loginWithFacebook(OnLoginWithFacebookCallback listener);
    void sendResetInstructions(OnResetInstructionsCallback listener, String email);
    void checkIfUserLoggedInAndRedirectToMap();
    void signOut();
    boolean isAdmin();
}