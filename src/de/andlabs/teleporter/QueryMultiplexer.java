package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.SharedPreferences;
import android.util.Log;

public class QueryMultiplexer {

    private Place orig;
    private Place dest;
    public ArrayList<Ride> rides;
    public ArrayList<ITeleporterPlugIn> plugIns;

    public QueryMultiplexer(Place o, Place d) {
        orig = o;
        dest = d;
        
        rides = new ArrayList<Ride>();
        plugIns = new ArrayList<ITeleporterPlugIn>();
        plugIns.add(new BahnDePlugIn());
        plugIns.add(new BvgPlugIn());
        
        // bvg
        Ride r = new Ride();
      
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
    }
    
    public boolean searchNext() {
        // TODO just query just the plugins that ...
        rides.addAll(plugIns.get(0).find(orig, dest, new Date()));
        rides.addAll(plugIns.get(1).find(orig, dest, new Date()));
        return true;
        
    }
    
    public void sort(final SharedPreferences prios) {
        Collections.sort(rides, new Comparator<Ride>() {

            @Override
            public int compare(Ride r1, Ride r2) {
                int score1= r1.fun * prios.getInt("fun", 0) +
                            r1.eco * prios.getInt("feco", 0) +
                            r1.fast * prios.getInt("fast", 0) +
                            r1.green * prios.getInt("green", 0) +
                            r1.social * prios.getInt("social", 0);
                int score2= r2.fun * prios.getInt("fun", 0) +
                            r2.eco * prios.getInt("eco", 0) +
                            r2.fast * prios.getInt("fast", 0) +
                            r2.green * prios.getInt("green", 0) +
                            r2.social * prios.getInt("social", 0);
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
    }

}
