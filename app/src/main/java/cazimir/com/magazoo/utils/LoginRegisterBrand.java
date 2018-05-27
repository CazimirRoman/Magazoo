package cazimir.com.magazoo.utils;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;

import cazimir.com.magazoo.R;

/**
 * TODO: Add a class header comment!
 */
public class LoginRegisterBrand implements BootstrapBrand {

    @ColorInt private final int defaultFill;
    @ColorInt private final int defaultEdge;
    @ColorInt private final int defaultTextColor;
    @ColorInt private final int activeFill;
    @ColorInt private final int activeEdge;
    @ColorInt private final int activeTextColor;
    @ColorInt private final int disabledFill;
    @ColorInt private final int disabledEdge;
    @ColorInt private final int disabledTextColor;

    @SuppressWarnings("deprecation") public LoginRegisterBrand(Context context) {
        defaultFill = context.getResources().getColor(R.color.colorSecondary);
        defaultEdge = context.getResources().getColor(R.color.colorPrimary);
        defaultTextColor = context.getResources().getColor(R.color.colorWhite);
        activeFill = context.getResources().getColor(R.color.colorPrimary);
        activeEdge = context.getResources().getColor(R.color.colorPrimary);
        activeTextColor = context.getResources().getColor(R.color.bootstrap_brand_success);
        disabledFill = context.getResources().getColor(R.color.colorSecondary);
        disabledEdge = context.getResources().getColor(R.color.colorSecondary);
        disabledTextColor = context.getResources().getColor(R.color.bootstrap_gray);
    }

    @Override public int defaultFill(Context context) {
        return defaultFill;
    }

    @Override public int defaultEdge(Context context) {
        return defaultEdge;
    }

    @Override public int defaultTextColor(Context context) {
        return defaultTextColor;
    }

    @Override public int activeFill(Context context) {
        return activeFill;
    }

    @Override public int activeEdge(Context context) {
        return activeEdge;
    }

    @Override public int activeTextColor(Context context) {
        return activeTextColor;
    }

    @Override public int disabledFill(Context context) {
        return disabledFill;
    }

    @Override public int disabledEdge(Context context) {
        return disabledEdge;
    }

    @Override public int disabledTextColor(Context context) {
        return disabledTextColor;
    }

    @Override public int getColor() {
        return defaultFill;
    }
}