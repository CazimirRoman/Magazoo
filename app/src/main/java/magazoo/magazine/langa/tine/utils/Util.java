package magazoo.magazine.langa.tine.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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

    public static MaterialDialog.Builder buildErrorDialog(final Context context, String title, final String content, final int errorType) {

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


}
