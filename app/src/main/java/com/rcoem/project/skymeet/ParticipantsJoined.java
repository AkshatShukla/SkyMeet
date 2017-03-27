package com.rcoem.project.skymeet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParticipantsJoined extends AppCompatActivity {
    private RecyclerView mBlogList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_joined);
        Intent intent = getIntent();
        String str = intent.getStringExtra("postkey");
        String key;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Room").child(str).child("Joined");
        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(
                BlogModel.class,
                R.layout.joined_users,
                BlogViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, int position) {
                viewHolder.setName(model.getName());
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }



        public void setName(String name){

            TextView post_Desc = (TextView) mView.findViewById(R.id.textView3);
            post_Desc.setText(name);
        }





    }
}
