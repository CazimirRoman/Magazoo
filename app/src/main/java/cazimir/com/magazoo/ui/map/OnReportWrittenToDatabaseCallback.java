package cazimir.com.magazoo.ui.map;

public interface OnReportWrittenToDatabaseCallback {
    void onSuccess();
    void onFailed(String error);
}
