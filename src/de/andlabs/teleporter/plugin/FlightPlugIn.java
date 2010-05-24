package de.andlabs.teleporter.plugin;

import java.util.ArrayList;
import java.util.Date;

import de.andlabs.teleporter.Place;
import de.andlabs.teleporter.Ride;

public class FlightPlugIn implements ITeleporterPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {

        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        Ride r = new Ride();
      
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis()+3*60000);
        r.arr = new Date(System.currentTimeMillis()+(3+3)*60000);
        r.mode = Ride.MODE_FLIGHT;
        
        r.fun = 2;
        r.eco = 1;
        r.fast = 5;
        r.social = 2;
        r.green = 1;
        rides.add(r);
        
        return rides;
    }

}
