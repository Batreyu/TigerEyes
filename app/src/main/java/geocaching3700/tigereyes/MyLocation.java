package geocaching3700.tigereyes;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyLocation extends Activity {
    protected Context context;
    Cache currentLocation;
    private ListView listView;
    private TextView saveLoc;
    private LocationManager mgr = null;
    private LocationListener listener = null;
    private Location lastKnownLocation = null;
    private SharedPreferences settings;
    private  float longitude, latitude;
    private DatabaseHandler dbHandler;
    private Activity a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.my_locations);
        a = this;
        saveLoc = (TextView) findViewById(R.id.saveloc);
        listView = (ListView) findViewById(R.id.cachelist);
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyLocationListener();
        context = getApplicationContext();
        settings = getSharedPreferences("settings", MODE_WORLD_WRITEABLE);
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        dbHandler = new DatabaseHandler(context);
        ArrayList<Cache> caches = dbHandler.getCaches();
        final CacheListLazyAdapter adapter= new CacheListLazyAdapter(
                this,
                android.R.layout.simple_list_item_1,
                caches );

        listView.setAdapter(adapter);
        //CacheListLazyAdapter adapter = new CacheListLazyAdapter(a, R.layout.cache_item, caches));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog d = new Dialog(MyLocation.this);
                d.setContentView(R.layout.cache_details);
                d.setTitle("Details");
                d.show();
                final Cache thisCache = (Cache) listView.getItemAtPosition(position);
                TextView title = (TextView) d.findViewById(R.id.title);
                TextView coords = (TextView) d.findViewById(R.id.coords);
                TextView distance = (TextView) d.findViewById(R.id.distance);
                TextView direction = (TextView) d.findViewById(R.id.direction);
                Button go = (Button) d.findViewById(R.id.dialogButtonOK);
                Button cancel = (Button) d.findViewById(R.id.dialogButtonCancel);
                title.setText(thisCache.getTitle());
                coords.setText("Latitude: " + thisCache.getLat() + ", Longitude: " + thisCache.getLon());
                //use shared prefs to calculate distance and direction

                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                go.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float destLat = thisCache.getLat();
                        float destLon = thisCache.getLon();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putFloat("destLatitude", destLat);
                        editor.putFloat("destLongitude", destLon);
                        editor.putString("destTitle", thisCache.getTitle());
                        editor.commit();
                        d.dismiss();
                        //launch start nav
                        Intent intent = new Intent(MyLocation.this, StartNavigation.class);
                        startActivity(intent);
                    }
                });

            }
        });
        saveLoc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //  v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));

                final Dialog d = new Dialog(MyLocation.this);
                d.setContentView(R.layout.save_location);
                d.setTitle("Name This Location");
                d.show();
                final EditText name = (EditText) d.findViewById(R.id.edittext);
                Button ok = (Button) d.findViewById(R.id.dialogButtonOK);
                Button cancel = (Button) d.findViewById(R.id.dialogButtonCancel);

                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     String locName = name.getText().toString();
                        float lat = settings.getFloat("currentLatitude", 0);
                        float lon = settings.getFloat("currentLongitude", 0);
                        currentLocation = new Cache(locName,lat,lon);
                        dbHandler.addCache(currentLocation);
                        CharSequence text = "Location Saved";
                        int duration = Toast.LENGTH_LONG;
                        adapter.notifyDataSetChanged();
                        d.dismiss();
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
                // currentLocation = new Cache();



            }
        });
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location location) {
            lastKnownLocation = location;
            longitude = (float) location.getLongitude();
            latitude = (float) location.getLatitude();
            settings = getSharedPreferences("settings", MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putFloat("Latitude", (float) location.getLatitude());
            prefEditor.putFloat("Longitude", (float)location.getLongitude());
            prefEditor.commit();
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
    }

    ;
}



