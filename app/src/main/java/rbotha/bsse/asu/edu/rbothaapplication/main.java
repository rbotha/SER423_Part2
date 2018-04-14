package rbotha.bsse.asu.edu.rbothaapplication;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link main.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayAdapter<String> adapter;
    private ArrayList<String> places = new ArrayList<String>();

    DatabaseHelper db;

    PlaceLibrary library  = new PlaceLibrary();
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public main(){
        // Required empty public constructor
    }


    public main(Context c) {
        // Required empty public constructor
        context = c;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static main newInstance(String param1, String param2) {
        main fragment = new main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.e("on Create", "onCreateView: On create call" );

        db = new DatabaseHelper(context);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView mLstView = (ListView) rootView.findViewById(android.R.id.list);

        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,places);
        mLstView.setAdapter(adapter);


        InputStream is = context.getResources().openRawResource(R.raw.places);
        String json = null;
        try {

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            Iterator keys = obj.keys();

            Cursor res = db.getAllData();
            if(res.getCount() == 0) {
                while (keys.hasNext()) {
                    Object key = keys.next();
                    JSONObject value = obj.getJSONObject((String) key);
                    //String component = value.getString("component");
                    PlaceDescription place = new PlaceDescription(value.toString());
                    library.addPlace(place);
                    boolean isInserted = false;

                    isInserted = db.insertData(key.toString(), place.description, place.category, place.addressTitle, place.addressStreet, place.elevation, place.latitude, place.longitute);

                    //Toast.makeText(context, "A place with that name already exists", Toast.LENGTH_LONG).show();

                    if (isInserted == true) {
                        Toast.makeText(context, "Data Inserted", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("Error", "onCreateView: ERror adding data");
                    }
                }
            }

            res = db.getAllData();
            library.clear();
            places.clear();
            while(res.moveToNext()){
                library.addPlace(new PlaceDescription(res.getString(0),res.getString(1),
                        res.getString(2),res.getString(3),res.getString(4),
                        res.getDouble(5),res.getDouble(6),res.getDouble(7)));
                places.add(res.getString(0));
            }


        }catch (Exception ex){
            android.util.Log.e(this.getClass().getSimpleName(),"exception reading places.json or reading the DB");
            android.util.Log.e(this.getClass().getSimpleName(),ex.toString());
        }
        adapter.notifyDataSetChanged();

        mLstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d("My App", "You clicked item" + adapterView.getAdapter().getItem(position).toString());

                Intent i = new Intent(context.getApplicationContext(),Place.class);
                i.putExtra("position", position);
                i.putStringArrayListExtra("list", places);

                i.putExtra("name", library.getPlaces().get(position).name);
                i.putExtra("address-title", library.getPlaces().get(position).addressTitle);
                i.putExtra("address-street", library.getPlaces().get(position).addressStreet);
                i.putExtra("elevation", library.getPlaces().get(position).elevation);
                i.putExtra("latitude", library.getPlaces().get(position).latitude);
                i.putExtra("longitude", library.getPlaces().get(position).longitute);
                i.putExtra("description", library.getPlaces().get(position).description);
                i.putExtra("category", library.getPlaces().get(position).category);
                startActivityForResult(i, 1);
            }
        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Spice", "onActivityResult: I fired at on activity");
        super.onActivityResult(requestCode, resultCode, data);

        Cursor res = db.getAllData();
        places.clear();
        library.clear();
        adapter.clear();
        Log.e("Adapt" ,"onActivityResult: Imi clear?  "  + adapter.getCount());
        refreshList(res);
        //dapter.addAll(refreshList(res));
        adapter.notifyDataSetChanged();



    }

    public void refreshList(Cursor res){
        places.clear();
        library.clear();

        while(res.moveToNext()){
            Log.e("Place", "refreshList: Res is " + res.getString(0));
            library.addPlace(new PlaceDescription(res.getString(0),res.getString(1),
                    res.getString(2),res.getString(3),res.getString(4),
                    res.getDouble(5),res.getDouble(6),res.getDouble(7)));
            places.add(res.getString(0));
        }
        Log.e("Place", "refreshList: Res is " + places);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
