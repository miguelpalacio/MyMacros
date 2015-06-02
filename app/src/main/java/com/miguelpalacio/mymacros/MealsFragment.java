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

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * My Meals Page.
 * Lists all the meals added by the user.
 */
public class MealsFragment extends Fragment implements SubheadersListAdapter.ViewHolder.ClickListener {

    public static final String isNewMealArg = "isNewMeal";
    public static final String mealIdArg = "mealId";

    RecyclerView mealListView;
    RecyclerView.LayoutManager mealListLayoutManager;
    SubheadersListAdapter mealListAdapter;

    DatabaseAdapter databaseAdapter;
    String[][] mealInfo;

    OnMealEditorFragment onMealEditorFragment;

    TextView emptyPageMessage;
    FloatingActionButton addMeal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_meals, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnMealEditorFragment interface.
        try {
            onMealEditorFragment = (OnMealEditorFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealEditorFragment interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get the names and summaries of meals inserted by the user.
        //mealInfo = databaseAdapter.getMeals();

        // Food List RecyclerView.
        mealListView = (RecyclerView) getActivity().findViewById(R.id.meal_list);

        // Set the adapter.
/*        mealListAdapter = new SubheadersListAdapter(mealInfo[1], mealInfo[2], mealInfo[3], this);
        mealListView.setAdapter(mealListAdapter);*/

        // Set the layout manager for the RecyclerView.
        mealListLayoutManager = new LinearLayoutManager(getActivity());
        mealListView.setLayoutManager(mealListLayoutManager);

        // If there are no meals, show the default message for empty page.
/*        if (mealInfo[0].length == 0) {*/
            emptyPageMessage = (TextView) getActivity().findViewById(R.id.meals_empty_page);
            emptyPageMessage.setVisibility(View.VISIBLE);
            mealListView.setVisibility(View.INVISIBLE);
/*        }*/

        // Define the add new meal button, and set a listener.
        addMeal = (FloatingActionButton) getActivity().findViewById(R.id.button_add_meal);
        addMeal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment mealEditorFragment = new MealEditorFragment();

                Bundle args = new Bundle();
                args.putBoolean(isNewMealArg, true);
                mealEditorFragment.setArguments(args);

                onMealEditorFragment.openMealEditorFragment(mealEditorFragment, R.string.toolbar_meal_new);
            }
        });
    }

    // When an item on the meal list is selected, open mealEditorFragment with the meal data.
    @Override
    public void onListItemClick(int position) {
        Fragment foodEditorFragment = new FoodEditorFragment();

        Bundle args = new Bundle();
        args.putBoolean(isNewMealArg, false);
        args.putLong(mealIdArg, Long.parseLong(mealInfo[0][position]));
        foodEditorFragment.setArguments(args);

        onMealEditorFragment.openMealEditorFragment(foodEditorFragment, R.string.toolbar_meal_edit);
    }

    public interface OnMealEditorFragment {
        void openMealEditorFragment(Fragment fragment, int newToolbarTitle);
    }
}
