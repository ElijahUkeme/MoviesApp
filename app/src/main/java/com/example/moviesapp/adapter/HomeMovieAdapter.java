package com.example.moviesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.activities.HomeDetailActivity;
import com.example.moviesapp.models.MovieModel;

import java.util.List;

public class HomeMovieAdapter extends RecyclerView.Adapter<HomeMovieAdapter.MyMovieVieHolder> {

    private Context context;
    private List<MovieModel>modelList;

    public HomeMovieAdapter(Context context, List<MovieModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyMovieVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_card,parent,false);
        return new MyMovieVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMovieVieHolder holder, int position) {
        holder.titleCard.setText(modelList.get(position).getTitle());
        holder.userRatingCard.setText(String.valueOf(modelList.get(position).getVote_average()));
        Glide.with(context).load("https://image.tmdb.org/t/p/w500"+modelList.get(position).getPoster_path())
                .placeholder(R.drawable.load).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyMovieVieHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView titleCard,userRatingCard;
        public MyMovieVieHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnail);
            titleCard = itemView.findViewById(R.id.title_card);
            userRatingCard = itemView.findViewById(R.id.userRating_card);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position !=RecyclerView.NO_POSITION){
                    MovieModel movieModel = modelList.get(position);
                    Intent intent = new Intent(context.getApplicationContext(), HomeDetailActivity.class);
                    intent.putExtra("movie",movieModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
    }
}
