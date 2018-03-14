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
import com.google.firebase.auth.FirebaseUser;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;
import magazoo.magazine.langa.tine.ui.profile.OnResetInstructionsSent;
import magazoo.magazine.langa.tine.ui.register.OnRegisterWithEmailFinishedListener;

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
                                listener.onLoginWithEmailFailed("Te rog sa iti verifici mailul pentru a intra in aplicatie.");

                            }
                        }
                    }
                });
    }

    @Override
    public void register(final OnRegisterWithEmailFinishedListener listener, String email, String password) {
        mFirebaseAuthenticationManager.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    listener.onRegisterWithEmailFailed(task.getException().getMessage());
                } else {
                    final FirebaseUser user = mFirebaseAuthenticationManager.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            listener.onRegisterWithEmailSuccess(user.getEmail());
                                        } else {
                                            listener.onRegisterWithEmailFailed(task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public boolean isLoggedIn() {
        return isLoggedInWithEmail() || isLoggedInWithFacebook();
    }

    @Override
    public String getUserEmail() {
        return mFirebaseAuthenticationManager.getCurrentUser().getEmail();
    }

    @Override
    public String getUserId() {
        return mFirebaseAuthenticationManager.getCurrentUser().getUid();
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

    @Override
    public void sendResetInstructions(final OnResetInstructionsSent listener, String email) {
        mFirebaseAuthenticationManager.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onResetInstructionsSentSuccess();
                            } else {
                            listener.onResetInstructionsSentFailed();
                        }
                    }
                });
    }

    @Override
    public void checkIfUserLoggedIn() {
        if (mFirebaseAuthenticationManager.getCurrentUser() != null) {
            ILoginActivityView view = (ILoginActivityView) this.mView.getInstance();
            view.goToMap();
        }
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
