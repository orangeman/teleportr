package de.andlabs.teleporter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class BahnDePlugIn {
    
    private static final String TAG = "PlugIn";
    private DefaultHttpClient client;
    private ArrayList<Ride> rides;

    public BahnDePlugIn() {
        rides = new ArrayList<Ride>();
        client = new DefaultHttpClient();
    }

    public ArrayList<Ride> find(Place orig, Place dest, Date time) {
        
        StringBuilder url = new StringBuilder();
        url.append("http://mobile.bahn.de/bin/mobil/query.exe/dox?");
        url.append("n=1");
        switch (orig.type) {
            case Place.TYPE_ADDRESS:
                url.append("&f=2&s=").append(URLEncoder.encode(orig.address));
                break;
            case Place.TYPE_STATION:
                url.append("&f=1&s=").append(URLEncoder.encode(orig.name+", "+orig.address));
                break;
        }
        switch (dest.type) {
            case Place.TYPE_ADDRESS:
                url.append("&o=2&z=").append(URLEncoder.encode(dest.address));
                break;
            case Place.TYPE_STATION:
                url.append("&o=1&z=").append(URLEncoder.encode(dest.name+", "+dest.address));
                break;
        }
        url.append("&d="); // date
        url.append((new SimpleDateFormat("ddMMyy")).format(time));
        url.append("&t="); // time
        url.append((new SimpleDateFormat("HHmm")).format(time));
        url.append("&start=Suchen");
//        Log.d(TAG, "url: "+url.toString());
        
        // fetch
        try {
            Ride r;
            MatchResult m;
            rides.clear();
            Scanner scanner = new Scanner(client.execute(new HttpGet(url.toString())).getEntity().getContent(), "iso-8859-1");
            while (scanner.findWithinHorizon("<a href=\"([^\"]*)\">(\\d\\d):(\\d\\d)<br />(\\d\\d):(\\d\\d)", 10000) != null) {
                m = scanner.match();
                Log.d(TAG, "found :) "+m);
                Date dep = parseDate(m.group(2), m.group(3));
                if (dep.getTime() - time.getTime() > 100000) {
                    r = new Ride();
                    r.orig = orig;
                    r.dest = dest;
                    r.dep = dep;
                    r.arr = parseDate(m.group(4), m.group(5));
                    r.plugin = R.drawable.bahn_de;
                    r.price = 240;
                    r.fun = 3;
                    r.eco = 3;
                    r.fast = 1;
                    r.social = 2;
                    r.green = 4;
                    rides.add(r);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Mist!");
            e.printStackTrace();
        }
        return rides;
    }


    private Date parseDate(String hours, String minutes) {
        Date date = new Date();
        date.setHours(Integer.parseInt(hours));
        date.setMinutes(Integer.parseInt(minutes));
        if (System.currentTimeMillis() - date.getTime() > 36000000) { // Mitternacht..
            long oneDay = (long) 1000.0 * 60 * 60 * 24;
            date.setTime(date.getTime() + oneDay);
        }
        return date;
    }

}
