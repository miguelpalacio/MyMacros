package com.miguelpalacio.mymacros;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements DrawerAdapter.ViewHolder.ClickListener {

    Toolbar toolbar;

    String[] drawerLabels;
    TypedArray drawerIcons;
    TypedArray drawerIconsSelected;

    RecyclerView drawerView;
    RecyclerView.LayoutManager drawerLayoutManager;
    DrawerLayout drawerLayout;
    DrawerAdapter drawerAdapter;

    ActionBarDrawerToggle mDrawerToggle;

    Runnable onDrawerClosedRunnable;
    Handler mHandler = new Handler();

    int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default values for preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Load savedInstanceState data.
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt("currentFragment");
        } else {
            // Default fragment when activity starts.
            currentFragment = 1;
        }

        // Toolbar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer.
        drawerLabels = getResources().getStringArray(R.array.drawer_labels);
        drawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
        drawerIconsSelected = getResources().obtainTypedArray(R.array.drawer_icons_selected);

        drawerView = (RecyclerView) findViewById(R.id.drawer_recycler);
        drawerView.setHasFixedSize(true);

        // Set the adapter for the Drawer's recycler view.
        drawerAdapter = new DrawerAdapter(drawerLabels, drawerIcons, drawerIconsSelected,
                "Miguel Palacio", "miguelpalacio@outlook.com", this);
        drawerView.setAdapter(drawerAdapter);

        // Set the layout manager for the Drawer's recycler view.
        drawerLayoutManager = new LinearLayoutManager(this);
        drawerView.setLayoutManager(drawerLayoutManager);

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
                super.onDrawerClosed(drawerView);

                // If onDrawerClosedRunnable is not null, then add it to the message queue.
                if (onDrawerClosedRunnable != null) {
                    mHandler.post(onDrawerClosedRunnable);
                    onDrawerClosedRunnable = null;
                }
            }
        };

        // Set drawer toggle and sync state.
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Open fragment.
        openFragment(currentFragment);

        // Highlight corresponding entry on Navigation Drawer.
        drawerAdapter.toggleSelection(currentFragment);
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

        // Open the Settings activity.
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        // Open the FAQ activity.
        else if (id == R.id.action_faq) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemClick(final int position) {
        // Highlight selected item.
        drawerAdapter.clearSelection();
        drawerAdapter.toggleSelection(position);

        // Set a runnable that runs once the drawer closes after clicking on item.
        onDrawerClosedRunnable = new Runnable() {
            @Override
            public void run() {
                openFragment(position);
            }
        };

        // Update selected item and title, then close the drawer.
        drawerLayout.closeDrawers();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current fragment before phone's re-orientation.
        outState.putInt("currentFragment", currentFragment);
    }

    /**
     * Opens the respective fragment according to the item selected.
     * If item selected corresponds to the fragment currently shown, don't re-open (replace) it.
     * Also sets the title on the Toolbar for the corresponding fragment.
     * @param position item clicked by the user on the navigation drawer.
     */
    private void openFragment(int position) {

        // Remember which fragment was opened.
        currentFragment = position;

        switch (position) {

            case 1:
                if (!(getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container) instanceof PlannerFragment)) {

                    getSupportActionBar().setTitle(R.string.toolbar_planner);

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

                    getSupportActionBar().setTitle(R.string.toolbar_stats);

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

                    getSupportActionBar().setTitle(R.string.toolbar_profile);

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

                    getSupportActionBar().setTitle(R.string.toolbar_meals);

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

                    getSupportActionBar().setTitle(R.string.toolbar_foods);

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
