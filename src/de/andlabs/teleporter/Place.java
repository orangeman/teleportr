package de.andlabs.teleporter;

public class Place {

    public static final String CONTENT_TYPE = "foo";
    
    public static final int TYPE_STATION = 0;
    public static final int TYPE_ADDRESS = 1;
    
    public int lat;
    public int lon;
    public int type;
    public String name;
    public String address;

}
