package com.raouf.android.muff.Models;


import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 *  Movie data model. holds movie details.
 *  @author Raouf
 */
public class Movie {
    private String id,title,year,released,poster;
    private float rating = -1;

    /**
     * Construct the object from jsonObject
     * @param jsonObject JSONObject carrying movie details.
     * @throws Exception if failed to parse json
     */
    public Movie(@NonNull JSONObject jsonObject) throws Exception {
        this.id = jsonObject.getString("imdbID");
        this.title = jsonObject.getString("Title");
        this.year = jsonObject.getString("Year");
        this.poster = jsonObject.getString("Poster");
    }

    /***
     * @return imdb movie id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return movie title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return movie year.
     */
    public String getYear() {
        return year;
    }

    /**
     * @return release date of the movie
     */
    public String getReleased() {
        return released;
    }

    /**
     * @return movie poster image link.
     */
    public String getPoster() {
        return poster;
    }

    /**
     * @return imdb rating of the movie
     */
    public float getRating() {
        return rating;
    }

    /**
     * @param released release date of the movie
     */
    public void setReleased(String released) {
        this.released = released;
    }

    /**
     * @param rating imdb rating of the movie
     */
    public void setRating(float rating) {
        this.rating = rating;
    }
}
