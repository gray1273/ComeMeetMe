package com.example.comemeetme.ui.signup;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comemeetme.R;
import com.example.comemeetme.data.LoginRepository;
import com.example.comemeetme.data.Result;
import com.example.comemeetme.data.model.LoggedInUser;

public class SignUpViewModel extends ViewModel {

    private MutableLiveData<SignUpFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<SignUpResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    SignUpViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<SignUpFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<SignUpResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new SignUpResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new SignUpResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new SignUpFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new SignUpFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new SignUpFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}