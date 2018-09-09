package cazimir.com.magazoo.reports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseBackActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.repository.Repository;

public class ReportsActivity extends BaseBackActivity {

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

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}