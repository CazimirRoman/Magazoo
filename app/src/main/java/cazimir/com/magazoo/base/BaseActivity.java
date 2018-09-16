package cazimir.com.magazoo.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.blankj.utilcode.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import cazimir.com.magazoo.BuildConfig;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.utils.MyAlertDialog;
import cazimir.com.magazoo.utils.LoginRegisterBrand;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private MyAlertDialog mAlertDialog;
    private Toolbar mToolbar;
    LoginRegisterBrand loginRegisterBrand;
    BootstrapBrand greenButtons;
    private FirebaseAnalytics mFirebaseAnalytics;

    public LoginRegisterBrand getLoginRegisterbrand() {
        return loginRegisterBrand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mAlertDialog = new MyAlertDialog(this);
        ButterKnife.bind(this);
        setUpToolbar();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loginRegisterBrand = new LoginRegisterBrand(this);
        Utils.init(getApplication());
        getFirebaseAnalytics().setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);

    }

    private void setUpToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(setActionBarTitle()));
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public MyAlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    protected abstract int getLayoutId();
    protected abstract int setActionBarTitle();

    protected void setLogoLanguageForRomanian(){
        Log.d(TAG, "setLogoLanguageForRomanian: called");
        if(Locale.getDefault().getLanguage().equals("ro")){
            ImageView logo = getActivity().findViewById(R.id.logo);
            Drawable logoDrawable = getResources().getDrawable(R.drawable.logo_magazoo_ro);
            logo.setImageDrawable(logoDrawable);
        }
    }

    protected boolean inRomania(LatLng location){
        Geocoder gcd = new Geocoder(this, Locale.US);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses.size() > 0) {
                if(addresses.get(0).getCountryName().equals("Romania")){
                    return true;
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "inRomania: " + e.getMessage());
            e.printStackTrace();

        }

        return false;

    }

    protected FirebaseAnalytics getFirebaseAnalytics() {
        if (mFirebaseAnalytics != null) {
            return mFirebaseAnalytics;
        }

        return FirebaseAnalytics.getInstance(this);
    }

    protected void logEvent(String event, Bundle bundle) {
        getFirebaseAnalytics().logEvent(event, bundle);
    }
}
