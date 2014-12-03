package geocaching3700.tigereyes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Kara on 10/2/2014.
 */

public class CacheListLazyAdapter extends ArrayAdapter<Cache> {
    private static LayoutInflater inflater = null;
    public SharedPreferences settings;
    private Activity activity;
    private int listitem;
    private ArrayList<Cache> caches;
    private Context context;
    private DatabaseHandler dbh;
//public ImageLoader imageLoader;

    public CacheListLazyAdapter(Context context, int textViewResourceId, ArrayList<Cache> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        caches = objects;
        settings = context.getSharedPreferences("settings", context.MODE_WORLD_WRITEABLE);

    }

    public void LazyAdapter(Activity a, int rowID, ArrayList<Cache> cachelist) {
        activity = a;
        caches = cachelist;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {

        return caches.size();
    }

//public int getItem(int position) {
    // return position;
    //}

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.cache_item, null);
        }


        TextView Name = (TextView) vi.findViewById(R.id.text1); // title of cache
        TextView Desc = (TextView) vi.findViewById(R.id.text2); // desc or location
        ImageView delete = (ImageView) vi.findViewById(R.id.icon); // image

        final Cache thisCache = caches.get(position);
        Name.setText(thisCache.getTitle());
        String coords = "Latitude: " + thisCache.getLat() + ", Longitude: " + thisCache.getLon();
        Desc.setText(coords);
        //if pressed delete
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click));
                final AlertDialog.Builder d = new AlertDialog.Builder(context);
                d.setCancelable(true);
                d.setMessage("Would you like to delete this cache?");
                d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbh = new DatabaseHandler(context);
                        dbh.deleteCache(thisCache);
                        caches = dbh.getCaches(settings.getFloat("currentLatitude", 0), settings.getFloat("currentLongitude", 0));
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                final AlertDialog dialog = d.create();
                dialog.show();
            }
        });


        return vi;
    }
}







