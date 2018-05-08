package magazoo.magazine.langa.tine.ui.tutorial;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.utils.CustomPaperOnboardingEngine;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);

        CustomPaperOnboardingEngine engine = new CustomPaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                finish();
            }
        });

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage start = new PaperOnboardingPage("Bine ai venit!", "Magazoo este aplicația pentru magazine si buticuri care se bazeaza pe comunitate pentru a putea oferi cele mai apropiate magazine.",
                Color.parseColor("#04d2ae"), R.drawable.tutorial_welcome, R.drawable.ic_first);
        PaperOnboardingPage add = new PaperOnboardingPage("Adauga magazine", "Daca stii un magazin care nu este in Magazoo adauga-l chiar tu pe harta.",
                Color.parseColor("#7ae582"), R.drawable.tutorial_add_shop, R.drawable.ic_add);
        PaperOnboardingPage type = new PaperOnboardingPage("Tipuri de magazine", "Magazoo iti arata magazine de cartier, piete, supermarketuri si hypermarketuri.",
                Color.parseColor("#75dddd"), R.drawable.tutorial_shop_type, R.drawable.ic_type);
        PaperOnboardingPage report = new PaperOnboardingPage("Raporteaza un magazin", "Daca observi ceva in neregula cu un magazin poti raporta acest lucru.",
                Color.parseColor("#508991"), R.drawable.tutorial_report_shop, R.drawable.ic_report);
        PaperOnboardingPage navigate = new PaperOnboardingPage("Navigheaza", "Navigheaza rapid catre cel mai aproape magazin.",
                Color.parseColor("#004346"), R.drawable.tutorial_navigate_shop, R.drawable.ic_navigate);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(start);
        elements.add(add);
        elements.add(type);
        elements.add(report);
        elements.add(navigate);
        return elements;
    }
}
