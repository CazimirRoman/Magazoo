package cazimir.com.magazoo.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LocationUtils;
import com.blankj.utilcode.util.NetworkUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cazimir.com.magazoo.R;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Shop;

public class Util implements IUtil {

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isGPSAvailable() {
        return LocationUtils.isGpsEnabled();

    }

    public static boolean isNetworkAvailable() {
        return NetworkUtils.isConnected();

    }

    public static MaterialDialog.Builder buildDialog(final Context context, String title, final String content, final int errorType) {

        final OnErrorHandledListener callback = (OnErrorHandledListener) context;

        return new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(errorType != Constants.ERROR_PERMISSION)
                .content(content)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (errorType) {
                            case Constants.ERROR_ACCURACY:
                                dialog.dismiss();
                                break;
                            case Constants.ERROR_PERMISSION:
                                callback.requestLocationPermissions();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
    }

    public static MaterialDialog.Builder buildCustomDialog(final Context context, int customLayout, boolean isCancelable, String tag) {

        return new MaterialDialog.Builder(context)
                .tag(tag)
                .customView(customLayout, true)
                .cancelable(isCancelable);
    }

    public static boolean isSameDay(Date day1, Date day2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(day1);
        cal2.setTime(day2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String loadJSONFromAsset(Activity activity, String jsonFile) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public boolean isUnderTheAddLimit(ArrayList<Shop> shopsAddedToday) {
        return shopsAddedToday.size() <= Constants.ADD_SHOP_LIMIT;
    }
}
