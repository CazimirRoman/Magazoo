package cazimir.com.magazoo.presenter.register;

import cazimir.com.magazoo.presenter.authentication.IAuthPresenter;
import cazimir.com.magazoo.ui.register.IRegisterActivityView;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailCallback;

/**
 * Handles the registration process
 */
public class RegisterPresenter implements IRegisterPresenter {

    private IRegisterActivityView mRegisterActivityView;
    private IAuthPresenter mAuthenticationPresenter;

    public RegisterPresenter(IRegisterActivityView view, IAuthPresenter authenticationPresenter) {
        mRegisterActivityView = view;
        mAuthenticationPresenter = authenticationPresenter;
    }

    public void performRegisterWithEmail(String email, String password) {
        mAuthenticationPresenter.register(new OnRegisterWithEmailCallback() {
            @Override
            public void onSuccess(String email) {
                mRegisterActivityView.showRegistrationConfirmationToast(email);
                mRegisterActivityView.redirectToLoginPage();
                mRegisterActivityView.hideProgressBar();
            }

            @Override
            public void onFailed(String error) {
                mRegisterActivityView.hideProgressBar();
                mRegisterActivityView.showToast(error);
            }
        }, email, password);
    }
}
