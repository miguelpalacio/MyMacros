package com.miguelpalacio.mymacros;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;

    private ListView drawerList;
    private String[] drawerLabels;
    private TypedArray drawerIcons;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private Window window;

    private float r1, g1, b1;
    private float r2, g2, b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Navigation Drawer.
        drawerLabels = getResources().getStringArray(R.array.drawer_labels);
        drawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
        drawerList = (ListView) findViewById(R.id.drawer_list);

        // Set the adapter for the drawer's list view.
        drawerList.setAdapter(new DrawerAdapter (this, drawerLabels, drawerIcons));

        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Preparation for setting status bar's color.
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Extract status bar color components to calculate color according to
        // the navigation drawer's position.
        r1 = (float) Color.red(getResources().getColor(R.color.primaryDark));
        g1 = (float) Color.green(getResources().getColor(R.color.primaryDark));
        b1 = (float) Color.blue(getResources().getColor(R.color.primaryDark));
        r2 = (float) Color.red(getResources().getColor(R.color.primaryDarkDrawer));
        g2 = (float) Color.green(getResources().getColor(R.color.primaryDarkDrawer));
        b2 = (float) Color.blue(getResources().getColor(R.color.primaryDarkDrawer));

        // Drawer toggle.
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Darken the Status Bar's color.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.primaryDarkDrawer));
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Enlighten the Status Bar's color.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Darken/Enlighten Status Bar's color according to the drawer's position.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    float r, g, b;

                    r = r1 *(1-slideOffset) + r2 *slideOffset;
                    g = g1 *(1-slideOffset) + g2 *slideOffset;
                    b = b1 *(1-slideOffset) + b2 *slideOffset;

                    window.setStatusBarColor(Color.rgb((int) r, (int) g, (int) b));
                }
            }
        };

        // Set drawer toggle and sync state.
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {}

}
