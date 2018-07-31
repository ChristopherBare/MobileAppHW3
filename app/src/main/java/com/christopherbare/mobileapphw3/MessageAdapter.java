package com.christopherbare.mobileapphw3;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    SendData data;
    Activity context;
    ArrayList<Message> messages;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button deleteButton, commentButton;

        public ViewHolder(final View itemView) {
            super(itemView);

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
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);

        //TODO eventually convert this into a message/comment

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       /* final Contact contact = mDataset.get(position);
        holder.name.setText("Name: " + contact.firstName + " " + contact.lastName);
        holder.phone.setText("Phone: " + contact.phone);
        holder.email.setText("Email: " + contact.email);

        Picasso.get().load(contact.imageURL).into(holder.profilePicture);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(activity, AddContactActivity.class);
                editIntent.putExtra("Contact", contact);
                editIntent.putExtra("Edit", true);
                activity.startActivity(editIntent);
            }*/
    }

    ;


    public interface SendData {
        void deleteMessage(Message message);
    }


}
