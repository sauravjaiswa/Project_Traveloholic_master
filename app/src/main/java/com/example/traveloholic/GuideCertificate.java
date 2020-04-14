package com.example.traveloholic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GuideCertificate extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewUserID;
    private Button btnLogout,btnAddPlaces;
    EditText editTextplace1,editTextplace2,editTextplace3,editTextplace4,editTextplace5;

    String place1,place2,place3,place4,place5;
    ProgressDialog dialog;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_certificate);

        textViewUserID=findViewById(R.id.textViewUserID);
        btnLogout=findViewById(R.id.btnLogout);
        btnAddPlaces=findViewById(R.id.btnAddPlaces);

        editTextplace1=findViewById(R.id.Place1);
        editTextplace2=findViewById(R.id.Place2);
        editTextplace3=findViewById(R.id.Place3);
        editTextplace4=findViewById(R.id.Place4);
        editTextplace5=findViewById(R.id.Place5);

        dialog=new ProgressDialog(this);

        //initializing firebase authentication object
        firebaseAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        //if user is not logged in
//        //that means current user will return null
//        if(firebaseAuth.getCurrentUser()==null)
//        {
//            //closing this activity
//            finish();
//            //starting login activity
//            startActivity(new Intent(this,GuideLogin.class));
//        }

        user=firebaseAuth.getCurrentUser();
        //displaying logged in user name
        textViewUserID.setText("Welcome, "+user.getEmail());

        btnLogout.setOnClickListener(this);
        btnAddPlaces.setOnClickListener(this);
    }

    public void addPlaces(){

        place1=editTextplace1.getText().toString().trim();
        place2=editTextplace2.getText().toString().trim();
        place3=editTextplace3.getText().toString().trim();
        place4=editTextplace4.getText().toString().trim();
        place5=editTextplace5.getText().toString().trim();

        if (TextUtils.isEmpty(place1)) {
            editTextplace1.setError("Required Field!!");
            return;
        } else {
            editTextplace1.setError(null);
        }
        if (TextUtils.isEmpty(place2)) {
            editTextplace2.setError("Required Field!!");
            return;
        } else {
            editTextplace2.setError(null);
        }
        if (TextUtils.isEmpty(place3)) {
            editTextplace3.setError("Required Field!!");
            return;
        } else {
            editTextplace3.setError(null);
        }

        //if the email and password are not empty
        //displaying a progress dialog
        dialog.setMessage("Adding PLaces...");
        dialog.show();

        mDatabase.child("users").child(user.getUid()).child("Place1").setValue(place1);
        mDatabase.child("users").child(user.getUid()).child("Place2").setValue(place2);
        mDatabase.child("users").child(user.getUid()).child("Place3").setValue(place3);
        mDatabase.child("users").child(user.getUid()).child("Place4").setValue(place4);
        mDatabase.child("users").child(user.getUid()).child("Place5").setValue(place5);

        dialog.dismiss();

    }

    @Override
    public void onClick(View v) {

        if(v==btnLogout){
            finish();
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this,GuideLogin.class));
        }
        if(v==btnAddPlaces){
            addPlaces();
            Toast.makeText(this,"Places Added Successfully",Toast.LENGTH_SHORT).show();
        }

    }

}
