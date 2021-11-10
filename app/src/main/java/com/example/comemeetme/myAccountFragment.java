package com.example.comemeetme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comemeetme.databinding.FragmentLoginBinding;
import com.example.comemeetme.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class myAccountFragment extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        return view;
    }
    public static myAccountFragment newInstance(String param1, String param2) {
        myAccountFragment fragment = new myAccountFragment();
        Bundle args = new Bundle();

        return fragment;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        TextView showEmail = view.findViewById(R.id.textViewShowEmail);
        showEmail.setText(user.getEmail());
        Button toLogOut = view.findViewById(R.id.buttonLogout);
        Button delete = view.findViewById(R.id.buttonDeleteAccount);
        Button goToNew = view.findViewById(R.id.buttonCreateNewEvent);
        Button goToEvents = view.findViewById(R.id.buttonGoToEvents);
        goToEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,loginActivity.class));

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, EventListFragment.class, null)
                        .addToBackStack(null)
                        .commit();

            }
        });

        goToNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,loginActivity.class));

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, NewEventFragment.class, null)
                        .addToBackStack(null)
                        .commit();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,loginActivity.class));
                /*String email = user.getEmail();
                EditText confirmEmail = view.findViewById(R.id.editTextConfirmPass);
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, confirmEmail.getText().toString());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       user.delete()
                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if (task.isSuccessful()) {
                                                                           Log.i("", "User account deleted.");
                                                                       }
                                                                   }
                                                               });
                                                   }
                                               });*/
                toastMessage("Feature coming soon!");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, LoginFragment.class, null)
                        .addToBackStack(null)
                        .commit();

            }
        });
        toLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,loginActivity.class));
               mAuth.signOut();
                toastMessage("Signing out...");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, LoginFragment.class, null)
                        .addToBackStack(null)
                        .commit();



            }
        });
    }

    public void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}
