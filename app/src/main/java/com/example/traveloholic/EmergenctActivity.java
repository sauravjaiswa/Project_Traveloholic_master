package com.example.traveloholic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

public class EmergenctActivity extends AppCompatActivity{
    EditText et,ec_name;
    Button call_btn,retrieve_btn,save_btn,del_btn;
    TextView textView4;
    ListView listView;
    private final int REQUEST_CODE=99;
    String num,value="",con_name,db_con_name="";
    int count_con=0,index;
    //defining firebase object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase,databaseReference;;
    ArrayList<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergenct);

        //initializing firebase auth object
        firebaseAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        et = (EditText) findViewById(R.id.editText1);
        ec_name=findViewById(R.id.editTextconname);
        call_btn = (Button) findViewById(R.id.button1);
        retrieve_btn=(Button)findViewById(R.id.button2);
        save_btn=(Button)findViewById(R.id.button3);
        listView=(ListView)findViewById(R.id.listView2);
        del_btn=(Button)findViewById(R.id.button4);
        textView4=(TextView)findViewById(R.id.textView4);

        mDatabase.child("contacts").child("100").child("Name").setValue("POLICE Help");
        mDatabase.child("contacts").child("100").child("Number").setValue("100");

        mDatabase.child("contacts").child("102").child("Name").setValue("AMBULANCE");
        mDatabase.child("contacts").child("102").child("Number").setValue("102");

        mDatabase.child("contacts").child("1091").child("Name").setValue("Women Helpline");
        mDatabase.child("contacts").child("1091").child("Number").setValue("1091");

        mDatabase.child("contacts").child("1078").child("Name").setValue("Disaster Management ( N.D.M.A )");
        mDatabase.child("contacts").child("1078").child("Number").setValue("1078");

        mDatabase.child("contacts").child("01124363260").child("Name").setValue("EARTHQUAKE / FLOOD / DISASTER Helpline No.");
        mDatabase.child("contacts").child("01124363260").child("Number").setValue("01124363260");

        mDatabase.child("contacts").child("1363").child("Name").setValue("Tourist Helpline");
        mDatabase.child("contacts").child("1363").child("Number").setValue("1363");

        call_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + et.getText().toString()));
                if (ContextCompat.checkSelfPermission(EmergenctActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(EmergenctActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    startActivity(i);
                }
            }
        });

        retrieve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        databaseReference=FirebaseDatabase.getInstance().getReference("contacts");

        save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                value=et.getText().toString().trim();
                db_con_name=ec_name.getText().toString().trim();
                if (value=="" || db_con_name=="")
                    Toast.makeText(EmergenctActivity.this,"No contact details added yet!!",Toast.LENGTH_LONG).show();
                else{
                    mDatabase.child("contacts").child(value).child("Name").setValue(db_con_name);
                    mDatabase.child("contacts").child(value).child("Number").setValue(value);
                    Toast.makeText(EmergenctActivity.this,"Saved successfully!!",Toast.LENGTH_LONG).show();

                    String tmp="\nName:"+db_con_name+"\n\n"+"Number:"+value+"\n";
                    //list.add(tmp);
                    textView4.setText("");
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        //To retrieve data from firebase
        readFromDatabase();

        //        now set item click on list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(String) (listView.getItemAtPosition(position));

                String tmpvalue[]=selectedFromList.split("\n\n");
                String na[]=tmpvalue[0].split(":");
                String val[]=tmpvalue[1].split(":");

                Log.e("Contact",val[1]);
//                loc_flag=1;

                ec_name.setText(na[1]);
                et.setText(val[1]);
                if ((et.getText().toString().trim()).equals("100")||(et.getText().toString().trim()).equals("102")||(et.getText().toString().trim()).equals("1078")||(et.getText().toString().trim()).equals("1091")||(et.getText().toString().trim()).equals("01124363260")||(et.getText().toString().trim()).equals("1363"))
                    del_btn.setEnabled(false);
                else
                    del_btn.setEnabled(true);
                index=position;

            }
        });
        // so item click is done now check list view


        del_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteData();
                finish();
                startActivity(getIntent());
                Toast.makeText(EmergenctActivity.this, "Deleted contact number:"+et.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void readFromDatabase(){
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView

                return view;
            }
        };
        listView.setAdapter(adapter);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value1="";// = dataSnapshot.getValue(String.class);
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.getKey().equals("Name")) {
                        value1 = value1+"\nName:"+data.getValue().toString();
                    }
                    if(data.getKey().equals("Number")) {
                        value1 = value1+"\n\nNumber:"+data.getValue().toString()+"\n";
                    }

                }
                Log.e("Added:",value1);
                list.add(value1);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                list.remove(dataSnapshot.getValue(String.class));
//                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void deleteData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("contacts").child(et.getText().toString().trim()).child("Name").removeValue();
        ref.child("contacts").child(et.getText().toString().trim()).child("Number").removeValue();
        et.setText("");
        ec_name.setText("");
            }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                con_name=getContactName(num,getApplicationContext());
                                ec_name.setText(con_name);
                                et.setText(num);
                                Toast.makeText(EmergenctActivity.this, "Name:"+con_name+"\tNumber="+num, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
                }
        }
    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


}