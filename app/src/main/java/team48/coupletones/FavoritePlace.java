package team48.coupletones;

import android.location.Location;



/**
 * Created by Zhuyupei on 5/7/16.
 */
public class FavoritePlace implements Comparable<FavoritePlace> {
    private String name;
    private Location location;

    public FavoritePlace(String name, Location location){
        this.name = new String(name);
        this.location = new Location(location);
    }

    public void changeName(String name){
        this.name = new String(name);
    }

    public String getName(){
        return name;
    }

    public Location getLocation(){
        return location;
    }

    @Override
    public int compareTo(FavoritePlace another) {
        if(another.getLocation().getAltitude() == this.getLocation().getAltitude()&&another.getLocation().getLatitude() == this.getLocation().getLatitude())
            return 0;
        return -1;
    }

}
