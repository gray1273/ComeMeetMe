package com.example.comemeetme.ui.signup;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class SignUpResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    SignUpResult(@Nullable Integer error) {
        this.error = error;
    }

    SignUpResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}