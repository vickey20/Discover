package com.vikram.discover.restaurantlist;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vikram.discover.R;

public class DiscoverActivity extends AppCompatActivity implements RestaurantListFragment.OnFragmentInteractionListener {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    private AlertDialog rationaleDialog;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private static final String FRAGMENT_RESTAURANT_LIST = "RESTAURANT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);

        if (savedInstanceState == null) {
            checkLocationPermission();
        }
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
            launchRestaurantListFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("ACTIVITY", "onLocationResult: " + locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("ACTIVITY", "location: " + location.toString());
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void showRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.location_rationale);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkLocationPermission();
            }
        });
        rationaleDialog = builder.create();
        rationaleDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchRestaurantListFragment();
                } else {
                    showRationaleDialog();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (rationaleDialog != null && rationaleDialog.isShowing()) {
            rationaleDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void findLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    RestaurantListFragment restaurantListFragment = (RestaurantListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_RESTAURANT_LIST);
                    restaurantListFragment.foundLastKnownLocation(location);
                }
            }
        });
    }
}