package de.andlabs.teleporter;

import java.text.SimpleDateFormat;

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
        int waitingTime = (int)(ride.dep.getTime()-System.currentTimeMillis())/60000;
        if (waitingTime < 100) {
            minutes.setText(String.valueOf(waitingTime));
            minutes.setTextSize(46);
            hours.setVisibility(GONE);
            hours_label.setVisibility(GONE);
        } else if (waitingTime < 36000000) {
            hours.setText(String.valueOf((int)waitingTime/60));
            hours.setVisibility(VISIBLE);
            hours_label.setVisibility(VISIBLE);
            minutes.setText(String.valueOf(waitingTime%60));
            minutes.setTextSize(23);
        }
//        int waitingTime = (int)(ride.arr.getTime()-ride.dep.getTime())/60000;
        
        dep.setText(new SimpleDateFormat("hh:mm").format(ride.dep));
        arr.setText(new SimpleDateFormat("hh:mm").format(ride.arr));
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
            case Ride.MODE_TRAIN:
                setBackgroundResource(R.drawable.mode_train);
                break;
            case Ride.MODE_TAXI:
                setBackgroundResource(R.drawable.mode_taxi);
                break;
            case Ride.MODE_CAR:
                setBackgroundResource(R.drawable.mode_car);
                break;
            case Ride.MODE_MFG:
                setBackgroundResource(R.drawable.mode_mfg);
                break;
        }
    }

}
