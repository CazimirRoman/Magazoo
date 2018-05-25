package cazimir.com.magazoo.base;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.facebook.FacebookSdk;

import org.slf4j.helpers.Util;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
