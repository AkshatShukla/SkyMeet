package com.rcoem.project.skymeet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhananjay on 20-03-2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {



    List<String> data;
    Context m;

    public ArrayList<BlogModel> b1;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        TextView textView1,textView2;
        final Context m;
        public ViewHolder(View v) {
            super(v);
            System.out.println("1.1");
            textView1=(TextView) v.findViewById(R.id.textView1);
            textView2=(TextView) v.findViewById(R.id.textView2);

            this.m=v.getContext();
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            final FirebaseAuth mAuth;
            mAuth  = FirebaseAuth.getInstance();

            final Intent i=new Intent(m,MessageRoomActivity.class);
            System.out.println(textView2.getText().toString()+" KEY NAME");
            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setMessage("Do you want to join this group");
            builder1.setCancelable(true);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Room").child(textView2.getText().toString()).child("Joined").push();
            builder1.setPositiveButton(
                    "Join",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    databaseReference.child("Name").setValue(dataSnapshot.child("name").getValue().toString());
                                    databaseReference.child("imageProfile").setValue(dataSnapshot.child("images").getValue().toString());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                            i.putExtra("entry",databaseReference.getKey());
                            i.putExtra("postkey",textView2.getText().toString());
                            m.startActivity(i);
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            Toast.makeText(view.getContext(),databaseReference.getKey(),Toast.LENGTH_LONG).show();


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<BlogModel> b1,Context m)
    {
        System.out.println("1.2");
        this.b1=b1;
        this.data = data;
        this.m=m;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meeting_cards, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!b1.isEmpty()) {
            System.out.println("1.3");


            String name = b1.get(position).getName();
            String msg = b1.get(position).getMsg();

            System.out.println(name + "     " + msg);
            holder.textView1.setText(name);
            holder.textView2.setText(msg);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return b1.size();
    }
}