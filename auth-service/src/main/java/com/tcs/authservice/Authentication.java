package com.tcs.authservice;

public class Authentication {

        boolean Authenticated;
        String message;
        Authtoken authtoken;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setAuthtoken(Authtoken authtoken) {
        this.authtoken = authtoken;
    }

    public Authtoken getAuthtoken() {
        return authtoken;
    }

    public void setAuthenticated(boolean authenticated) {
        Authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return Authenticated;
    }
}