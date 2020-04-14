package com.example.traveloholic.ui.AboutUs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.traveloholic.R;


public class AboutUs extends Fragment {

    TextView text_send;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_about_us,container, false);

        text_send=v.findViewById(R.id.text_send);
        String s="Traveloholic is an android mobile application that acts as a virtual tourist guide for all the tourists visiting the place. This project aims to provide the tourists with a map of the city depending on its current location entered by the user. This information would help the tourist to find the desired locations to visit. Well, it consists of entire details of those locations, how to reach the location as well as other emergency amenities like hospitals. This application is mainly beneficial for tourists having no idea about the places they want to visit. It would help all the people visiting different places and help them explore the new place without any difficulties faced. This application would help the tourists to locate all nearby schools, amusement parks, temples, various attractions etc. The major objective is to provide travellers an application with an easy and responsive interface that makes their trip more comfortable.\n" +
                "The application would also help in recommending the routes to the user wherever they want to visit and would help them as a virtual guide recommending the best paths to visit any particular location at a given time.";

        text_send.setText(s);
        return v;
    }
}
