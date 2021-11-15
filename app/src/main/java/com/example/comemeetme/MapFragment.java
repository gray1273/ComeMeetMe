package com.example.comemeetme;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static java.lang.Thread.sleep;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
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
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
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
            }


        });

        return view;
    }
    public void addPins(){
        ArrayList<ArrayList<Double>> coords = updateMap();

        // Use a layer manager here
        // create symbol manager object


        // add click listeners if desired
        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap1, mapboxMap1.getStyle());


        // set non-data-driven properties, such as:
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconTranslate(new Float[]{-4f,5f});
        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        for(int i = 0; i < coords.size(); i++){

            ArrayList<Double> temp = coords.get(i);
            Log.i("Coords " + i, temp.get(0) + ", " + temp.get(1));
            // Add symbol at specified lat/lon
            toastMessage("" + coords.get(0));
            Symbol symbol = symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(temp.get(1), temp.get(0)))
                    .withIconImage(ICON_ID)
                    .withIconSize(2.0f));


        }

    }

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

    public ArrayList<ArrayList<Double>> updateMap(){

        ArrayList<HashMap<String, String>> output1 = output;
        Log.i("Event number " , ""+output1.size());
        ArrayList<ArrayList<Double>> out = new ArrayList<>();
        //ArrayList<Double> coords = new ArrayList<>();

        for(int i = 0; i < output1.size(); i++){
            ArrayList<Double> coords = new ArrayList<>();
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
            //toastMessage(lat);
            coords.add(longitude);
            coords.add(latitude);
            out.add(coords);


        }

        return out;





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