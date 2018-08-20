package cazimir.com.magazoo.ui.reset;

/**
 * TODO: Add a class header comment!
 */
public interface IForgotPasswordActivityView {
    void showToast(String message);
    void showEmailResetSentToastSuccess();
    void showEmailResetSentToastError();
    void showProgress();
    void hideProgress();
    void redirectToLogin();

}
