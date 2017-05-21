package com.example.ghauri.mapsapp;

import android.Manifest;
import android.app.Dialog;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 8675309;
    static final int CAM_REQUEST = 0;
    GoogleMap mGoogleMap;
    GoogleApiClient apiClient;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (gServicesAvailable()) {
            Toast.makeText(this, "Play Services are available", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {
            Toast.makeText(this, "Error: Play Services Unavailable!!!", Toast.LENGTH_SHORT).show();
        }
        button = (Button) findViewById(R.id.button);
        Toast.makeText(this, "ok 1", Toast.LENGTH_SHORT).show();
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    invokeCamera();
                }else{
                    String[] permissionRequest = {Manifest.permission.CAMERA};
                    requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                invokeCamera();
            }else
            {
                Toast.makeText(this, "Cannot open Camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void invokeCamera() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         Toast.makeText(this, "i am in function", Toast.LENGTH_LONG).show();
        //File file = getFile();
        //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera_intent,0);
    }

   /* private File getFile(){
        File folder = new File("sdcard/camera_app");
        if(!folder.exists())
        {
            Toast.makeText(this, "ok file", Toast.LENGTH_SHORT).show();
            folder.mkdir();
        }
        File image_file = new File(folder,"image1.jpg");
        return image_file;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "On Activity Result Method Called", Toast.LENGTH_SHORT).show();
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean gServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant Connect to Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mGoogleMap != null){
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    MainActivity.this.setMarker("Local", latLng.latitude,latLng.longitude );
                }
            });
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){

                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder g = new Geocoder(MainActivity.this);
                    LatLng l4 = marker.getPosition();
                    List<android.location.Address> list = null;
                    try {
                         list = g.getFromLocation(l4.latitude, l4.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    android.location.Address addr = list.get(0);
                    marker.setTitle(addr.getLocality());
                    marker.showInfoWindow();
                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                    LatLng l3 = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + l3.latitude);
                    tvLng.setText("Longitude: " + l3.longitude);
                    tvSnippet.setText(marker.getSnippet());
                    return v;
                }
            });
        }

        goToLocationZoomed(33.546764,73.1818103,15);
 /*       if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();*/
    }

    private void goToLocation(double lat, double lon) {
        LatLng l = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLng(l);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocationZoomed(double lat, double lon, float z) {
        LatLng l = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(l, z);
        mGoogleMap.moveCamera(update);
    }
    Marker marker;
    Circle circle;
    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);

       // if(et.getText().toString().trim().equals("Where do you want to go!"))
       // {
       //     et.setError("Loaction Hint is Required");
       //     Toast.makeText(this, "Loaction Hint is Required", Toast.LENGTH_LONG).show();
       // }
        //et.setText("Searching...");
        if(et.getText().toString().equals(""))
        {
            et.setError("Please fill this Field!");
            Toast.makeText(this, "Loaction Hint is Required", Toast.LENGTH_LONG).show();
        }
        else {
            String loc = et.getText().toString();
            Geocoder gc = new Geocoder(this);
            List<android.location.Address> list = gc.getFromLocationName(loc, 1);
            if (list.get(0).getLocality().toString().equals("")) {
                Toast.makeText(this, "Location not found!!!", Toast.LENGTH_LONG).show();
            } else {
                android.location.Address add = list.get(0);
                String locality = add.getLocality();
                Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
                double lat = add.getLatitude();
                double lon = add.getLongitude();
                goToLocationZoomed(lat, lon, 15);
                setMarker(locality, lat, lon);
            }
        }
    }

    private void setMarker(String locality, double lat, double lon) {
        if(marker != null)
            clearMap();
        MarkerOptions options = new MarkerOptions()
                                .title(locality)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                .position(new LatLng(lat,lon))
                                .snippet("Current Location");
        marker = mGoogleMap.addMarker(options);
        circle = drawCircle(new LatLng(lat,lon));
    }

    private Circle drawCircle(LatLng latLng) {
        CircleOptions options = new CircleOptions()
                                .center(latLng)
                                .radius(1000)
                                .fillColor(0x90D1F2EB)
                                .strokeColor(Color.BLUE)
                                .strokeWidth(3);
        return mGoogleMap.addCircle(options);
    }

    private void clearMap()
    {
        marker.remove();
        marker = null;
        circle.remove();
        circle = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
        {
            Toast.makeText(this, "Location cannot found...", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "OK 1", Toast.LENGTH_SHORT).show();
            LatLng l2 = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(l2, 15);
            mGoogleMap.animateCamera(update);
        }
    }
}
