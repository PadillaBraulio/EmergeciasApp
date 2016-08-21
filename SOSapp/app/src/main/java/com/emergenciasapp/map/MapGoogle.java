package com.emergenciasapp.map;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emergenciasapp.sosapp.Permissions;
import com.emergenciasapp.sosapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by root on 2/05/16.
 */
public class MapGoogle extends SupportMapFragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private Marker marker;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;
    private float mAccuaracy = 100;

    private String deniedMessage = "";
    private boolean flagIntentMobileData = true;
    private SharedPreferences sharedPref ;
    public static final int SEND_MS_PERMISSION_REQUEST_CODE = 1;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    public static final int WRITE_PERMISSION_REQUEST_CODE = 3;
    private static final int ACCUARACY_ACEPTED = 99;
    private static final String STATION = "55888288";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        setUpMapIfNeeded();

    }


    @Override
    public void onResume() {
        super.onResume();
        sendDeniedMessage();
        startLocationIfNeeded();

    }


    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        RelativeLayout layout = new RelativeLayout(v.getContext());

        layout.addView(v, new RelativeLayout.LayoutParams(-1, -1));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(20, 0, 20, 40);
        View v2 = inflater.inflate(R.layout.map_menu, container, false);

        layout.addView(v2,params);
        //make action
        return layout;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void setUpMapIfNeeded(){
        if (mMap == null) {
            getMapAsync(this);
        }
    }



    public void sendDeniedMessage(){
        if(!deniedMessage.equals("")){
            DeniedDialog(deniedMessage);
            deniedMessage = "";
        }
    }
    public void DeniedDialog(String permission){
        AppCompatActivity app = (AppCompatActivity) getActivity() ;
        Permissions.PermissionDeniedDialog.newInstance(permission).show(app.getSupportFragmentManager(), "dialog");
    }

    public void startLocationIfNeeded(){
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            initLocation();
            startLocationUpdates();
        }
    }

    public void makeAction(){


        final String phone = sharedPref.getString("cellPhoneNumber", "");


        if(isOnline()){
            sendEmergency(phone);
            makeCall(phone);
        }
        else{
            if(flagIntentMobileData) {
                showChangeMobileDataDialog();
                flagIntentMobileData = false;
            }
            else{
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Permissions.requestPermission((AppCompatActivity) getActivity(),
                            SEND_MS_PERMISSION_REQUEST_CODE, Manifest.permission.SEND_SMS);
                    return;
                }
                makeMessageAndCall(phone);
            }


        }

    }
    public void makeMessageAndCall(String phone)
    {
        sendMs(phone);
        makeCall(phone);
        flagIntentMobileData = true;
    }
    public void showChangeMobileDataDialog(){
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.activar_datos)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // After click on Ok, request the permission.
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }
    public void sendMs(final String phone){
        if(!isAccuracyInRange()) return;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SmsManager sm = SmsManager.getDefault();
        String number = phone;
        String msg = "Latitud: " + mLastLocation.getLatitude() + " - Longitud : " + mLastLocation.getLongitude() +  " number: " + phone;
        sm.sendTextMessage(STATION, null, msg, null, null);
        Toast.makeText(getContext(), "Ubicación enviada por mensaje", Toast.LENGTH_SHORT).show();

    }
    public void makeCall(String phone){
        //if you use Intent.ACTION_CALL you need to request permission.
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", STATION, null));
        startActivity(intent);
    }
    public boolean isAccuracyInRange(){
        if (mAccuaracy  > ACCUARACY_ACEPTED) {
            Toast.makeText(getContext(),"No se envio su localización, no tiene buena señal", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }
    public void sendEmergency(final String phone) {

        if(!isAccuracyInRange()) return;

        Emergency em = new Emergency(mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                phone
        );
        Thread mesg = new Thread(em);
        mesg.start();
    }
    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission((AppCompatActivity) getActivity(),
                    LOCATION_PERMISSION_REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        settings.setRotateGesturesEnabled(true);
    }
    public void initLocation(){

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            createLocationRequest();
        }
    }
    protected void startLocationUpdates() {

        if(mLocationRequest == null) return;
        mRequestingLocationUpdates = true;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        final String phone = sharedPref.getString("cellPhoneNumber", "");
        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {

                if(Permissions.isPermissionGranted(permissions,grantResults,Manifest.permission.ACCESS_FINE_LOCATION)){

                }else {
                    deniedMessage = Manifest.permission.ACCESS_FINE_LOCATION;
                }
                return;
            }
            case SEND_MS_PERMISSION_REQUEST_CODE:{
                if(Permissions.isPermissionGranted(permissions,grantResults,Manifest.permission.SEND_SMS)){

                    makeAction();
                }
                else{
                    makeMessageAndCall(phone);
                    deniedMessage =Manifest.permission.SEND_SMS;
                }
                return;
            }
            case WRITE_PERMISSION_REQUEST_CODE:{
                if(Permissions.isPermissionGranted(permissions,grantResults,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                }
                else{
                    deniedMessage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                return;
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mAccuaracy = location.getAccuracy();
        //Toast.makeText(getContext(),mAccuaracy + "" , Toast.LENGTH_SHORT).show();
        updateMap();
    }
    public void updateMap(){
        LatLng ubication = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 15));
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocation();
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {

                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(),10);

                        } catch (Exception e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //THE GPS IS BAD
                        Toast.makeText(getContext(), R.string.error_gps_dontwork,Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
