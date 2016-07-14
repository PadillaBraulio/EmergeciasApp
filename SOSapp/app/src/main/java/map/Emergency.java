package map;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 30/06/16.
 */
public class Emergency implements Runnable{
    //LOCAL DIRECTION "http://192.168.1.11:3000"
    //CLOUD9 DIRECTION https://webserversosapp-brauliojuancarlos.c9users.io
    private final static String DOMAIN = "https://webserversosapp-brauliojuancarlos.c9users.io" ;
    private final double latitude;
    private final double longitude;
    private final String numberphone;

    public Emergency(double latitude, double longitude, String numberphone) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberphone = numberphone;
    }

    public boolean putEmergency() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;
        try {
            URL url = new URL( DOMAIN + "/webservices/PutEmergency?" +
                    "latitude=" + this.latitude +
                    "&longitude=" + this.longitude +
                    "&telefone=" +  this.numberphone);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return false;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return false;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(),"Error of IO",e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(this.getClass().getSimpleName(), "Error closing stream", e);
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        if(putEmergency())
        {
            Log.i(this.getClass().getSimpleName(), "Put emergency satisfactory");

        }else
        {
            Log.e(this.getClass().getSimpleName(), "put Emergency insatisfactory");
        }
    }
}
