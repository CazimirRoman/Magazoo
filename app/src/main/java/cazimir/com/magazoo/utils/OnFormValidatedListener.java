package cazimir.com.magazoo.utils;

public interface OnFormValidatedListener {
    void onValidateSuccess(String email, String password);
    void onValidateFail(String what);
}
