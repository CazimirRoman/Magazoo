package cazimir.com.magazoo.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.repository.OnGetAdminNameCallback;

/**
 * TODO: Add a class header comment!
 */
public class PlacesService {
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/getAdminName";
    private static final String TYPE_GEOCODE = "/geocode";

    private static final String OUT_JSON = "/json";

    // KEY!
    private static final String API_KEY = "AIzaSyDQ-HTxaZFfAFTSpcl0RdqF2jnjSh3iKhc";
    private static boolean apiError = false;

    public static void getAdminName(OnGetAdminNameCallback callback, double lat, double lng) throws ApiFailedException {

        String adminName = "";
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=44.405377,%2026.127084&sensor=false

            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(OUT_JSON);
            sb.append("?latlng=" + lat + "," + lng);
            sb.append("&sensor=false");
            sb.append("&key="+API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray results = jsonObj.getJSONArray("results");
            if(results.length() > 0){
                JSONArray address_components = results.getJSONObject(0).getJSONArray("address_components");

                for (int i=0; i < address_components.length(); i++) {
                    if(address_components.getJSONObject(i).getString("short_name").contains("Sector")){
                        adminName = address_components.getJSONObject(i).getString("short_name");
                        break;
                    }
                }

            }else{
                apiError = true;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        if(apiError){
            throw new ApiFailedException();
        }

        callback.onSuccess(adminName);
    }
}
