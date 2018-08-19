package cazimir.com.magazoo.ui.register;

public interface OnRegisterWithEmailCallback {
    void onSuccess(String email);
    void onFailed(String error);
}
