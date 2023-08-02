package com.example.moviesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.models.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyTrailerViewHolder> {
    private Context context;
    private List<Trailer> trailers;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public MyTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_card,parent,false);
        return new MyTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTrailerViewHolder holder, int position) {
        holder.title.setText(trailers.get(position).getName());

    }

    @Override
    public int getItemCount() {

        return trailers.size();
    }

    public class MyTrailerViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView imageView;
        public MyTrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trailer_title);
            imageView = itemView.findViewById(R.id.trailer_image);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos !=RecyclerView.NO_POSITION){
                    Trailer clickedData = trailers.get(pos);
                    String videoId = clickedData.getKey();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("videoId",videoId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
