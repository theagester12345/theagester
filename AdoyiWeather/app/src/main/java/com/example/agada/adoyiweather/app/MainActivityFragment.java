package com.example.agada.adoyiweather.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> forecastadapter;

    public MainActivityFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Creating fake string data in an array form
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Weds -  Cloudy - 72/63",
                "Thurs - Asteroid Shower - 84/62",
                "Fri - Heavy Rain - 65/53",
                "Sat - Heavy Rain - 60/51",
                "Sun - Windy - 60/50"
        };
        // Setting the Arrays to be displayed in list form

        List<String> Weekforecast = new ArrayList<String>(
                Arrays.asList(forecastArray)
        );

//Creating the adapter to communicate with the list view

 forecastadapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                Weekforecast
        );
// Setting the adapter of the list view to the created adapter
        ListView theview = (ListView) rootView.findViewById(R.id.listview_forecast);
        theview.setAdapter(forecastadapter);

        return rootView;
    }

}


