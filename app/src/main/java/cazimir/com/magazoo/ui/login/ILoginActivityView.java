package cazimir.com.magazoo.ui.login;

import cazimir.com.magazoo.base.IGeneralView;

public interface ILoginActivityView extends IGeneralView {
    void goToMap();
    void showToast(String message);
    void showProgressBar();
    void hideProgressBar();
}
