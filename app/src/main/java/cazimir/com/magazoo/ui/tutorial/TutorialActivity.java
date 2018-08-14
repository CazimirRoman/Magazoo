package cazimir.com.magazoo.ui.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import cazimir.com.magazoo.R;
import cazimir.com.magazoo.ui.map.MapActivityView;
import cazimir.com.magazoo.utils.CustomPaperOnboardingEngine;

public class TutorialActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        startMapActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);

        CustomPaperOnboardingEngine engine = new CustomPaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                startMapActivity();
            }
        });
    }

    private void startMapActivity() {
        startActivity(new Intent(TutorialActivity.this, MapActivityView.class));
        finish();
    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage start = new PaperOnboardingPage(getString(R.string.tutorial_welcome_title), getString(R.string.tutorial_welcome_text),
                Color.parseColor("#04d2ae"), R.drawable.tutorial_welcome, R.drawable.ic_first);
        PaperOnboardingPage add = new PaperOnboardingPage(getString(R.string.tutorial_add_shop_title), getString(R.string.tutorial_add_shop_text),
                Color.parseColor("#7ae582"), R.drawable.tutorial_add_shop, R.drawable.ic_add);
        PaperOnboardingPage type = new PaperOnboardingPage(getString(R.string.tutorial_shop_type_title), getString(R.string.tutorial_shop_type_text),
                Color.parseColor("#75dddd"), R.drawable.tutorial_shop_type, R.drawable.ic_type);
        PaperOnboardingPage report = new PaperOnboardingPage(getString(R.string.tutorial_report_shop_title), getString(R.string.tutorial_report_shop_text),
                Color.parseColor("#508991"), R.drawable.tutorial_report_shop, R.drawable.ic_report);
        PaperOnboardingPage navigate = new PaperOnboardingPage(getString(R.string.tutorial_navigate_title), getString(R.string.tutorial_navigate_text),
                Color.parseColor("#004346"), R.drawable.tutorial_navigate_shop, R.drawable.ic_navigate);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(start);
        elements.add(navigate);
        elements.add(add);
        elements.add(type);
        elements.add(report);
        return elements;
    }
}
