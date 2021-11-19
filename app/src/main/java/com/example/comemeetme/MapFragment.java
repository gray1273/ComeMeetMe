package com.example.comemeetme;


import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapFragment extends Fragment {

    private MapView mapView;
    private ArrayList<HashMap<String, String>> output = new ArrayList<>();
    private ArrayList<HashMap<String, String>> toSend = new ArrayList<>();
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    FusedLocationProviderClient mFusedLocationClient;
    LocationEngine locationEngine;

    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    Location originLayout;

    private MapboxMap mapboxMap;
    private MapboxMap mapboxMap1;
    public MapFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getEvents();
        Mapbox.getInstance(getContext().getApplicationContext(),getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_map,container,false);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.i("Permissions", "Permission denied");
                    LatLng latLng = new LatLng(39.103119, -84.512016);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)      // Sets the center of the map to Mountain View
                            .zoom(10)                  // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mapboxMap.setCameraPosition(cameraPosition);
                }else {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
//                            requestNewLocationData();
                                Log.i("Permissions", "Location = null");
                                LatLng latLng = new LatLng(39.103119, -84.512016);
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)      // Sets the center of the map to Mountain View
                                        .zoom(10)                  // Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder

                                mapboxMap.setCameraPosition(cameraPosition);
                            } else {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)      // Sets the center of the map to Mountain View
                                        .zoom(10)                  // Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                Log.i("Permissions", "Permission allowed, tried to center on location");
                                mapboxMap.setCameraPosition(cameraPosition);
                            }
                        }
                    });
                }
                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();



                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

// Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                                getActivity().getResources(), R.drawable.mapbox_marker_icon_default))

// Adding a GeoJson source for the SymbolLayer icons.
                        .withSource(new GeoJsonSource(SOURCE_ID,
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
// the coordinate point. This is offset is not always needed and is dependent on the image
// that you use for the SymbolLayer icon.
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                        iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                )
                        ), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        mapboxMap1 = mapboxMap;
                        //addPins();




                    }
                });
            }
        });

        Button buttonFindEvents = view.findViewById(R.id.buttonAddPins);
        buttonFindEvents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                addPins();
                toastMessage("Events displayed");
            }


        });

        return view;
    }
    public void addPins(){

        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap1, mapboxMap1.getStyle());


        // set non-data-driven properties, such as:
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconTranslate(new Float[]{-4f,5f});
        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        ArrayList<HashMap<String, String>> output1 = output;
        Log.i("Event number " , ""+output1.size());


        for(int i = 0; i < output1.size(); i++){

            HashMap<String, String> temp = output1.get(i);
            String position = temp.get("Event Location");
            Log.i("Event Locations: " , position);
            //toastMessage(position);
            String longi = position.substring(position.indexOf("longitude="), position.indexOf("latitude=") -3);
            String lat = position.substring(position.indexOf("latitude="), position.indexOf("altitude=") -3);
            longi = longi.substring(10);
            lat = lat.substring(9);
            double longitude = Double.parseDouble(longi);
            double latitude = Double.parseDouble(lat);
            Symbol symbol = symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(latitude, longitude))
                    .withIconImage(ICON_ID)
                    .withIconSize(2.0f));

            temp.put("symbolID", ""+symbol.getId());
            toSend.add(temp);

        }


        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Fragment tempFrag = new EventListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("symbolID", symbol.getId());
                bundle.putSerializable("array", toSend);
                tempFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, tempFrag, null)
                        .addToBackStack(null)
                        .commit();
            }
        });


        // Use a layer manager here
        // create symbol manager object


        // add click listeners if desired




    }
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Toast.makeText(getActivity(), "your location"+mLastLocation.getLatitude()+" , "+mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mapView.onDestroy();

    }


    public void getEvents(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
        //ArrayList<HashMap<String, String>> out = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if(snapshot1.exists()){
                    ArrayList<HashMap<String, String>> out = new ArrayList<>();
                    for(DataSnapshot snapshot : snapshot1.getChildren()) {
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("Event Location", snapshot.child("Event Location").getValue(String.class));
                        //toastMessage(temp.get("Event Location"));
                        temp.put("Event Name", snapshot.getKey());
                        temp.put("Event Description", snapshot.child("Event Description").getValue(String.class));
                        temp.put("Event End Time", snapshot.child("Event End Time").getValue(String.class));
                        temp.put("Number of People", snapshot.child("Number of People").getValue(String.class));
                        temp.put("Event Owner", snapshot.child("Event Owner").getValue(String.class));
                        temp.put("Original Location", snapshot.child("Original Location").getValue(String.class));


                        out.add(temp);

                    }
                   // toastMessage(""+out.size());
                    getMap(out);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }
    public void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
    public void getMap(ArrayList<HashMap<String, String>> temp){

        output = temp;



    }


}