package com.oldering.kintone.higashi.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.oldering.kintone.higashi.ui.activity.NotificationFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class NotificationPagerAdapter extends FragmentStatePagerAdapter {
    public static final int TO_ME = 0;
    public static final int ALL = 1;
    public static final int FLAGGED = 2;

    public NotificationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return NotificationFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case TO_ME:
                return "自分宛";
            case ALL:
                return "すべて";
            case FLAGGED:
                return "あとで読む";
        }
        return null;
    }
}