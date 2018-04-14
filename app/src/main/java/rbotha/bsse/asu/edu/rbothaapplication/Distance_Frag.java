package rbotha.bsse.asu.edu.rbothaapplication;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.function.DoublePredicate;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Distance_Frag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Distance_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Distance_Frag extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int R = 6371000;

    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Distance_Frag() {
        // Required empty public constructor
    }

    public Distance_Frag(Context c){
        context = c;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Distance_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Distance_Frag newInstance(String param1, String param2) {
        Distance_Frag fragment = new Distance_Frag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_distance_, container, false);

        Button buttonCalculate= (Button) rootView.findViewById(R.id.btnCalculate);
        final Spinner spnTop = (Spinner) rootView.findViewById(R.id.spnTop);
        final Spinner spnBottom = (Spinner) rootView.findViewById(R.id.spnBottom);
        final EditText txtGreat = (EditText) rootView.findViewById(R.id.txtGreatCircle);
        final EditText txtInitial = (EditText) rootView.findViewById(R.id.txtInitialBearing);

        final PlaceLibrary library = new PlaceLibrary();



        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<String> spinnyAlly = new ArrayList<String>();
        Cursor res = db.getAllData();
        library.clear();
        spinnyAlly.clear();

        while(res.moveToNext()){
            spinnyAlly.add(res.getString(0));
            library.addPlace(new PlaceDescription(res.getString(0),  res.getString(1),res.getString(2),
                    res.getString(3),res.getString(4), Double.parseDouble(res.getString(5)),
                    Double.parseDouble(res.getString(6)),Double.parseDouble(res.getString(7))));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnyAlly);
        adapter.notifyDataSetChanged();
        spnTop.setAdapter(adapter);
        spnBottom.setAdapter(adapter);


        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate(txtGreat, txtInitial, library.getPlaces().get(spnTop.getSelectedItemPosition()),
                        library.getPlaces().get(spnBottom.getSelectedItemPosition()));
            }
        });
        // Inflate the layout for this fragment
        return rootView;
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

    public void  calculate(EditText txtGreat, EditText txtInitial, PlaceDescription top, PlaceDescription bottom){
        Log.e("Selected items", "calculate: " + top.name + " and " + bottom.name);
        double theta1 = Math.toRadians(top.latitude);
        double theta2 = Math.toRadians(bottom.latitude);
        double thetaDelta = Math.toRadians((bottom.latitude - top.latitude));
        double lambdaDelta = Math.toRadians((bottom.longitute - top.longitute));

    }
}
