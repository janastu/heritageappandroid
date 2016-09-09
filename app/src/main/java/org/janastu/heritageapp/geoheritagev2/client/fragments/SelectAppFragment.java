package org.janastu.heritageapp.geoheritagev2.client.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.springframework.web.client.HttpServerErrorException;

import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link SelectAppFragment.OnSelectAppFragmentInteractionListener} interface
 * to handle interaction events.

 */


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public   class SelectAppFragment extends PreferenceFragment {
    private static final String TAG = "APP";
    static HeritageAppDTO[] heritageAppDTO;
    static String currentapp;

    static SharedPreferences.Editor editor;
    static SharedPreferences settings;
    static private Context context;

    public static HeritageAppDTO getCurrentAppInfo()
    {

        HeritageAppDTO  a = null;
        for(HeritageAppDTO h :heritageAppDTO)
        {
            if(h.getName().compareTo(currentapp) == 0)
            {
                a = h;
            }
        }

        return a;

    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if ((preference instanceof ListPreference) && (preference.getKey().equals("sync_apps"))) {

                    try {
                        currentapp = listPreference.getEntries()[index].toString();
                        Log.d("A", "cuurent" + currentapp);
                    }catch(Exception e )
                    {}
                }
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);
        setHasOptionsMenu(true);
        //check if internet exists -
        //else get from the local file ;

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        //   bindPreferenceSummaryToValue(findPreference("sync_apps"));
        //   bindPreferenceSummaryToValue(findPreference("sync_groups"));

        try {


            MapAppsServiceImpl mapAppsService = new MapAppsServiceImpl();
            mapAppsService.setContext(getActivity().getApplicationContext());
            heritageAppDTO =  mapAppsService.getAllApps();


            CharSequence[] entries = new CharSequence[heritageAppDTO.length];
            CharSequence[] entryValues =   new CharSequence[heritageAppDTO.length];

            int i = 0 ;
            for(HeritageAppDTO app : heritageAppDTO)
            {

                entries[i] = app.getName();
                Integer eValue = i + 1;
                entryValues[i] = eValue.toString();
                i++;
            }

            final ListPreference lp = (ListPreference) findPreference("sync_apps");


            lp.setEntries(entries);
            lp.setDefaultValue("1");
            lp.setEntryValues(entryValues);

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML


            lp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                    Log.d("onPreferenceClick", preference.getKey().toString());
                    bindPreferenceSummaryToValue(findPreference("sync_apps"));
                    if ((preference instanceof ListPreference) && (preference.getKey().equals("sync_apps"))) {
                        ListPreference lp = (ListPreference) preference;
                        return true;
                    }
                    return false;

                }
            });

        }catch(HttpServerErrorException e)
        {  Log.d(TAG, "HttpServerErrorException" + e);}
        catch(Exception e                             )
        {
            Log.d(TAG, "exception" + e);
        }





        final ListPreference lp2 = (ListPreference) findPreference("sync_groups");


        CharSequence[] entries2 = { "PondyMap", "KaveriMap" };
        CharSequence[] entryValues2 = {"1" , "2"};
        lp2.setEntries(entries2);
        lp2.setDefaultValue("1");
        lp2.setEntryValues(entryValues2);

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML


        lp2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Log.d("onPreferenceClick", preference.getKey().toString());
                bindPreferenceSummaryToValue(findPreference("sync_groups"));
                if ((preference instanceof ListPreference) && (preference.getKey().equals("sync_groups"))) {
                    ListPreference lp = (ListPreference) preference;
                    return true;
                }
                return false;

            }
        });
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
         //   startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnSelectAppFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSelectAppFragmentInteraction(Uri uri);
    }
}