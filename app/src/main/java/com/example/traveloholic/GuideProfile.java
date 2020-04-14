package com.example.traveloholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GuideProfile extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout,guideCertification;
    private DatabaseReference mDatabase;



    private StorageReference mStorageRef;

    Button btnPickImage,btnUpload,btnDel;
    ImageView imgSource, imgDestination;
    LinearLayout lv;
    Uri selectedImage;
    ProgressBar pbbar;
    DatabaseReference databaseReference, childReference;
    private static final int REQUEST_TAKE_GALLERY_PHOTO = 2;
    StorageReference fireRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String imageURL = "",tmp;
    TextView Name,Contact,Alternate,Age,DOB,Add,Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_profile);

        //initializing firebase authentication object
        firebaseAuth=FirebaseAuth.getInstance();

        //if user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser()==null)
        {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this,GuideLogin.class));
        }

        //getting current user
        user=firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail=(TextView)findViewById(R.id.textViewUserEmail);
        buttonLogout=(Button)findViewById(R.id.buttonLogout);

        //displaying logged in user name
        textViewUserEmail.setText("Welcome, "+user.getEmail());

        lv = findViewById(R.id.lv);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        childReference=databaseReference.child("users").child(user.getUid());

        btnPickImage = findViewById(R.id.btnPickImage);

        imgDestination = findViewById(R.id.img);
        imgSource=findViewById(R.id.img);

        Name=findViewById(R.id.Name);
        Contact=findViewById(R.id.ContactNumber);
        Alternate=findViewById(R.id.AlternateNumber);
        Add=findViewById(R.id.Address);
        DOB=findViewById(R.id.DOB);
        Email=findViewById(R.id.Email);
        guideCertification=findViewById(R.id.guideCertification);

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        guideCertification.setOnClickListener(this);

        retrieveProf();
    }


    public void retrieveProf(){
        Log.e("users",user.getUid());
        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Name.setText("Name : "+value.Name);
                Contact.setText("Contact Number : "+value.ph1);
                Alternate.setText("Alternate Contact Number : "+value.ph2);
                Add.setText("Address : "+value.Address);
                DOB.setText("Date Of Birth : "+value.DOB);
                Email.setText("Email ID : "+value.Email);
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().equals("url")) {
                        tmp = data.getValue().toString();
                        Log.d("Specific Node Value", tmp);
                        break;
                    }
                    }
                Glide.with(getApplicationContext()).load(tmp).into(imgDestination);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        //if logout is pressed
        if(v==buttonLogout){
            finish();
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this,GuideLogin.class));
        }

        if(v==guideCertification){
            //starting login activity
            startActivity(new Intent(this,GuideCertificate.class));
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_PHOTO) {
                Bitmap originBitmap = null;
                selectedImage = data.getData();
                InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(
                            selectedImage);
                    originBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (originBitmap != null) {
                    {
                        //imgSource.setVisibility(View.INVISIBLE);
                    }
                } else
                    selectedImage = null;
            }

        }
    }
}
