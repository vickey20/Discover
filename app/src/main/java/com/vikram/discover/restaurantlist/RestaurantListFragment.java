package com.vikram.discover.restaurantlist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vikram.discover.R;
import com.vikram.discover.adapter.RecyclerViewAdapter;
import com.vikram.discover.model.Restaurant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantListFragment extends Fragment implements RestaurantView {

    private static final String STATE_RESTAURANTS = "RESTAURANTS";
    private static final String STATE_RECYCLE_LAYOUT = "RECYCLE_LAYOUT";

    private static final String DEFAULT_LAT = "37.422740";
    private static final String DEFAULT_LNG = "-122.139956";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 50;

    private ArrayList<Restaurant> restaurants;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.contentLoadingProgressBar) ContentLoadingProgressBar contentLoadingProgressBar;

    private RecyclerViewAdapter adapter;

    private RestaurantListPresenter presenter;

    private Context context;
    OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {}

    public static RestaurantListFragment newInstance() {
        return new RestaurantListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new RestaurantListPresenter(this);
        restaurants = new ArrayList<>();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRecyclerView();
        presenter.fetchNearbyRestaurants(DEFAULT_LAT, DEFAULT_LNG, DEFAULT_OFFSET, DEFAULT_LIMIT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_RECYCLE_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(STATE_RESTAURANTS, restaurants);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            restaurants = savedInstanceState.getParcelableArrayList(STATE_RESTAURANTS);
            setRecyclerView();
            Parcelable savedRecyclerViewState = savedInstanceState.getParcelable(STATE_RECYCLE_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void setRecyclerView() {
        adapter = new RecyclerViewAdapter(context);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setRestaurantList(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public void notifyAdapter() {
        adapter.setRestaurants(restaurants);
        hideProgressBar();
    }

    @Override
    public void showProgressBar() {
        contentLoadingProgressBar.show();
        recyclerView.setVisibility(RecyclerView.GONE);
    }

    @Override
    public void hideProgressBar() {
        contentLoadingProgressBar.hide();
        recyclerView.setVisibility(RecyclerView.VISIBLE);
    }

    @Override
    public void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(getString(R.string.dialog_message));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.dialog_button_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.fetchNearbyRestaurants(DEFAULT_LAT, DEFAULT_LNG, DEFAULT_OFFSET, DEFAULT_LIMIT);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
