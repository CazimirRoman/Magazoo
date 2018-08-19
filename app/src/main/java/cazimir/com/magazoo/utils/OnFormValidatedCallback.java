package cazimir.com.magazoo.utils;

public interface OnFormValidatedCallback {
    void onSuccess(String email, String password);
    void onFailed(String what);
}
