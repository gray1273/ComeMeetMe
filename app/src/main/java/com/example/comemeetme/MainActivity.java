package com.example.comemeetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //Enter information to send to the database;
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mDbRef = mDatabase.getReference("Donor/Name");

                mDbRef.setValue("Test Test").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_view, BlankFragment.class, null)
                        .commit();
            }
        });
    }
    @Override
    protected void onPause() {
        Log.i("Main Activity","Triggered onPause for main activity");
        super.onPause();
    }
    @Override
    protected void onResume() {
        Log.i("Main Activity","Triggered onResume for main activity");
        super.onResume();
    }
}
