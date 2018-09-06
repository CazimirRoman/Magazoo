package cazimir.com.magazoo.reports;

import java.util.Map;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetAllShopsReportCallback {
    void onSuccess(int size, Map<String, Integer> shopType, Map<String, Integer> shopCountry);
    void onFailed();
}
