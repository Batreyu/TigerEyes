package geocaching3700.tigereyes;

/**
 * Created by Kyle on 9/2/2014.
 */
public class Cache {
    private long id;
    private String title;
    private float latitude;
    private float longitude;
    private boolean completed;

    //modified version of Mack's
    //will return four values, float highDegreeLat, float highDegreeLong, float lowDegreeLat, float lowDegreeLong
    public static String[] calcDistance() {
        return new String[] {};
    }

    public Cache(String  titleIn, float latitudeIn, float longitudeIn) {
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = false;
    }

    public Cache(String  titleIn, float latitudeIn, float longitudeIn, boolean completedIn) {
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = completedIn;
    }

    public Cache(int idIn, String  titleIn, float latitudeIn, float longitudeIn, boolean completedIn) {
        id = idIn;
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = completedIn;
    }

    public void setId(long idIn) {
        id = idIn;
    }

    public int getId() {
        return (int)id;
    }

    public void setTitle(String titleIn) {
        title = titleIn;
    }

    public String getTitle() {
        return title;
    }

    public void setLat(float lat) {
        latitude = lat;
    }

    public float getLat() {
        return latitude;
    }

    public void setLon(float lon) {
        longitude = lon;
    }

    public float getLon() {
        return longitude;
    }

    public void setCompleted(boolean completedIn) {
        completed = completedIn;
    }

    public boolean getCompleted() {
        return completed;
    }
}
