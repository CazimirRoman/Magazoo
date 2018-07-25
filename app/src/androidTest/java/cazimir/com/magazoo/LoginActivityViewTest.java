package cazimir.com.magazoo;

import android.app.Activity;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import cazimir.com.magazoo.ui.login.LoginActivityView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LoginActivityViewTest {

    @Rule
    public ActivityTestRule<LoginActivityView> mLoginActivityView = new ActivityTestRule<>(LoginActivityView.class);

    @Test
    public void clickLoginWithFacebookButton_showsProgressBar() throws Exception {

        onView(withId(R.id.btnLoginWithEmail))
                .perform(click());
        onView(withId(R.id.etEmail)).check(ViewAssertions.matches(withText("123")));
    }
}