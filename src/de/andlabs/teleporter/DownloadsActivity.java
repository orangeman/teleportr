package de.andlabs.teleporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DownloadsActivity extends PreferenceActivity implements OnPreferenceClickListener {

    private static final String TAG = "Settings";
    private ProgressDialog progress;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.downloads_activity_titel));
        getPreferenceManager().setSharedPreferencesName("autocompletion");
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        
        // clean up (autocompletion downloads deleted on sdcard)
        final Map<String, ?> vals = getPreferenceManager().getSharedPreferences().getAll();
        getPreferenceManager().getSharedPreferences().edit().clear().commit();
        
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/teleporter");
        if (!dir.exists()) dir.mkdir();
        for (String file : dir.list()) {
            CheckBoxPreference c = new CheckBoxPreference(this);
            getPreferenceScreen().addItemFromInflater(c);
            c.setKey(file);
            c.setTitle(file.split("_")[0]);
            c.setSummary(file.substring(file.indexOf("_")+1, file.lastIndexOf(".")));
            if (vals.containsKey(file)) {
                c.setChecked((Boolean)vals.get(file));
            }
        }
        if (getPreferenceScreen().getPreferenceCount() == 0)
            new FetchNearbyDownloads().execute("");
    }


    @Override
    public boolean onPreferenceClick(final Preference preference) {
        new AlertDialog.Builder(DownloadsActivity.this)
            .setTitle(preference.getTitle())
            .setMessage(preference.getSummary())
            .setPositiveButton(getString(R.string.dialog_yes), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Download().execute((preference.getKey()));
                }
            })
            .setNegativeButton(getString(R.string.dialog_no), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((CheckBoxPreference)preference).setChecked(false);
                    getPreferenceScreen().getSharedPreferences().edit().remove(preference.getKey()).commit();
                }
            })
        .show();
    return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new FetchNearbyDownloads().execute("");
        return super.onOptionsItemSelected(item);
    }




    private class Download extends AsyncTask<String, String, Boolean> {
        
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(DownloadsActivity.this);
            progress.setMessage(getString(R.string.downloading_prog_dnld));
            progress.show();
            super.onPreExecute();
        }
        
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                new DefaultHttpClient().execute(
                    new HttpGet("http://may.base45.de:3000/downloads/"+params[0])).getEntity()
                    .writeTo(
                    new FileOutputStream(new File("/sdcard/teleporter/"+params[0])));
                progress.setProgress(50);
//                progress.setMessage(getString(R.string.downloading_prog_indx));
                SQLiteDatabase newDB = SQLiteDatabase.openDatabase("/sdcard/teleporter/"+params[0], null, SQLiteDatabase.OPEN_READWRITE);
                newDB.execSQL("CREATE INDEX nameIdx ON places (name);");
                newDB.close();
                getPreferenceScreen().getSharedPreferences().edit().putBoolean(params[0], true).commit();
            } catch (Exception e) {
                Log.e(TAG, "error while downloading "+params[0]);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progress.dismiss();
            if (!success) 
                Toast.makeText(DownloadsActivity.this, getString(R.string.download_error), Toast.LENGTH_LONG).show();
        }
    }


    private class FetchNearbyDownloads extends AsyncTask<String, String, JSONArray> {
        
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(DownloadsActivity.this);
            progress.setMessage(getString(R.string.downloads_fetch_progress));
            progress.show();
            super.onPreExecute();
        }
        
        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                Location loc = ((LocationManager)getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc == null)
                    loc = ((LocationManager)getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc == null) { // fallback berlin 
                    loc = new Location(LocationManager.GPS_PROVIDER);
                    loc.setLatitude(52.5);
                    loc.setLongitude(13.4);
                }
                URL url = new URL("http://may.base45.de:3000/downloads.json?lat="+loc.getLatitude()+"&lon="+loc.getLongitude());
                return new JSONArray(new BufferedReader(new InputStreamReader(url.openStream())).readLine());
            } catch (Exception e) {
                Log.e(TAG, "problem location or internet");
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(JSONArray json) {
            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject j = json.getJSONObject(i).getJSONObject("download");
                    if (!getPreferenceScreen().getSharedPreferences().contains(j.getString("file"))) {
                        CheckBoxPreference c = new CheckBoxPreference(DownloadsActivity.this);
                        c.setKey(j.getString("file"));
                        String title = j.getString("title");
                        c.setTitle(title.split(" ")[0]);
                        c.setSummary(title.substring(title.indexOf(" ")));
                        c.setOnPreferenceClickListener(DownloadsActivity.this);
                        getPreferenceScreen().addItemFromInflater(c);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(DownloadsActivity.this, getString(R.string.download_error), Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
            super.onPostExecute(json);
        }
    }

}
