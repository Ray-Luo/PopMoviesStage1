package com.raystone.ray.popmoviesstage1;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ray on 2/1/2016.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    public static SettingFragment newInstance() {
         Bundle args = new Bundle();
         SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        bindPreferenceValueToSummary(findPreference("sort"));
    }

    private void bindPreferenceValueToSummary(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference)preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0)
                preference.setSummary(listPreference.getEntries()[prefIndex]);
        }
        return true;
    }

}
