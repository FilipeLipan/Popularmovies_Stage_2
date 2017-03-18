package com.github.filipelipan.popularmovies.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.broadcastreceiver.NetworkConnectReceiver;
import com.github.filipelipan.popularmovies.sync.PopularMoviesSyncUtils;
import com.github.filipelipan.popularmovies.ui.fragments.DetailFragment;
import com.github.filipelipan.popularmovies.ui.fragments.GridMoviesFragment;
import com.github.filipelipan.popularmovies.util.OperationType;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    public static final String GRID_MOVIE_FRAGMENT = "grid_movie_fragment";
    public static final String DETAIL_FRAGMENT = "detail_fragment";

    private GridMoviesFragment mGridMoviesFragment;
    private DetailFragment mDetailFragment;
    private NetworkConnectReceiver mReceiver = new NetworkConnectReceiver();

    @BindView(R.id.main_activity_snackbar_place_holder)
    RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        CustomActivityOnCrash.install(this);

        // is tablet receive true if the smaller screen is equal to 600dp
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        //initialize the jobDispatcher and starts syncing
        PopularMoviesSyncUtils.initialize(this);


        /*
        * if it is a table the main activity will create two fragments and display both
        * if it is a phone this activity will show one fragment
        */
        if (isTablet) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main_activity);
            setSupportActionBar(toolbar);

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                    .findFragmentByTag(GRID_MOVIE_FRAGMENT);
            if (mGridMoviesFragment == null) {
                mGridMoviesFragment = new GridMoviesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_movies_fragment_tablet, mGridMoviesFragment, GRID_MOVIE_FRAGMENT);
                fragmentTransaction.commit();
            }

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
            if (mDetailFragment == null) {
                mDetailFragment = new DetailFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_detail_fragment_tablet, mDetailFragment, DETAIL_FRAGMENT);
                fragmentTransaction.commit();
            }

        } else {

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                    .findFragmentByTag(GRID_MOVIE_FRAGMENT);
            if (mGridMoviesFragment == null) {
                mGridMoviesFragment = new GridMoviesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_main_activity, mGridMoviesFragment, GRID_MOVIE_FRAGMENT);
                fragmentTransaction.commit();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        *  register a receiver to warn the user about the connection
        */
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);

        IntentFilter customFilter = new IntentFilter(NetworkConnectReceiver.NOTIFY_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, customFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        *  unregister inside onPause, preventing the receiver to live outside our app
        */
        unregisterReceiver(mReceiver);
    }


    /*
    *  declare a local receiver to warn the user about the connection status
     */
    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(NetworkConnectReceiver.EXTRA_IS_CONNECTED, false);
            if (isConnected) {
                Snackbar.make(mRelativeLayout, "Network is connected", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mRelativeLayout, "Network is disconnected", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (getResources().getBoolean(R.bool.is_tablet)) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                .findFragmentByTag(GRID_MOVIE_FRAGMENT);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.it_most_popular:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.MOST_POPULAR);
                }
                return true;
            case R.id.it_top_rated:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.TOP_RATED);
                }
                return true;
            case R.id.it_favorites:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.FAVORITE);
                }
                return true;
        }
        return false;
    }
}
