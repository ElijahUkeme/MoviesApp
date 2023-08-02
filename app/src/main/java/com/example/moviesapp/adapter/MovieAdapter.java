package com.example.moviesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.interfaces.OnMovieListener;
import com.example.moviesapp.models.MovieModel;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<MovieModel> movies;
    OnMovieListener onMovieListener;

    public MovieAdapter(OnMovieListener onMovieListener) {
        this.onMovieListener = onMovieListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item,parent,false);
        return new MovieViewHolder(view,onMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((MovieViewHolder)holder).title.setText(movies.get(position).getTitle());
        ((MovieViewHolder)holder).release_date.setText(movies.get(position).getRelease_date());
        ((MovieViewHolder)holder).original_language.setText(movies.get(position).getOriginal_language());
        ((MovieViewHolder)holder).ratingBar.setRating(movies.get(position).getVote_average()/2);
        Glide.with(holder.itemView.getContext()).load("https://image.tmdb.org/t/p/w500"+movies.get(position).getPoster_path())
                .placeholder(R.drawable.load).into(((MovieViewHolder) holder).imageView);

    }

    @Override
    public int getItemCount() {
        if (movies !=null){
            return movies.size();
        }else {
            return 0;
        }

    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    //getting the id of the movie clicked
    public MovieModel getSelectedMovie(int position){
        if (movies !=null){
            if (movies.size() >0){
                return movies.get(position);
            }
        }
        return null;
    }
}
