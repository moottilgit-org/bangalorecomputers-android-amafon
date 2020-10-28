package com.atoz.johnnysapp.store.ui.order_details;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atoz.johnnysapp.store.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public ItemsFragment mItems;
    public MessagesFragment mMessages;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext= context;
    }

    @Override
    public FragmentEx getItem(int position) {
        switch (position) {
            case 0:
                if(mItems==null) {
                    mItems= ItemsFragment.create();
                }
            return mItems;
            case 1:
                if(mMessages==null) {
                    mMessages= MessagesFragment.create();
                }
            return mMessages;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getResources().getString(R.string.items);
            case 1: return mContext.getResources().getString(R.string.messages);
        }
        return null;
    }
}