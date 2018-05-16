package com.example.opilane.mapsgoogle;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = "MapActivity";
    private static final String FINAL_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean LoactionPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private GoogleMap gKaart;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Kaart on valmis", Toast.LENGTH_SHORT).show();
        gKaart = googleMap;
        if (LoactionPermissionsGranted) {
            getSeadmeAsukoht();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            gKaart.setMyLocationEnabled(true);
            gKaart.getUiSettings().setMyLocationButtonEnabled(false);
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINAL_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                LoactionPermissionsGranted = true;
                kaivitaKaart();

            } else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void kaivitaKaart() {
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoactionPermissionsGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            LoactionPermissionsGranted = false;
                            break;
                        }
                    }
                    LoactionPermissionsGranted = true;
                    kaivitaKaart();
                }
        }
    }
    private void getSeadmeAsukoht(){
        Log.d(TAG, "SeadmeAsukoht: Seadme asukoha tuvastamine");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (LoactionPermissionsGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MapActivity.this, "Asukoht on tuvastatud",
                                Toast.LENGTH_LONG).show();
                        Location currentLocation = (Location)task.getResult();
                        liigutaKaamerat(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude(),DEFAULT_ZOOM);
                    }
                    else{
                        Toast.makeText(MapActivity.this, "Asukoht ei olnud võmalik" +
                                "tuvastada", Toast.LENGTH_LONG).show();
                    }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Log.e(TAG,"SecurityException"+ e.getMessage());
        }
    }
    private void liigutaKaamerat(LatLng latLng, float zoom) {
        Log.d(TAG, "lat: " + latLng.latitude + "lng: " +latLng.longitude);
        gKaart.moveCamera(CameraUpdateFactory.newLatLng(latLng, zoom));
    }

}
