package com.example.comemeetme;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.comemeetme.ui.login.LoginFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void correctControlButtons(){
        //check that proper control buttons exist
        assertNotNull(onView(withId(R.id.buttonMyAccount)));
        assertNotNull(onView(withId(R.id.eventsMapButton)));
        assertNotNull(onView(withId(R.id.newEventButton)));
    }

    @Test
    public void addSignInText() {
        //test the login username and password fields
        FragmentScenario scenario = FragmentScenario.launchInContainer(LoginFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.username)).perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.username)).check(matches(withText("test")));
    }

    @Test
    public void addEventGoesToProperPage() {
        //test tht clicking new event leads to the create event page
        ActivityScenario scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        FragmentScenario scenario2 = FragmentScenario.launchInContainer(NewEventFragment.class);
        scenario2.moveToState(Lifecycle.State.RESUMED);
        try {
            onView(withId(R.id.buttonCreateEvent)).perform(click());
        } catch (NoMatchingViewException e) {
                assertTrue(false);
            }
    }
}
