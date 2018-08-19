package cazimir.com.magazoo.ui.reset;

/**
 * TODO: Add a class header comment!
 */
public interface IForgotPasswordActivityView {
    void showToast(String message);
    void showEmailResetSentToast();
    void showEmailResetSentToastError(String error);
    void showProgress();
    void hideProgress();
    void redirectToLogin();

}
