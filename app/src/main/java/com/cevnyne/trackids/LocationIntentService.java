package com.cevnyne.trackids;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.cevnyne.trackids.models.Position;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LocationIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_PROCESS_UPDATES = "com.cevnyne.trackids.action" +
            ".PROCESS_UPDATES";

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    updateChildPosition(location);
                }
            }
        }
    }

    private void updateChildPosition(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Position position = new Position(lat, lng);

        String childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("children").child(childId).child("position").setValue(position);
    }
}
