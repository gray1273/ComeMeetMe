package com.example.comemeetme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
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
