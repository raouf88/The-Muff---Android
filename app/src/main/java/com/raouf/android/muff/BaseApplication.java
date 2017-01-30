package com.raouf.android.muff;

import android.app.Application;

import com.raouf.android.muff.Helpers.Favorites;


/**
 * Application Base. first to run and always accessible.
 * - initialize {@link Favorites} object and hold it.
 *
 *  @author Raouf
 */
public class BaseApplication extends Application {

    /**
     * Favorites object to be accessible from any part of the app.
     * */
    private Favorites favorites;

    /**
     * called when application is created. initialize favorite object.
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        favorites = new Favorites(this);
    }

    /**
     * @return Favorite object that hold the favorite list.
     * */
    public Favorites getFavorites() {
        return favorites;
    }
}
