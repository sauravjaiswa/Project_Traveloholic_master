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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TravellerLogin extends AppCompatActivity implements View.OnClickListener {

    //defining views
    private Button buttonTravelLogin;
    private EditText editTextLoginEmail,editTextLoginPassword;
    private TextView textViewTravelRegister,textViewguide1;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_login);

        //getting firebase auth object
        firebaseAuth=FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseAuth.getCurrentUser()!=null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(),Guide.class));
        }

        //initializing views
        editTextLoginEmail=(EditText)findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword=(EditText)findViewById(R.id.editTextLoginPassword);
        buttonTravelLogin=(Button)findViewById(R.id.buttonTravelLogin);
        textViewTravelRegister=(TextView)findViewById(R.id.textViewTravelRegister);
        textViewguide1=findViewById(R.id.textViewguide1);

        progressDialog=new ProgressDialog(this);

        //attaching click listener
        buttonTravelLogin.setOnClickListener(this);
        textViewTravelRegister.setOnClickListener(this);
        textViewguide1.setOnClickListener(this);
    }

    //method for user login
    private void travellerLogin(){
        String email=editTextLoginEmail.getText().toString().trim();
        String password=editTextLoginPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)){
            editTextLoginEmail.setError("Please enter valid Email");
            return;
        }else{
            editTextLoginEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)){
            editTextLoginPassword.setError("Please enter valid Email");
            return;
        }else{
            editTextLoginPassword.setError(null);
        }

        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Logging In Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if task is successful
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(),Guide.class));
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v==buttonTravelLogin){
            travellerLogin();
        }

        if (v==textViewTravelRegister){
            finish();
            startActivity(new Intent(this,TravellerRegister.class));
        }
        if(v==textViewguide1){
            finish();
            startActivity(new Intent(this,Guide.class));
        }

    }
}
