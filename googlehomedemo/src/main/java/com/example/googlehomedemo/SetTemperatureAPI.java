package com.example.googlehomedemo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

/**
 * Created by ABC on 02/11/2016.
 */

public interface SetTemperatureAPI {
    /*static final Map<String, String> header;

    static {
        header = new HashMap<>();
        header.put("appKey", "bae175f3-4ef1-47d3-bc87-feb4c33fa061");
        header.put("Content-Type", "application/json");
    }*/
    @Headers({"appKey: bae175f3-4ef1-47d3-bc87-feb4c33fa061",
            "Content-Type: application/json"})
    @PUT("Properties/Temperature")
    Call<String> getTask(@Body TemperaturePayload temperaturePayload);
}
