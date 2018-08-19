package cazimir.com.magazoo.presenter.register;

import cazimir.com.magazoo.R;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.authentication.IAuthPresenter;
import cazimir.com.magazoo.ui.register.IRegisterActivityView;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailCallback;

/**
 * Handles the registration process
 */
public class RegisterPresenter implements IRegisterPresenter {

    private IRegisterActivityView mRegisterActivityView;
    private IAuthPresenter mAuthPresenter;

    public RegisterPresenter(IRegisterActivityView view, IAuthPresenter authPresenter) {
        mRegisterActivityView = view;
        mAuthPresenter = authPresenter;
    }

    public void performRegisterWithEmail(String email, String password) {
        mAuthPresenter.register(new OnRegisterWithEmailCallback() {
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
