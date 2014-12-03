package geocaching3700.tigereyes;

/**
 * Created by Kyle on 9/2/2014.
 */
public class Cache {
    private long id;
    private String title;
    private float latitude;
    private float longitude;
    private double distance;
    private boolean completed;

    public Cache(String titleIn, float latitudeIn, float longitudeIn) {
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = false;
        distance = 0;
    }

    public Cache(String titleIn, float latitudeIn, float longitudeIn, boolean completedIn) {
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = completedIn;
        distance = 0;
    }

    public Cache(int idIn, String titleIn, float latitudeIn, float longitudeIn, boolean completedIn) {
        id = idIn;
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
        completed = completedIn;
        distance = 0;
    }

    public int getId() {
        return (int) id;
    }

    public void setId(long idIn) {
        id = idIn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titleIn) {
        title = titleIn;
    }

    public float getLat() {
        return latitude;
    }

    public void setLat(float lat) {
        latitude = lat;
    }

    public float getLon() {
        return longitude;
    }

    public void setLon(float lon) {
        longitude = lon;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completedIn) {
        completed = completedIn;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distanceIn) {
        distance = distanceIn;
    }
}
