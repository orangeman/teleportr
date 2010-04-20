package de.andlabs.teleporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import de.andlabs.teleporter.R;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class PlaceProvider extends ContentProvider {
	
    private static final int PLACES = 1;
    private static final UriMatcher sUriMatcher;
	private static final String TAG = "LocLOGic";
	private static DatabaseHelper mDBHelper;
    private static final String DATABASE_NAME = "myPlaces.db";

	private String mCity;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}



	
	@Override
	public boolean onCreate() {
	    Context ctx = getContext();
	    Log.d(TAG, "onCreate");
		mDBHelper = new DatabaseHelper(getContext());
		
		mCity = PreferenceManager.getDefaultSharedPreferences(ctx).getString("homeBase", null);
		// quick&dirty check if muc or bln is closer
		if (mCity == null) {
		    Location loc = ((LocationManager)ctx.getSystemService(ctx.LOCATION_SERVICE))
		                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    if (loc == null) return false;
		    Location bln = new Location("foo");
		    bln.setLatitude(52.5);
		    bln.setLongitude(13.4);
		    Location muc = new Location("foo");
		    muc.setLatitude(48.1);
		    muc.setLongitude(11.6);
		    
		    if (loc.distanceTo(bln) < loc.distanceTo(muc))
		        mCity = "Berlin";
		    else
		        mCity = "Muenchen";
		    Log.d(TAG, "homeBase: "+mCity);
		    PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("homeBase", mCity);
		}
		
		// TODO this should be properly downloaded based on current location..
		final String path = ctx.getFilesDir().getPath()+"/"+mCity+".db";
        if (!new File(path).exists()) {
            try {
                InputStream in = ctx.getAssets().open(mCity+".db");
                FileOutputStream out = ctx.openFileOutput(mCity+".db", ctx.MODE_PRIVATE);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.d(TAG, "done copying");
            } catch (IOException e) {
                Log.d(TAG, "error while copying");
                e.printStackTrace();
            }
        }
		mDBHelper.getWritableDatabase().execSQL("ATTACH DATABASE '"+path+"' AS city;");
		return true;
	}
	
	
	@Override
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case PLACES:
        	return Place.CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		
		switch (sUriMatcher.match(uri)) {
		case PLACES:
			cursor = mDBHelper.getReadableDatabase().rawQuery(
					"SELECT _id, " +
					    "name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", " +
					    "'"+mCity+"' AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+", " +
					    "type AS "+SearchManager.SUGGEST_COLUMN_ICON_1+", " +
					    "'"+R.drawable.icon+"' AS "+SearchManager.SUGGEST_COLUMN_ICON_2+", " +
					    "name ||', "+mCity+"' AS "+SearchManager.SUGGEST_COLUMN_QUERY+", "+
					    "'"+SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT+"' AS "+SearchManager.SUGGEST_COLUMN_SHORTCUT_ID+" "+
					"FROM city.places " +
					(uri.getPathSegments().size() == 2 ? "WHERE name LIKE '%"+uri.getLastPathSegment()+"%' " : "") +
					"LIMIT 42", null);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return cursor;
		
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
	        String[] selectionArgs) {
	    // TODO Auto-generated method stub
	    return 0;
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI("de.andlabs.teleporter", SearchManager.SUGGEST_URI_PATH_QUERY, PLACES);
		sUriMatcher.addURI("de.andlabs.teleporter", SearchManager.SUGGEST_URI_PATH_QUERY+"/*", PLACES);
		
	}
}
