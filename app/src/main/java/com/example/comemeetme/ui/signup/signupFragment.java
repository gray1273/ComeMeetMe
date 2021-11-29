package com.example.comemeetme.ui.signup;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.comemeetme.R;
import com.example.comemeetme.databinding.FragmentSignupBinding;
import com.example.comemeetme.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class signupFragment extends Fragment {

    private SignUpViewModel signUpViewModel;
    private FragmentSignupBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        signUpViewModel = new ViewModelProvider(this, new SignUpViewModelFactory())
                .get(SignUpViewModel.class);

        final EditText usernameEditText = binding.username;
        usernameEditText.setHint("e-mail");
        final EditText passwordEditText = binding.password;
        final EditText confirmPasswordEditText = binding.confirmpassword;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        EditText confirmEmail;

        confirmEmail = usernameEditText;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo active = cm.getActiveNetworkInfo();
                if(active == null){
                    toastMessage("Error: Cannot connect to the internet");
                    return;
                }
                String email1 = usernameEditText.getText().toString();
                String email2 = confirmEmail.getText().toString();
                if (email1.equals("") || email2.equals("")) {
                    toastMessage("Please enter an E-mail");
                } else {
                    if (!email1.equals(email2)) {
                        toastMessage("Emails do not match");
                    } else {
                        String pass1 = passwordEditText.getText().toString();
                        String pass2 = confirmPasswordEditText.getText().toString();
                        if (pass1.equals("") || pass2.equals("")) {
                            toastMessage("Please enter a password");
                        } else {
                            if (!pass1.equals(pass2)) {
                                toastMessage("Passwords do not match");
                            } else {
                                //Create new account using info
                                if (pass1.length() < 6) {
                                    toastMessage("Password must be > 6 characters");
                                } else {
                                    //mAuth.createUserWithEmailAndPassword(email1, pass1);
                                    mAuth.createUserWithEmailAndPassword(email1, pass1)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (!task.isSuccessful()) {
                                                                try {
                                                                    throw task.getException();
                                                                }
                                                                // if user enters wrong email.
                                                                catch (FirebaseAuthWeakPasswordException weakPassword) {
                                                                    Log.d("", "onComplete: weak_password");

                                                                    toastMessage("Password is too weak");
                                                                }
                                                                // if user enters wrong password.
                                                                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                                                    Log.d("", "onComplete: malformed_email");

                                                                    toastMessage("This is an incorrect E-mail");
                                                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                                                    Log.d("", "onComplete: exist_email");

                                                                    toastMessage("This E-mail is already taken, try signing in");
                                                                } catch (Exception e) {
                                                                    Log.d("", "onComplete: " + e.getMessage());
                                                                }
                                                            } else {
                                                                toastMessage("Account Created");
                                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                                        .replace(R.id.fragment_container_view, LoginFragment.class, null)
                                                                        .addToBackStack(null)
                                                                        .commit();
                                                            }
                                                        }
                                                    }
                                            );
                                }
                            }

                        }


                    }


                }


            }
        });


        signUpViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<SignUpFormState>() {
            @Override
            public void onChanged(@Nullable SignUpFormState signUpFormState) {
                if (signUpFormState == null) {
                    return;
                }
                loginButton.setEnabled(signUpFormState.isDataValid());
                if (signUpFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(signUpFormState.getUsernameError()));
                }
                if (signUpFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(signUpFormState.getPasswordError()));
                }
            }
        });

        signUpViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<SignUpResult>() {
            @Override
            public void onChanged(@Nullable SignUpResult signUpResult) {
                if (signUpResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (signUpResult.getError() != null) {
                    showLoginFailed(signUpResult.getError());
                }
                if (signUpResult.getSuccess() != null) {
                    updateUiWithUser(signUpResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUpViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}