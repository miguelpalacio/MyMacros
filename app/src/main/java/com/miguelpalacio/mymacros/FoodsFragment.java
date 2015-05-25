package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;


/**
 * Foods Page.
 * Lists all the foods added by the user.
 */
public class FoodsFragment extends Fragment implements RecyclerListAdapter.ViewHolder.ClickListener {

    RecyclerView foodListView;
    RecyclerView.LayoutManager foodListLayoutManager;
    RecyclerListAdapter foodListAdapter;

    DatabaseAdapter databaseAdapter;
    String[][] foodInfo;

    OnFoodsInnerFragment onFoodsInnerFragment;

    FloatingActionButton addFood;

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

        // Set the add new food button.
        addFood = (FloatingActionButton) getActivity().findViewById(R.id.button_add_food);
        addFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open the New Food fragment.
                Fragment foodNewFragment = new FoodNewFragment();
                onFoodsInnerFragment.openFoodsInnerFragment(foodNewFragment, R.string.toolbar_food_new);
            }
        });

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
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnFoodsInnerFragment interface.
        try {
            onFoodsInnerFragment = (OnFoodsInnerFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFoodsInnerFragment");
        }
    }

    @Override
    public void onListItemClick(int position) {
        Toast.makeText(getActivity(), "Item at position " + position + " was clicked", Toast.LENGTH_SHORT).show();
    }

    public interface OnFoodsInnerFragment {
        void openFoodsInnerFragment(Fragment fragment, int newToolbarTitle);
    }
}
