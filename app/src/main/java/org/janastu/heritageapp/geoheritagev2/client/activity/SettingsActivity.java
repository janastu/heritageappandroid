package org.janastu.heritageapp.geoheritagev2.client.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.SimpleMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsService;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.MapAppsServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.rest.HeritageApp;
import org.janastu.heritageapp.geoheritagev2.client.rest.RestGroupComunication;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final String TAG = "SettingsActivity";
    static  RestGroupComunication restGroupComunication ;
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

    public static String getCurrentApp()
    {
        if(currentapp == null)
        {
            MapAppsServiceImpl mapService = new MapAppsServiceImpl();
            mapService.setContext(context);
            try {
                HeritageAppDTO[]  app = mapService.getAllApps();
                currentapp = app[0].getName();
            } catch (IOException e) {
                currentapp="PondyMap";
                e.printStackTrace();
            }

            catch ( Exception e) {
                currentapp="PondyMap";
                e.printStackTrace();
            }
        }


        return currentapp;
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
                        Log.d(TAG, "cuurent" + currentapp);
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

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id==android.R.id.home)
        {
            onBackPressed();
            //finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        restGroupComunication = new RestGroupComunication(getApplicationContext());
        restGroupComunication.init();
        context = getApplicationContext();

        settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);

        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)

                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                ;
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {


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





        /*    final ListPreference lp2 = (ListPreference) findPreference("sync_groups");


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
            });*/
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
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
