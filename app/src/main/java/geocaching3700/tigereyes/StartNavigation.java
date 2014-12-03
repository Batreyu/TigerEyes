package geocaching3700.tigereyes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Kara on 9/8/2014.
 */
public class StartNavigation extends Activity {

    public SharedPreferences settings;
    private LocationManager mgr = null;
    private LocationListener listener = null;
    private Location lastKnownLocation = null;
    private Map map;
    private DecimalFormat df;
    private ImageView arrowImage;
    private boolean isNavigating;
    private ImageButton StopNav;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            getActionBar().hide();
            setContentView(R.layout.start_navigation);
            mgr=(LocationManager)getSystemService(LOCATION_SERVICE);
            listener = new MyLocationListener();
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
            settings = getSharedPreferences("settings", MODE_WORLD_WRITEABLE);
            df = new DecimalFormat("#.00");
            isNavigating = true;
            arrowImage = (ImageView) findViewById(R.id.arrow);

            StopNav = (ImageButton) findViewById(R.id.stop);
            ImageButton saveLoc = (ImageButton) findViewById(R.id.saveloc);

            saveLoc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final Dialog d = new Dialog(StartNavigation.this);
                    d.setContentView(R.layout.save_location);
                    d.setTitle("Name This Location");
                    d.show();
                    final EditText name = (EditText) d.findViewById(R.id.edittext);
                    Button ok = (Button) d.findViewById(R.id.dialogButtonOK);
                    Button cancel = (Button) d.findViewById(R.id.dialogButtonCancel);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = getApplicationContext();
                            DatabaseHandler dbHandler = new DatabaseHandler(context);
                            String locName = name.getText().toString();
                            float lat = settings.getFloat("currentLatitude", 0);
                            float lon = settings.getFloat("currentLongitude", 0);
                            Cache currentLocation = new Cache(locName, lat, lon);
                            dbHandler.addCache(currentLocation);
                            CharSequence text = "Location Saved";
                            int duration = Toast.LENGTH_LONG;
                            d.dismiss();
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });

                }

            });

            StopNav.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(StartNavigation.this, R.anim.click));
                    if (isNavigating) {
                        //stop Nav
                        //Stop updates to location listener
                        mgr.removeUpdates(listener);
                        //change image to "Start Navigation"
                        StopNav.setBackgroundResource(R.drawable.startnav);
                        //Return arrow to start position
                        float r = arrowImage.getRotation();
                        arrowImage.setRotation(360 - r);
                        //set flag to false
                        isNavigating = false;
                    } else {
                        //start Nav
                        //Start updates to location listener
                        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                        //change image to "Stop Navigation"
                        StopNav.setBackgroundResource(R.drawable.stopnav);
                        //set flag true
                        isNavigating = true;
                    }

                }
            });

            map = new Map();


            //get and display distance
            double d = (double) settings.getLong("distance", 0);
            String unit = settings.getString("distanceUnit", "");
            TextView distance = (TextView) findViewById(R.id.distance);
            String disString = df.format(d) + " " + unit;
            distance.setText(disString);


        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // check if GPS is enabled and if not send user to the GSP settings
            // Better solution would be to display a dialog and suggesting to
            // go to the settings. Will implement later
            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }


            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    public boolean getNavigating() {
        return isNavigating;
    }


    private void rotateImageView(ImageView imageView, int drawable, float rotate) {

        // Decode the drawable into a bitmap
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                drawable);

        // Get the width/height of the drawable
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = bitmapOrg.getWidth(), height = bitmapOrg.getHeight();

        // Initialize a new Matrix
        Matrix matrix = new Matrix();

        // Decide on how much to rotate
        // rotate = rotate % 360;

        // Actually rotate the image
        matrix.postRotate(rotate, width, height);

        // recreate the new Bitmap via a couple conditions
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        //BitmapDrawable bmd = new BitmapDrawable( rotatedBitmap );

        //imageView.setImageBitmap( rotatedBitmap );
        imageView.setImageDrawable(new BitmapDrawable(getResources(), rotatedBitmap));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
    }

    public void destinationReached() {
        //destination reached
        //Stop navigation updates
        mgr.removeUpdates(listener);
        //change arrow to check mark, change orientation to upright
        arrowImage.setImageResource(R.drawable.check);

        float r = arrowImage.getRotation();
        arrowImage.setRotation(360 - r);
        arrowImage.setAdjustViewBounds(true);
        StopNav.setBackgroundResource(R.drawable.startnav);
        //set flag to false
        isNavigating = false;

        //ask user to launch camera
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("Would you like to take a picture of your destination?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .show();


        return;

    }

        public void changeLocation(Location location) {
            listener.onLocationChanged(location);
        }

        private class MyLocationListener implements LocationListener{
            //On location change must update current coords, bearing, and distance
            @Override
            public void onLocationChanged(final Location location) {
                lastKnownLocation = location;

                //set new information across app in settings

                String longitude = "Longitude: " + location.getLongitude();
                //Log.v(TAG, longitude);
                String latitude = "Latitude: " + location.getLatitude();
                //Log.v(TAG, latitude);


                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putFloat("currentLatitude", (float) location.getLatitude());
                prefEditor.putFloat("currentLongitude", (float) location.getLongitude());
                prefEditor.commit();

                //Calculate distance from new location
                float destLat = settings.getFloat("destLatitude", 0);
                float destLon = settings.getFloat("destLongitude", 0);
                float curLat = settings.getFloat("currentLatitude", 0);
                float curLon = settings.getFloat("currentLongitude", 0);

                //Get distance to travel in miles. If less than 1 miles to go, get it in feet. store both number and unit in shared pref
                double distance = map.getDistanceMiles(curLat, curLon, destLat, destLon);
                String distUnit = "miles";
                if (distance <= 1) {
                    distance = map.getDistanceFeet(curLat, curLon, destLat, destLon);
                    distUnit = "feet";
                }
                SharedPreferences.Editor e = settings.edit();
                e.putLong("distance", (long) distance);
                e.putString("distanceUnit", distUnit);
                e.commit();

                //display distance in layout
                String distanceString = df.format(distance) + " " + distUnit;
                TextView distanceDisplay = (TextView) findViewById(R.id.distance);
                distanceDisplay.setText(distanceString);

                //Get bearing, store it, display it
                double bearing = map.getBearing(curLat, curLon, destLat, destLon);
                e = settings.edit();
                e.putLong("bearing", (long) bearing);
                e.commit();

                //Destination Reached
                if (distUnit.equals("feet") && distance <= 5) {
                    destinationReached();
                    return;
                }
                if (lastKnownLocation == null) {
                    return; //do nothing
                } else { //rotate arrow


                    GeomagneticField geoField = new GeomagneticField(Double
                            .valueOf(lastKnownLocation.getLatitude()).floatValue(), Double
                            .valueOf(lastKnownLocation.getLongitude()).floatValue(),
                            Double.valueOf(lastKnownLocation.getAltitude()).floatValue(),
                            System.currentTimeMillis()
                    );

                    bearing -= geoField.getDeclination(); // converts magnetic north into true north

                    // If the bearing is smaller than 0, add 360 to get the rotation clockwise.
                    if (bearing < 0) {
                        bearing = bearing + 360;
                    }

                    float fDir = (float) bearing;
                    rotateImageView(arrowImage, R.drawable.arrow, fDir);
                    arrowImage.setRotation(fDir);
                }

            }

            @Override
            public void onProviderDisabled(final String provider) {

            }
            @Override
            public void onProviderEnabled(final String provider) {

            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras) {

            }


        };
    }
