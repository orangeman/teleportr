package de.andlabs.teleporter.plugin;

import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Date;

import de.andlabs.teleporter.Place;
import de.andlabs.teleporter.Ride;

public class BikePlugIn implements ITeleporterPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {

        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        Ride r = new Ride();
        
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+3*60000);
        r.arr = new Date(System.currentTimeMillis()+(3+47)*60000);
        r.mode = Ride.MODE_BIKE;
        
        r.fun = 4;
        r.eco = 5;
        r.fast = 2;
        r.social = 2;
        r.green = 5;
        rides.add(r);
        
        return rides;
    }

}
