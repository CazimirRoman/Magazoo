package magazoo.magazine.langa.tine.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.utils.MyAlertDialog;
import magazoo.magazine.langa.tine.utils.PrimaryBootstrapBrand;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    private MyAlertDialog mAlertDialog;
    private Toolbar mToolbar;
    PrimaryBootstrapBrand bootstrapBrand;

    public PrimaryBootstrapBrand getBootrapBrand() {
        return bootstrapBrand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mAlertDialog = new MyAlertDialog(this);
        ButterKnife.bind(this);
        setUpToolbar();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bootstrapBrand = new PrimaryBootstrapBrand(this);
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
}
