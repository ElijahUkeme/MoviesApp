package com.example.moviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.moviesapp.activities.HomeActivity;
import com.example.moviesapp.activities.MovieDetailsActivity;
import com.example.moviesapp.adapter.MovieAdapter;
import com.example.moviesapp.api.ServerClass;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.interfaces.OnMovieListener;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.MovieSearchResponse;
import com.example.moviesapp.utils.Credentials;
import com.example.moviesapp.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMovieListener {

    private MovieListViewModel movieListViewModel;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    Toolbar toolbar;
    boolean isPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        //for getting popular movies
        observePopularMoviesRequest();
        configureRecyclerView();
        //for getting search movies
        //setUpSearchRequest();
        obServeChanges();
        configureRecyclerView();


        //recyclerview pagination
        //loading the next page of the response

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)){
                    if (isPopular){
                        //here we display the next page result for the popular movies
                        movieListViewModel.searchNextMoviePage();
                    }else {
                        //here we need to display the next search result of the response in the next page of the api
                        movieListViewModel.searchNextPage();
                    }
                }
            }
        });

        Log.d("Main","Is popular before clicking on the search button "+isPopular);
    }

    private void observePopularMoviesRequest(){
        movieListViewModel.searchMoviesPage(1);
        movieListViewModel.getPopularMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {


                //observing any data changes
                if (movieModels !=null){
                    for (MovieModel movieModel: movieModels){
                        Log.d("Main","The popular movie is called");
                        Log.d("Main","The movies images are: "+movieModel.getPoster_path());
                        movieAdapter.setMovies(movieModels);
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }
    private void obServeChanges(){
        setUpSearchRequest();
        movieListViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                try {

                //observing any data changes
                if (movieModels !=null){
                    for (MovieModel movieModel: movieModels){
                        Log.d("Main","The Search method is called");
                        Log.d("Main","The fast movies are: "+movieModel.getPoster_path());
                        movieAdapter.setMovies(movieModels);
                    }
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void searchMovies(String query,int pageNumber){
        movieListViewModel.searchMovies(query, pageNumber);
    }

    private void configureRecyclerView(){
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getRetrofitResponse(){
        MovieApi movieApi = ServerClass.getMovieApi();
        Call<MovieSearchResponse> responseCall = movieApi
                .getPopularMovies(Credentials.API_KEY,1);
                //.searchForMovies(Credentials.API_KEY,"Jack Reacher",1);

        responseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.code()==200){
                    Log.d("Main","The response code is "+response.code());

                    List<MovieModel> movieModelList = new ArrayList<>(response.body().getMovies());
                    for (MovieModel movie: movieModelList){
                        Log.d("Main","The overviews are "+movie.getPoster_path());
                    }
                }else {
                    try {
                        Log.d("Main","The response code is "+response.code());
                        Log.d("Main","The error message is "+response.errorBody().toString());
                    }catch (Exception e){
                        e.getMessage();
                        Log.d("Main","The error in the catch block is "+e.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                t.getMessage();
                Log.d("Main","The error in the throwable is "+t.getMessage());

            }
        });
    }

    private void  getMovieById(){
        MovieApi movieApi = ServerClass.getMovieApi();
        Call<MovieModel> responseCall = movieApi.getMoviesById(550,Credentials.API_KEY);

        responseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code()==200){
                    MovieModel movieModel = response.body();
                    Log.d("Main",movieModel.getTitle());
                }else {
                    Log.d("Main",response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    @Override
    public void onMovieClick(int position) {

        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra("movie",movieAdapter.getSelectedMovie(position));
        startActivity(intent);

    }

    @Override
    public void onCategoryClick(String category) {

    }
    private void setUpSearchRequest(){
        final SearchView searchView = findViewById(R.id.searView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                movieListViewModel.searchMovies(query,1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Main","You are tapped");
                isPopular=false;
            }
        });
    }
}