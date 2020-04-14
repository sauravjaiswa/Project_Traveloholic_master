package com.example.traveloholic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context=ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view=((Activity)context).getLayoutInflater()
                .inflate(R.layout.activity_custom_info_window_google_map,null);

        TextView name_tv = view.findViewById(R.id.name);

        name_tv.setText(marker.getTitle());

        return view;
    }
}
