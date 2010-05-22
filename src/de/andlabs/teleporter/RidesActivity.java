package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
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
        o.name = "Dahlem Cube";
        o.address = "hjhjhjhj";
        Place d = new Place();
        o.name = "C-Base";
        o.address = "kjkjkkkj";
        
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
        
        setListAdapter(new BaseAdapter() {
            
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = getLayoutInflater().inflate(R.layout.rideview, parent, false);
                
                ((RideView)view).setRide(rides.get(position));
                return view;
            }
            
            @Override
            public long getItemId(int position) {
                return rides.get(position).plugin;
            }
            
            @Override
            public Object getItem(int position) {
                return rides.get(position);
            }
            
            @Override
            public int getCount() {
                return rides.size();
            }
        });
        
        
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
