package magazoo.magazine.langa.tine.presenter.authentication;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;
import magazoo.magazine.langa.tine.ui.profile.OnResetInstructionsSent;
import magazoo.magazine.langa.tine.ui.register.OnRegisterWithEmailFinishedListener;

public interface IAuthenticationPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void register (OnRegisterWithEmailFinishedListener listener, String email, String password);
    void checkIfLoggedIn();
    FacebookCallback<LoginResult> loginWithFacebook(OnLoginWithFacebookFinishedListener listener);
    void sendResetInstructions(OnResetInstructionsSent listener, String email);
}
