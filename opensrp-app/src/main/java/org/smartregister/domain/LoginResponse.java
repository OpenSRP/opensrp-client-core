package org.smartregister.domain;

import org.json.JSONObject;
import org.smartregister.domain.jsonmapping.LoginResponseData;

public enum LoginResponse {
    SUCCESS("Login successful."),
    NO_INTERNET_CONNECTIVITY("No internet connection. Please ensure data connectivity"),
    MALFORMED_URL("Incorrect url"),
    UNKNOWN_RESPONSE("Dristhi login failed. Try later"),
    UNAUTHORIZED("Please check the credentials"),
    UNAUTHORIZED_CLIENT("Please check the oauth client credentials"),
    TIMEOUT("The server could not be reached. Try again"),
    SUCCESS_WITH_EMPTY_RESPONSE("The server returned an empty response. Try again"),
    SUCCESS_WITHOUT_USER_DETAILS("User information was not accessible. Try again"),
    SUCCESS_WITHOUT_USER_USERNAME("User name was not accessible. Try again"),
    SUCCESS_WITHOUT_USER_PREFERREDNAME("User preferred name was not accessible. Try again"),
    SUCCESS_WITHOUT_TEAM_DETAILS("User team information was not accessible. Try again"),
    SUCCESS_WITHOUT_TEAM_LOCATION("User team location was not accessible. Try again"),
    SUCCESS_WITHOUT_TEAM_LOCATION_UUID("User team location uuid was not accessible. Try again"),
    SUCCESS_WITHOUT_TEAM_UUID("User team UUID was not accessible. Try again"),
    SUCCESS_WITHOUT_TEAM_NAME("User team name was not accessible. Try again"),
    SUCCESS_WITHOUT_USER_LOCATION("User location was not accessible. Try again"),
    SUCCESS_WITHOUT_TIME_DETAILS("Time Details was not accessible. Try again"),
    SUCCESS_WITHOUT_TIME("Server time was not accessible. Try again"),
    SUCCESS_WITHOUT_TIME_ZONE("Server time zone was not accessible. Try again"),
    CUSTOM_SERVER_RESPONSE("Custom server response.");


    private LoginResponseData payload;
    private String message;
    private JSONObject rawData;

    LoginResponse(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public LoginResponseData payload() {
        return payload;
    }

    public LoginResponse withPayload(LoginResponseData payload) {
        this.payload = payload;
        return this;
    }

    public LoginResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public JSONObject getRawData() {
        return rawData;
    }

    public void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }
}
