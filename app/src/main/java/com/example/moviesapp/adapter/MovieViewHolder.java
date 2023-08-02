package com.example.moviesapp.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.interfaces.OnMovieListener;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    OnMovieListener onMovieListener;

    TextView title,original_language,release_date;
    ImageView imageView;
    RatingBar ratingBar;
    public MovieViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
        super(itemView);

        this.onMovieListener = onMovieListener;
        title = itemView.findViewById(R.id.movie_title);
        original_language = itemView.findViewById(R.id.movie_duration);
        release_date = itemView.findViewById(R.id.movie_category);
        imageView = itemView.findViewById(R.id.image_movie);
        ratingBar = itemView.findViewById(R.id.rating_bar);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        onMovieListener.onMovieClick(getAdapterPosition());
    }
}
