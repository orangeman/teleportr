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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + lat;
        result = prime * result + lon;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + type;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Place other = (Place) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (lat != other.lat)
            return false;
        if (lon != other.lon)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
