package magazoo.magazine.langa.tine.utils;

import android.content.Context;
import android.text.TextUtils;

import magazoo.magazine.langa.tine.utils.data.NetworkStatus;

public class Util {

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isInternetAvailable(Context context) {
        return NetworkStatus.getInstance(context).isOnline();
    }


}
