package com.miguelpalacio.mymacros;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * @author Miguel Palacio
 * <p>
 *     This Fragment extends from PreferenceFragment since it uses the SharedPreferences
 *     object in order to store user's data.
 * </p>
 */
public class ProfileFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.fragment_profile);
    }
}
