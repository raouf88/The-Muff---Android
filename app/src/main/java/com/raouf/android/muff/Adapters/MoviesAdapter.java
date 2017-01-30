package com.raouf.android.muff.Adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.raouf.android.muff.Helpers.MovieHelper;
import com.raouf.android.muff.Models.Movie;
import com.raouf.android.muff.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for recycler views for movies listing .
 * inflate the movie data into the each cell of the recycler view.
 *
 * <b>Requires: </b> {@link R.layout#view_movie}
 * @author Raouf
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>  {

    /**
     * list of {@link Movie} to show
     */
    private List<Movie> movies;


    /**
     * @param movies list of movies to show.
     */
    public MoviesAdapter(@NonNull List<Movie> movies) {
        this.movies = movies;
    }

    public void notifyDataSetChanged(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
    /**
     * called to get number of items
     * @return {@link #movies} size
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * called to get {@link ViewHolder}
     * @return viewholder
     */
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_movie, parent, false);
        return new ViewHolder(v);
    }

    /**
     * bind the item to the view.
     * sets movie title , poster , rating , dates.
     * @param holder view holder
     * @param position items position
     */
    @Override
    public void onBindViewHolder(final MoviesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // remember the position in the holder
        holder.position = position;
        // get movie to fill from the list
        final Movie movie = movies.get(position);
        // set title
        holder.titleTextView.setText(movie.getTitle()+" ("+movie.getYear()+")");
        // create movie helper object to fetch extra details of movie from api
        final MovieHelper movieHelper = new MovieHelper(holder.itemView.getContext(),movie);
        movieHelper.setOnFetchListener(new MovieHelper.OnFetchListener() {
            @Override
            public void OnPosterFetched(boolean success, File posterImage) {
                if (holder.position == position){
                    if(success)
                        holder.posterImageView.setImageURI(Uri.fromFile(posterImage));
                    else
                        holder.posterImageView.setImageResource(R.drawable.image_noposter);
                }

            }

            @Override
            public void OnRatingAndReleaseFetched(boolean success, float rating, String released) {
                if (holder.position == position){
                    if(success){
                        holder.releaseTextView.setText(released);
                        if (rating != -1){
                            holder.ratingBar.setVisibility(View.VISIBLE);
                            holder.ratingBar.setRating(rating);
                        }

                    }
                }
            }
        });
        // set poster
        holder.posterImageView.setImageBitmap(null);
        movieHelper.fetchPoster();
        // set release and ratings
        holder.releaseTextView.setText(movie.getReleased());
        holder.ratingBar.setVisibility(View.GONE);
        if (movie.getReleased() == null || movie.getRating() != -1){
            movieHelper.fetchRatingAndRelease();
        }
        if (movie.getRating() != -1){
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(movie.getRating());
        }
        // setup share click
        holder.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieHelper.share();
            }
        });
        // set favorite status
        if (movieHelper.isFavorite()){
            holder.favoriteImageButton.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.favoriteImageButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        // setup favorite click
        holder.favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieHelper.isFavorite()){
                    holder.favoriteImageButton.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    holder.favoriteImageButton.setImageResource(R.drawable.ic_star_black_24dp);
                }
                movieHelper.favoriteClick();

            }
        });


    }

    /**
     * {@link RecyclerView.ViewHolder} to hold and define the views displayed on this adapter.
     * <b>Requires: </b> {@link R.layout#view_movie}
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        int position;
        TextView titleTextView , releaseTextView ;
        RatingBar ratingBar;
        ImageView posterImageView;
        ImageButton favoriteImageButton, shareImageButton;

        ViewHolder(@NonNull View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.view_movie_titleTextView);
            releaseTextView = (TextView) v.findViewById(R.id.view_movie_releaseTextView);
            posterImageView = (ImageView) v.findViewById(R.id.view_movie_posterImageView);
            ratingBar = (RatingBar) v.findViewById(R.id.view_movie_ratingBar);
            favoriteImageButton = (ImageButton) v.findViewById(R.id.view_movie_favoriteImageButton);
            shareImageButton = (ImageButton) v.findViewById(R.id.view_movie_shareImageButton);
        }
    }

}