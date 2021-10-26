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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        TextView eventName = (TextView) view.findViewById(R.id.textViewEventName);
        EditText description = view.findViewById(R.id.editTextDesc);
        EditText tvTime = view.findViewById(R.id.editTextTimeUpdate);
        EditText numPar = view.findViewById(R.id.editTextNumParticipants);

        eventName.setText("Event Name Here");
        EditText locationTV = view.findViewById(R.id.editTextLocationUpdate);
        Button updateButton = (Button) view.findViewById(R.id.updateButton);
        Button toAdd = view.findViewById(R.id.buttonToAddEvent);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query mQuery;
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("events");


    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (!task.isSuccessful()) {
                toastMessage("No events in database");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, NewEventFragment.class, null)
                        .addToBackStack(null)
                        .commit();
            } else {
                String total = String.valueOf(task.getResult().getValue());
                String eventNameStr = "Event Name: " + total.substring(total.indexOf("{") + 1, total.indexOf("="));

                String location = total.substring(total.indexOf("Event Location="), total.indexOf("Number of People") - 2);
                locationTV.setText(location.substring(15));

                String time = total.substring(total.indexOf("Event End Time="), total.indexOf("Is Private?") - 2);
                tvTime.setText(time.substring(15));
                String numPeople = total.substring(total.indexOf("Number of People="), total.indexOf("}"));
                numPar.setText(numPeople.substring(17));
                String desc = total.substring(total.indexOf("Event Description="), total.indexOf("Event Owner=") - 2);
                description.setText(desc.substring(18));
                eventName.setText(eventNameStr.substring(12));
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
            }
        }
    });


        //eventName.setText(mDatabase.getKey());


        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, NewEventFragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
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
                mDatabase = FirebaseDatabase.getInstance().getReference().child("events").child(output[0]);
                mDatabase.child("Event Location").setValue(output[1]);
                mDatabase.child("Event End Time").setValue(output[2]);
                mDatabase.child("Event Description").setValue(output[4]);
                mDatabase.child("Number of People").setValue(output[3]);
                toastMessage("Entry has been updated");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, EventListFragment.class, null)
                        .addToBackStack(null)
                        .commit();

            }
        });
        updateButton.setText("Update");
        Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String title = eventName.getText().toString();
                title = title.substring(12);
                if(title.equalsIgnoreCase("Test")){
                    toastMessage("Please do not delete the test event");
                }else{
                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
                    mDatabase = mDatabase.child(title);
                    mDatabase.removeValue();
                    
                    toastMessage("Event Deleted");
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, EventListFragment.class, null)
                            .addToBackStack(null)
                            .commit();
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
}