package cazimir.com.magazoo.ui.register;

import cazimir.com.magazoo.base.IGeneralView;

/**
 * TODO: Add a class header comment!
 */
public interface IRegisterActivityView extends IGeneralView {
    void redirectToLoginPage();
    void showToast(String message);
    void showProgressBar();
    void hideProgressBar();
}
