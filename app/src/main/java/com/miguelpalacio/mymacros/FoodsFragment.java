package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;


/**
 * Foods Page.
 * Lists all the foods added by the user.
 */
public class FoodsFragment extends Fragment implements RecyclerListAdapter.ViewHolder.ClickListener {

    public static final String isNewFoodArg = "isNewFood";
    public static final String foodIdArg = "foodId";

    RecyclerView foodListView;
    RecyclerView.LayoutManager foodListLayoutManager;
    RecyclerListAdapter foodListAdapter;

    DatabaseAdapter databaseAdapter;
    String[][] foodInfo;

    OnFoodEditorFragment onFoodEditorFragment;

    TextView emptyPageMessage;
    FloatingActionButton addFood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnFoodEditorFragment interface.
        try {
            onFoodEditorFragment = (OnFoodEditorFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFoodEditorFragment interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get the names and summaries of foods inserted by the user.
        foodInfo = databaseAdapter.getFoods();

        // Food List RecyclerView.
        foodListView = (RecyclerView) getActivity().findViewById(R.id.food_list);

        // Set the adapter.
        foodListAdapter = new RecyclerListAdapter(foodInfo[1], foodInfo[2], foodInfo[3], this);
        foodListView.setAdapter(foodListAdapter);

        // Set the layout manager for the RecyclerView.
        foodListLayoutManager = new LinearLayoutManager(getActivity());
        foodListView.setLayoutManager(foodListLayoutManager);

        // If there are no foods, show the default message for empty page.
        if (foodInfo[0].length == 0) {
            emptyPageMessage = (TextView) getActivity().findViewById(R.id.foods_empty_page);
            emptyPageMessage.setVisibility(View.VISIBLE);
            foodListView.setVisibility(View.INVISIBLE);
        }

        // Define the add new food button, and set a listener.
        addFood = (FloatingActionButton) getActivity().findViewById(R.id.button_add_food);
        addFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment foodEditorFragment = new FoodEditorFragment();

                Bundle args = new Bundle();
                args.putBoolean(isNewFoodArg, true);
                foodEditorFragment.setArguments(args);

                onFoodEditorFragment.openFoodEditorFragment(foodEditorFragment, R.string.toolbar_food_new);
            }
        });
    }

    // When an item on the food list is selected, open FoodEditorFragment with the food data.
    @Override
    public void onListItemClick(int position) {
        //Toast.makeText(getActivity(), "Item with ID " + foodInfo[0][position] + " was clicked", Toast.LENGTH_SHORT).show();
        Fragment foodEditorFragment = new FoodEditorFragment();

        Bundle args = new Bundle();
        args.putBoolean(isNewFoodArg, false);
        args.putLong(foodIdArg, Long.parseLong(foodInfo[0][position]));
        foodEditorFragment.setArguments(args);

        onFoodEditorFragment.openFoodEditorFragment(foodEditorFragment, R.string.toolbar_food_edit);
    }

    public interface OnFoodEditorFragment {
        void openFoodEditorFragment(Fragment fragment, int newToolbarTitle);
    }
}
