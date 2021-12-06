package edu.temple.foodiego;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateAccountTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void createAccountTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.firstNameCreateAccountEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Tester"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.lastNameCreateAccountEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Testson"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.usernameCreateAccountEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("test123"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.passwordCreateAccountEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.confirmPasswordCreateAccountEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.firstNameCreateAccountEditText), withText("Tester"),
                        withParent(withParent(withId(android.R.id.custom))),
                        isDisplayed()));
        editText.check(matches(withText("Tester")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.lastNameCreateAccountEditText), withText("Testson"),
                        withParent(withParent(withId(android.R.id.custom))),
                        isDisplayed()));
        editText2.check(matches(withText("Testson")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.usernameCreateAccountEditText), withText("test123"),
                        withParent(withParent(withId(android.R.id.custom))),
                        isDisplayed()));
        editText3.check(matches(withText("test123")));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.passwordCreateAccountEditText), withText("••••••••"),
                        withParent(withParent(withId(android.R.id.custom))),
                        isDisplayed()));
        editText4.check(matches(withText("••••••••")));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.confirmPasswordCreateAccountEditText), withText("••••••••"),
                        withParent(withParent(withId(android.R.id.custom))),
                        isDisplayed()));
        editText5.check(matches(withText("••••••••")));

        ViewInteraction button = onView(
                allOf(withId(android.R.id.button1), withText("CREATE"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
