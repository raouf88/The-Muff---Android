package com.raouf.android.muff.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raouf.android.muff.Models.Movie;

import java.util.HashMap;

/**
 *  provides functions to add/remove movie to favorite list as well as storing and restoring the list locally.
 *  @author Raouf
 */
@SuppressWarnings("WeakerAccess")
public class Favorites {

    private Context context;
    private HashMap<String,Movie> movies = new HashMap<>();
    private OnFavoritesListener onFavoritesListener;

    /**
     * Construct object.
     * - restores favorites from local storage.
     * @param context needed to access storage
     */
    public Favorites(@NonNull Context context) {
        this.context = context;
        restoreFavorites();
    }


    public void setOnFavoritesListener(OnFavoritesListener onFavoritesListener) {
        this.onFavoritesListener = onFavoritesListener;
    }

    /***
     * add a movie to the list then update storage.
     * @param movie movie to add
     */
    public void addFavorite(@NonNull Movie movie){
        movies.put(movie.getId(),movie);
        storeFavorites();
        if (onFavoritesListener != null) onFavoritesListener.OnUpdate(movies);
    }

    /**
     * remove a movie to the list then update storage
     * @param movie movie to remove
     */
    public void removeFavorite(@NonNull Movie movie){
        movies.remove(movie.getId());
        storeFavorites();
        if (onFavoritesListener != null) onFavoritesListener.OnUpdate(movies);
    }

    /**
     * check if a movie is on the list.
     * @param movie movie to check
     * @return true of found in the list
     */
    public boolean isFavorite(@NonNull Movie movie){
        return movies.get(movie.getId()) != null;
    }

    public HashMap<String, Movie> getMovies() {
        return movies;
    }

    /**
     * store current list into storage
     */
    private void storeFavorites(){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("favorites", new Gson().toJson(movies));
        prefsEditor.apply();
    }

    /**
     * restore list from storage.
     */
    private void restoreFavorites(){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("favorites", null);
        if(json!=null){
            movies = gson.fromJson(json, new TypeToken<HashMap<String, Movie>>(){}.getType());
        }
    }

    /**
     * listens to {@link Favorites} operations.
     */
    public interface OnFavoritesListener{
        /**
         * called when list is updated.
         *
         * @param movies list of favorite movies
         * */
        void OnUpdate(HashMap<String,Movie> movies);
    }
}
