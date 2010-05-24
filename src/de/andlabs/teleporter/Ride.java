package de.andlabs.teleporter;

import java.util.Date;

public class Ride {
    
    public final static int MODE_TELEPORTER = 0; 
    public final static int MODE_SKATEBOARD = 1; 
    public final static int MODE_TRANSIT = 2; 
    public final static int MODE_FLIGHT = 3; 
    public final static int MODE_TRAIN = 4; 
    public final static int MODE_DRIVE = 5; 
    public static final int MODE_BIKE = 6; 
    public final static int MODE_WALK = 7; 
    public final static int MODE_TAXI = 8; 
    public final static int MODE_MFG = 9;
    
    public Place orig;
    public Place dest;
    public int plugin;
    public int price;
    public int mode;
    public Date dep;
    public Date arr;

    // scoring
    public int fun;
    public int eco;
    public int fast;
    public int green;
    public int social;
    
}
