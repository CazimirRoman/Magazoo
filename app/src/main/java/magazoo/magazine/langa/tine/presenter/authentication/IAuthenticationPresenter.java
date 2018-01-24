package magazoo.magazine.langa.tine.presenter.authentication;

import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

public interface IAuthenticationPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
}
