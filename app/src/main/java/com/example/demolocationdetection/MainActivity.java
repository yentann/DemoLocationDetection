package com.example.demolocationdetection;


import android.Manifest;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button btnLocation, btnAddUpdate, btnRemoveUpdate;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);

        btnLocation = findViewById(R.id.btnGetLastLocation);
        btnAddUpdate = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveUpdate = findViewById(R.id.btnRemoveLocationUpdate);

        LocationRequest mLocationRequest = LocationRequest.create();


         mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    String msg = "New Loc Detected \n Lat: " + lat + "Lng: " + lng;
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            };
        };




        //get last location button
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission() == true) {
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String msg = "Lat : " + location.getLatitude() + " Lng : " + location.getLongitude();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = "No last known location found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String msg = "Permission not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }

            }
        });


        //get location update
        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission() == true) {
                    //location request
                    LocationRequest mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(10000);
                    mLocationRequest.setFastestInterval(5000);
                    mLocationRequest.setSmallestDisplacement(100);

                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);


                } else {
                    String msg = "Permission not granted to retrieve location info";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }

            }
        });


        //get remove update
        btnRemoveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.removeLocationUpdates(mLocationCallback);
            }
        });
    }


        //check for permission
        private boolean checkPermission() {
            int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                    || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }