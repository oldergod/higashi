package com.oldering.kintone.higashi.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.oldering.kintone.higashi.R;
import com.oldering.kintone.higashi.api.slash.AuthApi;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.ui.adapter.NotificationPagerAdapter;
import com.oldering.kintone.higashi.util.AccountStore;

import rx.SingleSubscriber;

public class NotificationPagerActivity extends BaseActivity {
    private static final String TAG = "NotificationPagerAct";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private NotificationPagerAdapter notificationPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    @Override
    void inflateViews() {
        setContentView(R.layout.activity_notification_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    void fillViews() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        notificationPagerAdapter = new NotificationPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(notificationPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
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
            try {
                AuthApi.attemptLogin(AccountStore.loadAccount(), new SingleSubscriber<Account>() {
                    @Override
                    public void onSuccess(Account value) {
                        Toast.makeText(NotificationPagerActivity.this, "onSuccess: yo", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NotificationPagerActivity.this, "onError: yo", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (KeyStoreMacInvalidException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
