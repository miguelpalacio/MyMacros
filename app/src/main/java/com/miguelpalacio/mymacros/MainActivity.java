package com.miguelpalacio.mymacros;

import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements DrawerAdapter.ViewHolder.ClickListener {

    Toolbar toolbar;

    String[] drawerLabels;
    TypedArray drawerIcons;
    TypedArray drawerBackgrounds;

    RecyclerView drawerView;
    RecyclerView.LayoutManager drawerLayoutManager;
    DrawerLayout drawerLayout;
    DrawerAdapter drawerAdapter;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    ActionBarDrawerToggle mDrawerToggle;

    // TODO: correct this.
    Runnable mPendingRunnable;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer.
        drawerLabels = getResources().getStringArray(R.array.drawer_labels);
        drawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
        drawerBackgrounds = getResources().obtainTypedArray(R.array.drawer_backgrounds);

        drawerView = (RecyclerView) findViewById(R.id.drawer_recycler);
        drawerView.setHasFixedSize(true);

        // Set the adapter for the Drawer's recycler view.
        drawerAdapter = new DrawerAdapter(drawerLabels, drawerIcons, drawerBackgrounds,
                "Miguel Palacio", "miguelpalacio@outlook.com", this);
        drawerView.setAdapter(drawerAdapter);

        // Set the layout manager for the Drawer's recycler view.
        drawerLayoutManager = new LinearLayoutManager(this);
        drawerView.setLayoutManager(drawerLayoutManager);

        // Define and set the item's OnClick listener.
/*        drawerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {

                        // Set a runnable that runs once the drawer closes after clicking on item.
                        mPendingRunnable = new Runnable() {
                            @Override
                            public void run() {
                                openFragment(position);
*//*                                view.setBackgroundColor(getResources().getColor(R.color.selected_item));*//*

                            }
                        };

                        // TODO: improve the implementation of item selected.
                        // Restore default background for previously selected item.
*//*                        for (int i = 1; i < drawerLayoutManager.getChildCount(); i++) {
                            View childView = drawerLayoutManager.getChildAt(i);
                            childView.setBackgroundResource(R.drawable.custom_bg);
                        }*//*
*//*                        previousSelectedChild.setBackgroundResource(R.drawable.custom_bg);*//*

                        // Update selected item and title, then close the drawer.
                        drawerLayout.closeDrawers();
                    }
                })
        );*/

        // Set the Navigation Drawer's layout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Since Status Bar is transparent in styles.xml, set its color.
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        // Drawer toggle.
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
/*                super.onDrawerClosed(drawerView);*/

                // TODO: CHANGE TOOLBAR TITLE HERE

                // If mPendingRunnable is not null, then add to the message queue.
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }
        };

        // Set drawer toggle and sync state.
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Runnable support.
        // TODO: read about this runnable interface and Handler class.
        mHandler.post(mPendingRunnable);

        // Set initial fragment.
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

/*            // Highlight item in Drawer corresponding to initial fragment.
            previousSelectedChild = drawerLayoutManager.getChildAt(1);
            previousSelectedChild.setBackgroundColor(getResources().getColor(R.color.selected_item));*/

            PlannerFragment plannerFragment = new PlannerFragment();

            plannerFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, plannerFragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
/*        if (actionMode != null) {*/
        drawerAdapter.clearSelection();
        drawerAdapter.toggleSelection(position);
/*            toggleSelection(position);*/
        Log.d("onItemClicked", "It's being clicked at " + position);
/*        }
        else {
            actionMode = startSupportActionMode(actionModeCallback);
        */
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        drawerAdapter.toggleSelection(position);
        int count = drawerAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            drawerAdapter.clearSelection();
            actionMode = null;
        }
    }

    /**
     * Open the respective fragment according to the item selected.
     * If item selected corresponds to the fragment currently shown, don't replace it.
     * @param position item clicked by the user on the navigation drawer.
     */
    private void openFragment(int position) {

        switch (position) {

            case 1:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof PlannerFragment)) {

                    PlannerFragment plannerFragment = new PlannerFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, plannerFragment)
                            .commit();
                }
                break;

            case 2:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof StatsFragment)) {

                    StatsFragment statsFragment = new StatsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, statsFragment)
                            .commit();
                }
                break;

            case 3:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof ProfileFragment)) {

                    ProfileFragment profileFragment = new ProfileFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, profileFragment)
                            .commit();
                }
                break;

            case 4:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof MealsFragment)) {

                    MealsFragment mealsFragment = new MealsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mealsFragment)
                            .commit();
                }
                break;

            case 5:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof FoodsFragment)) {

                    FoodsFragment foodsFragment = new FoodsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, foodsFragment)
                            .commit();
                }
                break;
        }
    }
}
