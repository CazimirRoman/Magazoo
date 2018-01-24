package magazoo.magazine.langa.tine.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import magazoo.magazine.langa.tine.presenter.common.LoginPresenter;

public abstract class BaseActivity extends AppCompatActivity {

    private Context mContext;
    private LoginPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ButterKnife.bind(this);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = getApplicationContext();
        mPresenter = new LoginPresenter();
    }

    public Context getContext() {
        return mContext;
    }

    public LoginPresenter getPresenter() {
        return mPresenter;
    }
}
