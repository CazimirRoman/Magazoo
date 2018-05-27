package cazimir.com.magazoo.presenter.authentication;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.magazoo.presenter.login.OnLoginWithFacebookFinishedListener;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailFinishedListener;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsSent;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailFinishedListener;

public interface IAuthPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void register (OnRegisterWithEmailFinishedListener listener, String email, String password);
    boolean isLoggedIn();
    String getUserEmail();
    String getUserId();
    FacebookCallback<LoginResult> loginWithFacebook(OnLoginWithFacebookFinishedListener listener);
    void sendResetInstructions(OnResetInstructionsSent listener, String email);
    void checkIfUserLoggedInAndRedirectToMap();
    void signOut();
}