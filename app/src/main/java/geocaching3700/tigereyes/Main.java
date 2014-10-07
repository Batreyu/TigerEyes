package geocaching3700.tigereyes;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.Toast;


public class Main extends Activity {
    ImageButton startNav = null;
    ImageButton myLocations = null;
    ImageButton viewGallery = null;
    Activity activity = this;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
        settings = getSharedPreferences("settings", MODE_WORLD_WRITEABLE);
        //initialize buttons with layout elements
        startNav = (ImageButton) findViewById(R.id.start_navigation);
        myLocations = (ImageButton) findViewById(R.id.locations);
        viewGallery = (ImageButton) findViewById(R.id.gallery);

        //set listener for click, start new activity
        startNav.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //  v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));
                Intent intent = new Intent(Main.this, StartNavigation.class);
                startActivity(intent);
            }
        });
        myLocations.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //  v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));
                Intent intent = new Intent(Main.this, MyLocation.class);
                startActivity(intent);
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



