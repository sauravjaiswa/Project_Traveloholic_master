package com.example.traveloholic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GuideList extends AppCompatActivity {

    ListView listView;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    FloatingActionButton email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_list);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");

        listView=findViewById(R.id.listViewGuide);
        email=(FloatingActionButton)findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);
                /* Fill it with Data */
                sendEmail.setType("plain/text");
                sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
                        ""
                });
                sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                        "");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(sendEmail, "Choose a client"));
            }
        });
        retrieveGuide();
    }


    public void retrieveGuide() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User value = dataSnapshot.getValue(User.class);
                String tempval="\nName:"+value.Name+"\nContact numbers:"+value.ph1+"\t,\t"+value.ph2+"\nAddress:"+value.Address+"\nEmail ID:"+value.Email+"\n\n"+"Priority of Spots:\n";
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals("Place1") || data.getKey().equals("Place2") || data.getKey().equals("Place3") || data.getKey().equals("Place4") || data.getKey().equals("Place5")) {
                        tempval = tempval + data.getValue().toString()+"  ";
                    }
                }
                tempval=tempval+"\n";
                arrayList.add(tempval);
                arrayAdapter = new ArrayAdapter<String>(GuideList.this, android.R.layout.simple_list_item_1, arrayList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        // Get the Item from ListView
                        View view = super.getView(position, convertView, parent);

                        // Initialize a TextView for ListView each Item
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);

                        // Set the text color of TextView (ListView Item)
                        tv.setTextColor(Color.WHITE);

                        tv.setTextIsSelectable(true);
                        tv.isTextSelectable();

                        // Generate ListView Item using TextView
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    }
