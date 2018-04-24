package rbotha.bsse.asu.edu.rbothaapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;


/*
 * Copyright 2018 Ruan Botha,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Assignment for week 5 demonstrating multiple views, database
 * integration (SQLite), lists, and some maths.
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 * @author Ruan Botha rbotha@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2018
 */

public class Place extends AppCompatActivity {

    ArrayList<String> list;

    String name;
    String addressTitle;
    String addressStreet;
    double elevation;
    double latitude;
    double longitude;
    String description;
    String category;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        EditText txtName = (EditText) findViewById(R.id.txtname);
        EditText txtAddressTitle = (EditText) findViewById(R.id.txtAddressTitle);
        EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
        EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        EditText txtCategory = (EditText) findViewById(R.id.txtCategory);
        EditText txtLongitude = (EditText) findViewById(R.id.txtLongitute);
        EditText txtLatitude = (EditText) findViewById(R.id.txtLatitude);
        EditText txtElevation = (EditText) findViewById(R.id.txtElevation);

        name = getIntent().getStringExtra("name");
        addressTitle = getIntent().getStringExtra("address-title");
        addressStreet = getIntent().getStringExtra("address-street");
        elevation = getIntent().getDoubleExtra("elevation", 0.00);
        latitude = getIntent().getDoubleExtra("latitude", 0.00);
        longitude = getIntent().getDoubleExtra("longitude",0.00);
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");

        txtName.setText(name);
        txtAddressTitle.setText(addressTitle);
        txtAddress.setText(addressStreet);
        txtElevation.setText(String.valueOf(elevation));
        txtLatitude.setText(String.valueOf(latitude));
        txtLongitude.setText(String.valueOf(longitude));
        txtDescription.setText(description);
        txtCategory.setText(category);

        final int position = getIntent().getIntExtra("position", -1);
        list = getIntent().getStringArrayListExtra("list");
        Button button= (Button) findViewById(R.id.btnDelete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(name);
            }
        });
        Button buttonChange= (Button) findViewById(R.id.btnChange);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Change(position, new PlaceDescription(name, description, category, addressTitle, addressStreet, elevation, latitude, longitude));
            }
        });

        Button btnMap= (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Maps.class);
                intent.putStringArrayListExtra("list", list);
                startActivityForResult(intent, 1);

            }
        });

    }

    private void Delete(String oldName) {

        db = new DatabaseHelper(getApplicationContext());

        Intent intent = new Intent();

        int deleted = db.deleteData(oldName);

        Log.e("DELETE", "Delete: rows were Delete: " + deleted);

        URL url = null;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String urlStr = sharedPrefs.getString("pref_url",getString(R.string.defaulturl));
        try {
            url = new URL(urlStr);
            JsonRPCRequestViaHttp request = new JsonRPCRequestViaHttp(url, new Handler(), "remove", "[" +oldName + "]", db, getApplicationContext());
            request.start();
            request.join();
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), "Unable to connect to:  " + urlStr, Toast.LENGTH_SHORT).show();
            android.util.Log.d(this.getClass().getSimpleName(),"Exception in JsonRPC request: "+ex.toString());
        }



        setResult(RESULT_OK, intent);
        finish();
    }

    private void Change(int position, PlaceDescription place){

        db = new DatabaseHelper(getApplicationContext());

        EditText txtName = (EditText) findViewById(R.id.txtname);
        EditText txtAddressTitle = (EditText) findViewById(R.id.txtAddressTitle);
        EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
        EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        EditText txtCategory = (EditText) findViewById(R.id.txtCategory);
        EditText txtLongitude = (EditText) findViewById(R.id.txtLongitute);
        EditText txtLatitude = (EditText) findViewById(R.id.txtLatitude);
        EditText txtElevation = (EditText) findViewById(R.id.txtElevation);

        int added = db.updateData(place.name, txtName.getText().toString(), txtDescription.getText().toString(), txtCategory.getText().toString(),
                txtAddressTitle.getText().toString(), txtAddress.getText().toString(), Double.parseDouble(txtElevation.getText().toString()),
                Double.parseDouble(txtLatitude.getText().toString()), Double.parseDouble(txtLongitude.getText().toString()));

        URL url = null;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String urlStr = sharedPrefs.getString("pref_url",getString(R.string.defaulturl));
        try {
            url = new URL(urlStr);
            JsonRPCRequestViaHttp request = new JsonRPCRequestViaHttp(url, new Handler(), "remove", "[" +place.name + "]", db, getApplicationContext());
            request.start();
            request.join();

            PlaceDescription newPlace = new PlaceDescription(txtName.getText().toString(), txtDescription.getText().toString(),
                    txtCategory.getText().toString(), txtAddressTitle.getText().toString(),
                    txtAddress.getText().toString(), Double.parseDouble(txtElevation.getText().toString()),
                    Double.parseDouble(txtLatitude.getText().toString()), Double.parseDouble(txtLongitude.getText().toString()));

            request = new JsonRPCRequestViaHttp(url, new Handler(), "add", "[" +newPlace.toJSonString()+ "]", db, getApplicationContext());
            request.start();
            request.join();
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), "Unable to connect to:  " + urlStr, Toast.LENGTH_SHORT).show();
            android.util.Log.d(this.getClass().getSimpleName(),"Exception in JsonRPC request: "+ex.toString());
        }


    }

    @Override
    public void onBackPressed() {
        Log.e("delarted", "On back: back button  ");
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
