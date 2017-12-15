package com.cevnyne.trackids;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cevnyne.trackids.models.Child;
import com.cevnyne.trackids.models.Parent;
import com.cevnyne.trackids.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfField;
    private EditText mParentEmailField;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mNameField = findViewById(R.id.field_name);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mPasswordConfField = findViewById(R.id.field_password_confirmation);
        mParentEmailField = findViewById(R.id.field_parent_email);
        mSignUpButton = findViewById(R.id.button_sign_up);

        // Click listener
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String userType;
        String name = mNameField.getText().toString();

        if (TextUtils.isEmpty(mParentEmailField.getText().toString())) {
            userType = "parent";
        } else {
            userType = "child";
        }

        // Write new user
        writeNewUser(user.getUid(), user.getEmail(), userType, name);

        // Go to EntranceActivity
        Intent intent = new Intent(SignUpActivity.this, EntranceActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(mNameField.getText().toString())) {
            mNameField.setError("Required");
            result = false;
        } else {
            mNameField.setError(null);
        }

        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(mPasswordConfField.getText().toString())) {
            mPasswordConfField.setError("Required");
            result = false;
        }

        if (! mPasswordField.getText().toString().equals(mPasswordConfField.getText().toString())) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

    private void writeNewUser(final String userId, String email, String type, String name) {
        User user = new User(email, type);

        mDatabase.child("users").child(userId).setValue(user);

        if (type.equals("parent")) {
            Parent parent = new Parent(name);
            mDatabase.child("parents").child(userId).setValue(parent);
        } else {
            Child child = new Child(name);
            mDatabase.child("children").child(userId).setValue(child);

            String parentEmail = mParentEmailField.getText().toString();
            Query query = mDatabase.child("users").orderByChild("email").equalTo(parentEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String parentId = "";
                        for (DataSnapshot parent : dataSnapshot.getChildren()) {
                            parentId = parent.getKey();
                        }
                        mDatabase.child("parents").child(parentId).child("children").child(userId).setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SignUpActivity.this, "Failed to load parent.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
