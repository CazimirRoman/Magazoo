package magazoo.magazine.langa.tine.presenter.authentication;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.BasePresenter;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public class AuthenticationPresenter extends BasePresenter implements IAuthenticationPresenter {

    private IGeneralView mView;

    public AuthenticationPresenter(IGeneralView view) {
        super(view);
        this.mView = view;
    }

    @Override
    public void login(final OnLoginWithEmailFinishedListener listener, String email, String password) {
        getFirebaseAuthenticationManager().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getLoginActivityView(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (getFirebaseAuthenticationManager().getCurrentUser().isEmailVerified()) {

                                listener.onLoginWithEmailSuccess();

                            } else {
                                listener.onLoginWithEmailFailed(task.getException().getMessage());

                            }


                        }
                    }
                });
    }

    private ILoginActivityView getLoginActivityView(){
        return (ILoginActivityView) this.mView.getInstance();
    }
}
