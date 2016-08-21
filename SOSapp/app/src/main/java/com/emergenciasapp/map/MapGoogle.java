package com.emergenciasapp.map;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
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
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
public class MapGoogle extends SupportMapFragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    /**
     * Google Api variables
     */
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;

    /**
     * Counters and flags
     */
    private boolean showUbication;
    private boolean flagIntentMobileData = true;
    private String deniedMessage = "";
    private int counterGpsCalls;

    /**
     * Telefone of the central station, this is only for san jose pinula station.
     */
    private static final String STATION = "55888288";

    /**
     * Shared Preferences
     */
    private SharedPreferences sharedPref ;
    /**
     * UI view variables
     */
    private TextView latitude;
    private TextView longitude;
    private TextView senal;
    private Button btnCall;
    private ProgressBar mprogressBar;

    /**
     * Request_Codes to check the permission results.
     */
    private static final int SEND_MS_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 3;
    private static final int REQUEST_CHECK_SETTINGS = 4;
    private static final int ACCEPTABLE_SIGNAL = 50;


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
        params.setMargins(20, 0, 20, 50);
        View v2 = inflater.inflate(R.layout.map_menu, container, false);
        layout.addView(v2,params);

        btnCall = (Button) v2.findViewById(R.id.btn_llamar);
        latitude = (TextView) v2.findViewById(R.id.latitude);
        longitude = (TextView) v2.findViewById(R.id.longitude);
        senal = (TextView) v2.findViewById(R.id.senal);
        mprogressBar = (ProgressBar) v2.findViewById(R.id.progressBar);
        btnCall.setOnClickListener(this);
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

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SmsManager sm = SmsManager.getDefault();
        String number = phone;
        String msg = R.string.latitude + mLastLocation.getLatitude() + " " + R.string.longitude + mLastLocation.getLongitude() + R.string.telefone + phone;
        sm.sendTextMessage(STATION, null, msg, null, null);
        Toast.makeText(getContext(), R.string.advice_message_was_sended, Toast.LENGTH_SHORT).show();

    }
    public void makeCall(String phone){
        //if you use Intent.ACTION_CALL you need to request permission.
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", STATION, null));
        startActivity(intent);
    }

    public void sendEmergency(final String phone) {
        Thread Emergency = new Thread(new Emergency(mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                phone));
        Emergency.start();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), R.string.dontProvideGpsPermission,Toast.LENGTH_LONG).show();
                        getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateMap();
    }
    private void updateSignalView(){
        int accuaracy = (int)mLastLocation.getAccuracy();
        if(accuaracy < ACCEPTABLE_SIGNAL/2 ){
            senal.setText(R.string.good_senal);
            senal.setTextColor(Color.parseColor("#4CAF50"));
        }else if (accuaracy < ACCEPTABLE_SIGNAL){
            senal.setText(R.string.regular_senal);
            senal.setTextColor(Color.parseColor("#FF9800"));
        }else{
            senal.setText(R.string.bad_senal);
            senal.setTextColor(Color.RED);
        }
    }
    private void updateTextViews(){
        latitude.setText(R.string.latitude + " " + mLastLocation.getLatitude() );
        longitude.setText(R.string.longitude + " " + mLastLocation.getLongitude() );
    }

    private void makeCallIfNeeded(){
        int signal = (mLastLocation.getAccuracy()< ACCEPTABLE_SIGNAL)?1:0;
        if(showUbication && signal == 1) {
            showUbication = false;
            makeAction();
            showProgress(false);
        }else if(counterGpsCalls == 2){
            showUbication = false;
            counterGpsCalls = 0;
            showProgress(false);
            Toast.makeText(getContext(), R.string.gpsUbicationUnrechable,Toast.LENGTH_LONG).show();
            makeCall(STATION);
        }
        else if(showUbication && signal == 0){
            counterGpsCalls++;

        }


    }
    public void updateMap(){
        LatLng ubication = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 15));
        updateTextViews();
        updateSignalView();
        makeCallIfNeeded();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void setInterval(int seg){
        mLocationRequest.setInterval(seg * 1000);
        mLocationRequest.setFastestInterval(seg * 500);
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
                            status.startResolutionForResult(getActivity(),REQUEST_CHECK_SETTINGS);

                        } catch (Exception e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(getContext(), R.string.error_gps_dontwork,Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void showProgress(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            btnCall.setVisibility(show ? View.GONE : View.VISIBLE);
            mprogressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mprogressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mprogressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mprogressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            btnCall.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Call button pressed
     * @param view
     */
    @Override
    public void onClick(View view) {
        showProgress(true);
        setInterval(2);// CHANGING THE TIME FOR EACH UPDATE FOR GPS
        showUbication = true;
    }
}
