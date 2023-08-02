package com.example.moviesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.models.MovieModel;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView title,overview;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        imageView = findViewById(R.id.movie_details_image);
        title = findViewById(R.id.details_movie_title);
        overview = findViewById(R.id.movie_overview_details);
        ratingBar = findViewById(R.id.rating_bar_details);
        getDataFromIntent();
    }

    private void getDataFromIntent(){
        if (getIntent().hasExtra("movie")){
            MovieModel movieModel = getIntent().getParcelableExtra("movie");

            title.setText(movieModel.getTitle());
            overview.setText(movieModel.getOverview());
            ratingBar.setRating(movieModel.getVote_average()/2);
            Glide.with(this).load("https://image.tmdb.org/t/p/w500"+movieModel.getBackdrop_path())
                    .placeholder(R.drawable.load).into(imageView);
            Log.d("Main","The title is: "+movieModel.getTitle());
            Log.d("Main","The overview is: "+movieModel.getOverview());
            Log.d("Main","The Rating is: "+movieModel.getVote_average());
            Log.d("Main","The image path is: "+movieModel.getBackdrop_path());
        }
    }
}