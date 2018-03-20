package magazoo.magazine.langa.tine.presenter;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.ui.register.IRegisterActivityView;
import magazoo.magazine.langa.tine.ui.register.OnRegisterWithEmailFinishedListener;

/**
 * Handles the registration process
 */
public class RegisterPresenter implements IRegisterPresenter {

    private IGeneralView mView;
    private AuthPresenter mAuthPresenter;

    public RegisterPresenter(IGeneralView view) {
        mView = view;
        mAuthPresenter = new AuthPresenter(mView);
    }

    public void performRegisterWithEmail(String email, String password) {
        mAuthPresenter.register(new OnRegisterWithEmailFinishedListener() {
            @Override
            public void onRegisterWithEmailSuccess(String email) {
                getRegisterActivityView().showToast("Ti-am trimis un email de verificare la adresa: " + email);
                getRegisterActivityView().redirectToLoginPage();
                getRegisterActivityView().hideProgressBar();
            }

            @Override
            public void onRegisterWithEmailFailed(String error) {
                getRegisterActivityView().hideProgressBar();
                getRegisterActivityView().showToast(error);
            }
        }, email, password);
    }

    private IRegisterActivityView getRegisterActivityView() {
        return (IRegisterActivityView) mView.getInstance();
    }
}
