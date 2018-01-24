package magazoo.magazine.langa.tine.ui;

public interface OnFormValidatedListener {
    void onValidateSuccess(String email, String password);
    void onValidateFail(String what);
}
