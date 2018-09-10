package cazimir.com.magazoo.reports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseListener;
import cazimir.com.magazoo.repository.OnGetAdminNameCallback;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.utils.ApiFailedException;
import cazimir.com.magazoo.utils.PlacesService;
import cazimir.com.magazoo.utils.Util;

public class ReportsActivity extends BaseBackActivity {

    private static final String TAG = ReportsActivity.class.getSimpleName();

    boolean mPausedForAddingShop = false;
    boolean mPausedForGettingAdmin = false;
    int mTotalNumberOfImportedShops = 0;
    String mAdminName = "";

    Repository mRepository;
    @BindView(R.id.report_total_shops)
    TextView mTotalShopsTextView;
    @BindView(R.id.list_country)
    ListView mListCountry;
    @BindView(R.id.list_type)
    ListView mListType;

    ArrayList<String> mShopsInCountries;
    ArrayList<String> mTypeOfShops;
    @BindView(R.id.report_total_shops_bucuresti)
    TextView mTotalShopsBucurestiTextView;
    @BindView(R.id.list_type_bucuresti)
    ListView mListTypeBucuresti;
    @BindView(R.id.import_shops)
    Button mImportShops;
    private int mTotalNumberOfShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = new Repository();
        getReportData();
        //updateAdminData();
    }

    private void updateAdminData() {
        mRepository.updateAdminNameForBucharest();
    }

    private void getReportData() {
        mRepository.getAllShopsForReport(new OnGetAllShopsReportCallback() {
            @Override
            public void onSuccess(int total, Map<String, Integer> shopType, Map<String, Integer> shopCountry, int totalNumberOfShopsBucuresti, Map<String, Integer> shopTypeBucuresti) {
                mTotalShopsTextView.setText(String.format(getString(R.string.report_total_shops), String.valueOf(total)));
                mTotalShopsBucurestiTextView.setText(String.format(getString(R.string.report_total_shops_bucuresti), String.valueOf(totalNumberOfShopsBucuresti)));
                mTotalNumberOfShops = total;
                populateCountryList(shopCountry);
                populateTypeList(shopType);
                populateTypeListBucuresti(shopTypeBucuresti);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void populateTypeListBucuresti(Map<String, Integer> shopTypeBucuresti) {

        mTypeOfShops = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopTypeBucuresti.entrySet()) {
            String type = entry.getKey();
            Integer total = entry.getValue();
            mTypeOfShops.add(type + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mTypeOfShops);

        mListTypeBucuresti.setAdapter(adapter);
    }

    private void populateTypeList(Map<String, Integer> shopType) {

        mTypeOfShops = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopType.entrySet()) {
            String type = entry.getKey();
            Integer total = entry.getValue();
            mTypeOfShops.add(type + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mTypeOfShops);

        mListType.setAdapter(adapter);
    }

    private void populateCountryList(Map<String, Integer> shopCountry) {

        mShopsInCountries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopCountry.entrySet()) {
            String name = entry.getKey();
            Integer total = entry.getValue();
            mShopsInCountries.add(name + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mShopsInCountries);

        mListCountry.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reports;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.nothing;
    }

    @Override
    protected void setBackArrowColour() {
        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_arrow_back, null);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export) {
            sendReportData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendReportData() {

        StringBuilder report = new StringBuilder();

        report.append("Total magazine: ").append(mTotalNumberOfShops);
        report.append("\n\n");

        report.append("Tipuri de magazine").append("\n\n");
        for (String string : mTypeOfShops
                ) {
            report.append(string).append("\n");
        }

        report.append("Magazine pe țări").append("\n\n");
        for (String string : mShopsInCountries
                ) {

            report.append(string).append("\n");
        }


        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Raportul tau pentru Magazoo");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, report.toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    public void importShopsFromJson(View view) {

        String json = Util.loadJSONFromAsset(this);

        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json.toString());

            JSONArray results = jsonObj.getJSONArray("elements");

            if(results.length() > 0){

                for (int i=0; i < results.length(); i++) {

                    final String lat = results.getJSONObject(i).getString("lat");
                    final String lon = results.getJSONObject(i).getString("lon");

                    mPausedForGettingAdmin = true;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PlacesService.getAdminName(new OnGetAdminNameCallback() {
                                    @Override
                                    public void onSuccess(String adminName) {
                                        mAdminName = adminName;
                                        mPausedForGettingAdmin = false;
                                    }

                                    @Override
                                    public void onFailed() {

                                    }
                                }, Double.valueOf(lat), Double.valueOf(lon));
                            } catch (ApiFailedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    while(mPausedForGettingAdmin) {
                        Log.d(TAG, "pausing and waiting until admin name is fetched... ");
                    }

                    final Shop shop = new Shop(Constants.ID_PLACEHOLDER, Double.valueOf(lat), Double.valueOf(lon), mAdminName, Constants.GAS_STATION, true,
                            true, false, Constants.CAZIMIR, "București", "Romania");

                    //mRepository.deleteShopWithTypeInCity(Constants.GAS_STATION);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mRepository.addMarkerToDatabase(new OnAddMarkerToDatabaseListener() {
                                @Override
                                public void onAddMarkerSuccess() {
                                    Log.d(TAG, "added gas station to map");
                                    mPausedForAddingShop = false;
                                    mTotalNumberOfImportedShops++;
                                }

                                @Override
                                public void onAddMarkerFailed(String error) {
                                    Log.e(TAG, "failed adding gas station to map");
                                }
                            }, shop);
                        }
                    }).start();

//                    while(mPausedForAddingShop) {
//                        Log.d(TAG, "pausing and waiting until shop is added to db... ");
//                    }
                }

                Log.d(TAG, "Total number of gas station imported: " + mTotalNumberOfImportedShops);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "something is wrong with the JSON file");
        }
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void deleteTypeShops(View view) {
        mRepository.deleteShopWithTypeInCity(Constants.GAS_STATION, Constants.BUCURESTI);
    }
}