package com.example.comemeetme;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.comemeetme.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String[] eventTypes = { "event type", "sports", "gaming", "other" };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewEventFragment newInstance(String param1, String param2) {
        NewEventFragment fragment = new NewEventFragment();
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

        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, eventTypes);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // spinner.setOnItemSelectedListener(this);


        Button mButton = view.findViewById(R.id.buttonCreateEvent);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("Test", "Recorded onClick");
                DatabaseReference mDatabase;
                boolean isAChar = false;

                EditText eventName = view.findViewById(R.id.editTextEventName);
                EditText eventLocation = view.findViewById(R.id.editTextTextPostalAddress);
                EditText description = view.findViewById(R.id.editTextEventDescriptionMultiLine);
                EditText numPeople = view.findViewById(R.id.editTextNumber);
                EditText endTime = view.findViewById(R.id.editTextTime);
                CheckBox isPrivate = view.findViewById(R.id.checkBox);
                TextView type = view.findViewById(R.id.HelperForSpinner);

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("events");
                    boolean isPriv = isPrivate.isChecked();
                    String eventNameStr = eventName.getText().toString();
                    String descStr = description.getText().toString();
                    String numPeopleStr = numPeople.getText().toString();
                    String eventLocationStr = eventLocation.getText().toString();
                    String endTimeStr = endTime.getText().toString();

                    for(int i = 0; i < eventNameStr.length(); i++){
                        if(eventNameStr.charAt(i) >= 'a' && eventNameStr.charAt(i) <= 'z'){
                            isAChar = true;
                            break;
                        }else if(eventNameStr.charAt(i) >= 'A' && eventNameStr.charAt(i) <= 'Z'){
                            isAChar = true;
                            break;
                        }else if(eventNameStr.charAt(i) >= '0' && eventNameStr.charAt(i) <= '9'){
                            isAChar = true;
                            break;
                        }
                    }
                    //mDatabase.child("Event Name").setValue(eventNameStr);
                if(isAChar) {
                    mDatabase = mDatabase.child(eventNameStr);
                    mDatabase.child("Event Location").setValue(eventLocationStr);
                    mDatabase.child("Event Description").setValue(descStr);
                    mDatabase.child("Event End Time").setValue(endTimeStr);
                    mDatabase.child("Number of People").setValue(numPeopleStr);
                    mDatabase.child("Is Private?").setValue(isPriv);
                    mDatabase.child("Event Type").setValue(type.getText());
                    //Process next fragment

                    CharSequence text = "Event Created";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getActivity(), text, duration);
                    toast.show();
                }else{

                    CharSequence text = "Error: Event Name has to contain a character or number";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getActivity(), text, duration);
                    toast.show();
                }
            }
        });


        // Inflate the layout for this fragment
        return view;
    }






}