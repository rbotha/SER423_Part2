package rbotha.bsse.asu.edu.rbothaapplication;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add_Frag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_Frag extends android.support.v4.app.Fragment {

    ArrayList<String> list;

    Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Add_Frag() {
        // Required empty public constructor
    }

    public  Add_Frag(Context c){
        context = c;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Add_Frag newInstance(String param1, String param2) {
        Add_Frag fragment = new Add_Frag();
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_, container, false);

        Button buttonAdd= (Button) rootView.findViewById(R.id.btnAdd);

        final EditText txtName = (EditText) rootView.findViewById(R.id.txtname);
        EditText txtAddressTitle = (EditText) rootView.findViewById(R.id.txtAddressTitle);
        EditText txtAddress = (EditText) rootView.findViewById(R.id.txtAddress);
        EditText txtDescription = (EditText) rootView.findViewById(R.id.txtDescription);
        EditText txtCategory = (EditText) rootView.findViewById(R.id.txtCategory);
        EditText txtLongitude = (EditText) rootView.findViewById(R.id.txtLongitute);
        EditText txtLatitude = (EditText) rootView.findViewById(R.id.txtLatitude);
        EditText txtElevation = (EditText) rootView.findViewById(R.id.txtElevation);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add(rootView);
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

    private void Add(View rootView) {

        DatabaseHelper db = new DatabaseHelper(context);

        EditText txtName = (EditText) rootView.findViewById(R.id.txtname);
        EditText txtAddressTitle = (EditText) rootView.findViewById(R.id.txtAddressTitle);
        EditText txtAddress = (EditText) rootView.findViewById(R.id.txtAddress);
        EditText txtDescription = (EditText) rootView.findViewById(R.id.txtDescription);
        EditText txtCategory = (EditText) rootView.findViewById(R.id.txtCategory);
        EditText txtLongitude = (EditText) rootView.findViewById(R.id.txtLongitute);
        EditText txtLatitude = (EditText) rootView.findViewById(R.id.txtLatitude);
        EditText txtElevation = (EditText) rootView.findViewById(R.id.txtElevation);

        if(db.insertData(txtName.getText().toString(),txtDescription.getText().toString(),txtCategory.getText().toString(),txtAddressTitle.getText().toString(),
                    txtAddress.getText().toString(),Double.parseDouble(txtElevation.getText().toString()),Double.parseDouble(txtLatitude.getText().toString()),
                    Double.parseDouble(txtLongitude.getText().toString()))){

            txtName.getText().clear();
            txtAddressTitle.getText().clear();
            txtAddress.getText().clear();
            txtDescription.getText().clear();
            txtCategory.getText().clear();
            txtLongitude.getText().clear();
            txtLatitude.getText().clear();
            txtElevation.getText().clear();
        }





    }
}
