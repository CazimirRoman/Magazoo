package magazoo.magazine.langa.tine.presenter;

import com.google.firebase.auth.FirebaseAuth;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.presenter.authentication.IAuthenticationPresenter;

/**
 * TODO: Add a class header comment!
 */
public class BasePresenter {
    private IGeneralView mView;
    private IAuthenticationPresenter mAuthentication;

    private FirebaseAuth mFirebaseAuthenticationManager;

    public BasePresenter(IGeneralView view) {
        mView = view;
        mAuthentication = new AuthenticationPresenter(mView);
        mFirebaseAuthenticationManager = FirebaseAuth.getInstance();
    }

    public IAuthenticationPresenter getAuthenticationPresenter() {
        return mAuthentication;
    }

    public FirebaseAuth getFirebaseAuthenticationManager() {
        return mFirebaseAuthenticationManager;
    }
}
