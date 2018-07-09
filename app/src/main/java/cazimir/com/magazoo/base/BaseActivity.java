package cazimir.com.magazoo.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.blankj.utilcode.util.Utils;

import butterknife.ButterKnife;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.utils.MyAlertDialog;
import cazimir.com.magazoo.utils.LoginRegisterBrand;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    private MyAlertDialog mAlertDialog;
    private Toolbar mToolbar;
    LoginRegisterBrand loginRegisterBrand;
    BootstrapBrand greenButtons;

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
