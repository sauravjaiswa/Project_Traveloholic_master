package com.example.traveloholic.ui.Travel;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.traveloholic.DataParser;
import com.example.traveloholic.GetNearbyPlacesData;
import com.example.traveloholic.HospitalActivity;
import com.example.traveloholic.MapsActivity;
import com.example.traveloholic.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class travel extends Fragment implements OnMapReadyCallback,View.OnClickListener{

    GoogleMap mMap;
    GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE=99;
    int PROXIMITY_RADIUS=10000;
    double latitude,longitude;



    Geocoder geocoder;
    List<Address> addresses;
    //Button b_hospital,b_school,b_restaurant,b_attraction,b_temple,b_park;

    // Links marker id and place object
    HashMap<String, DataParser> mHMReference = new HashMap<String, DataParser>();
    Object dataTransfer[]=new Object[2];

    String url;
    Button list;
    String travel;
    CardView B_airport,B_railway,B_bus;


    public travel() {
        // Required empty public constructor
        latitude=MapsActivity.latitude;
        longitude=MapsActivity.longitude;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_travel,container, false);

        list=v.findViewById(R.id.list);
        list.setEnabled(false);

        list.setText("Show List Of Travel Stations");
        list.setVisibility(View.INVISIBLE);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), HospitalActivity.class);
                startActivity(i);
            }
        });
        B_airport=v.findViewById(R.id.B_airport);
        B_airport.setOnClickListener(this);
        B_railway=v.findViewById(R.id.B_railway);
        B_railway.setOnClickListener(this);
        B_bus=v.findViewById(R.id.B_bus);
        B_bus.setOnClickListener(this);


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);

        MarkerOptions markerOptions=new MarkerOptions();
        LatLng latLng=new LatLng(latitude,longitude);
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions).getTitle();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

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
    public void onClick(View v) {
        GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
        list.setEnabled(true);
        if(v==B_airport) {
            travel="airport";
            list.setVisibility(View.VISIBLE);
            list.setText("Show List of Nearby Airports");
            B_airport.setEnabled(false);
            B_railway.setEnabled(true);
            B_bus.setEnabled(true);
        }
        if(v==B_railway){
            list.setVisibility(View.VISIBLE);
            list.setText("Show List of Nearby Railway Stations");
            travel="train_station";
            B_airport.setEnabled(true);
            B_railway.setEnabled(false);
            B_bus.setEnabled(true);
        }
        if (v==B_bus){
            list.setVisibility(View.VISIBLE);
            list.setText("Show List of Nearby Bus Stations");
            travel="bus_station";
            B_airport.setEnabled(true);
            B_railway.setEnabled(true);
            B_bus.setEnabled(false);
        }

        url=getUrl(latitude,longitude,travel);
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;
//        Log.e("Data",dataTransfer[1].toString());
        getNearbyPlacesData.execute(dataTransfer);

//        religious_places="mosque";
//        url=getUrl(latitude,longitude,religious_places);
//        dataTransfer[0]=mMap;
//        dataTransfer[1]=url;
////        Log.e("Data",dataTransfer[1].toString());
//        getNearbyPlacesData.execute(dataTransfer);
//
//        religious_places="church";
//        url=getUrl(latitude,longitude,religious_places);
//        dataTransfer[0]=mMap;
//        dataTransfer[1]=url;
////        Log.e("Data",dataTransfer[1].toString());
//        getNearbyPlacesData.execute(dataTransfer);

        Toast.makeText(getContext(),"Showing nearby Travel Stations",Toast.LENGTH_SHORT).show();

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




                // Getting a reference to Fragment Manager
                FragmentManager fm = getChildFragmentManager();

                // Starting Fragment Transaction
                FragmentTransaction ft = fm.beginTransaction();


                // Committing the fragment transaction
                ft.commit();

                return false;
            }
        });
    }
}
