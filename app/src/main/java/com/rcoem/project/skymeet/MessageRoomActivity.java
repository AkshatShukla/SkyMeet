package com.rcoem.project.skymeet;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stfalcon.chatkit.messages.MessageInput;

import java.util.Date;

public class MessageRoomActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int count;

    private Query mquery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);

        mBlogList.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mBlogList.setLayoutManager(linearLayoutManager);
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Room").child(str).child("message");
        mquery = mDatabase1.orderByChild("servertime");


        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(

                        BlogModel.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, final int position) {

                        final String Post_Key = getRef(position).toString();
                        //   Intent intent = getIntent();
                        //   final String str = intent.getStringExtra("location");
                        viewHolder.setUsername(model.getMsg());




                    }
                };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
        count = firebaseRecyclerAdapter.getItemCount();








        MessageInput inputView = (MessageInput) findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {

            @Override
            public boolean onSubmit(CharSequence input) {

                //validate and send message
              //  String messageToSend = (String) input;
                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) mBlogList.getLayoutManager();
                linearLayoutManager1.scrollToPosition(0);
                final DatabaseReference send = mDatabase1.push();
                final long currentLongTime = -1 * new Date().getTime();
                final String currentTime = "" + currentLongTime;

                send.child("sender").setValue("No Name");
                send.child("msg").setValue(input.toString());
                send.child("servertime").setValue(currentLongTime);

//                Toast.makeText(MessageRoomActivity.this,count,Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUsername(String username){

            TextView post_Desc = (TextView) mView.findViewById(R.id.title_card);
            post_Desc.setText(username);
        }


    }
}
