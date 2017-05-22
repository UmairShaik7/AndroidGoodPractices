package com.example.googlehomedemo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

/**
 * Created by ABC on 02/11/2016.
 */

public interface SetTVStatusAPI {

    @Headers({"appKey: bae175f3-4ef1-47d3-bc87-feb4c33fa061",
            "Content-Type: application/json"})
    @PUT("SmartTV_umair_shaik/Properties/status")
    Call<Void> setTVStatus(@Body TVStatusPayload payload);
}
