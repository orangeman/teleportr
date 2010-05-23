package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Date;

public interface ITeleporterPlugIn {

    public abstract ArrayList<Ride> find(Place orig, Place dest, Date time);

}