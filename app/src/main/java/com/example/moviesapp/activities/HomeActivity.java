package com.example.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.moviesapp.MainActivity;
import com.example.moviesapp.R;
import com.example.moviesapp.adapter.HomeMovieAdapter;
import com.example.moviesapp.database.FavouriteDbHelper;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<MovieModel> moviesList;
    private HomeMovieAdapter adapter;
    private MovieListViewModel movieListViewModel;
    private FavouriteDbHelper favouriteDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recyclerview_home);
        swipeRefreshLayout = findViewById(R.id.main_content_home);
        Toolbar toolbar = findViewById(R.id.toolBar_home);
        favouriteDbHelper = new FavouriteDbHelper(this);
        moviesList = new ArrayList<>();
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        setSupportActionBar(toolbar);

        initViews();

    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
    private void initViews(){
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Movies...");
        pd.setCancelable(false);
        pd.show();
        moviesList = new ArrayList<>();
        adapter = new HomeMovieAdapter(this,moviesList);
        if (getActivity().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                moviesList.clear();
                initViews();
                Toast.makeText(HomeActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)){
                    movieListViewModel.searchNextMoviePage();
                }
            }
        });
        checkSortOrder();



    }
    private void observeFavoriteSorting(){
                moviesList.clear();
                moviesList.addAll(favouriteDbHelper.getAllFavouriteMovies());
                recyclerView.setAdapter(new HomeMovieAdapter(getApplicationContext(),moviesList));
                recyclerView.smoothScrollToPosition(0);
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                pd.dismiss();

    }

    private void observePopularMoviesRequest(){
        moviesList.clear();
        movieListViewModel.searchMoviesPage(1);
        movieListViewModel.getPopularMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {

                    //observing any data changes
                    if (movieModels !=null){
                        for (MovieModel movieModel: movieModels){
                            Log.d("Main","The movies id are: "+movieModel.getMovie_id());
                            //adapter = new HomeMovieAdapter(getApplicationContext(),movieModels);
                        }
                        List<MovieModel> modelList = new ArrayList<>();
                        modelList.addAll(movieModels);
                        recyclerView.setAdapter(new HomeMovieAdapter(getApplicationContext(),modelList));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);

                        }
                        pd.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }
    private void observeNowPlayingMoviesRequest(){
        movieListViewModel.searchMoviesPage(1);
        movieListViewModel.getNowPlayingMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {


                    //observing any data changes
                    if (movieModels !=null){
                        for (MovieModel movieModel: movieModels){
                            Log.d("Main","The movies title are: "+movieModel.getTitle());
                            moviesList.add(movieModel);
                        }

                        //recyclerView.setAdapter(new HomeMovieAdapter(getApplicationContext(),moviesList));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        pd.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }
    private void observeMoviesTopRatedRequest(){
        movieListViewModel.searchMoviesPage(1);
        movieListViewModel.getTopRatedMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {


                    //observing any data changes
                    if (movieModels !=null){
                        for (MovieModel movieModel: movieModels){
                            Log.d("Main","The movies title are: "+movieModel.getTitle());
                            moviesList.add(movieModel);
                        }
                        //recyclerView.setAdapter(new HomeMovieAdapter(getApplicationContext(),moviesList));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        pd.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }
    private void observeUpcomingMoviesRequest(){
        movieListViewModel.searchMoviesPage(1);
        movieListViewModel.getUpcomingMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {


                    //observing any data changes
                    if (movieModels !=null){
                        for (MovieModel movieModel: movieModels){
                            Log.d("Main","The movie titles are: "+movieModel.getTitle());
                            moviesList.add(movieModel);
                        }
                        //recyclerView.setAdapter(new HomeMovieAdapter(getApplicationContext(),moviesList));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        pd.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_home:
                toSearchScreen();
                return true;
            case R.id.settings:
                toSettingsScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toSearchScreen(){
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void toSettingsScreen(){
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("Main","SharedPreference updated");
        checkSortOrder();
    }
    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(this.getString(R.string.pref_sort_order_key),
                            this.getString(R.string.pref_most_popular));

        if (sortOrder.equals(this.getString(R.string.pref_most_popular))){
            Log.d("Main","Sorting Order: Most Popular");
            observePopularMoviesRequest();
        }else if (sortOrder.equals(this.getString(R.string.pref_top_rated))){
            Log.d("Main","Sorting Order: Top Rated");
            observeMoviesTopRatedRequest();
        }else if (sortOrder.equals(this.getString(R.string.pref_now_playing))){
            Log.d("Main","Sorting Order: Now Playing");
            observeNowPlayingMoviesRequest();
        }else if (sortOrder.equals(this.getString(R.string.pref_upcoming))){
            Log.d("Main","Sorting Order: Upcoming");
            observeUpcomingMoviesRequest();
        }else if (sortOrder.equals(this.getString(R.string.favorite))){
            observeFavoriteSorting();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (moviesList.isEmpty()){
            observePopularMoviesRequest();
        }else {
            observePopularMoviesRequest();
        }
    }
}