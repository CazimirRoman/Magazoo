package magazoo.magazine.langa.tine.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import magazoo.magazine.langa.tine.R;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setUpToolbar();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(setActionBarTitle()));
    }

    public Context getContext() {
        return getApplicationContext();
    }

    protected abstract int getLayoutId();
    protected abstract int setActionBarTitle();
}
