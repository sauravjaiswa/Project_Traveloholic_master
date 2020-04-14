package com.example.traveloholic;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;

public class Guide extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewRegister,cardViewLogin,cardViewChat,cardViewEmergency,cardViewLanguage,cardViewList,cardViewLogout;
    private TextView textViewFlag;
    //firebase auth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //initializing firebase authentication object
        firebaseAuth= FirebaseAuth.getInstance();


        cardViewRegister = (CardView) findViewById(R.id.guide1);
        cardViewLogin = (CardView) findViewById(R.id.guide2);
        cardViewList = (CardView) findViewById(R.id.guidelist);
        cardViewChat = (CardView) findViewById(R.id.guide3);
        cardViewLanguage = (CardView) findViewById(R.id.lang_translate);
        cardViewEmergency = (CardView) findViewById(R.id.emergency);
        cardViewLogout=findViewById(R.id.card_logout);
        textViewFlag=findViewById(R.id.textViewFlag);

        if(firebaseAuth.getCurrentUser()!=null){
            cardViewRegister.setEnabled(false);
            cardViewLogin.setEnabled(false);
            textViewFlag.setText("Welcome, "+firebaseAuth.getCurrentUser().getEmail()+"\nTo access Guide features login as a guest\n");
        }
        else{
            cardViewRegister.setEnabled(true);
            cardViewLogin.setEnabled(true);
            cardViewChat.setEnabled(false);
            cardViewList.setEnabled(false);
            textViewFlag.setText("Welcome, as a Guest\nYou won't be able to access Traveller's Group Chat feature in Guest mode\n");
        }

        cardViewRegister.setOnClickListener(this);
        cardViewLogin.setOnClickListener(this);
        cardViewList.setOnClickListener(this);
        cardViewChat.setOnClickListener(this);
        cardViewLanguage.setOnClickListener(this);
        cardViewEmergency.setOnClickListener(this);
        cardViewLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Object dataTransfer[]=new Object[2];
        GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
        String url;
        MapsActivity o=new MapsActivity();

        if(v==cardViewRegister) {
            startActivity(new Intent(this,GuideRegister.class));
        }
        if(v==cardViewLogin){
            startActivity(new Intent(this,GuideLogin.class));
        }
        if(v==cardViewList){
            startActivity(new Intent(this,GuideList.class));
        }
        if(v==cardViewEmergency){
            startActivity(new Intent(this,EmergenctActivity.class));
        }
        if(v==cardViewLanguage){
            startActivity(new Intent(this,LanguageTranslator.class));
        }
        if(v==cardViewChat){
            startActivity(new Intent(this,ChatWithGuide.class));
        }
        if(v==cardViewLogout){
            finish();
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this,Guide.class));
        }
    }
}