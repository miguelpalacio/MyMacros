package com.miguelpalacio.mymacros;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
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
import android.view.ViewParent;
import android.widget.LinearLayout;


public class MainActivity extends ActionBarActivity {

    Toolbar toolbar;

    String[] drawerLabels;
    TypedArray drawerIcons;

    RecyclerView drawerView;
    RecyclerView.Adapter drawerAdapter;
    RecyclerView.LayoutManager drawerLayoutManager;
    DrawerLayout drawer;

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

        // Set initial fragment.
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            PlannerFragment plannerFragment = new PlannerFragment();

            plannerFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, plannerFragment).commit();
        }

        // Navigation Drawer.
        drawerLabels = getResources().getStringArray(R.array.drawer_labels);
        drawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);

        drawerView = (RecyclerView) findViewById(R.id.drawer_recycler);
        drawerView.setHasFixedSize(true);

        // Set the adapter for the Drawer's recycler view.
        drawerAdapter = new DrawerAdapter(drawerLabels, drawerIcons,
                "Miguel Palacio", "miguelpalacio@outlook.com");
        drawerView.setAdapter(drawerAdapter);

        // Set the layout manager for the Drawer's recycler view.
        drawerLayoutManager = new LinearLayoutManager(this);
        drawerView.setLayoutManager(drawerLayoutManager);

        // Define and set the item's OnClick listener.
        drawerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {

                        // Set a runnable that runs once the drawer closes after clicking on item.
                        mPendingRunnable = new Runnable() {
                            @Override
                            public void run() {
                                openFragment(position);
                                view.setBackgroundColor(getResources().getColor(R.color.selected_item));
                            }
                        };

                        // TODO: improve the implementation of item selected.
                        // Restore default background for items.
                        for (int i = 1; i < drawerLayoutManager.getChildCount(); i++) {
                            View childView = drawerLayoutManager.getChildAt(i);
                            childView.setBackgroundResource(R.drawable.custom_bg);
                        }

                        // Update selected item and title, then close the drawer.
                        drawer.closeDrawers();
                    }
                })
        );

        // Set the Navigation Drawer's layout.
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Since Status Bar is transparent in styles.xml, set its color.
        drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        // Drawer toggle.
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
/*                super.onDrawerClosed(drawerView);*/

                // If mPendingRunnable is not null, then add to the message queue.
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }
        };

        // Set drawer toggle and sync state.
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Runnable support.
        // TODO: read about this runnable interface and Handler class.
        mHandler.post(mPendingRunnable);
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

    /**
     * Open the respective fragment according to the item selected.
     * If item selected corresponds to the fragment currently shown, don't replace it.
     * @param position item clicked by the user on the navigation drawer.
     */
    private void openFragment(int position) {

/*                                Toast.makeText(MainActivity.this, "Fragment " + position + " not replaced.", Toast.LENGTH_SHORT).show();*/

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
