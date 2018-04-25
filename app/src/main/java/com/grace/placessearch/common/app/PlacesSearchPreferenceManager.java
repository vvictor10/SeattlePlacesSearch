package com.grace.placessearch.common.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.grace.placessearch.BuildConfig;
import com.grace.placessearch.common.app.injection.qualifier.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlacesSearchPreferenceManager {

    public static final String PLACES_SEARCH_PREFERENCES_KEY = BuildConfig.PROJECT_NAME;
    private static final String FAVORITE_VENUE_PREFERENCES_CONTENT = PlacesSearchPreferenceManager.class.getSimpleName() + ".favoriteVenuePreferences";

    private final SharedPreferences sharedPreferences;
    private FavoritePlacesPreferences favoriteVenuePreferences;

    @Inject
    public PlacesSearchPreferenceManager(@ForApplication Context context) {
        this.sharedPreferences = context.getSharedPreferences(PLACES_SEARCH_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public FavoritePlacesPreferences getFavoriteVenuePreferences() {
        if (favoriteVenuePreferences != null) {
            return favoriteVenuePreferences;
        } else {
            String jsonString = sharedPreferences.getString(FAVORITE_VENUE_PREFERENCES_CONTENT, null);
            if (jsonString == null) {
                return new FavoritePlacesPreferences();
            } else {
                return new Gson().fromJson(jsonString, FavoritePlacesPreferences.class);
            }
        }
    }

    public void setFavoriteVenuePreferences(FavoritePlacesPreferences favoriteVenuePreferences) {
        this.favoriteVenuePreferences = favoriteVenuePreferences;
        sharedPreferences.edit().putString(FAVORITE_VENUE_PREFERENCES_CONTENT, new Gson().toJson(favoriteVenuePreferences)).commit();
    }

}
