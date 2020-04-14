package com.example.traveloholic;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TravellerRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextRegisterEmail,editTextRegisterPassword,editTextRegisterName;
    private Button buttonTravelRegister;
    private TextView textViewTravelSignIn,textViewguide;

    private ProgressDialog progressDialog;

    private String name,email,password;

    //defining firebase object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_register);

        //initializing firebase auth object
        firebaseAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser()!=null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(),Guide.class));
        }

        //initializing views
        editTextRegisterName=(EditText)findViewById(R.id.editTextRegisterName);
        editTextRegisterEmail=(EditText)findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword=(EditText)findViewById(R.id.editTextRegisterPassword);
        textViewTravelSignIn=(TextView)findViewById(R.id.textViewTravelSignIn);
        buttonTravelRegister=(Button)findViewById(R.id.buttonTravelRegister);
        textViewguide=findViewById(R.id.textViewguide);
        progressDialog=new ProgressDialog(this);

        buttonTravelRegister.setOnClickListener(this);
        textViewTravelSignIn.setOnClickListener(this);
        textViewguide.setOnClickListener(this);
    }

    private void registerTraveller(){
        //getting email and password from edit texts
        name=editTextRegisterName.getText().toString().trim();
        email=editTextRegisterEmail.getText().toString().trim();
        password=editTextRegisterPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(name)) {
            editTextRegisterName.setError("Name Required!!");
            return;
        } else {
            editTextRegisterName.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            editTextRegisterEmail.setError("Email ID Required!!");
            return;
        } else {
            editTextRegisterEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            editTextRegisterPassword.setError("Password Required!!");
            return;
        }
        else if(password.length()<=6)
        {
            editTextRegisterPassword.setError("Password should be more than 6 characters");
            return;
        }
        else {
            editTextRegisterPassword.setError(null);
        }


        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if(task.isSuccessful()) {
                            //display some message here
                            onAuthSuccess(task.getResult().getUser());
                            Toast.makeText(TravellerRegister.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message
                            Toast.makeText(TravellerRegister.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Write new user
        writeNewTraveller(user.getUid(), name,email,password);
    }

    private void writeNewTraveller(String userId, String name, String email, String password) {

        Traveller traveller = new Traveller(name, email, password);
        mDatabase.child("traveller").child(userId).setValue(traveller);
    }

    @Override
    public void onClick(View v) {
        if (v==buttonTravelRegister) {
            registerTraveller();
                //startActivity(new Intent(this,GuideLogin.class));
        }
        if (v == textViewTravelSignIn){
            startActivity(new Intent(this,TravellerLogin.class));
        }
        if(v==textViewguide){
            finish();
            startActivity(new Intent(this,Guide.class));
        }

    }
}
