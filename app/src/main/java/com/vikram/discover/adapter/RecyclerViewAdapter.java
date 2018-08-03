package com.vikram.discover.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vikram.discover.R;
import com.vikram.discover.model.Restaurant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Restaurant> restaurants;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.restaurantName) TextView name;
        @BindView(R.id.restaurantDescription) TextView description;
        @BindView(R.id.ratingCount) TextView ratingCount;
        @BindView(R.id.status) TextView status;
        @BindView(R.id.deliveryFee) TextView deliveryFee;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.ratingBar) RatingBar ratingBar;

        View view;

        public RestaurantHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        public void bind(final Restaurant restaurant, final int position, final OnItemClickListener onItemClickListener) {
            name.setText(restaurant.getName());
            description.setText(restaurant.getDescription());
            status.setText(restaurant.getStatus());
            ratingCount.setText(String.valueOf(restaurant.getAverageRating()));
            deliveryFee.setText(context.getString(R.string.delivery_fee) + restaurant.getDeliveryFee());

            Picasso.get().load(restaurant.getImageUrl()).into(image);

            ratingBar.setRating(restaurant.getAverageRating());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(restaurant, position);
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (restaurants == null) {
            return 0;
        }
        return restaurants.size();
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RestaurantHolder) holder).bind(restaurants.get(position), position, onItemClickListener);
    }
}
