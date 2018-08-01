package com.christopherbare.mobileapphw3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView usersName;
    ImageView logout;
    ImageView send, addPicture, newPicture;
    EditText messageEt;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    MessageAdapter adapter;
    String urlImage;
    static ArrayList<Message> messages = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    static final int REQUEST_CAMERA_APP = 1;
    DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        usersName = findViewById(R.id.usersName);
        send = findViewById(R.id.addMessage);
        messageEt = findViewById(R.id.newMessage);
        addPicture = findViewById(R.id.addPhoto);
        newPicture = findViewById(R.id.newPhoto);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        //set users name
        usersName.setText(mAuth.getCurrentUser().getDisplayName());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


       dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", mAuth.getCurrentUser().getEmail());
                        editor.commit();

                        mAuth.signOut();
                        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No was clicked
                        break;
                }
            }
        };

        //set users name
        usersName.setText(mAuth.getCurrentUser().getDisplayName());

        //event listener for the database
        mDatabase.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot node : dataSnapshot.getChildren() ) {

                    Message message = node.getValue(Message.class);
                    message.key = node.getKey();
                    messages.add(message);


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(ChatActivity.this, messages, new MessageAdapter.SendData() {
            @Override
            public void deleteMessage(Message message) {
                try {
                    if (mDatabase.child("message").child(message.key).getKey() != null) {
                        mDatabase.child("message")
                                .child(message.key)
                                .removeValue();
                    }
                } catch (Exception e) {
                    Log.e("demo", "deleteMessage: Exception", e);
                }
            }

            @Override
            public void addComment(Message m) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.add_comment, null);
                final EditText editTextComment = alertLayout.findViewById(R.id.comment);
                final ImageView sendComment = alertLayout.findViewById(R.id.submitComment);
                final Message message = m;
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this);
                alert.setView(alertLayout);
                final android.support.v7.app.AlertDialog dialog = alert.create();
                dialog.show();
                sendComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Comment comment = new Comment();
                        if(editTextComment != null && !editTextComment.getText().toString().isEmpty() && !editTextComment.getText().toString().equals("")) {
                            comment.setTime(new Date());
                            comment.setUser(mAuth.getCurrentUser().getDisplayName());
                            comment.setComment(editTextComment.getText().toString());
                            message.getComments().add(comment);
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/message/" + message.key, message);
                            mDatabase.updateChildren(childUpdates);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ChatActivity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message;
                if(messageEt.getText() != null && !messageEt.getText().toString().isEmpty() && !messageEt.getText().toString().equals("")){
                    message = new Message(messageEt.getText().toString(), mAuth.getCurrentUser().getDisplayName(), Calendar.getInstance().getTime(), false);
                    mDatabase.child("message").push().setValue(message);
                    messageEt.getText().clear();
                    messages.add(message);
                    adapter.notifyDataSetChanged();
                } else if(newPicture.getVisibility() == View.VISIBLE){
                    upload();
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter either a message or a photo", Toast.LENGTH_SHORT).show();
                }


            }
        });

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPhoto();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutButton){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder .setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        } else {
            return false;
        }
    }


    public void submitPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_APP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_APP && resultCode == RESULT_OK) {
            //uri = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            newPicture.setImageBitmap(imageBitmap);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            storageReference = storageReference.child("JPEG_" + timeStamp + "_");
            newPicture.setVisibility(View.VISIBLE);
            messageEt.setVisibility(View.GONE);

        }
    }

    public boolean upload() {
        newPicture.setDrawingCacheEnabled(true);
        newPicture.buildDrawingCache();
        if(newPicture.getVisibility() == View.GONE){
            return false;
        }
        Bitmap bitmap = ((BitmapDrawable) newPicture.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    urlImage = downloadUri.toString();
                    Message message = new Message(urlImage, mAuth.getCurrentUser().getDisplayName(), Calendar.getInstance().getTime());
                    finalSubmit(message);
                } else {

                }
            }
        });
        return true;
    }

    public void finalSubmit(Message message){

        mDatabase.child("message").push().setValue(message);
        newPicture.setVisibility(View.GONE);
        messageEt.setVisibility(View.VISIBLE);
        newPicture.setImageDrawable(null);
        messages.add(message);
        adapter.notifyDataSetChanged();
    }
}
