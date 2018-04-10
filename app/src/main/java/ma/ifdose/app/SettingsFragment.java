package ma.ifdose.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(ma.ifdose.app.R.xml.preferences, rootKey);
        if (getString(ma.ifdose.app.R.string.app_mode).equals("dev"))
            getPreferenceScreen().findPreference("pref_debug").setEnabled(true);

    }

}
