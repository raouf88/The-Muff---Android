package com.raouf.android.muff.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.raouf.android.muff.Adapters.MoviesAdapter;
import com.raouf.android.muff.Helpers.Search;
import com.raouf.android.muff.Models.Movie;
import com.raouf.android.muff.R;

import java.util.ArrayList;


/**
 *  Search screen.
 *  - Offers the capability to search for movies using omdbapi through {@link Search}.
 *  - shows results in a list in a {@link RecyclerView}.
 *  @author Raouf
 */
public class SearchFragment extends Fragment {

    /**
     * loading progress bar that is placed in the ActionBar
     * */
    private ProgressBar progressBar;

    /**
     * serach helper that offers search through omdbapi
     * */
    private Search search = new Search();

    /**
     * Required empty public constructor
     * */
    public SearchFragment() {}

    /**
     * Called when fragment is created.
     * - allow option menu to be created from this fragment.
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // allow option menu to be created from this fragment
        setHasOptionsMenu(true);
    }

    /**
     * Called to create the fragment view.
     * - returns {@link R.layout#fragment_serach} as layout.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_serach, container, false);
    }

    /**
     * Called after the fragment view is created.
     * - setup {@link RecyclerView} to show list of movies from {@link #search}
     * */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setup recyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_search_recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        final MoviesAdapter moviesAdapter = new MoviesAdapter(search.getMovies());
        recyclerView.setAdapter(moviesAdapter);
        // when list scrolls to buttom, fetch more search results if possib
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (search.isSearching() || !search.canSearchMore())
                    return;
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount-2) {
                    progressBar.setAlpha(1);
                    search.searchMore();
                }
            }
        });
        // set callbacks listeners for search results
        search.setOnSearchListener(new Search.OnSearchListener() {
            @Override
            public void onFail(@NonNull String error) {
                // hide progressBar
                progressBar.setAlpha(0);
                // show error
                new AlertDialog.Builder(getActivity())
                        .setTitle("Unsuccessful")
                        .setMessage(error)
                        .setPositiveButton(android.R.string.yes, null)
                        .show();
            }

            @Override
            public void onSuccess(@NonNull ArrayList<Movie> movies, int totalResults) {
                // hide progressBar
                progressBar.setAlpha(0);
                // refresh list
                if (movies.size() == 0)
                    moviesAdapter.notifyDataSetChanged();
                else
                    moviesAdapter.notifyItemRangeInserted(linearLayoutManager.getItemCount()-1,movies.size()-1);
            }
        });

    }

    /**
     * Called to create menu.
     * - inflate menu with {@link R.menu#menu_search}.
     * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Called after menu is created.
     * - set {@link #progressBar}
     * - setup {@link SearchView} to do search using {@link #search}
     * */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // set progressBar
        progressBar = (ProgressBar) menu.findItem(R.id.menu_search_progressBar).getActionView();
        // setup searchView
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_searchView).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Movies");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // hide keyboard
                searchView.clearFocus();
                // clear old results
                search.clear();
                // show progressBar
                progressBar.setAlpha(1);
                // search passed query
                search.search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
