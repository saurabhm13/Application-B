package com.example.applicationb;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Adapter class for RecyclerView to display a list of albums
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private static List<Album> data = new ArrayList<>();

    // Click listeners for item and delete actions
    private OnItemClickListener itemClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(String userName, String artist);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String userName);
    }

    // Method to set new data for the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Album> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    // Method to set click listener
    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // Inflates the layout for each item in the RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new MyViewHolder(view, itemClickListener, deleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String userName = data.get(position).getTitle();
        String artist = data.get(position).getArtist();
        holder.bind(userName, artist);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder class to hold references to views in each item
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, artist;
        ConstraintLayout cv_album;
        ImageView deleteImage;

        MyViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener, final OnDeleteClickListener deleteClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);

            deleteImage = itemView.findViewById(R.id.delete);
            cv_album = itemView.findViewById(R.id.cv_album);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(data.get(position).getTitle(), data.get(position).getArtist());
                        }
                    }
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteClickListener.onDeleteClick(data.get(position).getTitle());
                        }
                    }
                }
            });
        }

        void bind(String userName, String artist) {
            title.setText(userName);
            this.artist.setText(artist);
        }
    }
}
