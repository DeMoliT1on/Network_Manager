package com.dhruv.networkmanager.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.fragments.AppUsageFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_1, R.string.tab_2, R.string.tab_3};
    private final Context mContext;
    private Fragment dayFragment;
    private Fragment monthFragment;
    private Fragment yearFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (dayFragment == null)
                    dayFragment = AppUsageFragment.newInstance(position);
                return dayFragment;
            case 1:
                if (monthFragment == null)
                    monthFragment = AppUsageFragment.newInstance(position);
                return monthFragment;
            case 2:
                if (yearFragment == null)
                    yearFragment = AppUsageFragment.newInstance(position);
                return yearFragment;
            default:return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}