package com.vikram.discover.restaurantlist;

import com.vikram.discover.model.Restaurant;
import com.vikram.discover.network.GetRestaurantsInterface;
import com.vikram.discover.network.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantListPresenter {

    RestaurantView view;

    GetRestaurantsInterface getRestaurantsInterface;

    public RestaurantListPresenter(RestaurantView restaurantView) {
        this.view = restaurantView;
        getRestaurantsInterface = RetrofitClientInstance
                .getRetrofitInstance()
                .create(GetRestaurantsInterface.class);
    }

    public void setRestaurantsInterface(GetRestaurantsInterface getRestaurantsInterface) {
        this.getRestaurantsInterface = getRestaurantsInterface;
    }

    public void fetchNearbyRestaurants(String lat, String lng, int offset, int limit) {
        view.showProgressBar();

        Call<ArrayList<Restaurant>> call = getRestaurantsInterface.getRestaurants(lat, lng, offset, limit);
        call.enqueue(new Callback<ArrayList<Restaurant>>() {
            @Override
            public void onResponse(Call<ArrayList<Restaurant>> call, Response<ArrayList<Restaurant>> response) {
                view.setRestaurantList(response.body());
                view.notifyAdapter();
                view.hideProgressBar();
            }

            @Override
            public void onFailure(Call<ArrayList<Restaurant>> call, Throwable t) {
                view.hideProgressBar();
                view.showErrorDialog();
            }
        });
    }


}
