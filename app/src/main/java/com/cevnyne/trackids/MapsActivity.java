package com.cevnyne.trackids;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cevnyne.trackids.models.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<String> mChildIds = new ArrayList<>();
    private int mChildIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference parentReference = mDatabase.child("parents").child(userId).child("children");
        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childId : dataSnapshot.getChildren()) {
                    mChildIds.add(childId.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Failed to load child",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        Button buttonSelectChild = findViewById(R.id.button_select_child);
        buttonSelectChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childId;
                mChildIndex++;
                if (mChildIndex < mChildIds.size()) {
                    childId = mChildIds.get(mChildIndex);
                } else {
                    mChildIndex = 0;
                    childId = mChildIds.get(mChildIndex);
                }

                DatabaseReference childReference = mDatabase.child("children").child(childId);
                childReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Position position = dataSnapshot.child("position").getValue(Position.class);
                        String name = dataSnapshot.child("name").getValue().toString();
                        double lat = position.getLat();
                        double lng = position.getLng();
                        LatLng location = new LatLng(lat, lng);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(location).title(name)).showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MapsActivity.this, "Failed to load location",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
