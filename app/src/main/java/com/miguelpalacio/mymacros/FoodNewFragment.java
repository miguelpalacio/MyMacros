package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FoodNewFragment extends Fragment {

    EditText foodName;
    EditText portion;
    EditText portionUnits;
    EditText protein;
    EditText carbohydrates;
    EditText fat;
    EditText fiber;

    Button button;
    Button button2;

    EditText enterDetails;
    Button button3;

    DatabaseAdapter databaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_food_new, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get references to the view.
        foodName = (EditText) getActivity().findViewById(R.id.food_name);
        portion = (EditText) getActivity().findViewById(R.id.portion);
        portionUnits = (EditText) getActivity().findViewById(R.id.portion_units);
        protein = (EditText) getActivity().findViewById(R.id.protein);
        carbohydrates = (EditText) getActivity().findViewById(R.id.carbohydrates);
        fat = (EditText) getActivity().findViewById(R.id.fat);
        fiber = (EditText) getActivity().findViewById(R.id.fiber);

        button = (Button) getActivity().findViewById(R.id.add_food);
        button2 = (Button) getActivity().findViewById(R.id.view_details);

        enterDetails = (EditText) getActivity().findViewById(R.id.enter_food_name);
        button3 = (Button) getActivity().findViewById(R.id.get_details);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insertFood();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewDetails();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDetails();
            }
        });
    }

    public void insertFood() {

        String name = foodName.getText().toString();
        int prot = Integer.parseInt(protein.getText().toString());
        int carb = Integer.parseInt(carbohydrates.getText().toString());
        int fa = Integer.parseInt(fat.getText().toString());
        int fib = Integer.parseInt(fiber.getText().toString());
        int por = Integer.parseInt(portion.getText().toString());
        int porU = Integer.parseInt(portionUnits.getText().toString());

        long id = databaseAdapter.insertFood(name, prot, carb, fa, fib, por, porU);

        if (id < 0) {
            Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Row Successfully Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewDetails() {
        String data = databaseAdapter.getAllData();
        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
    }

    public void getDetails() {
        String data = databaseAdapter.getData(enterDetails.getText().toString());
        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
    }

    public void update() {
    }

    public void delete() {

    }

/*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodNewFragment.
     *//*

    // TODO: Rename and change types and number of parameters
    public static FoodNewFragment newInstance(String param1, String param2) {
        FoodNewFragment fragment = new FoodNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FoodNewFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_new, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    */
/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
*/

}
