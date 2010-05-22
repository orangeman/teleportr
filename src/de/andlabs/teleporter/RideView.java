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
    private TextView waitingTime;



    public RideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dep = (TextView) findViewById(R.id.dep);
        arr = (TextView) findViewById(R.id.arr);
        price = (TextView) findViewById(R.id.price);
        plugin = (ImageView) findViewById(R.id.plugIn);
        duration = (TextView) findViewById(R.id.duration);
        waitingTime = (TextView) findViewById(R.id.waitingTime);
    }



    public void setRide(Ride ride) {
        waitingTime.setText(String.valueOf((int)(ride.dep.getTime()-System.currentTimeMillis())/60000));
        duration.setText("--- "+((int)(ride.arr.getTime()-ride.dep.getTime())/60000)+" min -->");
        dep.setText(new SimpleDateFormat("hh:mm").format(ride.dep));
        arr.setText(new SimpleDateFormat("hh:mm").format(ride.arr));
        plugin.setImageResource(ride.plugin);
        
        price.setText(String.valueOf(((int)ride.price/100)));
        int cents = ride.price%100;
        if (cents != 0)
            price.append(","+cents+"€");
        else
            price.append("€");
    }

}
