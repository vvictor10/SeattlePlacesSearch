package com.grace.placessearch.service.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.placessearch.common.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.common.data.model.VenueResponse;
import com.grace.placessearch.common.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApiOrig;
import com.grace.placessearch.service.VenuesService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Deprecated
public class PlacesApiRetrofitOrig implements PlacesApiOrig {

    private final Retrofit retrofit;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final String currentLatLong;
    private final String apiVersion;

    private CallAdapter.Factory callAdapterFactory;


    public PlacesApiRetrofitOrig(String baseUrl, String apiVersion, String clientId, String clientSecret, String currentLatLong) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.currentLatLong = currentLatLong;
        this.apiVersion = apiVersion;

        retrofit = newInstance(baseUrl);
    }

    private Retrofit newInstance(String baseUrl) {
        Gson gson = getGsonInstance();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        HttpUrl.Builder urlBuilder = request.url().newBuilder();

                        // api version
                        urlBuilder.addQueryParameter("v", apiVersion).build();

                        // add client id
                        urlBuilder.addQueryParameter("client_id", clientId).build();

                        // add client secret
                        urlBuilder.addQueryParameter("client_secret", clientSecret).build();

                        // add current location
                        urlBuilder.addQueryParameter("ll", currentLatLong).build();

                        request = request.newBuilder().url(urlBuilder.build()).build();

                        return chain.proceed(request);
                    }
                })
                .readTimeout(PlacesApiOrig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        callAdapterFactory = PlacesRxJavaCallAdapterFactoryOrig.create();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl) //
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(callAdapterFactory)
                .build();

    }

    private Gson getGsonInstance() {
        return new GsonBuilder().create();
    }

    @Override
    public Observable<Result<VenuesResponse>> getTrendingVenues() {
        return retrofit.create(VenuesService.class).trendingVenues();
    }

    @Override
    public Observable<Result<VenuesResponse>> searchForVenues(String searchTerm) {
        return retrofit.create(VenuesService.class).search(searchTerm);
    }

    @Override
    public Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm) {
        return retrofit.create(VenuesService.class).suggestCompletion(searchTerm);
    }

    @Override
    public Observable<Result<VenueResponse>> getVenue(String venueId) {
        return retrofit.create(VenuesService.class).venue(venueId);
    }
}
