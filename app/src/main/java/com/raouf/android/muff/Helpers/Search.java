package com.raouf.android.muff.Helpers;

import android.support.annotation.NonNull;

import com.raouf.android.muff.Models.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 *  provides the functions to search a movie from Omdb api.
 *  @author Raouf
 */
public class Search {

    private OnSearchListener onSearchListener;
    private boolean canSearchMore = false;
    private boolean searching = false;
    private ArrayList<Movie> movies = new ArrayList<>();
    private int totalResults = 0,currentPage = 1;
    private String textToSearch = "";
    private HttpPost httpPost;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    /**
     * clear results and reset.
     * */
    public void clear() {
        this.textToSearch = "";
        this.totalResults = 0;
        this.currentPage = 1;
        this.movies.clear();
        if (onSearchListener != null) onSearchListener.onSuccess(movies,totalResults);
    }

    /**
     * @return true if current search can fetch more results
     */
    public boolean canSearchMore() {
        return canSearchMore;
    }

    /**
     * @return true if searching in progress.
     */
    public boolean isSearching() {
        return searching;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }


    /***
     * search a movie using text.
     * @param textToSearch title of movie to search.
     */
    public void search(@NonNull  String textToSearch){
        try {
            this.textToSearch = URLEncoder.encode(textToSearch.trim().replace(" ","+"), "utf-8");
            this.totalResults = 0;
            this.currentPage = 1;
            this.movies.clear();
            search();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * fetch more results if possible.
     */
    public void searchMore(){
        if (canSearchMore) {
            currentPage += 1;
            search();
        }
    }

    /**
     * calls search api of omdbpi
     */
    private void search() {

        if (httpPost != null) httpPost.cancel(true);
        searching = true;
        httpPost = new HttpPost("http://www.omdbapi.com/?type=movie&y=true&page="+currentPage+"&s="+textToSearch, "", new HttpPost.HttpPostListener() {
            @Override
            public void onFinish() {
                searching = false;
            }

            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject json = new JSONObject(msg);
                    if (json.getString("Response").equals("True")){
                        JSONArray jsonArray = new JSONArray(json.getString("Search"));
                        for (int i=0; i < jsonArray.length(); i++) {
                            movies.add(new Movie(jsonArray.getJSONObject(i)));
                        }
                        totalResults = Integer.parseInt(json.getString("totalResults"));
                        canSearchMore = movies.size()<totalResults;
                        if (onSearchListener != null) onSearchListener.onSuccess(movies,totalResults);
                    } else {
                        if (onSearchListener != null) onSearchListener.onFail(json.getString("Error"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (onSearchListener != null) onSearchListener.onFail(e.getMessage());
                }

            }

            @Override
            public void onFail() {
                if (onSearchListener != null) onSearchListener.onFail("Error communicating with the server. Please make sure you have internet access.");
            }
        });
        httpPost.send();

    }


    /**
     * listener for {@link Search} operations.
     */
    public interface OnSearchListener{
        /**
         * called when error happens
         * @param error error message
         */
        void onFail(@NonNull String error);

        /**
         * called when search is a success
         * @param movies result of movies
         * @param totalResults total number of results found.
         */
        void onSuccess(@NonNull ArrayList<Movie> movies, int totalResults);
    }
}
