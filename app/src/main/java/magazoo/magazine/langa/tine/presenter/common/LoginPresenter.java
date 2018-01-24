package magazoo.magazine.langa.tine.presenter.common;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.presenter.BasePresenter;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public class LoginPresenter extends BasePresenter implements ICommonPresenter, OnLoginWithEmailFinishedListener {

    public LoginPresenter(IGeneralView view) {
        super(view);
    }

    public void performLogin(String email, String password) {
        getAuthenticationPresenter().login(this, email, password);
    }

    @Override
    public void onLoginWithEmailSuccess() {

    }

    @Override
    public void onLoginWithEmailFailed(String error) {

    }
}
