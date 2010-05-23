package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.commonsware.cwac.endless.EndlessAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class RidesActivity extends ListActivity implements OnSeekBarChangeListener {

    private ArrayList<Ride> rides;
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
        
        // some mock rides
        rides = new ArrayList<Ride>();
        Place o = new Place();
        o.type = Place.TYPE_ADDRESS;
        o.name = "Droidcamp - Dahlem Cube";
        o.address = "Takustraße 39, Berlin";
        Place d = new Place();
        d.type = Place.TYPE_ADDRESS;
        d.name = "C-Base Raumstation";
        d.address = "Rungestr 22, Berlin";
        
        // bvg
        Ride r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+4*60000);
        r.arr = new Date(System.currentTimeMillis()+(4+37)*60000);
        r.plugin = R.drawable.bvg;
        r.price = 240;
        r.fun = 1;
        r.eco = 3;
        r.fast = 2;
        r.social = 2;
        r.green = 4;
        rides.add(r);
        
        // bvg
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+12*60000);
        r.arr = new Date(System.currentTimeMillis()+(12+39)*60000);
        r.plugin = R.drawable.bvg;
        r.price = 240;
        r.fun = 3;
        r.eco = 3;
        r.fast = 1;
        r.social = 2;
        r.green = 4;
        rides.add(r);

        // bvg
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+22*60000);
        r.arr = new Date(System.currentTimeMillis()+(22+37)*60000);
        r.plugin = R.drawable.bvg;
        r.price = 240;
        r.fun = 2;
        r.eco = 3;
        r.fast = 2;
        r.social = 2;
        r.green = 4;
        rides.add(r);
        
        // auto
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+2*60000);
        r.arr = new Date(System.currentTimeMillis()+(2+22)*60000);
        r.plugin = R.drawable.car;
        r.fun = 1;
        r.eco = 1;
        r.fast = 5;
        r.social = 1;
        r.green = 1;
        rides.add(r);

        // taxi
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+7*60000);
        r.arr = new Date(System.currentTimeMillis()+(7+22)*60000);
        r.plugin = R.drawable.taxi;
        r.price = 2300;
        r.fun = 1;
        r.eco = 1;
        r.fast = 4;
        r.social = 1;
        r.green = 1;
        rides.add(r);

        
        // mfg
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+7*60000);
        r.arr = new Date(System.currentTimeMillis()+(7+22)*60000);
        r.plugin = R.drawable.mfg;
        r.price = 150;
        r.fun = 3;
        r.eco = 5;
        r.fast = 3;
        r.social = 4;
        r.green = 3;
        rides.add(r);

        // taxi teiler
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+7*60000);
        r.arr = new Date(System.currentTimeMillis()+(7+22)*60000);
        r.plugin = R.drawable.taxi_teiler;
        r.price = 320;
        r.fun = 3;
        r.eco = 4;
        r.fast = 3;
        r.social = 5;
        r.green = 3;
        rides.add(r);
        
        
        setListAdapter(new RidesAdapter(new BaseAdapter() {
            
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = getLayoutInflater().inflate(R.layout.rideview, parent, false);
                
                ((RideView)view).setRide(rides.get(position));
                return view;
            }
            
            @Override
            public long getItemId(int position) {
                if (position < rides.size())
                    return rides.get(position).plugin;
                else 
                    return 0;
            }
            
            @Override
            public Object getItem(int position) {
                return rides.get(position);
            }
            
            @Override
            public int getCount() {
                return rides.size();
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
        sort();
    }
    
    private class RidesAdapter extends EndlessAdapter {

        private RotateAnimation rotate;
        private ArrayList<Ride> nextRides;

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
            Place o = new Place();
            o.type = Place.TYPE_ADDRESS;
            o.name = "Droidcamp - Dahlem Cube";
            o.address = "Takustraße 39, Berlin";
            Place d = new Place();
            d.type = Place.TYPE_ADDRESS;
            d.name = "C-Base Raumstation";
            d.address = "Rungestr 22, Berlin";
            nextRides = new BahnDePlugIn().find(o, d, new Date());
            if (!nextRides.isEmpty())
                return true;
            else
                return false;
        }

        @Override
        protected void appendCachedData() {
            rides.addAll(nextRides);
            sort();
        }
        
        @Override
        protected void rebindPendingView(int position, View view) {
            if (position < rides.size()) {
                ((RideView)view).setRide(rides.get(position));
                View child = view.findViewById(R.id.throbber);
                child.setVisibility(View.GONE);
                child.clearAnimation();
            }
        }
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("aha", "changed "+progress);
        sort();
        priorities.edit().putInt((String)seekBar.getTag(), progress).commit();
    }

    private void sort() {
        Collections.sort(rides, new Comparator<Ride>() {

            @Override
            public int compare(Ride r1, Ride r2) {
                int score1= r1.fun * fun.getProgress() +
                            r1.eco * eco.getProgress() +
                            r1.fast * fast.getProgress() +
                            r1.green * green.getProgress() +
                            r1.social * social.getProgress();
                int score2= r2.fun * fun.getProgress() +
                            r2.eco * eco.getProgress() +
                            r2.fast * fast.getProgress() +
                            r2.green * green.getProgress() +
                            r2.social * social.getProgress();
                Log.d("aha", "score2: "+score1 + ",  score2: "+score2);
                if (score1 < score2)
                    return 1;
                else if (score1 > score2)
                    return -1;
                else {
                    if (r1.dep.after(r2.dep))
                        return 1;
                    if (r1.dep.before(r2.dep))
                        return -1;
                    return 0;
                }
            }
        });
        getListView().invalidateViews();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    
    
}
