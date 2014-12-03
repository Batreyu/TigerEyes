package geocaching3700.tigereyes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;


public class Main extends Activity {
    ImageButton startNav = null;
    ImageButton myLocations = null;
    ImageButton viewGallery = null;
    Activity activity = this;
    private Map map;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
        map = new Map();

        //Make sure when starting app not continuing last session, but keep last known location until it updates
        settings = getSharedPreferences("settings", MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = settings.edit();
        e.putFloat("destLatitude", 0);
        e.putFloat("destLongitude", 0);
        // e.putFloat("currentLatitude", 0);
        // e.putFloat("currentLongitude", 0);
        e.putLong("bearing", 0);
        e.putLong("distance", 0);
        e.putString("distanceUnit", "feet");
        e.commit();
        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
        //initialize buttons with layout elements
        startNav = (ImageButton) findViewById(R.id.start_navigation);
        myLocations = (ImageButton) findViewById(R.id.locations);
        viewGallery = (ImageButton) findViewById(R.id.gallery);

        //set listener for click, start new activity
        startNav.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.click));
                //OptionMenuWithDialog d = new OptionMenuWithDialog();
                //Intent intent = new Intent(Main.this, OptionMenuWithDialog.class);
                //startActivity(intent);
                final String[] option = new String[]{"Enter Coordinates", "My Closest Cache", "Choose From My Locations"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this,
                        android.R.layout.select_dialog_item, option);
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

                builder.setTitle("Select Option");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { //Enter Coordinates
                            final Dialog d = new Dialog(Main.this);
                            d.setContentView(R.layout.enter_coords);
                            d.setTitle("Enter Coordinates");
                            d.show();
                            final EditText latText = (EditText) d.findViewById(R.id.lat);
                            final EditText lonText = (EditText) d.findViewById(R.id.lon);
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
                                    //check if coords are numbers
                                    try {
                                        float latfloat = Float.parseFloat(latText.getText().toString());
                                        float lonfloat = Float.parseFloat(lonText.getText().toString());
                                        //set valid coords to destination and launch start nav
                                        SharedPreferences.Editor e = settings.edit();
                                        e.putFloat("destLatitude", latfloat);
                                        e.putFloat("destLongitude", lonfloat);
                                        e.commit();
                                        d.dismiss();
                                        Intent intent = new Intent(Main.this, StartNavigation.class);
                                        startActivity(intent);

                                    } catch (NumberFormatException e) {
                                        CharSequence text = "You must enter valid numerical coordinates";
                                        int duration = Toast.LENGTH_LONG;
                                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                        toast.show();
                                    }
                                }
                            });

                        } else if (which == 1) { //Closest Cache
                            Context context = getApplicationContext();
                            DatabaseHandler dbHandler = new DatabaseHandler(context);
                            ArrayList<Cache> caches = dbHandler.getCaches(settings.getFloat("currentLatitude", 0), settings.getFloat("currentLongitude", 0));
                            Cache closest = caches.get(0); //closest cache
                            //set destination coords
                            float latfloat = closest.getLat();
                            float lonfloat = closest.getLon();
                            SharedPreferences.Editor e = settings.edit();
                            e.putFloat("destLatitude", latfloat);
                            e.putFloat("destLongitude", lonfloat);
                            e.commit();
                            //launch navigation
                            Intent intent = new Intent(Main.this, StartNavigation.class);
                            startActivity(intent);

                        } else if (which == 2) { // My Locations
                            Intent intent = new Intent(Main.this, MyLocation.class);
                            startActivity(intent);
                        }

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        myLocations.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.click));
                Intent intent = new Intent(Main.this, MyLocation.class);
                startActivity(intent);
            }
        });

        viewGallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.click));
                final int RESULT_GALLERY = 0;

                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_GALLERY);
            }
        });
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
            final AlertDialog.Builder d = new AlertDialog.Builder(Main.this);
            d.setCancelable(true);
            d.setMessage("You must enable location services to use this application.");
            d.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                @Override

                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialogInterface.dismiss();
                }
            });
            d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "You must enable location services in your settings",
                            Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                }
            });
            final AlertDialog dialog = d.create();
            dialog.show();
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

}



