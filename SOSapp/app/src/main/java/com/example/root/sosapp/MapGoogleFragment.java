package com.example.root.sosapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by root on 2/05/16.
 */
public class MapGoogleFragment extends Fragment implements OnMapReadyCallback {
    public static final String ARG_PAGE = "pagina";
    private static final String CLASS = "fragmentogooglemap";
    private GoogleMap mMap;

    public static MapGoogleFragment newInstance() {

        Bundle args = new Bundle();

        MapGoogleFragment fragment = new MapGoogleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapgoogle, container, false);
        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        //Button boton = (Button) view.findViewById(R.id.button1);
        //boton.setText("PRESIONE EL BOTON");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(CLASS, "READY");
        mMap = googleMap;


    }
}
