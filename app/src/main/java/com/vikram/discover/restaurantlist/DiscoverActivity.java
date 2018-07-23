package com.vikram.discover.restaurantlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vikram.discover.R;

public class DiscoverActivity extends AppCompatActivity implements RestaurantListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, RestaurantListFragment.newInstance())
                    .commit();
        }
    }
}
