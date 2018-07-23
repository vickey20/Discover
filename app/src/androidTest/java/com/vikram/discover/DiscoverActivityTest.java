package com.vikram.discover;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.vikram.discover.restaurantlist.DiscoverActivity;
import com.vikram.discover.restaurantlist.RestaurantListFragment;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;

import android.support.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class DiscoverActivityTest {

    RestaurantListFragment fragment;

    @Rule
    public ActivityTestRule<DiscoverActivity> activityActivityTestRule = new
            ActivityTestRule<> (DiscoverActivity.class);

    @Before
    public void init() {
        activityActivityTestRule.launchActivity(new Intent(InstrumentationRegistry.getTargetContext(), DiscoverActivity.class));
        fragment = RestaurantListFragment.newInstance();
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment).commit();
    }

    @Test
    public void verifyContentLoad() throws Throwable {
        // check if progress bar is displayed when the view loads
        onView(ViewMatchers.withId(R.id.contentLoadingProgressBar)).check(matches(isDisplayed()));

        // perform UI action
        activityActivityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.notifyAdapter();
            }
        });

        // check if progress bar is gone and recycler view is shown
        onView(withId(R.id.contentLoadingProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.recyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // wait for recycler view to load
        Thread.sleep(1000);

        // click the third item
        int position = 3;
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

        // check if toast is displayed
        onView(withText(getString(R.string.clicked_item_toast) + " " + (position + 1)))
                .inRoot(withDecorView(IsNot.not(activityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    private String getString(int resId) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getString(resId);
    }
}
