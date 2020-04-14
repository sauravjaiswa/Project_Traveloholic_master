package com.example.traveloholic.ui.Hospitals;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
public class hospitals extends Fragment implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    GoogleMap mMap;
    GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE=99;
    int PROXIMITY_RADIUS=10000;
    double latitude,longitude;
    ImageButton type;



    Geocoder geocoder;
    List<Address> addresses;
    //Button b_hospital,b_school,b_restaurant,b_attraction,b_temple,b_park;

    // Links marker id and place object
    HashMap<String, DataParser> mHMReference = new HashMap<String, DataParser>();
    Object dataTransfer[]=new Object[2];
    GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
    String url;
    Button list;


    public hospitals() {
        // Required empty public constructor
        latitude=MapsActivity.latitude;
        longitude=MapsActivity.longitude;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.global_fragment,container, false);

        list=v.findViewById(R.id.list);
        list.setText("Show List Of Hospitals");
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), HospitalActivity.class);
                startActivity(i);
            }
        });

        type=v.findViewById(R.id.type_map);
//        type.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(getContext(), v);
//                popup.setOnMenuItemClickListener((androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener) getContext());
//                popup.inflate(R.menu.popup_menu);
//                popup.show();
//            }
//        });
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

        String hospital="hospital";
        url=getUrl(latitude,longitude,hospital);
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;
//        Log.e("Data",dataTransfer[1].toString());
        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(getContext(),"Showing nearby Hospitals",Toast.LENGTH_SHORT).show();

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
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(getContext(), "Showing " +item.getTitle()+" view", Toast.LENGTH_SHORT).show();
        Log.e("sd","jfhh");
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