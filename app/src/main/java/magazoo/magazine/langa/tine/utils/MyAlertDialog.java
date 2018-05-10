package magazoo.magazine.langa.tine.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import magazoo.magazine.langa.tine.R;

public class MyAlertDialog extends AlertDialog {

    private Context context;
    private AlertDialog dialog;

    public MyAlertDialog(Context context) {
        super(context);
        this.context = context;
    }

    public AlertDialog buildAlertDialog() {

        if (dialog == null) {
            dialog = new Builder(context).create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.popup_close), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    public void show(String message) {
        buildAlertDialog().setMessage(message);
        if (!buildAlertDialog().isShowing()) buildAlertDialog().show();
    }
}
