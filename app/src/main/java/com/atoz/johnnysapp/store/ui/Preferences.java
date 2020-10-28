package com.atoz.johnnysapp.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.utils.Pref;

public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = Preferences.class.getSimpleName();
    private ListPreference pVIEW_TYPE, pAPP_SORT;
    private Pref prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new SelfExceptionHandler(this));
        addPreferencesFromResource(R.xml.preferences);
        prefs= Pref.init(this);
    }

    public void onResume() {
    	super.onResume();

        refreshSummary();
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {

        } catch (Exception e) {
            Log.e(TAG, "onPreferenceChange: " + e.toString(), e);
        }
		return true;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshSummary();
    }

    private void refreshSummary() {
        try {

        } catch (Exception e) {
            Log.e(TAG, "refreshSummary: " + e.toString(), e);
        }
    }
}