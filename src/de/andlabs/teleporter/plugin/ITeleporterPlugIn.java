package de.andlabs.teleporter.plugin;

import java.util.ArrayList;
import java.util.Date;

import de.andlabs.teleporter.Place;
import de.andlabs.teleporter.Ride;

public interface ITeleporterPlugIn {

    public abstract ArrayList<Ride> find(Place orig, Place dest, Date time);

}