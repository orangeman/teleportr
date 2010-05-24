package de.andlabs.teleporter.plugin;

import java.util.ArrayList;
import java.util.Date;

import de.andlabs.teleporter.Place;
import de.andlabs.teleporter.R;
import de.andlabs.teleporter.Ride;
import de.andlabs.teleporter.R.drawable;

public class SkateboardPlugIn implements ITeleporterPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {

        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        Ride r = new Ride();
      
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+3*60000);
        r.arr = new Date(System.currentTimeMillis()+(3+123)*60000);
        r.plugin = R.drawable.car;
        r.mode = Ride.MODE_SKATEBOARD;
        
        r.fun = 5;
        r.eco = 5;
        r.fast = 1;
        r.social = 3;
        r.green = 5;
        rides.add(r);
        
        return rides;
    }

}
