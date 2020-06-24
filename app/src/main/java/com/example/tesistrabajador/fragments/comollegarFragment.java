package com.example.tesistrabajador.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tesistrabajador.R;
import com.example.tesistrabajador.mapclasses.FetchURL;
import com.example.tesistrabajador.mapclasses.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;



public class comollegarFragment extends Fragment implements OnMapReadyCallback , TaskLoadedCallback {


    GoogleMap map;
    SupportMapFragment mapFragment;
    private Polyline currentPolyline;
    MarkerOptions ubicaciontrabajador, ubicacioncliente,place1,place2;
    Button btncomollegarcliente;
    boolean actualposition= true;
    Double latitud=0.0,longitud=0.0,latitudorigen,longitudorigen;

    public comollegarFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comollegar, container, false);
        Bundle args = getArguments();
        if (args == null) {
            // No hay datos, manejar excepción
        }else{
            //se cargan los datos enviados del trabajador
            latitud= args.getDouble("latitudcliente");
            longitud= args.getDouble("longitudcliente");
        }
        btncomollegarcliente = (Button) v.findViewById(R.id.btncomollegarcliente);

        mapFragment= (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





/*
        btncomollegarcliente = v.findViewById(R.id.btncomollegarcliente);
        btncomollegarcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(comollegarFragment.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });
        //27.658143,85.3199503
        //27.667491,85.3208583
        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");




*/





        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }


        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);

        //map.addMarker(place1);
        //map.addMarker(place2);

        btncomollegarcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ubicacioncliente = new MarkerOptions().position(new LatLng(latitud,longitud)).title("ubicacion cliente");
                ubicaciontrabajador = new MarkerOptions().position(new LatLng(latitudorigen,longitudorigen)).title("ubicacion trabajador");

                String url =getUrl(ubicacioncliente.getPosition(),ubicaciontrabajador.getPosition(),"driving");

                new FetchURL(comollegarFragment.this).execute(url,"driving");

            }
        });



        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if(actualposition){
                    latitudorigen= location.getLatitude();
                    longitudorigen= location.getLongitude();
                    actualposition=false;

                    LatLng miposicion = new LatLng(latitudorigen,longitudorigen);

                    map.addMarker(new MarkerOptions().position(miposicion).title("estoy aqui!"));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(miposicion,15));



                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            map.clear();
                            latitudorigen=0.0;
                            longitudorigen=0.0;
                            //se crea el marcador
                            MarkerOptions markerOptions = new MarkerOptions();
                            //se setea la posicion al marcador
                            markerOptions.position(latLng);

                            latitudorigen=latLng.latitude;
                            longitudorigen=latLng.longitude;
                            //descripcion del titulo
                            markerOptions.title(latLng.latitude+" ; "+latLng.longitude);
                            //borrar los click anteriors
                            map.clear();

                            //zoom al marcador
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                            //añadir el marcador al mapa
                            map.addMarker(markerOptions);

                        }
                    });


                }
            }
        });


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }
}