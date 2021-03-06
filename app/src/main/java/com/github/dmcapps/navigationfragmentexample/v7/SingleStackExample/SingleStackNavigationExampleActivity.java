package com.github.dmcapps.navigationfragmentexample.v7.SingleStackExample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.dmcapps.navigationfragment.v7.NavigationManagerFragment;
import com.github.dmcapps.navigationfragment.v7.StackNavigationManagerFragment;
import com.github.dmcapps.navigationfragmentexample.R;
import com.github.dmcapps.navigationfragmentexample.v7.NavigationFragments.SampleFragment;

import java.util.UUID;

public class SingleStackNavigationExampleActivity extends AppCompatActivity {

    private static final String STATE_NAV_TAG = "NAV_TAG";

    private String mSingleStackNavigationManagerFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSingleStackNavigationManagerFragmentTag = savedInstanceState.getString(STATE_NAV_TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSingleStackNavigationManagerFragmentTag == null) {
            StackNavigationManagerFragment navManager = StackNavigationManagerFragment.newInstance(SampleFragment.newInstance("Root Fragment in the Stack", 0));

            navManager.setDefaultPresentAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            navManager.setDefaultDismissAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

            mSingleStackNavigationManagerFragmentTag = UUID.randomUUID().toString();

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.content, navManager, mSingleStackNavigationManagerFragmentTag);
            ft.commit();
        }
        else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(mSingleStackNavigationManagerFragmentTag);

            if (fragment != null && fragment.isDetached()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.attach(fragment);
                ft.commit();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(mSingleStackNavigationManagerFragmentTag);
        ft.detach(fragment);
        ft.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_NAV_TAG, mSingleStackNavigationManagerFragmentTag);
    }

    @Override
    public void onBackPressed() {
        NavigationManagerFragment fragment = (NavigationManagerFragment)getSupportFragmentManager().findFragmentByTag(mSingleStackNavigationManagerFragmentTag);
        if (!fragment.getNavigationManager().onBackPressed()) {
            super.onBackPressed();
        }
    }
}
