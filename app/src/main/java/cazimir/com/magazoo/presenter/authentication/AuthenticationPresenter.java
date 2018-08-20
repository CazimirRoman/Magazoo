package cazimir.com.magazoo.presenter.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.presenter.login.OnLoginWithFacebookCallback;
import cazimir.com.magazoo.ui.login.ILoginActivityView;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailCallback;
import cazimir.com.magazoo.ui.register.IRegisterActivityView;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailCallback;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsCallback;

public class AuthenticationPresenter implements IAuthenticationPresenter {

    private IGeneralView mView;
    private FirebaseAuth mAuthManager;

    public AuthenticationPresenter(IGeneralView view) {
        mView = view;
        mAuthManager = FirebaseAuth.getInstance();
    }

    @Override
    public void login(final OnLoginWithEmailCallback listener, String email, String password) {
        mAuthManager.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mView.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (mAuthManager.getCurrentUser().isEmailVerified()) {
                                listener.onSuccess();

                            } else {
                                listener.onFailed(getLoginActivityView().getActivity().getString(R.string.email_not_verified));

                            }
                        } else {
                            listener.onFailed(getLoginActivityView().getActivity().getString(R.string.login_failed));
                        }
                    }
                });
    }

    @Override
    public void register(final OnRegisterWithEmailCallback registerPresenter, String email, String password) {
        mAuthManager.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    registerPresenter.onFailed(getRegisterActivityView().getActivity().getString(R.string.register_failed));
                } else {
                    final FirebaseUser user = mAuthManager.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            registerPresenter.onSuccess(user.getEmail());
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
        return mAuthManager.getCurrentUser().getEmail();
    }

    @Override
    public String getUserId() {
        return mAuthManager.getCurrentUser().getUid();
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook(final OnLoginWithFacebookCallback listener) {
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
    public void sendResetInstructions(final OnResetInstructionsCallback forgotPasswordActivityView, String email) {
        mAuthManager.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            forgotPasswordActivityView.onSuccess();
                        } else {
                            forgotPasswordActivityView.onFailed();
                        }
                    }
                });
    }

    @Override
    public void checkIfUserLoggedInAndRedirectToMap() {
        if ((mAuthManager.getCurrentUser() != null && mAuthManager.getCurrentUser().isEmailVerified()) || isLoggedInWithFacebook()) {
            ILoginActivityView view = (ILoginActivityView) this.mView.getInstance();
            view.goToMap();
        }
    }

    @Override
    public void signOut() {
        mAuthManager.signOut();
        LoginManager.getInstance().logOut();
    }

    private void handleFacebookAccessToken(final OnLoginWithFacebookCallback listener, AccessToken accessToken) {

        final ILoginActivityView view = (ILoginActivityView) this.mView.getInstance();
        Activity context = view.getActivity();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuthManager.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            listener.onSuccess();

                        } else {
                            // If sign in fails, display a message to the user.
                            listener.onFailed(task.getException().toString());
                        }

                    }
                });
    }

    private boolean isLoggedInWithEmail() {
        return mAuthManager.getCurrentUser() != null && mAuthManager.getCurrentUser().isEmailVerified();
    }

    private boolean isLoggedInWithFacebook() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private ILoginActivityView getLoginActivityView() {
        return (ILoginActivityView) this.mView.getInstance();
    }

    private IRegisterActivityView getRegisterActivityView() {
        return (IRegisterActivityView) this.mView.getInstance();
    }
}