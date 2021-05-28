package org.smartregister.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by keyman on 26/06/2018.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private static final String ARG_PAGE = "page";
    private final Fragment mBaseFragment;
    private final Fragment[] fragments;

    public PagerAdapter(FragmentManager fragmentManager, Fragment baseFragment, Fragment[] fragments) {
        super(fragmentManager);
        this.mBaseFragment = baseFragment;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;
            default:
                fragment = fragments[position - 1];
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        if (fragment != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments == null ? 1 : fragments.length + 1;
    }
}
