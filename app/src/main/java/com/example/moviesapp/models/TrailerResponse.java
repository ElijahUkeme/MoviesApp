package com.example.moviesapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    private int id;

    @SerializedName("results")
    private List<Trailer> trailers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }
}
