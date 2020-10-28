package com.atoz.johnnysapp.store.ui.order_details;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.view.View;

import com.atoz.johnnysapp.store.data.DatabaseHandler;

public class FragmentEx extends Fragment {
    public Context mContext;
    public View view;
    public DatabaseHandler DB;
    public FragmentListener mMainActivity;

    public FragmentEx() {
        super();
        mContext= mContext==null ? getContext() : mContext;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext= getContext();
        super.onAttach(activity);
    }
}
