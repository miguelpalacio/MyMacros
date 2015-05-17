package com.miguelpalacio.mymacros;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
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

    //DatabaseAdapter databaseAdapter;

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
            // Set to 0 to init in openFragment() when activity starts.
            currentFragment = 0;
        }

        // Create or upgrade database.
        //databaseAdapter = new DatabaseAdapter(this);

        // If database doesn't exist, onCreate in helper is called.
        //SQLiteDatabase sqLiteDatabase = databaseAdapter.getWritableDatabase();

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

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Disable the Hamburguer-BackToArrow animation on Toolbar.
                super.onDrawerSlide(drawerView, 0);
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
        // TODO: Find something useful for this menu, remove otherwise.
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: see previous TODO.
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
        // Don't set highlighting for Settings and FAQ.
        if (position < 6) {
            drawerAdapter.clearSelection();
            drawerAdapter.toggleSelection(position);
        }

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
     * <p>
     *      If item selected corresponds to the fragment currently shown, don't reopen it.
     *      Also sets the title on the Toolbar for the corresponding fragment.
     * </p>
     * @param position item clicked by the user on the navigation drawer.
     */
    private void openFragment(int position) {

        // Select initial fragment upon Activity creation.
        if (position == 0) {
/*            // If it's first time opening the app, open ProfileFragment.
            if (PreferenceManager.getDefaultSharedPreferences(this). != null) {*/
            position = 1;
/*            } else {
                position = 3;
            }*/
        }

        Fragment fragment;

        switch (position) {

            case 1:
                getSupportActionBar().setTitle(R.string.toolbar_planner);
                fragment = new PlannerFragment();
                break;

            case 2:
                getSupportActionBar().setTitle(R.string.toolbar_stats);
                fragment = new StatsFragment();
                break;

            case 3:
                getSupportActionBar().setTitle(R.string.toolbar_profile);
                fragment = new ProfileFragment();
                break;

            case 4:
                getSupportActionBar().setTitle(R.string.toolbar_meals);
                fragment = new MealsFragment();
                break;

            case 5:
                getSupportActionBar().setTitle(R.string.toolbar_foods);
                fragment = new FoodsFragment();
                break;

            case 7:
                startActivity(new Intent(this, SettingsActivity.class));
                return;

            case 8:
                return;

            default:
                fragment = new Fragment();
        }

        // If item selected is the current selected, no need to replace fragment.
        if (currentFragment == position) {
            // Only title was set.
            return;
        }

        // Remember the fragment opened. Neither Settings nor FAQ are taken remembered.
        currentFragment = position;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
