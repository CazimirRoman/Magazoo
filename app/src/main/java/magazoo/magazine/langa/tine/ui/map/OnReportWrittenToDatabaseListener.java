package magazoo.magazine.langa.tine.ui.map;

public interface OnReportWrittenToDatabaseListener {
    void onReportWrittenSuccess();
    void onReportWrittenFailed(String error);
}
