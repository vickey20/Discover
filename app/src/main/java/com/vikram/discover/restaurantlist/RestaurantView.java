package com.vikram.discover.restaurantlist;

import com.vikram.discover.model.Restaurant;

import java.util.ArrayList;

interface RestaurantView {
    void setRestaurantList(ArrayList<Restaurant> restaurants);
    void notifyAdapter();
    void showProgressBar();
    void hideProgressBar();
    void showErrorDialog();
}
