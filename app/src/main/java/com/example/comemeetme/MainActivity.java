package com.example.comemeetme;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comemeetme.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, LoginFragment.class, null)
                .commit();
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
