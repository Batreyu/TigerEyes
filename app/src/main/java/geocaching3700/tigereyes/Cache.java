package geocaching3700.tigereyes;

/**
 * Created by Kyle on 9/2/2014.
 */
public class Cache {
    private long id;
    private String title;
    private float latitude;
    private float longitude;

    public Cache(String  titleIn, float latitudeIn, float longitudeIn) {
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
    }

    public Cache(int idIn, String  titleIn, float latitudeIn, float longitudeIn) {
        id = idIn;
        title = titleIn;
        latitude = latitudeIn;
        longitude = longitudeIn;
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
}
