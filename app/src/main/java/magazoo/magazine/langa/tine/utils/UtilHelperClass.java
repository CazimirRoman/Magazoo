package magazoo.magazine.langa.tine.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.ui.login.OnFormValidatedListener;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.ui.OnFormValidatedListener;

public class UtilHelperClass {

    public static boolean isSameDay(Date day1, Date day2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(day1);
        cal2.setTime(day2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String convertEpochToDate(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date(epoch));
    }

    public static boolean isInternetAvailable(Context context) {
        return NetworkStatus.getInstance(context).isOnline();
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void validateFormData(OnFormValidatedListener listener, String email, String password, String password2) {

        if (TextUtils.isEmpty(email)) {
            listener.onValidateFail(Constants.EMAIL_EMPTY);
            return;
        } else {
            if (!isValidEmail(email)) {
                listener.onValidateFail(Constants.EMAIL_INVALID);
                return;
            }
        }

        if (TextUtils.isEmpty(password)) {
            listener.onValidateFail(Constants.PASSWORD_EMPTY);
            return;
        } else {
            if (password.length() < 6) {
                listener.onValidateFail(Constants.PASSWORD_INVALID);
                return;
            }
        }

        if(!password2.equals("")){
            if(!password2.equals(password)){
                listener.onValidateFail(Constants.PASSWORD_MATCH_ERROR);
                return;
            }
        }

        listener.onValidateSuccess(email, password);
    }
}
