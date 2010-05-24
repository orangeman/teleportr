package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.andlabs.teleporter.plugin.BahnDePlugIn;
import de.andlabs.teleporter.plugin.BvgPlugIn;
import de.andlabs.teleporter.plugin.ITeleporterPlugIn;
import de.andlabs.teleporter.plugin.SkateboardPlugIn;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;

public class QueryMultiplexer {

    private static final String TAG = "Multiplexer";
    private Place orig;
    private Place dest;
    public ArrayList<Ride> rides;
    public ArrayList<ITeleporterPlugIn> plugIns;
    private ArrayList<Ride> nextRides;
    private SharedPreferences priorities;

    public QueryMultiplexer(Context ctx, Place o, Place d) {
        orig = o;
        dest = d;
        
        rides = new ArrayList<Ride>();
        nextRides = new ArrayList<Ride>();
        plugIns = new ArrayList<ITeleporterPlugIn>();
        SharedPreferences plugInSettings = ctx.getSharedPreferences("plugIns", ctx.MODE_PRIVATE);
        try {
            for (String p : plugInSettings.getAll().keySet()) {
                Log.d(TAG, "plugin "+p);
                if (plugInSettings.getBoolean(p, false))
                    Log.d(TAG, "add plugin "+p);
                    plugIns.add((ITeleporterPlugIn) Class.forName("de.andlabs.teleporter.plugin."+p).newInstance());
            }
        } catch (Exception e) {
            Log.e(TAG, "Schade!");
            e.printStackTrace();
        }
        priorities = ctx.getSharedPreferences("priorities", ctx.MODE_PRIVATE);
    }

    public boolean searchNext() {
        // TODO just query just plugins that ...
        // TODO use ThreadPoolExecutor ...
        for (ITeleporterPlugIn p : plugIns) {
            Log.d(TAG, "query plugin "+p);
            nextRides.addAll(p.find(orig, dest, new Date()));
        }
        return true;
        
    }
    
    public void sort() {
        rides.addAll(nextRides);
        nextRides.clear();
        Collections.sort(rides, new Comparator<Ride>() {

            @Override
            public int compare(Ride r1, Ride r2) {
                int score1= r1.fun * priorities.getInt("fun", 1) +
                            r1.eco * priorities.getInt("feco", 1) +
                            r1.fast * priorities.getInt("fast", 1) +
                            r1.green * priorities.getInt("green", 1) +
                            r1.social * priorities.getInt("social",1);
                int score2= r2.fun * priorities.getInt("fun", 1) +
                            r2.eco * priorities.getInt("eco", 1) +
                            r2.fast * priorities.getInt("fast", 1) +
                            r2.green * priorities.getInt("green", 1) +
                            r2.social * priorities.getInt("social", 1);
//                Log.d("aha", "score1: "+score1 + ",  score2: "+score2);
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
