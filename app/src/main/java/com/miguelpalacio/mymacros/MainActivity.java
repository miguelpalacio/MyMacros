package com.miguelpalacio.mymacros;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
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
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements
        DrawerAdapter.ViewHolder.ClickListener,
        FoodsFragment.OnFoodEditorFragment, FoodEditorFragment.OnFoodSaved,
        MealsFragment.OnMealEditorFragment, MealEditorFragment.OnMealSaved,
        MealEditorFragment.OnMealAddFood, MealAddFoodFragment.OnFoodQuantitySet,
        PlannerFragment.OnPlannerAddMeal, AddMealFragment.OnMealSet {

    private static final String CURRENT_FRAGMENT = "currentFragment";
    private static final String IN_INNER_FRAGMENT = "inInnerFragment";
    private static final String DRAWER_ICON_ANIMATION = "drawerIconAnimation";
    private static final String TOOLBAR_TITLES = "toolbarTitles";

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

    AlarmManager alarmManager;
    PendingIntent alarmIntent;

    int currentFragment;
    boolean inInnerFragment;
    boolean drawerIconAnimation;
    ArrayList<Integer> toolbarTitles;

    // Private variables to be exposed to some fragment children.

    private boolean foodAddedToMeal;
    private long mealFoodId;
    private double mealFoodQuantity;

    private boolean mealAddedToPlanner;
    private long plannerMealId;

    private String barcodeScanFormat;
    private String barcodeScanResult;
    private boolean productScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default values for preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialize local variables.
        drawerIconAnimation = false;
        inInnerFragment = false;

        // Initialize private variables that are accessed by child fragments.
        foodAddedToMeal = false;
        mealFoodId = -1;
        mealFoodQuantity = 0;

        // Load savedInstanceState data.
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT);
            inInnerFragment = savedInstanceState.getBoolean(IN_INNER_FRAGMENT);
            drawerIconAnimation = savedInstanceState.getBoolean(DRAWER_ICON_ANIMATION);
            toolbarTitles = savedInstanceState.getIntegerArrayList(TOOLBAR_TITLES);
        } else {
            // Set to 0 to init in openFragment() when activity starts.
            currentFragment = 0;
            // Init the "stack" with the toolbar titles.
            toolbarTitles = new ArrayList<>();
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

        // Open fragment and set title.
        openFragment(currentFragment);

        // Highlight corresponding entry on Navigation Drawer.
        drawerAdapter.toggleSelection(currentFragment);

        // If application is in inner fragment, enable the Home Button.
        if (inInnerFragment) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set an alarm to save user stats every 24 hours in the database.

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);

/*        // Alarm goes off every minute... just a try-out. Start from AlarmReceiver to do what is needed!
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000, alarmIntent);*/

        // Sets AlarmReceiver to go off every day at about 12:00 am.
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
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
     * Save local variables in case of restart of activity (due to re-orientation,
     * because it went into stopped state, etc).
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, currentFragment);
        outState.putBoolean(IN_INNER_FRAGMENT, inInnerFragment);
        outState.putBoolean(DRAWER_ICON_ANIMATION, drawerIconAnimation);
        outState.putIntegerArrayList(TOOLBAR_TITLES, toolbarTitles);
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

    // Daily Planner Callbacks.

    // Open the Add Meal fragment.
    @Override
    public void openAddMealFragment(Fragment fragment, int newToolbarTitle) {
        openInnerFragment(fragment, newToolbarTitle);
    }

    @Override
    public void setMealOnPlanner(long mealId) {
        backToPreviousFragment();

        mealAddedToPlanner = true;
        plannerMealId = mealId;
    }

    // MyMeals Callbacks.

    // Open the Meal Editor fragment.
    @Override
    public void openMealEditorFragment(Fragment fragment, int newToolbarTitle) {
        openInnerFragment(fragment, newToolbarTitle);
    }

    // Open the Meal Add Food fragment.
    @Override
    public void openMealAddFoodFragment(Fragment fragment, int newToolbarTitle) {
        openInnerFragment(fragment, newToolbarTitle);
    }

    // After saving (create/edit) meal, close inner fragment a go back to the Meals page.
    @Override
    public void onMealSavedSuccessfully() {
        backToPreviousFragment();
    }

    // Send information of food selected in MealAddFoodFragment to MealEditorFragment.
    @Override
    public void setFoodOnMealEditor(long foodId, double foodQuantity) {
        backToPreviousFragment();

        foodAddedToMeal = true;
        mealFoodId = foodId;
        mealFoodQuantity = foodQuantity;
    }

    // Foods Callbacks.

    // Open the Food Editor fragment.
    @Override
    public void openFoodEditorFragment(Fragment fragment, int newToolbarTitle) {
        openInnerFragment(fragment, newToolbarTitle);
    }

    // After saving (create/edit) food, close inner fragment a go back to the Foods page.
    @Override
    public void onFoodSavedSuccessfully() {
        backToPreviousFragment();
    }

    // Check for the result of barcode scan.
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            barcodeScanFormat = scanningResult.getFormatName();
            barcodeScanResult = scanningResult.getContents();

            if (barcodeScanFormat != null && barcodeScanResult != null) {
                productScanned = true;
            }
        } else {
            Toast.makeText(this, "No scan data received", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Opens the respective fragment according to the item selected.
     * <p>
     *      If item selected corresponds to the fragment currently shown, don't reopen it.
     *      When the application is in an inner fragment, retrieves the corresponding title.
     *      Also sets the title on the Toolbar for the corresponding fragment.
     * </p>
     * @param position item clicked by the user on the navigation drawer.
     */
    private void openFragment(int position) {

        // Select initial fragment upon Activity creation.
        if (position == 0) {
            position = 1;
        }

        Fragment fragment;
        int toolbarTitle;

        switch (position) {

            case 1:
                toolbarTitle = R.string.toolbar_planner;
                fragment = new PlannerFragment();
                break;

            case 2:
                toolbarTitle = R.string.toolbar_stats;
                fragment = new StatsFragment();
                break;

            case 3:
                toolbarTitle = R.string.toolbar_profile;
                fragment = new ProfileFragment();
                break;

            case 4:
                toolbarTitle = R.string.toolbar_meals;
                fragment = new MealsFragment();
                break;

            case 5:
                toolbarTitle = R.string.toolbar_foods;
                fragment = new FoodsFragment();
                break;

            case 7:
                startActivity(new Intent(this, SettingsActivity.class));
                return;

            case 8:
                return;

            default:
                fragment = new Fragment();
                toolbarTitle = 0;
        }

        // Set toolbar title: If the app is in inner fragment, set the respective title.
        if (toolbarTitles.size() > 1) {
            getSupportActionBar().setTitle(toolbarTitles.get(0));
        } else if (toolbarTitles.size() == 1) {
            toolbarTitles.set(0, toolbarTitle);
            getSupportActionBar().setTitle(toolbarTitle);
        } else {
            toolbarTitles.add(toolbarTitle);
            getSupportActionBar().setTitle(toolbarTitle);
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

        // Set title.
        toolbarTitles.add(0, newToolbarTitle);
        getSupportActionBar().setTitle(newToolbarTitle);

/*      // Apparently, this is not needed since onDrawerSlide below take care of this.
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        // Lock Navigation Drawer.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (getFragmentManager().getBackStackEntryCount() == 0) {

            // Animate the drawer icon (Hamburger to BackArrow animation).
            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    // The actual animation is performed by onDrawerSlide.
                    mDrawerToggle.onDrawerSlide(drawerLayout, slideOffset);
                }
            });
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);

            // Enable the drawer icon animation.
            drawerIconAnimation = true;
            animator.start();
        }

        // Close Soft Keyboard if it's open.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
    }

    /**
     * When in a "sub-fragment" (i.e., a fragment called inside another fragment), returns
     * to the caller fragment. Re-enables the navigation drawer if necessary.
     */
    public void backToPreviousFragment() {

        getFragmentManager().popBackStack();
        int NewBackStackEntryCount = getFragmentManager().getBackStackEntryCount() - 1;

        // Set the toolbar title.
        toolbarTitles.remove(0);
        getSupportActionBar().setTitle(toolbarTitles.get(0));

        if (NewBackStackEntryCount == 0) {

            // Re-enable the navigation drawer.
            mDrawerToggle.syncState();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            inInnerFragment = false;

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

        // Close Soft Keyboard if it's open.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
    }


    // Getters and Setters.

    public boolean wasFoodAddedToMeal() {
        return foodAddedToMeal;
    }
    public void setFoodAddedToMeal(boolean foodAddedToMeal) {
        this.foodAddedToMeal = foodAddedToMeal;
    }

    public double getMealFoodQuantity() {
        return mealFoodQuantity;
    }

    public long getMealFoodId() {
        return mealFoodId;
    }

    public boolean wasMealAddedToPlanner() {
        return mealAddedToPlanner;
    }

    public void setMealAddedToPlanner(boolean mealAddedToPlanner) {
        this.mealAddedToPlanner = mealAddedToPlanner;
    }

    public long getPlannerMealId() {
        return plannerMealId;
    }

    public String getBarcodeScanFormat() {
        return barcodeScanFormat;
    }

    public String getBarcodeScanResult() {
        return barcodeScanResult;
    }

    public boolean wasProductScanned() {
        return productScanned;
    }

    public void setProductScanned(boolean productScanned) {
        this.productScanned = productScanned;
    }
}

