package com.example.moviesapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.adapter.TrailerAdapter;
import com.example.moviesapp.api.ServerClass;
import com.example.moviesapp.database.FavouriteDbHelper;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.MovieSearchResponse;
import com.example.moviesapp.models.Trailer;
import com.example.moviesapp.models.TrailerResponse;
import com.example.moviesapp.utils.Credentials;
import com.example.moviesapp.viewmodel.MovieListViewModel;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDetailActivity extends AppCompatActivity {

    private TextView title, plotSynopsis, userRating, releaseDate;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private List<Trailer> trailerList;
    private TrailerAdapter adapter;
    private int movie_id;
    private MovieListViewModel movieListViewModel;
    private FavouriteDbHelper favouriteDbHelper;
    private MovieModel movieModel;
    private MaterialFavoriteButton materialFavoriteButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail_activity);
        title = findViewById(R.id.movieTitle);
        plotSynopsis = findViewById(R.id.plotSynopsis);
        userRating = findViewById(R.id.userRating);
        releaseDate = findViewById(R.id.releaseDate);
        imageView = findViewById(R.id.thumbnail_image_header);
        recyclerView = findViewById(R.id.recyclerview_trailer);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        favouriteDbHelper = new FavouriteDbHelper(this);
        materialFavoriteButton = findViewById(R.id.material_button_favorite);
        movieModel = new MovieModel();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCollapsingToolBar();
        getDataFromIntent();
        initViews();

        if (getIntent().hasExtra("movie")) {
            MovieModel movieModel = getIntent().getParcelableExtra("movie");
            movie_id = movieModel.getMovie_id();
            try {

                if (favouriteDbHelper.isExist(movieModel.getTitle())) {
                    materialFavoriteButton.setFavorite(true);
                    materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite) {
                                showAddingDialog(buttonView);

                            } else {
                                showRemovingDialog(buttonView);
                            }
                        }
                    });
                } else {
                    materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite) {
                                showAddingDialog(buttonView);
                            } else {
                                showRemovingDialog(buttonView);
                            }
                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getDataFromIntent() {
        if (getIntent().hasExtra("movie")) {
            MovieModel movieModel = getIntent().getParcelableExtra("movie");
            movie_id = movieModel.getMovie_id();
            Log.d("Main", "The movie id in the getData method is: " + movie_id);

            title.setText(movieModel.getTitle());
            plotSynopsis.setText(movieModel.getOverview());
            userRating.setText(String.valueOf(movieModel.getVote_average()));
            releaseDate.setText(movieModel.getRelease_date());
            Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movieModel.getPoster_path())
                    .placeholder(R.drawable.load).into(imageView);
        }
    }

    private void initCollapsingToolBar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Movie Details");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void initViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        observeMovieTrailerSearch();
    }

    private void observeMovieTrailerSearch() {
        //querying the movies trailer from the viewmodel class

        movieListViewModel.searchMovieTrailer(movie_id);
        movieListViewModel.getTrailerMovies().observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                try {
                    if (trailers != null) {
                        for (Trailer trailer : trailers) {
                            trailerList.add(trailer);

                            recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailerList));
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addToFavourite(){
        if (getIntent().hasExtra("movie")) {
            MovieModel selectedMovie = getIntent().getParcelableExtra("movie");
            movieModel.setMovie_id(selectedMovie.getMovie_id());
            movieModel.setVote_average(selectedMovie.getVote_average());
            movieModel.setPoster_path(selectedMovie.getPoster_path());
            movieModel.setOverview(selectedMovie.getOverview());
            movieModel.setTitle(selectedMovie.getTitle());
            favouriteDbHelper.addToFavouriteDatabase(movieModel);
        }
    }



    private void showAddingDialog(View view){
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Want to Add to Favorite?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        addToFavourite();
                        if (view !=null){
                            Snackbar.make(view, "Added to Favorite", Snackbar.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void showRemovingDialog(View view){
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Already Added to Favorite, want to Remove?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        favouriteDbHelper.deleteFromFavouriteDatabase(movie_id);
                        if (view !=null){
                            Snackbar.make(view, "Removed From Favorite", Snackbar.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

}