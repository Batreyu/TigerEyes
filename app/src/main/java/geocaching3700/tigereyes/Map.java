package geocaching3700.tigereyes;


import android.app.Activity;
import android.os.Bundle;

/**
 * Created by mackbartus on 10/6/14.
 */
public class Map extends Activity {

    /* will return four values, float highDegreeLat, float highDegreeLong, float lowDegreeLat, float lowDegreeLong
     * plug straight into the getWithin() method
     *
     * taken from http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java
     */
    public static String[] calcBounds(double distance, float curLat, float curLon) {
        double R = 3958.8; // miles
        double MIN_LAT = Math.toRadians(-90d);  // -PI/2
        double MAX_LAT = Math.toRadians(90d);   //  PI/2
        double MIN_LON = Math.toRadians(-180d); // -PI
        double MAX_LON = Math.toRadians(180d);  //  PI

        //distance in radians
        double radDist = distance / R;
        double minLat = curLat - radDist;
        double maxLat = curLat + radDist;

        double minLon;
        double maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) / Math.cos(curLat));
            minLon = curLon - deltaLon;
            if (minLon < MIN_LON) minLon += 2d * Math.PI;
            maxLon = curLon + deltaLon;
            if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
        } else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }
        return new String[]{String.valueOf(maxLat), String.valueOf(maxLon),
                String.valueOf(minLat), String.valueOf(minLon)};
    }

    //@Override
    protected void onCreate(Bundle savedInstanceState, Cache cache) {
        super.onCreate(savedInstanceState);
            /*
            //setContentView(R.layout.map_activity); // MAP VIEW NEEDED

            // Get a handle to the Map Fragment - NOT WORKING ATM
            //GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            LatLng cacheLocation = new LatLng(cache.getLat(), cache.getLon());
            LatLng myLocation = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            LatLng center = new LatLng((cache.getLat() + map.getMyLocation().getLatitude()) / 2, (cache.getLon() + map.getMyLocation().getLongitude()) / 2);

            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));

            map.addMarker(new MarkerOptions() // add marker at the location of the cache with title
                 .title(cache.getTitle())
                 .position(cacheLocation));

            map.addMarker(new MarkerOptions() // add marker at the current location
                  .title("My Location")
                  .position(myLocation));

            map.addPolyline(new PolylineOptions().geodesic(true)
                  .add(myLocation)
                  .add(cacheLocation));
            */
    }

    public double getBearing(float lat1, float lon1, float lat2, float lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);
        double b = Math.toDegrees(Math.atan2(y, x));
        return b;
    }

    public double getDistanceFeet(float lat1, float lon1, float lat2, float lon2) //uses haversine formula
    {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1); // get the distance between lats
        double dLon = Math.toRadians(lon2 - lon1); // get the distance between longs
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // taken from yourhomenow.com
        double d = R * c;
        d = d * 3280.8399; // d from km to ft
        return d;
    }

    public double getDistanceMiles(float lat1, float lon1, float lat2, float lon2) //uses haversine formula
    {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1); // get the distance between lats
        double dLon = Math.toRadians(lon2 - lon1); // get the distance between longs
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // taken from yourhomenow.com
        double d = R * c;
        d = d / 1.609344; // d from km to mi
        return d;
    }
}
