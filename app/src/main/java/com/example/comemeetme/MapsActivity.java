package com.example.comemeetme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.type.LatLng;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapzen.android.lost.api.LocationRequest;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static androidx.databinding.DataBindingUtil.setContentView;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MapsActivity {
    private PermissionsManager mPermissionsManager;
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationEngine mLocationEngine;

    private EditText mEditLocation;
    private String whereAmIString = null;

    private static final String WHERE_AM_I_STRING = "WhereAmIString";
    private static final String TAG = MapsActivity.class.getSimpleName();
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mMapboxMap.getLocationComponent();
            LocationComponentActivationOptions.Builder builder =
                    new LocationComponentActivationOptions.Builder(this,
                            Objects.requireNonNull(mMapboxMap.getStyle()));
            builder.useDefaultLocationEngine(true);
            LocationComponentActivationOptions options = builder.build();
            locationComponent.activateLocationComponent(options);
        } else {
            mPermissionsManager = new PermissionsManager(this);
            mPermissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the MapView.
        Mapbox.getInstance(this, BuildConfig.MapboxAccessToken);

        setContentView(R.layout.activity_maps);

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            mMapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                UiSettings uiSettings = mMapboxMap.getUiSettings();
                uiSettings.setCompassEnabled(true);
                uiSettings.setZoomGesturesEnabled(true);
                enableLocationComponent();
            });
        });




    }

    //REQUESTING LOCATION STUFF
    private void requestLocation() {
        Timber.tag(TAG).d("requestLocation()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lacksLocationPermission()) {
                int PERMISSION_REQUEST_LOCATION = 1;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            } else {
                doRequestLocation();
            }
        } else {
            doRequestLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean lacksLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void doRequestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLocationEngine = initializeLocationEngine();
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private LocationEngine initializeLocationEngine() {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest.Builder locRequestBuilder = new LocationEngineRequest.Builder(60000);
        locRequestBuilder.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationEngineRequest locRequest = locRequestBuilder.build();

        if (hasLocationPermission()) {
            locationEngine.requestLocationUpdates(locRequest, this, Looper.getMainLooper());
        }

        return locationEngine;
    }

    private void setCameraPosition(Location location) {
        mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Timber.tag(TAG).d("onExplanationNeeded()");
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            //FragmentManager fragmentManager = getSupportFragmentManager();
            //LocationDeniedDialogFragment deniedDialogFragment = new LocationDeniedDialogFragment();
            //deniedDialogFragment.show(fragmentManager, "location_denied");
            Timber.tag(TAG).e("User denied permission to get location");
            Toast.makeText(getApplicationContext(), "Permission to get location denied", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Fetch location if user allowed it.
        if (requestCode == ENABLE_GPS_REQUEST_CODE) {
            requestLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionsManager != null) {
            mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

            // Direct users to turn on GPS (and send them to Location Settings if it's off).
            // Source: https://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getApplicationContext(), "Please turn on GPS in the Settings app", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, ENABLE_GPS_REQUEST_CODE);
                }
            }
        }
    }
}
