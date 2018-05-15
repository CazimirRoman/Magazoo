package cazimir.com.magazoo.utils;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.ui.OnFormValidatedListener;

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

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void validateFormData(OnFormValidatedListener listener, String email, String password, String passwordRepeat) {

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

        if(!passwordRepeat.equals(Constants.PASSWORD_MATCH_NA)){
            if (TextUtils.isEmpty(passwordRepeat)) {
                listener.onValidateFail(Constants.PASSWORD_MATCH_ERROR);
                return;
            }

            if(!passwordRepeat.equals(password)){
                listener.onValidateFail(Constants.PASSWORD_MATCH_ERROR);
                return;
            }
        }

        listener.onValidateSuccess(email, password);
    }
}