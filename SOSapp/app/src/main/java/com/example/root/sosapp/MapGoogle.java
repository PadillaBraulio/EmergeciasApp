package com.example.root.sosapp;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by root on 2/05/16.
 */
public  class MapGoogle extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker;
    private final String class_name = "MapGoogle";
    private final String location_provider = LocationManager.GPS_PROVIDER;
    private Context context;
    Button abuton;

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container,savedInstanceState);

        //save context
        this.context = v.getContext();

        RelativeLayout view = new RelativeLayout(v.getContext());


        //add relative layout
        view.addView(v, new RelativeLayout.LayoutParams(-1, -1));

        abuton = new Button(v.getContext());
        //abuton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
        abuton.setText(" Activar GPS !");
        abuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abuton.setText("CAMBIO");
                Log.i(class_name, "CLICKEADO");
            }
        });
        //abuton.setBackgroundColor(Color.YELLOW);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        view.addView(abuton, params);


       // initializeMap();
        return view;  //return
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(class_name, "READY");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {


        //mMap.setMyLocationEnabled(true);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            UiSettings settings = mMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
            /**

            // Get LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //
                    Log.i(class_name,"Cambio Ubicacion -> latitud : " + location.getAltitude() + " -- longitud : " + location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(location_provider, 0, 0, locationListener);

            Location mylocation = locationManager.getLastKnownLocation(location_provider);

            // Create a criteria object to retrieve provider
            //Criteria criteria = new Criteria();

            // Get the name of the best provider
            //String provider = locationManager.getBestProvider(criteria, true);

            // Get Current Location
            // Location myLocation = locationManager.getLastKnownLocation(provider);

            // set map type
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Get latitude of the current location
            double latitude = mylocation.getLatitude();

            // Get longitude of the current location
            double longitude = mylocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));

            **/

            Log.i(class_name, "Permission_granted");
        } else {
            Log.i(class_name, "Permission_not_granted");
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                Log.i(class_name, "OnCLickMap");

            }
        });

    }


}
