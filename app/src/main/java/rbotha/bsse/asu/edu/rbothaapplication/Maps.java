package rbotha.bsse.asu.edu.rbothaapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.HashMap;

public class Maps extends AppCompatActivity implements OnMapReadyCallback, Add_Frag.OnFragmentInteractionListener,
        GoogleMap.OnMapLongClickListener,
        DialogInterface.OnClickListener {

    private String TAG = getClass().getSimpleName();
    private Context context;
    private EditText dialogEditText;
    private LatLng newPoint;

    private String dialogCallBox;

    DatabaseHelper db;
    SQLiteDatabase dbCursor;
    ArrayList<MarkerOptions> markerArr = new ArrayList<MarkerOptions>();

    private String selectedPlace;

    private HashMap<String, LatLng> places;
    private ArrayList<String> list  = new ArrayList<String>();

    static final LatLng myPos = new LatLng(40, -79);

    private GoogleMap gMap;
    MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = getIntent().getStringArrayListExtra("list");

        Log.w(TAG, "onCreate");
        context = this;

        places = new HashMap<>();

        db = new DatabaseHelper(this);
        dbCursor = db.getReadableDatabase();

        Cursor placesCursor = dbCursor.rawQuery("select NAME, LATITUDE, LONGITUDE from pl_places;", new String[]{});

        while(placesCursor.moveToNext()) {
            try {
                places.put(placesCursor.getString(0), new LatLng(placesCursor.getDouble(1), placesCursor.getDouble(2)));
            } catch (Exception e) {
                Log.w(TAG, "Exception while getting latitude, longitude from database" + e.getMessage());
            }
        }

        try {
            if(gMap == null) {
                mapView = (MapView) findViewById(R.id.mapsfragment);

                mapView.onCreate(Bundle.EMPTY);

                mapView.getMapAsync(this);
                MapsInitializer.initialize(this);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.setTrafficEnabled(true);
        gMap.setBuildingsEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                newPoint = point;

                dialogEditText = new EditText(getApplicationContext());
                dialogEditText.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);

                dialogCallBox = "add";
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Add Place?");
                alert.setView(dialogEditText);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Double newLatitude = newPoint.latitude;
                        Double newLongitude = newPoint.longitude;
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(newPoint.latitude, newPoint.longitude))
                                .title(dialogEditText.getText().toString())
                                .snippet("Long Press to Edit");
                        String insertCommand = "insert into pl_places (NAME, LATITUDE, LONGITUDE) values ('"
                                + dialogEditText.getText().toString() + "', " + newLatitude + ", " + newLongitude + ");";

                        gMap.addMarker(new MarkerOptions()
                                .position(newPoint)
                                .title(dialogEditText.getText().toString())
                                .snippet("Long Press to Edit"));

                        dbCursor.execSQL(insertCommand);
                        gMap.addMarker(marker);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();

            }
        });
        gMap.setOnMapLongClickListener(this);
        android.util.Log.d(this.getClass().toString(), "Loading your map");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng coordinate = new LatLng(latitude, longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 1);
                gMap.animateCamera(yourLocation);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        for (String placeName: places.keySet()) {
            MarkerOptions markerOpts = new MarkerOptions()
                    .position(places
                            .get(placeName))
                    .title(placeName)
                    .snippet("Long Press to Edit");

            markerArr.add(markerOpts);
            gMap.addMarker(markerOpts);
        }

        gMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                selectedPlace = marker.getTitle();

                dialogCallBox = "edit";
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Edit this Place?    " + selectedPlace);
                alert.setTitle(selectedPlace);
                alert.setPositiveButton("OK", (DialogInterface.OnClickListener) context);
                alert.setNegativeButton("Cancel", (DialogInterface.OnClickListener) context);
                alert.show();
            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        newPoint = latLng;

        dialogCallBox = "edit";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        for(MarkerOptions marker : markerArr) {
            if(Math.abs(marker.getPosition().latitude - newPoint.latitude) < 5 && Math.abs(marker.getPosition().longitude - newPoint.longitude) < 5) {
                selectedPlace = marker.getTitle();
                break;
            }
        }
        alert.setTitle("Edit Place?    " + selectedPlace);
        alert.setPositiveButton("OK", this);
        alert.setNegativeButton("Cancel", this);
        alert.show();
    }



    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (dialogCallBox == "add") {

                String newPlaceName = dialogEditText.getText().toString();

                if (!newPlaceName.trim().equals("")) {
                    Double newLatitude = newPoint.latitude;
                    Double newLongitude = newPoint.longitude;

                    gMap.addMarker(new MarkerOptions()
                            .position(newPoint)
                            .title(newPlaceName)
                            .snippet("Long Press to Edit"));

                    String insertCommand = "insert into pl_places (NAME, LATITUDE, LONGITUDE) values ('"
                            + newPlaceName + "', " + newLatitude + ", " + newLongitude + ");";

                    dbCursor.execSQL(insertCommand);
                }
                else {
                    Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
                }
            }
            else if (dialogCallBox == "edit") {

                Intent place = new Intent(context.getApplicationContext(),Place.class);
                for(MarkerOptions marker : markerArr) {
                    if (Math.abs(marker.getPosition().latitude - newPoint.latitude) < 5 && Math.abs(marker.getPosition().longitude - newPoint.longitude) < 5) {

                        String query = "select * from pl_places where NAME = '" + marker.getTitle() + "';";

                        Cursor res = db.getPlace(marker.getTitle());
                        PlaceDescription placeDesc = null;
                        if(res.getCount() > 0){
                            while(res.moveToNext()) {
                                placeDesc = new PlaceDescription(res.getString(0), res.getString(1),
                                        res.getString(2), res.getString(3), res.getString(4),
                                        res.getDouble(5), res.getDouble(6), res.getDouble(7));
                                break;
                            }
                        }

                        place.putExtra("name", placeDesc.name);
                        place.putExtra("address-title", placeDesc.addressTitle);
                        place.putExtra("address-street", placeDesc.addressStreet);
                        place.putExtra("elevation", placeDesc.elevation);
                        place.putExtra("latitude", placeDesc.latitude);
                        place.putExtra("longitude", placeDesc.longitute);
                        place.putExtra("description", placeDesc.description);
                        place.putExtra("category", placeDesc.category);
                        startActivityForResult(place, 1);
                        break;
                    }
                }
            }
        }
        else if (i == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
