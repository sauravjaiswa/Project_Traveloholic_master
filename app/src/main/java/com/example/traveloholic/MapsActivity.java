package com.example.traveloholic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.traveloholic.ui.Hospitals.hospitals;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE=99;
    int PROXIMITY_RADIUS=10000;
    public static double latitude,longitude;
    Geocoder geocoder;
    List<Address> addresses;
    ImageButton type;
    public static EditText TF_location;
    public static ImageButton B_search;

    // Links marker id and place object
    HashMap<String, DataParser> mHMReference = new HashMap<String, DataParser>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(!isConnected(MapsActivity.this))
            buildDialog(MapsActivity.this).show();
        else {
            Toast.makeText(MapsActivity.this,"Welcome", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_navigation);
        }*/

        setContentView(R.layout.activity_navigation);

        if (isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            // Create an Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Restart the Activity
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.sos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = findViewById(R.id.drawer_layout);
        /*ImageView menuIcon = (ImageView) findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });*/
        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        @SuppressLint("ResourceType") ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_hospitals, R.id.nav_schools,
                R.id.nav_restaurants, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(savedInstanceState==null)
        {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        type=findViewById(R.id.type_map);
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MapsActivity.this, v);
                popup.setOnMenuItemClickListener(MapsActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();
            }
        });

        TF_location=findViewById(R.id.TF_location);
        B_search=findViewById(R.id.B_search);

        Log.e("Working"," "+HospitalActivity.loc_flag);
        if (HospitalActivity.loc_flag==1){
            TF_location.setText(HospitalActivity.tmp[0]);
            B_search.performClick();
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if_else user pressed "yes", then he is allowed to exit from application
                    finish();
                }
            });
            builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if_else user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert=builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        int id = menuItem.getItemId();

        /*if (id == R.id.nav_gallery) {
            //gotohospitals();
        } else */if (id == R.id.nav_share) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SSB Practice ");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }

            if (id==R.id.nav_send){
                //startActivity(new Intent(this, AboutUs.class));
            }

        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                    {
                        if(client==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient(){
        client=new  GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();
        lastLocation=location;

        if(currentLocationMarker!=null)
        {
            currentLocationMarker.remove();
        }

        Log.d("lat = ",""+latitude);
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocationMarker=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client!=null)
        {
            LocationServices.getFusedLocationProviderClient(this);
        }

    }


    public void onClick(View v)
    {
        Object dataTransfer[]=new Object[2];
        GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
        String url;

        switch (v.getId())
        {
            case R.id.B_search:
                String location="";
                location=TF_location.getText().toString();
                List<Address> addressList;

                mMap.clear();
                if(!location.equals(""))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try{
                        addressList=geocoder.getFromLocationName(location,5);
                        if (addressList!=null)
                        {
                            for (int i=0;i<addressList.size();i++)
                            {
                                LatLng latLng=new LatLng(addressList.get(i).getLatitude(),addressList.get(i).getLongitude());
                                MarkerOptions markerOptions=new MarkerOptions();

                                mMap.getUiSettings().setZoomControlsEnabled(true);
                                mMap.setMinZoomPreference(4);

                                markerOptions.position(latLng)
                                        .title(location.toUpperCase())
                                        .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));


                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                Marker m = mMap.addMarker(markerOptions);
                                m.showInfoWindow();

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.sos:
                String mail = "MAIL_ID";
                String subject="Alert/Danger At ";
                String message = " the coordinates "+latitude+", "+longitude;

                //Send Mail
                JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message,v);

                javaMailAPI.execute();
                //  Toast.makeText(getApplicationContext(),"Alert Sent",Toast.LENGTH_SHORT).show();
                break;

            case R.id.guide:
                Intent i=new Intent(this, TravellerRegister.class);
                startActivity(i);
                break;

            case R.id.weather:
                i=new Intent(this, WeatherActivity.class);
                startActivity(i);
                break;
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                // If touched at User input location
                if(!mHMReference.containsKey(marker.getId()))
                    return false;

                // Getting place object corresponding to the currently clicked Marker
                DataParser place = mHMReference.get(marker.getId());

                // Creating an instance of DisplayMetrics
                DisplayMetrics dm = new DisplayMetrics();

                // Getting the screen display metrics
                getWindowManager().getDefaultDisplay().getMetrics(dm);



                // Getting a reference to Fragment Manager
                FragmentManager fm = getSupportFragmentManager();

                // Starting Fragment Transaction
                FragmentTransaction ft = fm.beginTransaction();


                // Committing the fragment transaction
                ft.commit();

                return false;
            }
        });
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlaceUrl=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+ getString(R.string.google_maps_key));

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){

        locationRequest=new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,  this);
        }
    }

    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Showing " +item.getTitle()+" view", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.Normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.Satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.Terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                return false;
        }
    }
}
