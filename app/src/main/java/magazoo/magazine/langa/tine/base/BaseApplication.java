package magazoo.magazine.langa.tine.base;

import android.app.Application;

import com.facebook.FacebookSdk;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
