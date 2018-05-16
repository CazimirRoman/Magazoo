package cazimir.com.magazoo.ui;

public interface OnFormValidatedListener {
    void onValidateSuccess(String email, String password);
    void onValidateFail(String what);
}
