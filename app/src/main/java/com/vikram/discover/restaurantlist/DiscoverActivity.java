package com.vikram.discover.restaurantlist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vikram.discover.R;

public class DiscoverActivity
        extends AppCompatActivity
        implements RestaurantListFragment.OnFragmentInteractionListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final String FRAGMENT_RESTAURANT_LIST = "RESTAURANT_LIST";

    private AlertDialog locationRationaleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // THIS IS WRONG! location permission should be checked at the exact place where I'm using the location
        // i.e. RestaurantListFragment.fetchNearbyRestaurants().
        if (savedInstanceState == null) {
            checkLocationPermission();
        }
    }

    private void findLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            RestaurantListFragment restaurantListFragment =
                                    (RestaurantListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_RESTAURANT_LIST);
                            if (restaurantListFragment != null) {
                                restaurantListFragment.currentLocationFound(location);
                            }
                        }
                    }
                });
    }

    private void launchRestaurantListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, RestaurantListFragment.newInstance(), FRAGMENT_RESTAURANT_LIST)
                .commit();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            // Already granted launch fragment
            launchRestaurantListFragment();
        }
    }

    private void showRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(getString(R.string.location_permission_rationale));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkLocationPermission();
            }
        });
        locationRationaleDialog = builder.create();
        locationRationaleDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // launch fragment
                    launchRestaurantListFragment();
                } else {
                    // ask for permission again with rationale.
                    showRationaleDialog();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationRationaleDialog != null && locationRationaleDialog.isShowing()) {
            locationRationaleDialog.dismiss();
            locationRationaleDialog = null;
        }
    }

    @Override
    public void findCurrentLocation() {
        findLastKnownLocation();
    }
}
