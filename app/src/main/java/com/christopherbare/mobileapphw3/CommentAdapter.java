package com.christopherbare.mobileapphw3;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Activity context;
    ArrayList<Comment> comments;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewComment, textViewUser, textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewComment = itemView.findViewById(R.id.textMessage);
            textViewUser = itemView.findViewById(R.id.user);
            textViewDate = itemView.findViewById(R.id.time);
        }
    }

    public CommentAdapter(Activity context, ArrayList<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.textViewComment.setText(comment.getComment());
        holder.textViewUser.setText(comment.getUser());
        holder.textViewDate.setText(new PrettyTime().format(comment.getTime()));

    }
}
