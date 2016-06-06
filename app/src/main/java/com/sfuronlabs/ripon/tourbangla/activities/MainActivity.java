package com.sfuronlabs.ripon.tourbangla.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.parse.ParseUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sfuronlabs.ripon.tourbangla.R;
import com.sfuronlabs.ripon.tourbangla.fragment.VisitedPlacesFragment;
import com.sfuronlabs.ripon.tourbangla.fragment.AboutAppFragment;
import com.sfuronlabs.ripon.tourbangla.fragment.FeedbackFragment;
import com.sfuronlabs.ripon.tourbangla.fragment.FragmentDrawer;
import com.sfuronlabs.ripon.tourbangla.fragment.HomeFragment;
import com.sfuronlabs.ripon.tourbangla.fragment.MessagesFragment;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        adView = (AdView) findViewById(R.id.adView);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);


        AdRequest adRequest = new AdRequest.Builder().addTestDevice("18D9D4FB40DF048C506091E42E0FDAFD").build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.logout)
        {
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        String subtitle = "";
        switch (position) {
            case 0:

                fragment = new HomeFragment();
                subtitle = getString(R.string.title_home);
                break;
            case 1:
                fragment = new MessagesFragment();
                subtitle = getString(R.string.title_messages);
                break;
            case 2:
                fragment = new VisitedPlacesFragment();
                subtitle = getString(R.string.title_visitedplaces);
                break;
            case 3:
                fragment = new AboutAppFragment();
                subtitle = getString(R.string.aboutapp);
                break;
            case 4:
                fragment = new FeedbackFragment();
                subtitle = getString(R.string.givefeedback);
                break;
            default:
                break;
        }

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }


}