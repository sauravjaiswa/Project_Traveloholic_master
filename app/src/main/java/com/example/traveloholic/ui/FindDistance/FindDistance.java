package com.example.traveloholic.ui.FindDistance;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.traveloholic.CustomInfoWindowGoogleMap;
import com.example.traveloholic.DirectionsJSONParser;
import com.example.traveloholic.MapsActivity;
import com.example.traveloholic.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FindDistance extends Fragment implements OnMapReadyCallback{

    GoogleMap mMap;
    double latitude, longitude;
    String tmp="",source,destination;
    List<Address> addressList;

    ArrayList<LatLng> markerPoints;
    ImageButton buttonreverse,buttonrecenter;
    Button buttonFindDistance;
    EditText editTextsource,editTextdestination;
    TextView tv1_distance,tv1_time;
    CardView card;


    public FindDistance() {
        // Required empty public constructor
        latitude=MapsActivity.latitude;
        longitude=MapsActivity.longitude;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_find_distance,container, false);

        editTextsource=v.findViewById(R.id.editTextsource);
        editTextdestination=v.findViewById(R.id.editTextdestination);
        buttonreverse=v.findViewById(R.id.buttonreverse);
        buttonrecenter=v.findViewById(R.id.buttonrecenter);
        buttonFindDistance=v.findViewById(R.id.buttonFindDistance);
        tv1_distance=v.findViewById(R.id.tv1_distance);
        tv1_time=v.findViewById(R.id.tv1_time);
        card=v.findViewById(R.id.card);

        card.setVisibility(View.INVISIBLE);
        buttonrecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextsource.setText("Your Location");
            }
        });

        buttonreverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp=source;
                source=destination;
                destination=tmp;
                editTextsource.setText(source);
                editTextdestination.setText(destination);
            }
        });



        buttonFindDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source=editTextsource.getText().toString().trim();
                destination=editTextdestination.getText().toString().trim();
                if(!(source.equals(""))&&!(destination.equals(""))) {
                    card.setVisibility(View.VISIBLE);
                    findDistance();
                }
            }
        });

        // Initializing
        markerPoints = new ArrayList<LatLng>();

//        list=v.findViewById(R.id.list);
//        list.setText("Show List Of Attractions");
//        list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(getContext(), HospitalActivity.class);
//                startActivity(i);
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

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        String key="&key="+ getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error in download url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.e("url1",data);
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            Log.e("Result",result.toString());

            if (result.size() < 1) {
                Toast.makeText(getContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            tv1_distance.setText("Distance:" + distance);
            tv1_time.setText("Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

    public void findDistance(){
        mMap.clear();
        MarkerOptions markerOptions=new MarkerOptions();
        LatLng latLng=new LatLng(latitude,longitude);
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions).getTitle();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        markerPoints.clear();

        if(source.equals("Your Location")){
            MarkerOptions markerOptionsn=new MarkerOptions();
            LatLng latLngn=new LatLng(latitude,longitude);
            markerPoints.add(latLngn);
            markerOptionsn.position(latLngn);
            markerOptionsn.title("Your Location");
            markerOptionsn.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptionsn).getTitle();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngn));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        else{
            if(!source.equals(""))
            {
                Geocoder geocoder=new Geocoder(getContext());
                try{
                    addressList=geocoder.getFromLocationName(source,5);
                    if (addressList!=null)
                    {
                        for (int i=0;i<addressList.size();i++)
                        {
                            LatLng latLng1=new LatLng(addressList.get(i).getLatitude(),addressList.get(i).getLongitude());
                            markerPoints.add(latLng1);
                            MarkerOptions markerOptions1=new MarkerOptions();



                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.setMinZoomPreference(4);

                            markerOptions1.position(latLng1)
                                    .title(source.toUpperCase())
                                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED));


                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
                            mMap.setInfoWindowAdapter(customInfoWindow);

                            Marker m = mMap.addMarker(markerOptions1);
                            m.showInfoWindow();

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }


        if(destination.equals("Your Location")){
            MarkerOptions markerOptionsn=new MarkerOptions();
            LatLng latLngn=new LatLng(latitude,longitude);
            markerPoints.add(latLngn);
            markerOptionsn.position(latLngn);
            markerOptionsn.title("Your Location");
            markerOptionsn.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptionsn).getTitle();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngn));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        else{
            if(!destination.equals(""))
            {
                Geocoder geocoder=new Geocoder(getContext());
                try{
                    addressList=geocoder.getFromLocationName(destination,5);
                    if (addressList!=null)
                    {
                        for (int i=0;i<addressList.size();i++)
                        {
                            LatLng latLng1=new LatLng(addressList.get(i).getLatitude(),addressList.get(i).getLongitude());
                            markerPoints.add(latLng1);
                            MarkerOptions markerOptions1=new MarkerOptions();



                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.setMinZoomPreference(4);

                            markerOptions1.position(latLng1)
                                    .title(destination.toUpperCase())
                                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN));


                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
                            mMap.setInfoWindowAdapter(customInfoWindow);

                            Marker m = mMap.addMarker(markerOptions1);
                            m.showInfoWindow();

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }


        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            Log.e("Origin,dest",origin+" "+dest);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

}