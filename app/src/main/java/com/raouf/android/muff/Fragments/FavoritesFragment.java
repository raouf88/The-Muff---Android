package com.raouf.android.muff.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raouf.android.muff.Adapters.MoviesAdapter;
import com.raouf.android.muff.BaseApplication;
import com.raouf.android.muff.Helpers.Favorites;
import com.raouf.android.muff.Models.Movie;
import com.raouf.android.muff.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 *  Favorite screen.
 *  - shows favorites in a list in a {@link RecyclerView}.
 *  @author Raouf
 */
public class FavoritesFragment extends Fragment {

    /**
     * list of favorite movies used in the list
     * */
    private ArrayList<Movie> movies;

    /**
     * adapter for the list.
     * */
    private MoviesAdapter moviesAdapter;

    /**
     * Required empty public constructor
     * */
    public FavoritesFragment() {}


    /**
     * Called to create the fragment view.
     * - returns {@link R.layout#fragment_favorites} as layout.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    /**
     * Called after the fragment view is created.
     * - setup {@link RecyclerView} to show list of favorites movies.
     * */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get favorites from BaseApplication
        Favorites favorites = ((BaseApplication) getActivity().getApplication()).getFavorites();
        // get movies list
        movies = new ArrayList<>(favorites.getMovies().values());
        // listens to update on the favorites array to update this list.
        favorites.setOnFavoritesListener(new Favorites.OnFavoritesListener() {
            @Override
            public void OnUpdate(HashMap<String, Movie> movies) {
                FavoritesFragment.this.movies = new ArrayList<>(movies.values());
                moviesAdapter.notifyDataSetChanged(FavoritesFragment.this.movies);
            }
        });
        // setup recyclerview
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_favorites_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        moviesAdapter = new MoviesAdapter(movies);
        recyclerView.setAdapter(moviesAdapter);

    }
}
