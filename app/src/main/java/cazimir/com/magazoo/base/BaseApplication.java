package cazimir.com.magazoo.base;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;

import org.slf4j.helpers.Util;

import cazimir.com.magazoo.BuildConfig;
import io.fabric.sdk.android.Fabric;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        setUpCrashlytics();
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }

    private void setUpCrashlytics() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
    }
}
