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
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseCallback;
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
    ListView mShopsInWorldListView;
    @BindView(R.id.list_type)
    ListView mShopsInWorldTypesListView;

    private ArrayList<String> mShopsInWorld = new ArrayList<>();
    private ArrayList<String> mShopTypesInWorld = new ArrayList<>();
    private ArrayList<String> mShopTypesInBucharest = new ArrayList<>();
    private ArrayList<String> mShopsInSectors = new ArrayList<>();

    @BindView(R.id.report_total_shops_bucuresti)
    TextView mTotalShopsBucurestiTextView;
    @BindView(R.id.list_type_bucuresti)
    ListView mListTypeBucuresti;
    @BindView(R.id.import_shops)
    Button mImportShops;
    @BindView(R.id.list_total_bucuresti_sector)
    ListView mListTotalBucurestiSector;
    private int mTotalNumberOfShops;
    private int mTotalNumberOfShopsBucuresti;

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
            public void onSuccess(int total, Map<String, Integer> shopType, Map<String, Integer> shopCountry, int totalNumberOfShopsBucuresti, Map<String, Integer> shopTypeBucuresti, Map<String, Integer> shopSectorBucuresti) {
                mTotalNumberOfShops = total;
                mTotalNumberOfShopsBucuresti = totalNumberOfShopsBucuresti;
                mTotalShopsTextView.setText(String.format(getString(R.string.report_total_shops), String.valueOf(total)));
                mTotalShopsBucurestiTextView.setText(String.format(getString(R.string.report_total_shops_bucuresti), String.valueOf(mTotalNumberOfShopsBucuresti)));

                populateShopsInWorldList(shopCountry);
                populateShopTypeInWorldList(shopType);
                populateShopTypeListBucuresti(shopTypeBucuresti);
                populateSectorListBucuresti(shopSectorBucuresti);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void populateSectorListBucuresti(Map<String, Integer> shopSectorBucuresti) {
        mShopsInSectors = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopSectorBucuresti.entrySet()) {
            String type = entry.getKey();
            Integer total = entry.getValue();
            mShopsInSectors.add(type + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mShopsInSectors);

        mListTotalBucurestiSector.setAdapter(adapter);
    }

    private void populateShopTypeListBucuresti(Map<String, Integer> shopTypeBucuresti) {

        mShopTypesInBucharest = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopTypeBucuresti.entrySet()) {
            String type = entry.getKey();
            Integer total = entry.getValue();
            mShopTypesInBucharest.add(type + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mShopTypesInBucharest);

        mListTypeBucuresti.setAdapter(adapter);
    }

    private void populateShopTypeInWorldList(Map<String, Integer> shopType) {

        mShopTypesInWorld = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopType.entrySet()) {
            String type = entry.getKey();
            Integer total = entry.getValue();
            mShopTypesInWorld.add(type + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mShopTypesInWorld);

        mShopsInWorldTypesListView.setAdapter(adapter);
    }

    private void populateShopsInWorldList(Map<String, Integer> shopCountry) {

        mShopsInWorld = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : shopCountry.entrySet()) {
            String name = entry.getKey();
            Integer total = entry.getValue();
            mShopsInWorld.add(name + " " + total);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mShopsInWorld);

        mShopsInWorldListView.setAdapter(adapter);
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

        report.append("Total magazine in lume: ").append(mTotalNumberOfShops);
        report.append("\n\n");

        report.append("Numar de magazine pe țări").append("\n\n");
        for (String string : mShopsInWorld
                ) {
            report.append(string).append("\n");
        }

        report.append("\n");

        report.append("Total magazine in București:").append(mTotalNumberOfShopsBucuresti);
        report.append("\n\n");
        report.append("Tipuri de magazine in București").append("\n\n");
        for (String string : mShopTypesInBucharest
                ) {

            report.append(string).append("\n");
        }

        report.append("\n");

        report.append("Total magazine pe sectoare").append("\n\n");

        for (String string : mShopsInSectors
                ) {

            report.append(string).append("\n");
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Raportul tău pentru Magazoo din " + String.valueOf(new Date(System.currentTimeMillis()))) ;
        sharingIntent.putExtra(Intent.EXTRA_TEXT, report.toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    public void importShopsFromJson(View view) {

        String json = Util.loadJSONFromAsset(this, "import_farmers_market.json");

        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json.toString());

            JSONArray results = jsonObj.getJSONArray("elements");

            if (results.length() > 0) {

                for (int i = 0; i < results.length(); i++) {

                    final String lat = results.getJSONObject(i).getString("lat");
                    final String lon = results.getJSONObject(i).getString("lon");
                    boolean nonstop = false;
                    boolean pos = false;

                    try {
                        String opening_hours = results.getJSONObject(i).getJSONObject("tags").getString("opening_hours");
                        if (opening_hours.equals("24/7")) {
                            nonstop = true;
                        }
                    } catch (JSONException e) {
                        //tag not present in json. nonstop stays on false.
                    }

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

                    while (mPausedForGettingAdmin) {
                        Log.d(TAG, "pausing and waiting until admin name is fetched... ");
                    }

                    final Shop shop = new Shop(Constants.ID_PLACEHOLDER, Double.valueOf(lat), Double.valueOf(lon), mAdminName, Constants.FARMER_MARKET, false,
                            nonstop, false, Constants.CAZIMIR, "București", "Romania");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mRepository.addMarkerToDatabase(new OnAddMarkerToDatabaseCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "added gas station to map with nonstop set to " + shop.getNonstop());
                                    mPausedForAddingShop = false;
                                    mTotalNumberOfImportedShops++;
                                }

                                @Override
                                public void onFailed(String error) {
                                    Log.e(TAG, "failed adding gas station to map");
                                }
                            }, shop);
                        }
                    }).start();
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