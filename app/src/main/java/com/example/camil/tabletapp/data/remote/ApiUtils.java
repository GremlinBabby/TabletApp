package com.example.camil.tabletapp.data.remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://fcm.googleapis.com/";
    //public static final String BASE_URL = "http://ptsv2.com/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
