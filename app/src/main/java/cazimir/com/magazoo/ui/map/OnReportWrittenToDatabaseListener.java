package cazimir.com.magazoo.ui.map;

public interface OnReportWrittenToDatabaseListener {
    void onReportWrittenSuccess();
    void onReportWrittenFailed(String error);
}
