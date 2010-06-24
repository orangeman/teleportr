package de.andlabs.teleporter;

import java.net.URLDecoder;
import java.util.Map.Entry;

import de.andlabs.teleporter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Main extends ListActivity implements OnSeekBarChangeListener {
    
    private static final String TAG = "Teleporter";
    private BroadcastReceiver mTimeTickReceiver;
    private QueryMultiplexer multiplexer;
    private ProgressBar progress;
    private SeekBar fun;
    private SeekBar eco;
    private SeekBar fast;
    private SeekBar green;
    private SeekBar social;
    private SharedPreferences priorities;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rides);
        
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        
        if (getSharedPreferences("autocompletion", MODE_PRIVATE).getAll().isEmpty()) {
            getSharedPreferences("plugIns", MODE_WORLD_WRITEABLE).edit().putBoolean("BahnDePlugIn", true).commit();
            startActivity(new Intent(this, DownloadsActivity.class));
        }
        
        mTimeTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context pContext, final Intent pIntent) {
                getListView().invalidateViews();
            }
        };
        
        multiplexer = new QueryMultiplexer(this, null, null);
        progress = new ProgressBar(this);
        setListAdapter(new BaseAdapter() {
            
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = getLayoutInflater().inflate(R.layout.rideview, parent, false);
                if (position < multiplexer.rides.size()) {
                    ((RideView)view).setRide((Ride) multiplexer.rides.get(position));
                    return view;
                } else {
                    multiplexer.searchLater();
                    return progress;
                }
            }
            
            @Override
            public long getItemId(int position) {
                if (position < multiplexer.rides.size())
                    return multiplexer.rides.get(position).hashCode();
                else 
                    return 2342;
            }
            
            @Override
            public Object getItem(int position) {
                return multiplexer.rides.get(position);
            }
            
            @Override
            public int getCount() {
                if (multiplexer.rides.isEmpty())
                    return 0;
                else
                    return multiplexer.rides.size()+1;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < multiplexer.rides.size())
                    return 0;
                else
                    return 1;
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }
        });
        
        priorities = getSharedPreferences("priorities", MODE_PRIVATE);
        fun = ((SeekBar)findViewById(R.id.fun));
        eco = ((SeekBar)findViewById(R.id.eco));
        fast = ((SeekBar)findViewById(R.id.fast));
        green = ((SeekBar)findViewById(R.id.green));
        social = ((SeekBar)findViewById(R.id.social));
        fun.setOnSeekBarChangeListener(this);
        eco.setOnSeekBarChangeListener(this);
        fast.setOnSeekBarChangeListener(this);
        green.setOnSeekBarChangeListener(this);
        social.setOnSeekBarChangeListener(this);
        
        fun.setProgress(priorities.getInt("fun", 0));
        eco.setProgress(priorities.getInt("eco", 0));
        fast.setProgress(priorities.getInt("fast", 0));
        green.setProgress(priorities.getInt("green", 0));
        social.setProgress(priorities.getInt("social", 0));
        multiplexer.sort();
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("aha", "changed "+progress);
        priorities.edit().putInt((String)seekBar.getTag(), progress).commit();
        multiplexer.sort();
        getListView().invalidateViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "newIntent: "+intent.toString());
        
        String destination = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            destination = "\n\n\n"+intent.getStringExtra(SearchManager.QUERY);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String query = URLDecoder.decode(getIntent().getDataString().substring(10));
            String[] q = query.split(",");
            if (Character.isDigit(query.charAt(0))) {
                destination = q[0].substring(q[0].indexOf(" ")+1) +" "+ q[0].substring(0, q[0].indexOf(" "))+", "+q[1].split(" ")[1];
            } else {
                destination = q[0]+", "+ q[1].split(" ")[2];
            }
        }
        if (destination != null) {
            Place o = new Place();
            o.type = Place.TYPE_ADDRESS;
            o.name = "Droidcamp";
            o.address = "Nobelstraße 10, Stuttgart";
            Place d = new Place();
            d.type = Place.TYPE_ADDRESS;
            d.address = destination;
            multiplexer = new QueryMultiplexer(this, o, d);
            multiplexer.searchLater();
            setTitle("-->"+destination);
            MediaPlayer.create(this, R.raw.sound_long).start();
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mTimeTickReceiver);
    }

    @Override
    protected void onListItemClick(final ListView pListView, final View pView, final int pPosition, final long pID) {
        Ride ride = (Ride) getListAdapter().getItem(pPosition);
        startActivity(ride.intent);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.search:
            onSearchRequested();
            break;

        case R.id.settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
        case R.id.feedback:
            LogCollector.feedback(this, "flo@andlabs.de", "bla bla");
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void datasetChanged() {
        getListView().invalidateViews();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        
    }

}