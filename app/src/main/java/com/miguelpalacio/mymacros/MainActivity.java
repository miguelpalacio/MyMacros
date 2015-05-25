package com.miguelpalacio.mymacros;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends ActionBarActivity implements
        DrawerAdapter.ViewHolder.ClickListener,
        FoodsFragment.OnFoodsInnerFragment {

    Toolbar toolbar;

    String[] drawerLabels;
    TypedArray drawerIcons;
    TypedArray drawerIconsSelected;

    RecyclerView drawerView;
    RecyclerView.LayoutManager drawerLayoutManager;
    DrawerLayout drawerLayout;
    DrawerAdapter drawerAdapter;

    ScrimInsetsFrameLayout scrimInsetsFrameLayout;

    ActionBarDrawerToggle mDrawerToggle;

    Runnable onDrawerClosedRunnable;
    Handler mHandler = new Handler();

    int currentFragment;
    boolean inInnerFragment;
    boolean drawerIconAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default values for preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialize local variables.
        drawerIconAnimation = false;
        inInnerFragment = false;

        // Load savedInstanceState data.
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt("currentFragment");
            inInnerFragment = savedInstanceState.getBoolean("inInnerFragment");
            drawerIconAnimation = savedInstanceState.getBoolean("drawerIconAnimation");
        } else {
            // Set to 0 to init in openFragment() when activity starts.
            currentFragment = 0;
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

        // Set the layout that envelops the Navigation Drawer to be used later.
        scrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

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
                if (drawerIconAnimation) {
                    super.onDrawerSlide(drawerView, slideOffset);
                } else {
                    // Disable the Hamburger-BackToArrow animation on Toolbar.
                    super.onDrawerSlide(drawerView, 0);
                }
            }
        };

        // Set drawer toggle and sync state.
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Enable back button in Toolbar (Navigation drawer must be deactivated for this).
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    backToPreviousFragment();
                } else if (drawerLayout.isDrawerOpen(scrimInsetsFrameLayout)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(scrimInsetsFrameLayout);
                    mDrawerToggle.syncState();
                }
            }
        });

        // Open fragment.
        openFragment(currentFragment);

        // Highlight corresponding entry on Navigation Drawer.
        drawerAdapter.toggleSelection(currentFragment);

        // If application is in inner fragment, enable the Home Button.
        if (inInnerFragment) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    /**
     * Override Hardware Back Button functionality.
     */
    @Override
    public void onBackPressed() {
        // When in a "sub-fragment", turn back to previous fragment.
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            backToPreviousFragment();
        } else {
            super.onBackPressed();
        }
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

    /**
     * Save local variables in case of restart of activity (due to re-orientation,
     * because it went into stopped state, etc).
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragment", currentFragment);
        outState.putBoolean("inInnerFragment", inInnerFragment);
        outState.putBoolean("drawerIconAnimation", drawerIconAnimation);
    }

    // Open Foods' inner fragments.
    @Override
    public void openFoodsInnerFragment(Fragment fragment, int newToolbarTitle) {
        openInnerFragment(fragment, newToolbarTitle);
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

    /**
     * Opens (inner) fragments called from other fragments.
     */
    public void openInnerFragment(Fragment fragment, int newToolbarTitle) {

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        inInnerFragment = true;

        // Enable back-arrow in toolbar.
        getSupportActionBar().setTitle(newToolbarTitle);
/*      // Apparently, this is not needed since onDrawerSlide below take care of this.
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        // Lock Navigation Drawer.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // Animate the drawer icon (Hamburger to BackArrow animation).
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                // The actual animation is performed by onDrawerSlide.
                mDrawerToggle.onDrawerSlide(drawerLayout, slideOffset);

/*                // Enable home button (necessary if application gets stopped).
                if (slideOffset == 1.0) {
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }*/
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(400);

        // Enable the drawer icon animation.
        drawerIconAnimation = true;
        animator.start();
    }

    /**
     * When in a "sub-fragment" (i.e., a fragment called inside another fragment), returns
     * to the caller fragment. Re-enables the navigation drawer if necessary.
     */
    public void backToPreviousFragment() {

        getFragmentManager().popBackStack();
        int NewBackStackEntryCount = getFragmentManager().getBackStackEntryCount() - 1;

        // When BackStack empty, re-enable the navigation drawer.
        if (NewBackStackEntryCount == 0) {
/*          // Apparently, because of the animation this is not needed either.
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);*/
            mDrawerToggle.syncState();
            openFragment(currentFragment);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            inInnerFragment = false;
        }

        // Animate the drawer icon (BackArrow to Hamburger animation).
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                // The actual animation is performed by onDrawerSlide.
                mDrawerToggle.onDrawerSlide(drawerLayout, slideOffset);

                // Disable the drawer icon animation.
                if (slideOffset == 0.0) {
                    drawerIconAnimation = false;
                }
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(400);
        animator.start();
    }
}
