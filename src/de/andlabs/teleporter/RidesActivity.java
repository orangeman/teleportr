package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.commonsware.cwac.endless.EndlessAdapter;

public class RidesActivity extends ListActivity implements OnSeekBarChangeListener {

    private ArrayList<Ride> rides;
    private SeekBar fun;
    private SeekBar eco;
    private SeekBar fast;
    private SeekBar green;
    private SeekBar social;
    private SharedPreferences priorities;
    private QueryMultiplexer multiplexer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        setContentView(R.layout.rides);
        
        // some mock rides
        rides = new ArrayList<Ride>();
        Place o = new Place();
        o.type = Place.TYPE_ADDRESS;
        o.name = "Droidcamp - Dahlem Cube";
        o.address = "Takustraße 39, Berlin";
        o.lat = 52457577;
        o.lon = 13292519;
        Place d = new Place();
        d.type = Place.TYPE_ADDRESS;
        d.name = "C-Base Raumstation";
        d.address = "Rungestr 20, Berlin";
        d.lat = 52512288;
        d.lon = 13419910;
        
        multiplexer = new QueryMultiplexer(this, o, d);
        
  
        
        ((ImageButton)findViewById(R.id.close)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((SlidingDrawer)findViewById(R.id.priorities)).animateClose();
            }
        });
        
        setListAdapter(new RidesAdapter(new BaseAdapter() {
            
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = getLayoutInflater().inflate(R.layout.rideview, parent, false);
                
                ((RideView)view).setRide((Ride) multiplexer.rides.get(position));
                view.findViewById(R.id.throbber).setVisibility(View.GONE);
                view.findViewById(R.id.throbber).clearAnimation();;
                return view;
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
                return multiplexer.rides.size();
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
            
            
        }));
        
        
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
        
        priorities = getSharedPreferences("priorities", MODE_PRIVATE);
        fun.setProgress(priorities.getInt("fun", 0));
        eco.setProgress(priorities.getInt("eco", 0));
        fast.setProgress(priorities.getInt("fast", 0));
        green.setProgress(priorities.getInt("green", 0));
        social.setProgress(priorities.getInt("social", 0));
        multiplexer.sort();
    }
    
    private class RidesAdapter extends EndlessAdapter {

        private RotateAnimation rotate;

        public RidesAdapter(ListAdapter wrapped) {
            super(wrapped);
            rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setRepeatMode(Animation.RESTART);
            rotate.setRepeatCount(Animation.INFINITE);
        }
        
        @Override
        protected View getPendingView(ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.rideview, parent, false);
            View child = v.findViewById(R.id.throbber);
            child.setVisibility(View.VISIBLE);
            child.startAnimation(rotate);
            return v;
        }

        @Override
        protected boolean cacheInBackground() {
            return multiplexer.searchLater();
        }

        @Override
        protected void appendCachedData() {
            multiplexer.sort();
            this.notifyDataSetChanged();
        }
        
        @Override
        protected void rebindPendingView(int position, View view) {
            if (!multiplexer.rides.isEmpty() && position < multiplexer.rides.size()) {
                ((RideView)view).setRide((Ride) multiplexer.rides.get(position));
                View child = view.findViewById(R.id.throbber);
                child.setVisibility(View.GONE);
                child.clearAnimation();
            }
        }
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("aha", "changed "+progress);
        priorities.edit().putInt((String)seekBar.getTag(), progress).commit();
        multiplexer.sort();
        getListView().invalidateViews();
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
            ((SlidingDrawer)findViewById(R.id.priorities)).animateOpen();
            break;
        case R.id.feedback:
            LogCollector.feedback(this, "flo@andlabs.de", "bla bla");
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    
    
}
