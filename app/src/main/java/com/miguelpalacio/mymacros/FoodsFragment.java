package com.miguelpalacio.mymacros;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Foods Page.
 * <p>
 *      Add, Edit, Remove Foods.
 * </p>
 */
public class FoodsFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_foods, container, false);
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
}
