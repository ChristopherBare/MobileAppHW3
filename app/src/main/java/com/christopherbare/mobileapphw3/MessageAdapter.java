package com.christopherbare.mobileapphw3;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    SendData data;
    Activity context;
    ArrayList<Message> messages;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    ArrayList<ListItem> array = new ArrayList<>();
    CommentAdapter adapter;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView deleteButton, commentButton;
        TextView messageView, user, date;
        ImageView image;
        RecyclerView commentRecyclerView;

        public ViewHolder(final View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.textMessage);
            user = itemView.findViewById(R.id.user);
            date = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.picture);
            deleteButton = itemView.findViewById(R.id.delete);
            commentButton = itemView.findViewById(R.id.comment);
            commentRecyclerView = itemView.findViewById(R.id.recyclerViewComments);
        }

        public void bind(final Message item, final SendData data) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.deleteMessage(item);
                }
            });
        }

    }

    public MessageAdapter(Activity context, ArrayList<Message> messages, SendData data) {
        this.context = context;
        this.messages = messages;
        this.data = data;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.user.setText(message.getUser());
        holder.date.setText(new PrettyTime().format(message.getTime()));

        if(message.isPicture()){
            holder.image.setVisibility(View.VISIBLE);
            holder.messageView.setVisibility(View.GONE);
            Picasso.get().load(message.imageURL).into(holder.image);
        } else {
            holder.messageView.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.messageView.setText(message.getMessage());
        }

        if(message.getUser().equals(mAuth.getCurrentUser().getDisplayName())){
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.deleteMessage(message);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.addComment(message);
            }
        });

        if(message.getComments().size() > 0){
            holder.commentRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.commentRecyclerView.setLayoutManager(mLayoutManager);
            adapter = new CommentAdapter(context, message.getComments());
            holder.commentRecyclerView.setAdapter(adapter);
        } else {
            holder.commentRecyclerView.setVisibility(View.GONE);
        }

    }


    public interface SendData {
        void deleteMessage(Message message);
        void addComment(Message message);
    }

    public class ListItem {
        Message message;
        Comment comment;
    }


}
