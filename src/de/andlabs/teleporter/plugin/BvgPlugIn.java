package de.andlabs.teleporter.plugin;

import java.util.ArrayList;
import java.util.Date;

import de.andlabs.teleporter.Place;
import de.andlabs.teleporter.R;
import de.andlabs.teleporter.Ride;
import de.andlabs.teleporter.R.drawable;

public class BvgPlugIn implements ITeleporterPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {
        
        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        // bvg
        Ride r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+4*60000);
        r.arr = new Date(System.currentTimeMillis()+(4+37)*60000);
        r.mode = Ride.MODE_TRANSIT;
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
        r.arr = new Date(System.currentTimeMillis()+(12+233)*60000);
        r.mode = Ride.MODE_TRANSIT;
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
        r.dep = new Date(System.currentTimeMillis()+155*60000);
        r.arr = new Date(System.currentTimeMillis()+(155+37)*60000);
        r.mode = Ride.MODE_TRANSIT;
        r.price = 240;
        r.fun = 2;
        r.eco = 3;
        r.fast = 2;
        r.social = 2;
        r.green = 4;
        rides.add(r);
        
        return rides;
    }

}
