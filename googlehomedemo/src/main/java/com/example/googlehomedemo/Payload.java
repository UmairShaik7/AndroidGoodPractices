package com.example.googlehomedemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by umshaik on 5/20/17.
 */

public class Payload {
    @SerializedName("Temperature")
    @Expose
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

}
