package magazoo.magazine.langa.tine.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LocationUtils;

import java.util.Calendar;
import java.util.Date;

import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.constants.IConstants;
import magazoo.magazine.langa.tine.utils.data.NetworkStatus;

public class Util implements IConstants {

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        return NetworkStatus.getInstance(context).isOnline();
    }

    public static boolean isGPSAvailable() {
        if (!LocationUtils.isLocationEnabled()) {
            return false;
        }

        return true;
    }

    public static MaterialDialog.Builder buildDialog(final Context context, String title, final String content, final int errorType) {

        final OnErrorHandledListener listener = (OnErrorHandledListener) context;

        return new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (errorType) {

                            case ERROR_ACCURACY:
                                dialog.dismiss();
                                break;
                            case ERROR_PERMISSION:
                                listener.requestLocationPermissions();
                                break;
                            case ERROR_MAX_ZOOM:
                                dialog.dismiss();
                                listener.zoomToCurrentLocation();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
    }

    public static boolean isSameDay(Date day1, Date day2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(day1);
        cal2.setTime(day2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


}
