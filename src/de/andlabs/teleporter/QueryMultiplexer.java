package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import de.andlabs.teleporter.plugin.ITeleporterPlugIn;

public class QueryMultiplexer {

    private static final String TAG = "Multiplexer";
    private Place orig;
    private Place dest;
    public ArrayList<Ride> rides;
    public ArrayList<ITeleporterPlugIn> plugIns;
    private ArrayList<Ride> nextRides;
    private Map<String, Integer> priorities;
    private long mLastSearchTimestamp;
    private BroadcastReceiver mPluginResponseReceiver;
    private final Context ctx;
    private Handler mUpdateHandler;

    public QueryMultiplexer(Context ctx, Place o, Place d) {
        this.ctx = ctx;
        orig = o;
        dest = d;

        
        nextRides = new ArrayList<Ride>();
        rides = new ArrayList<Ride>() {
            @Override
            public boolean add(Ride object) {
                if(!contains(object))
                    return super.add(object);
                else 
                    return false;
            }
            
            @Override
            public boolean addAll(Collection<? extends Ride> collection) {
                for(Ride r : collection)
                    if(!contains(r))
                        super.add(r);
                return true;
            }
            
            @Override
            public void add(int index, Ride object) {
                if(!contains(object))
                    super.add(index, object);
            }
        };
        plugIns = new ArrayList<ITeleporterPlugIn>();
        SharedPreferences plugInSettings = ctx.getSharedPreferences("plugIns", ctx.MODE_PRIVATE);
        try {
            for (String p : plugInSettings.getAll().keySet()) {
                Log.d(TAG, "plugin "+p);
                if (plugInSettings.getBoolean(p, false)){
                    Log.d(TAG, "add plugin "+p);
                    plugIns.add((ITeleporterPlugIn) Class.forName("de.andlabs.teleporter.plugin."+p).newInstance());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Schade!");
            e.printStackTrace();
        }
        priorities = (Map<String, Integer>) ctx.getSharedPreferences("priorities", ctx.MODE_PRIVATE).getAll();

        this.mUpdateHandler = new Handler();

        this.mPluginResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Plugin Response Received.");
                final int duration = intent.getIntExtra("dur", -1);

                final Ride ride = new Ride();
                ride.duration = duration;
                ride.orig = orig;
                ride.dest = dest;
                ride.mode = Ride.MODE_DRIVE;
                ride.fun = 1;
                ride.eco = 1;
                ride.fast = 5;
                ride.social = 1;
                ride.green = 1;
                
                mUpdateHandler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        nextRides.add(ride);
                    }
                });
            }
        };

        this.ctx.registerReceiver(this.mPluginResponseReceiver, new IntentFilter("org.teleporter.intent.action.RECEIVE_RESPONSE"));
    }

    public boolean searchLater() {
        // TODO just query just plugins that ...
        // TODO use ThreadPoolExecutor ...
        for (ITeleporterPlugIn p : plugIns) {
            Log.d(TAG, "query plugin "+p);

            final long requestTimestamp;
            if(this.mLastSearchTimestamp == 0 ) {
                requestTimestamp = System.currentTimeMillis();
            } else {
                requestTimestamp = this.mLastSearchTimestamp + 300000; // 5 Minutes
            }

            nextRides.addAll(p.find(orig, dest, new Date(requestTimestamp)));

            this.mLastSearchTimestamp = requestTimestamp;
        }

        Intent requestIntent = new Intent("org.teleporter.intent.action.RECEIVE_REQUEST");
        requestIntent.putExtra("origLatitude", orig.lat);
        requestIntent.putExtra("origLongitude", orig.lon);
        requestIntent.putExtra("destLatitude", dest.lat);
        requestIntent.putExtra("destLongitude", dest.lon);
        this.ctx.sendBroadcast(requestIntent);

        return true;
    }

    public void sort() {
        rides.addAll(nextRides);
        nextRides.clear();

        priorities = (Map<String, Integer>) ctx.getSharedPreferences("priorities", ctx.MODE_PRIVATE).getAll();
        Collections.sort(rides, new Comparator<Ride>() {

            @Override
            public int compare(Ride r1, Ride r2) {
                // TODO Neue Faktor für Score: "Quickness" (Abfahrtszeit minus Jetzt)
                // TODO Faktoren normalisieren
                int score1= r1.fun * priorities.get("fun") +
                r1.eco * priorities.get("eco") +
                r1.fast * priorities.get("fast") +
                r1.green * priorities.get("green") +
                r1.social * priorities.get("social");
                int score2= r2.fun * priorities.get("fun") +
                r2.eco * priorities.get("eco") +
                r2.fast * priorities.get("fast") +
                r2.green * priorities.get("green") +
                r2.social * priorities.get("social");
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
