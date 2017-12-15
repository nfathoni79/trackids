package com.cevnyne.trackids;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends BaseActivity {

    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL = 5000;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mUserType;

    // UI Widget
    private Button mButtonStartTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mButtonStartTracking = findViewById(R.id.button_start_tracking);

        // Get user type from intent
        mUserType = getIntent().getStringExtra("userType");

        // Hide button if type is child
        if (mUserType.equals("child")) {
            mButtonStartTracking.setVisibility(View.GONE);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            createLocationRequest();
        } else {
            mButtonStartTracking.setVisibility(View.VISIBLE);
            mButtonStartTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUserType.equals("child")) {
            requestLocationUpdates();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationIntentService.class);
        intent.setAction(LocationIntentService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void requestLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
