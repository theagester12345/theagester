package com.example.agada.adoyiweather.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecastfragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            fetchweatherfrmowm getweather = new fetchweatherfrmowm();
            getweather.execute("2298890");



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Background Thread containing network code
    // NB: OMW = OpenWeatherMap
    public class fetchweatherfrmowm extends AsyncTask<String, Void, String[]> {
        private final String LOG = fetchweatherfrmowm.class.getSimpleName();

        // Method formats the JSON string to give actual date
        // Json gives unix timestamp that is in seconds and must be converted
        // to milliseconds in order to get the actual date
        private String getactualdate (long time){
            // converts the time from seconds to milliseconds
            Date date = new Date(time * 1000);
            SimpleDateFormat actualdate = new SimpleDateFormat("E, MMM d");
            return actualdate.format(date).toString();


        }
        //Method formats JSON string to give temperatures
        // in desirable format
        private String formattemp (double high,double low){
            //formatting temperature form JSON string into desirable string
            long h_formatted = Math.round(high);
            long l_formatted = Math.round(low);

            String formattempstr = h_formatted+"/"+l_formatted;
            return  formattempstr;
        }

        //Method takes specific values from the JSON string to produce
        // a final string that is displayed on the app
        // The method will be returing multiple json strings, which explains
        //the use of the return type string []
        private String[] getdesiredjsonstring (String weatherjson, int numdays)
        throws JSONException{
            // Creating variable to hold the the specfic values we require from the json string
            String day;
            String description;
            String temperature;

            // String array take the number of days of weather forecast the user requires
            String[] desiredstring=new String[numdays];

            //converts the json string to a json object
            JSONObject weatherjsonobj = new JSONObject(weatherjson);

            // Points to the list array in the json string from open weather map
            JSONArray listarray = weatherjsonobj.getJSONArray("list");

            for (int i=0;i<listarray.length();i++){
                //Points to the ith element in the list array
                JSONObject listarrayelement = listarray.getJSONObject(i);

                //Getting day from the list array in dt
                // and converting into readable day by applying the
                // get actual date method
                long jsonday = listarrayelement.getLong("dt");
                day = getactualdate(jsonday);

                // Getting the description of the weather from the json string
                //in the weather array.
                JSONObject weatherarrayelement = listarrayelement.getJSONArray("weather").getJSONObject(0);
                description = weatherarrayelement.getString("description");

                // Getting the temperatures from the temp array in the Json string
                // and converting into desired format by applying the format temp method
                JSONObject temperatureobj = listarrayelement.getJSONObject("temp");
                double max = temperatureobj.getDouble("max");
                double min= temperatureobj.getDouble("min");
                temperature = formattemp(max,min);

                //Placing the readable string extracted from the json string
                // in the string array
                desiredstring[i]= day+" - "+description+" - "+temperature;


            }

            for (String s : desiredstring){
                Log.v(LOG,"Forecast entry : "+s);
            }
            return desiredstring;




        }

        protected String[] doInBackground(String... params) {

            //returning null if there is no string passed into fetch weather class
            if (params==null){
                return null;
            }
            //Creating variables to hold the https url connection and
            // buffered reader objects

            HttpURLConnection urlconnect = null;
            BufferedReader urlconnectreader = null;

            //Creating variable to hold raw JSON response as String
            String strJson = null;
            // Creating variables to hold values of url query
            String key_value = "32386b584eeda2843102c549025e2504";
            String mode_value="json";
            String units_value="metric";
            String count_value="7";


            try {
                // Creating variable to hold the base url
                final String api_base_url = "http://api.openweathermap.org/data/2.5/forecast/daily?";

                // Creating variables to hold query container (lol) i think only i will understand this comments
                String id_hold="id";
                String key_hold="APPID";
                String mode_hold="mode";
                String units_hold="units";
                String count_hold="cnt";

                //Building url for the open weather map api
                Uri uribuild = Uri.parse(api_base_url).buildUpon().appendQueryParameter(id_hold,params[0]).appendQueryParameter(key_hold,key_value).appendQueryParameter(mode_hold,mode_value).appendQueryParameter(units_hold,units_value).appendQueryParameter(count_hold,count_value).build();


                URL url = new URL(uribuild.toString());


                //Creating the request to OpenWeatherMap and the connection
                urlconnect = (HttpURLConnection) url.openConnection();
                urlconnect.setRequestMethod("GET");
                urlconnect.connect();

                //Getting input stream from OpenWeatherMap
                InputStream inputOWM = urlconnect.getInputStream();

                //Creating buffer to hold input from OpenWeatherMap
                StringBuffer inputholder = new StringBuffer();

                //Handling null input from OpenWeatherMap
                if (inputOWM == null) {
                    // do nothing
                    return null;
                }


                //Placing input stream from OpenWeatherMap in a buffer reader to be read
                urlconnectreader = new BufferedReader(new InputStreamReader(inputOWM));

                //Editing input from OpenWeatherMap
                // Also placing edited inputs into String buffer
                String line;
                while ((line = urlconnectreader.readLine()) != null) {
                    inputholder.append(line + "\n");
                }

                // Checking that input holder buffer is not empty
                if (inputholder.length() == 0) {
                    return null;
                }

                // Placing edited inputs in input holder to to string json
                strJson = inputholder.toString();


            } catch (IOException e) {

                Log.e("LOG", "Error", e);
                return null;

            } finally {
                if (urlconnect != null) {
                    urlconnect.disconnect();
                }

                if (urlconnectreader != null) {
                    try {
                        urlconnectreader.close();
                    } catch (final IOException e) {
                        Log.e("LOG", "Error in closing the stream", e);
                    }
                }
            }
            int numdays = Integer.parseInt(count_value);
            try {
                return getdesiredjsonstring(strJson,numdays);
            }catch (JSONException e){
                Log.e(LOG,e.getMessage(),e);
                e.printStackTrace();
            }




            return null;
        }
        // The on post execute method takes the desired forecast from the do in background method
        // and applies it to the forecast adapter.
        @Override
        protected void onPostExecute (String [] desiredstring){
            if (desiredstring != null){
                // Creating a main activity fragment object in other
                // to access the fore cast adapter, clear it and add the desired string
                MainActivityFragment adapterfrag = new MainActivityFragment();
                // checking that the fore cast adapter is not null before executing clear task
                if (adapterfrag.forecastadapter != null){
                adapterfrag.forecastadapter.clear();

                }
                for (String s : desiredstring){
                    if (adapterfrag.forecastadapter == null){
                    adapterfrag.forecastadapter.add(s);}
                }
            }

        }


    }
}
