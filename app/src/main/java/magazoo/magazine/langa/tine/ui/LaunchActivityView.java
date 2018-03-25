package magazoo.magazine.langa.tine.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.OnClick;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.ui.login.LoginActivityView;
import magazoo.magazine.langa.tine.ui.register.RegisterActivityView;

public class LaunchActivityView extends BaseActivity {

    @BindView(R.id.btnLogin)
    BootstrapButton btnLogin;
    @BindView(R.id.btnRegister)
    BootstrapButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.btnLogin, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                startActivity(new Intent(LaunchActivityView.this, LoginActivityView.class));
                break;
            case R.id.btnRegister:
                startActivity(new Intent(LaunchActivityView.this, RegisterActivityView.class));
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launch_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.login;
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
