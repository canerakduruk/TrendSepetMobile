package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.CommentDbModel;
import com.example.eticaretapp.datamodels.CommentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {
    private final Context context;
    private final List<CommentModel> commentList;

    public CommentRecyclerAdapter(Context context, List<CommentModel> commentList) {

        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ratingBar.setRating(Float.parseFloat(commentList.get(position).getStars()));
        holder.comment.setText(commentList.get(position).getComment());
        Glide.with(context).load(commentList.get(position).getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RatingBar ratingBar;
        private final ImageView imageView;
        private final TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.item_recyclerview_comment_ratingbar);
            imageView = itemView.findViewById(R.id.item_recyclerview_comment_image);
            comment = itemView.findViewById(R.id.item_recyclerview_comment_content);
        }
    }
}
