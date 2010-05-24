package de.andlabs.teleporter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "Settings";
    private SharedPreferences prefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        getPreferenceManager().setSharedPreferencesName("plugIns");

        ((PreferenceScreen)findPreference("autocompletion"))
        .setIntent(new Intent(this, DownloadsActivity.class));
        
    }


   

}
