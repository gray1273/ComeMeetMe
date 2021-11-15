package com.example.comemeetme;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HashMap<String, String> tempMap = new HashMap<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventListFragment newInstance(String param1, String param2) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        long symbolID = bundle.getLong("symbolID");
        Log.i("Symbol ID: " , ""+symbolID);

        ArrayList<HashMap<String, String>> importedMaps = (ArrayList<HashMap<String, String>>) bundle.getSerializable("array");
        for(int i = 0; i < importedMaps.size(); i++){

            tempMap = importedMaps.get(i);
            if(tempMap.get("symbolID").equals(""+symbolID)){
                break;
            }

        }
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        TextView eventName = (TextView) view.findViewById(R.id.textViewEventName);
        EditText description = view.findViewById(R.id.editTextDesc);
        EditText tvTime = view.findViewById(R.id.editTextTimeUpdate);
        EditText numPar = view.findViewById(R.id.editTextNumParticipants);

        eventName.setText(tempMap.get("Event Name"));
        EditText locationTV = view.findViewById(R.id.editTextLocationUpdate);
        locationTV.setText(tempMap.get("Original Location"));
        description.setText(tempMap.get("Event Description"));
        tvTime.setText(tempMap.get("Event End Time"));
        numPar.setText(tempMap.get("Number of People"));
        Button updateButton = (Button) view.findViewById(R.id.updateButton);





        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase;

                String[] output = new String[5];
                output[0] =  eventName.getText().toString();
                output[1]=  locationTV.getText().toString();
                output[2] =  tvTime.getText().toString();
                output[3] =  numPar.getText().toString();
                output[4] =  description.getText().toString();
                geocode(output[1],output[0]);
                if(user.getEmail().equals(tempMap.get("Event Owner"))) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("events").child(output[0]);
                    mDatabase.child("Original Location").setValue(output[1]);
                    mDatabase.child("Event End Time").setValue(output[2]);
                    mDatabase.child("Event Description").setValue(output[4]);
                    mDatabase.child("Number of People").setValue(output[3]);
                    toastMessage("Entry has been updated");
                    toastMessage("Event Updated");
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, MapFragment.class, null)
                            .addToBackStack(null)
                            .commit();
                }else{
                    toastMessage("Error: You do not have permission to update this event");
                }
            }
        });
        updateButton.setText("Update");
        Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(user.getEmail().equals(tempMap.get("Event Owner"))){
                    String title = eventName.getText().toString();
                    //title = title.substring(12);
                    Log.i("title name = ",title);
                    //toastMessage(title);
                    if(title.equalsIgnoreCase("Test")){
                        toastMessage("Please do not delete the test event");
                    }else{
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
                        mDatabase = mDatabase.child(title);
                        mDatabase.removeValue();

                        toastMessage("Event Deleted");
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, MapFragment.class, null)
                                .addToBackStack(null)
                                .commit();
                    }
                }else{
                    toastMessage("Error: You do not have permission to delete this event");
                }

            }
        });
        deleteButton.setText("Delete");


        // Inflate the layout for this fragment
        return view;
    }
    public void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
    public void geocode(String str, String name){

        try {
            MapboxGeocoding client = new MapboxGeocoding.Builder()
                    .setAccessToken(getString(R.string.mapbox_access_token))
                    .setLocation(str).setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {

                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                    DatabaseReference mDatabase;
                    boolean isAChar = false;
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("events").child(name);
                    GeocodingResponse responseBody = response.body();
                    if (responseBody != null) {
                        List<CarmenFeature> results = responseBody.getFeatures();
                        if (results != null && results.size() > 0) {
                            // Log the first results position.
                            Position firstResultPos = results.get(0).asPosition();
                            String eventLocationRes = firstResultPos.toString();
                            //toastMessage("Location is " + eventLocationRes);
                            mDatabase.child("Event Location").setValue(eventLocationRes);

                        }

                    }
                }


                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
                    // log t.getMessage()
                    toastMessage("Error, failed to locate address, please try again");
                    return;
                }
            });
            } catch (Exception e) {
            Timber.tag("").e("Could not locate this address");
            e.printStackTrace();
            return;
        }


    }

}