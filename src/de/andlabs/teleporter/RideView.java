package de.andlabs.teleporter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RideView extends RelativeLayout {

    private TextView dep;
    private TextView arr;
    private ImageView plugin;
    private TextView duration;
    private TextView price;
    private TextView minutes;
    private TextView hours;
    private TextView hours_label;



    public RideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dep = (TextView) findViewById(R.id.dep);
        arr = (TextView) findViewById(R.id.arr);
        price = (TextView) findViewById(R.id.price);
        hours = (TextView) findViewById(R.id.hours);
        plugin = (ImageView) findViewById(R.id.plugIn);
        minutes = (TextView) findViewById(R.id.minutes);
        duration = (TextView) findViewById(R.id.duration);
        hours_label = (TextView) findViewById(R.id.hours_label);
    }



    public void setRide(Ride ride) {
        final long waitingTime;
        final long travelTime; 
        if(ride.dep == null || ride.arr == null) {
            waitingTime = 0;
            travelTime = ride.duration / 60000;
            dep.setText(new SimpleDateFormat("hh:mm").format(new Date()));
            arr.setText(new SimpleDateFormat("hh:mm").format(new Date(System.currentTimeMillis()+ride.duration)));
        } else {
            waitingTime = (int)(ride.dep.getTime()-System.currentTimeMillis())/60000;
            travelTime = (int) (ride.arr.getTime() - ride.dep.getTime()) / 60000;
            dep.setText(new SimpleDateFormat("hh:mm").format(ride.dep));
            arr.setText(new SimpleDateFormat("hh:mm").format(ride.arr));
        }
//        if (waitingTime < 100) {
        if (waitingTime < 60) {
            minutes.setText(String.valueOf(waitingTime));
            minutes.setTextSize(46);
            hours.setVisibility(GONE);
            hours_label.setVisibility(GONE);
        } else if (waitingTime < 10*60) {
            hours.setText(String.valueOf((int)waitingTime/60));
            hours.setVisibility(VISIBLE);
            hours_label.setVisibility(VISIBLE);
            minutes.setText(String.valueOf(waitingTime%60));
            minutes.setTextSize(20);
        } else if (waitingTime < 100*60) {
            hours.setText(String.valueOf((int)waitingTime/60));
            hours.setVisibility(VISIBLE);
            hours_label.setVisibility(VISIBLE);
        }
        if (travelTime < 60)
            duration.setText(travelTime+" min");
        else if (travelTime < 24*60)
            duration.setText(((int)travelTime/60)+"h "+travelTime%60+"min");
        else
            duration.setText("toooooo long!!!");
        
//        plugin.setImageResource(ride.plugin);
        
        price.setText(String.valueOf(((int)ride.price/100)));
        int cents = ride.price%100;
        if (cents != 0) price.append(","+cents);
        
        switch (ride.mode) {
            case Ride.MODE_TELEPORTER:
                setBackgroundResource(R.drawable.mode_teleporter);
                break;
            case Ride.MODE_SKATEBOARD:
                setBackgroundResource(R.drawable.mode_sk8);
                break;
            case Ride.MODE_TRANSIT:
                setBackgroundResource(R.drawable.mode_transit);
                break;
            case Ride.MODE_FLIGHT:
                setBackgroundResource(R.drawable.mode_flight);
                break;
            case Ride.MODE_TRAIN:
                setBackgroundResource(R.drawable.mode_train);
                break;
            case Ride.MODE_WALK:
                setBackgroundResource(R.drawable.mode_walk);
                break;
            case Ride.MODE_BIKE:
                setBackgroundResource(R.drawable.mode_bike);
                break;
            case Ride.MODE_TAXI:
                setBackgroundResource(R.drawable.mode_taxi);
                break;
            case Ride.MODE_DRIVE:
                setBackgroundResource(R.drawable.mode_drive);
                break;
            case Ride.MODE_MFG:
                setBackgroundResource(R.drawable.mode_mfg);
                break;
        }
    }

}
