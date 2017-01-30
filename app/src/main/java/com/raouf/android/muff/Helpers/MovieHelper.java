package com.raouf.android.muff.Helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.raouf.android.muff.BaseApplication;
import com.raouf.android.muff.Models.Movie;

import org.json.JSONObject;

import java.io.File;

/**
 *  provides functions for a movie to fetch poster , rating and release. add/remove favorite. share to social media
 *  @author Raouf
 */
public class MovieHelper {

    private Context context;
    private Movie movie;
    private File posterFile;
    private OnFetchListener onFetchListener;
    private Favorites favorites;

    /**
     * Contruct object
     * @param context Context of activity
     * @param movie movie to process.
     */
    public MovieHelper(@NonNull Context context, @NonNull Movie movie) {
        this.context = context;
        this.movie = movie;
        this.posterFile = new File(Environment.getExternalStorageDirectory()
                + File.separator + "Android" + File.separator + "data"
                + File.separator + "com.raouf.android.muff" + File.separator + "files",
                movie.getId()+".jpg");
        this.favorites = ((BaseApplication)context.getApplicationContext()).getFavorites();
    }

    public void setOnFetchListener(OnFetchListener onFetchListener) {
        this.onFetchListener = onFetchListener;
    }

    /**
     * Fetch movie poster image then return result through {@link OnFetchListener}
     */
    public void fetchPoster(){
        if (posterFile.exists()){
            if (onFetchListener != null) onFetchListener.OnPosterFetched(true,posterFile);
        } else {
            // download Image
            ImageDownloader imageDownloader = new ImageDownloader(movie.getPoster(),posterFile, new ImageDownloader.OnImageDownloaderListener() {
                @Override
                public void onSuccess(File file) {
                    if (onFetchListener != null) onFetchListener.OnPosterFetched(true,posterFile);
                }

                @Override
                public void onFail(String error) {
                    if (onFetchListener != null) onFetchListener.OnPosterFetched(false,posterFile);
                }
            });
            imageDownloader.download();
        }
    }

    /**
     * Fetch movie rating and release date then return result through {@link OnFetchListener}
     */
    public void fetchRatingAndRelease(){
        new HttpPost("http://www.omdbapi.com/?i=" + movie.getId(), "", new HttpPost.HttpPostListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject json = new JSONObject(msg);
                    if (json.getString("Response").equals("True")){
                        movie.setReleased(json.getString("Released"));
                        try {
                            movie.setRating(Float.parseFloat(json.getString("imdbRating")));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        if (onFetchListener != null) onFetchListener.OnRatingAndReleaseFetched(true,movie.getRating(),movie.getReleased());
                    } else {
                        if (onFetchListener != null) onFetchListener.OnRatingAndReleaseFetched(false,0,null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (onFetchListener != null) onFetchListener.OnRatingAndReleaseFetched(false,0,null);
                }
            }

            @Override
            public void onFail() {
                if (onFetchListener != null) onFetchListener.OnRatingAndReleaseFetched(false,0,null);
            }
        }).send();

    }

    /***
     * share movie through Intent
     */
    public void share(){
        if (posterFile.exists()){
            //  share image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,  movie.getTitle()+" ("+movie.getYear()+")");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(posterFile));
            shareIntent.setType("image/jpeg");
            context.startActivity(Intent.createChooser(shareIntent, "Share Movie"));
        }
    }

    /**
     * add movie to favorite if not added already. else remove it.
     */
    public void favoriteClick(){
        if (favorites.isFavorite(movie)){
            favorites.removeFavorite(movie);
        } else {
            favorites.addFavorite(movie);
        }
    }

    /**
     * @return true if movie is in favorite already.
     */
    public boolean isFavorite(){
        return favorites.isFavorite(movie);
    }

    /**
     * listens to {@link MovieHelper} operations.
     */
    public interface OnFetchListener{
        /**
         * called when fetching poster image finish
         * @param success true if success
         * @param posterImage poster image file
         */
        void OnPosterFetched(boolean success,File posterImage);

        /**
         * called when fetching rating and release finish
         * @param success  true if success
         * @param rating rating of movie. -1 if non
         * @param released release date of movie.
         */
        void OnRatingAndReleaseFetched(boolean success,float rating, String released);
    }
}
