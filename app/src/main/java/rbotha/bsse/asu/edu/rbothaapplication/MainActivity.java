package rbotha.bsse.asu.edu.rbothaapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


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

public class MainActivity extends AppCompatActivity implements Add_Frag.OnFragmentInteractionListener, main.OnFragmentInteractionListener, Distance_Frag.OnFragmentInteractionListener {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            android.support.v4.app.Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new main(getApplicationContext());
                    break;
                case R.id.navigation_add:
                    fragment = new Add_Frag(getApplicationContext());
                    break;
                case R.id.navigation_distance:
                    fragment = new Distance_Frag(getApplicationContext());
                    break;
            }

            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.commit();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        android.support.v4.app.Fragment mFragment = null;
        mFragment = new main(getApplicationContext());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        logUserSettings();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent i = new Intent(getApplicationContext(),Settings.class);
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean http = sharedPrefs.getBoolean("pref_http_or_tcp",false);
        android.util.Log.w(this.getClass().getSimpleName(),"http connection is: "+http);
        String url = sharedPrefs.getString("pref_url",getString(R.string.defaulturl));
        android.util.Log.w(this.getClass().getSimpleName(),"JsonRPC host is: "+url);
        //this.urlET.setText(url);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
