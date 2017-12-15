package com.cevnyne.trackids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cevnyne.trackids.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntranceActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUserType = "child";

    private Button mSignInButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mSignInButton = findViewById(R.id.button_sign_in);
        mSignUpButton = findViewById(R.id.button_sign_up);

        // Click listeners
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to SignInActivity
                startActivity(new Intent(EntranceActivity.this, SignInActivity.class));
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to SignUpActivity
                startActivity(new Intent(EntranceActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userReference = mDatabase.child("users").child(userId);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    mUserType = user.getType();
                    onAuthSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(EntranceActivity.this, "Failed to load user",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onAuthSuccess() {
        // Go to Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userType", mUserType);
        startActivity(intent);
        finish();
    }
}
