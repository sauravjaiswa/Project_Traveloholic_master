package com.example.traveloholic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.nio.charset.MalformedInputException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GuideRegister extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail,editTextPassword,editTextName,editTextContact,editTextAlternate,editTextAge,editTextDOB,editTextAddress;
    private RadioGroup radioGroupGender;
    private Button buttonSignup;
    private DatePickerDialog picker;

    private TextView textViewSignin,statustxt;

    private ProgressDialog progressDialog;

    //defining firebase object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    String name,contact,alternate,address,age,dob,gender,email,password;
    Long ph1,ph2;
    Integer Age;
    int f=0;


    private StorageReference mStorageRef;

    Button btnPickImage;
    LinearLayout lv;
    Uri selectedImage;
    DatabaseReference databaseReference;
    private static final int REQUEST_TAKE_GALLERY_PHOTO = 2;
    StorageReference fireRef;
    public String imageURL = "",strFileName;

    public static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_register);

        //initializing firebase auth object
        firebaseAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser()!=null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(),GuideProfile.class));
        }

        //initializing views
        editTextName=(EditText)findViewById(R.id.editTextFirstName);
        editTextContact=(EditText)findViewById(R.id.editContactNumber);
        editTextAlternate=(EditText)findViewById(R.id.editAlternateNumber);
        editTextAddress=(EditText)findViewById(R.id.editAddress);
        editTextAge=(EditText)findViewById(R.id.editTextAge);
        editTextDOB=(EditText)findViewById(R.id.editDOB);
        radioGroupGender=(RadioGroup) findViewById(R.id.radioSex);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        textViewSignin=(TextView)findViewById(R.id.textViewSignin);
        statustxt=(TextView)findViewById(R.id.statustxt);

        buttonSignup=(Button)findViewById(R.id.buttonSignup);

        progressDialog=new ProgressDialog(this);

        //attaching listener to button
        editTextDOB.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);



        lv = findViewById(R.id.lv);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();



        btnPickImage = findViewById(R.id.btnPickImage);



        btnPickImage.setOnClickListener(this);
    }

    private void registerUser(){
        //getting email and password from edit texts
        name=editTextName.getText().toString().trim();
        contact=editTextContact.getText().toString().trim();

        alternate=editTextAlternate.getText().toString().trim();

        address=editTextAddress.getText().toString().trim();
        age=editTextAge.getText().toString().trim();

        dob=editTextDOB.getText().toString().trim();
        gender=radioGroupGender.toString();
        email=editTextEmail.getText().toString().trim();
        password=editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name Required!!");
            return;
        } else {
            editTextName.setError(null);
        }
        if (TextUtils.isEmpty(contact)) {
            editTextContact.setError("Contact Number Required!!");
            return;
        } else {
            editTextContact.setError(null);
        }
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError("Address Required!!");
            return;
        } else {
            editTextAddress.setError(null);
        }
        if (TextUtils.isEmpty(age)) {
            editTextAge.setError("Age Required!!");
            return;
        } else {
            editTextAge.setError(null);
        }
        if (TextUtils.isEmpty(dob)) {
            editTextDOB.setError("Date of Birth Required!!");
            return;
        } else {
            editTextDOB.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email ID Required!!");
            return;
        } else {
            editTextEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required!!");
            return;
        }
        else if(password.length()<=6)
        {
            editTextPassword.setError("Password should be more than 6 characters");
            return;
        }
        else {
            editTextPassword.setError(null);
        }

        ph1=Long.parseLong(contact);
        ph2=Long.parseLong(alternate);
        Age=Integer.parseInt(age);


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
                            Toast.makeText(GuideRegister.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            f=1;
                        }else{
                            //display some message
                            Toast.makeText(GuideRegister.this,"Registration Error",Toast.LENGTH_LONG).show();
                            f=0;
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Write new user
        Toast.makeText(getApplicationContext(),"Uploading image",Toast.LENGTH_SHORT).show();
        UploadImages(user.getUid());
        writeNewUser(user.getUid(), name,address,gender,email,password,dob,ph1,ph2,Age);
    }

    private void writeNewUser(String userId, String name, String address, String gender, String email, String password, String DOB, Long ph1, Long ph2, Integer age) {

        User user = new User(name, address, gender, email, password, DOB, ph1, ph2, age);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public String GetDate() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentdate = df.format(Calendar.getInstance().getTime());
        return currentdate;
    }

    @Override
    public void onClick(View v) {
        //calling register method on click
        if(v==editTextDOB){
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(GuideRegister.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            editTextDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            picker.show();
        }
        if (v==buttonSignup) {
            registerUser();
            if(f==1)
                startActivity(new Intent(this,GuideLogin.class));
        }
        if (v == textViewSignin){
            startActivity(new Intent(this,GuideLogin.class));
        }

        if(v==btnPickImage){
            Toast.makeText(this,"Picking image",Toast.LENGTH_SHORT).show();
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)
                    && !Environment.getExternalStorageState().equals(
                    Environment.MEDIA_CHECKING)) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_TAKE_GALLERY_PHOTO);

            } else
                Toast.makeText(GuideRegister.this, "No gallery found.", Toast.LENGTH_SHORT).show();
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
                        statustxt.setText("Image Picked");
                        //imgSource.setVisibility(View.INVISIBLE);
                    }
                } else
                    selectedImage = null;
            }

        }
    }


    public void UploadImages(final String uid) {
        try {
            //lv.setVisibility(View.GONE);

            strFileName =  GetDate() + "img.jpg";

            Uri file = selectedImage;

            fireRef = mStorageRef.child("images/" + uid + "/" + strFileName);

            UploadTask uploadTask = fireRef.putFile(file);
            Log.e("Fire Path", fireRef.toString());
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fireRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("Image URL", downloadUri.toString());

                        selectedImage = null;
                        imageURL = downloadUri.toString();
                        url=imageURL;
                    } else {
                        Toast.makeText(GuideRegister.this, "Image upload unsuccessful. Please try again."
                                , Toast.LENGTH_LONG).show();
                    }
                    lv.setVisibility(View.VISIBLE);

                    mDatabase.child("users").child(uid).child("url").setValue(imageURL);
                    DownloadImageFromURL downloadImageFromURL = new DownloadImageFromURL();
                    downloadImageFromURL.execute("");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            //pbbar.setVisibility(View.GONE);
            Log.e("Upload Error"," "+ex.getMessage());
            Toast.makeText(GuideRegister.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadImageFromURL extends AsyncTask<String, Void, String> {
        Bitmap bitmap = null;

        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... urls) {
            try {
                mDatabase = FirebaseDatabase.getInstance().getReference();

                Log.e("imageURL is ", imageURL);
                InputStream in = new java.net.URL(imageURL).openStream();
                if (in != null) {
//                    mDatabase.child("users").child(user.getUid()).child("Photo").setValue(imageURL);
                    bitmap = BitmapFactory.decodeStream(in);
                } else
                    Log.e("Empty InputStream", "InputStream is empty.");
            } catch (MalformedInputException e) {
                Log.e("Error URL", e.getMessage().toString());
            } catch (Exception ex) {
                Log.e("Input stream error", "Input stream error");
            }
            return "";
        }

        protected void onPostExecute(String result) {
            if (bitmap != null) {
                //imgDestination.setImageBitmap(bitmap);
            } else
                Log.e("Empty Bitmap", "Bitmap is empty.");
        }
    }


}
