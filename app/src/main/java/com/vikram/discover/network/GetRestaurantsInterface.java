package com.vikram.discover.network;

import com.vikram.discover.model.Restaurant;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRestaurantsInterface {

    @GET("/v2/restaurant/")
    Call<ArrayList<Restaurant>> getRestaurants(@Query("lat") String lat, @Query("lng") String lng, @Query("offset") int offset, @Query("limit") int limit);
}
