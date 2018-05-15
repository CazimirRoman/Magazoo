package cazimir.com.magazoo.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */
public class CustomPaperOnboardingEngine extends PaperOnboardingEngine {

    public CustomPaperOnboardingEngine(View rootLayout, ArrayList<PaperOnboardingPage> contentElements, Context appContext) {
        super(rootLayout, contentElements, appContext);
    }

    @Override
    protected ViewGroup createContentTextView(PaperOnboardingPage PaperOnboardingPage) {
        ViewGroup group = super.createContentTextView(PaperOnboardingPage);
        ((TextView) group.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) group.getChildAt(1)).setTextColor(Color.WHITE);
        return group;

    }
}
