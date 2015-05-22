package com.miguelpalacio.mymacros;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * Foods Page.
 * <p>
 *      Add, Edit, Remove Foods.
 * </p>
 */
public class FoodsFragment extends Fragment implements RecyclerListAdapter.ViewHolder.ClickListener {

    RecyclerView foodListView;
    RecyclerView.LayoutManager foodListLayoutManager;
    RecyclerListAdapter foodListAdapter;

    String[][] foodInfo;

    DatabaseAdapter databaseAdapter;

    Button button;

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

        button = (Button) getActivity().findViewById(R.id.open_new_food);

        // Get the names and summaries of foods inserted by the user.
        foodInfo = databaseAdapter.getFoods();

        // Food List RecyclerView.
        foodListView = (RecyclerView) getActivity().findViewById(R.id.food_list);

        // Set the adapter.
        foodListAdapter = new RecyclerListAdapter(foodInfo[0], foodInfo[1], foodInfo[2], this);
        foodListView.setAdapter(foodListAdapter);

        // Set the layout manager for the RecyclerView.
        foodListLayoutManager = new LinearLayoutManager(getActivity());
        foodListView.setLayoutManager(foodListLayoutManager);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment foodNewFragment = new FoodNewFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, foodNewFragment);
                transaction.addToBackStack(null);

                transaction.commit();
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("New Food");
/*                ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((ActionBarActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);*/

            }
        });
    }

    @Override
    public void onListItemClick(int position) {
        Toast.makeText(getActivity(), "Item at position " + position + " was clicked", Toast.LENGTH_SHORT).show();
    }
}
