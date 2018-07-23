package com.vikram.discover;

import com.vikram.discover.model.Restaurant;
import com.vikram.discover.network.GetRestaurantsInterface;
import com.vikram.discover.restaurantlist.RestaurantListPresenter;
import com.vikram.discover.restaurantlist.RestaurantView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantListPresenterTest {

    @Mock
    private RestaurantView view;

    @Mock
    GetRestaurantsInterface mockedRestaurantInterface;

    @Mock
    Call<ArrayList<Restaurant>> mockedCall;

    private RestaurantListPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new RestaurantListPresenter(view);
    }

    @Test
    public void shouldCallUIMethodsInOrderWhenFetchNearbyRestaurantCalled() throws Exception {
        String lat = "37.422740";
        String lng = "-122.139956";
        int offset = 0;
        int limit = 50;

        mockedRestaurantInterface = mock(GetRestaurantsInterface.class);
        mockedCall = mock(Call.class);

        when(mockedRestaurantInterface.getRestaurants(lat,lng,offset,limit)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                Callback<ArrayList<Restaurant>> callback = invocation.getArgumentAt(0, Callback.class);

                callback.onResponse(mockedCall, Response.success(new ArrayList<Restaurant>()));
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));

        presenter.setRestaurantsInterface(mockedRestaurantInterface);

        presenter.fetchNearbyRestaurants(lat, lng, offset, limit);

        ArrayList<Restaurant> restaurants = new ArrayList<>();

        InOrder inOrder = Mockito.inOrder(view);

        inOrder.verify(view).showProgressBar();
        verify(view).setRestaurantList(restaurants);
        inOrder.verify(view).notifyAdapter();
        inOrder.verify(view).hideProgressBar();
    }

    @Test
    public void shouldCallShowErrorDialogWhenFetchNearbyRestaurantFails() throws Exception {
        String lat = "37.422740";
        String lng = "-122.139956";
        int offset = 0;
        int limit = 50;

        mockedRestaurantInterface = mock(GetRestaurantsInterface.class);
        mockedCall = mock(Call.class);

        when(mockedRestaurantInterface.getRestaurants(lat,lng,offset,limit)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                Callback<ArrayList<Restaurant>> callback = invocation.getArgumentAt(0, Callback.class);

                callback.onFailure(mockedCall, new Throwable());
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));

        presenter.setRestaurantsInterface(mockedRestaurantInterface);

        presenter.fetchNearbyRestaurants(lat, lng, offset, limit);

        InOrder inOrder = Mockito.inOrder(view);

        inOrder.verify(view).showProgressBar();
        inOrder.verify(view).hideProgressBar();
        inOrder.verify(view).showErrorDialog();
    }
}