package com.example.googlehomedemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TemperaturePayload {
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
