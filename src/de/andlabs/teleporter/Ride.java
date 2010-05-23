package de.andlabs.teleporter;

import java.util.Date;

public class Ride {
    
    public final static int MODE_TELEPORTER = 0; 
    public final static int MODE_SKATEBOARD = 1; 
    public final static int MODE_TRANSIT = 2; 
    public final static int MODE_TRAIN = 3; 
    public final static int MODE_PLANE = 4; 
    public final static int MODE_FOOT = 5; 
    public final static int MODE_TAXI = 6; 
    public final static int MODE_CAR = 7; 
    public final static int MODE_MFG = 8; 
    
    Place orig;
    Place dest;
    int plugin;
    int price;
    int mode;
    Date dep;
    Date arr;

    // scoring
    int fun;
    int eco;
    int fast;
    int green;
    int social;
    
    public Ride() {
        // TODO Auto-generated constructor stub
    }

}
