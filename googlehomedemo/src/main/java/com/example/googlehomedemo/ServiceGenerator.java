package com.example.googlehomedemo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ABC on 02/11/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "https://academic.cloud.thingworx.com/Thingworx/Things/SmartHomeThermostat_umair_shaik/";

    static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Retrofit builder =
                    new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
            return builder;

        } else return retrofit;
    }
}