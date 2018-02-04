package magazoo.magazine.langa.tine.presenter.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public class AuthenticationPresenter implements IAuthenticationPresenter {

    private IGeneralView mView;
    private AuthenticationPresenter mAuthentication;
    private FirebaseAuth mFirebaseAuthenticationManager;

    public AuthenticationPresenter(IGeneralView view) {
        mView = view;
        mFirebaseAuthenticationManager = FirebaseAuth.getInstance();
    }

    @Override
    public void login(final OnLoginWithEmailFinishedListener listener, String email, String password) {
        mFirebaseAuthenticationManager.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mView.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (mFirebaseAuthenticationManager.getCurrentUser().isEmailVerified()) {
                                listener.onLoginWithEmailSuccess();

                            } else {
                                listener.onLoginWithEmailFailed(task.getException().getMessage());

                            }
                        }
                    }
                });
    }

    @Override
    public void checkIfLoggedIn() {
        if (isLoggedInWithEmail() || isLoggedInWithFacebook()) {
            getLoginActivityView().goToMap();
            getLoginActivityView().getActivity().finish();
        }
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook(final OnLoginWithFacebookFinishedListener listener) {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(listener, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        };
    }

    private void handleFacebookAccessToken(final OnLoginWithFacebookFinishedListener listener, AccessToken accessToken) {

        final ILoginActivityView view = (ILoginActivityView) this.mView.getInstance();
        Activity context = view.getActivity();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuthenticationManager.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            listener.onLoginWithFacebookSuccess();

                        } else {
                            // If sign in fails, display a message to the user.
                            listener.onLoginWithFacebookFailed(task.getException().toString());
                        }

                    }
                });
    }

    private boolean isLoggedInWithEmail() {
        return mFirebaseAuthenticationManager.getCurrentUser() != null && mFirebaseAuthenticationManager.getCurrentUser().isEmailVerified();
    }

    private boolean isLoggedInWithFacebook() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private ILoginActivityView getLoginActivityView() {
        return (ILoginActivityView) this.mView.getInstance();
    }
}
