package geocaching3700.tigereyes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Kara on 10/2/2014.
 */

public class CacheListLazyAdapter extends ArrayAdapter<Cache> {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private int listitem;
    private ArrayList<Cache> caches;
//public ImageLoader imageLoader;

    public CacheListLazyAdapter(Context context, int textViewResourceId, ArrayList<Cache> objects) {
        super(context, textViewResourceId, objects);
        caches = objects;
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
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.icon); // image

        Cache thisCache = caches.get(position);
        Name.setText(thisCache.getTitle());
        String coords = "Latitude: " + thisCache.getLat() + ", Longitude: " + thisCache.getLon();
        Desc.setText(coords);
        //ADD code to for JSON here
        return vi;
    }
}







