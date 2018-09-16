package cazimir.com.magazoo.reports;

import java.util.Map;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetAllShopsReportCallback {
    void onSuccess(int totalNumberOfShops, Map<String, Integer> shopTypeWorld, Map<String, Integer> shopCountryWorld, int totalNumberOfShopsBucuresti, Map<String, Integer> shopTypeBucuresti, Map<String, Integer> shopSectorBucuresti);
    void onFailed();
}
