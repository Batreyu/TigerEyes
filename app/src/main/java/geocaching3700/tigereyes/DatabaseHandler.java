package geocaching3700.tigereyes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Kyle on 9/2/2014.
 * Send me a message over hangout if you need a feature added to the database.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "cacheManager";

    private static final String TABLE_CACHES = "caches";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_COMPLETED = "completed";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CACHES_TABLE = "CREATE TABLE " + TABLE_CACHES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_LATITUDE + " TEXT," +
                KEY_LONGITUDE + " TEXT," + KEY_COMPLETED + " TEXT" + ")";
        db.execSQL(CREATE_CACHES_TABLE);

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, "Bridge? Over Troubled Water");
        values.put(KEY_LATITUDE, "32.5037667");
        values.put(KEY_LONGITUDE, "-085.5946500");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "Eye of the Needle");
        values.put(KEY_LATITUDE, "32.4854667");
        values.put(KEY_LONGITUDE, "-085.5850000");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "Monkey Business");
        values.put(KEY_LATITUDE, "32.4846667");
        values.put(KEY_LONGITUDE, "-085.6075333");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "ASSIGNMENT: Fire Tower");
        values.put(KEY_LATITUDE, "32.4598000");
        values.put(KEY_LONGITUDE, "-085.6228333");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "Revenge of the Cache Monkey");
        values.put(KEY_LATITUDE, "32.4376833");
        values.put(KEY_LONGITUDE, "-085.6356833");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "Rocky");
        values.put(KEY_LATITUDE, "32.4401167");
        values.put(KEY_LONGITUDE, "-085.6397167");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        values.clear();
        values.put(KEY_TITLE, "CC's Cache");
        values.put(KEY_LATITUDE, "32.4413667");
        values.put(KEY_LONGITUDE, "-085.6539667");
        values.put(KEY_COMPLETED, "false");
        db.insert(TABLE_CACHES, null, values);

        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHES);
        onCreate(db);
    }

    // Adding new cache
    public void addCache(Cache cache) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, cache.getTitle());
        values.put(KEY_LATITUDE, String.valueOf(cache.getLat()));
        values.put(KEY_LONGITUDE, String.valueOf(cache.getLon()));
        values.put(KEY_COMPLETED, String.valueOf(cache.getCompleted()));
        long rowID = db.insert(TABLE_CACHES, null, values);
        cache.setId(rowID);
        db.close();
    }

    // Getting single cache by id
    public Cache getCache(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(TABLE_CACHES, new String[]{KEY_ID, KEY_TITLE, KEY_LATITUDE, KEY_LONGITUDE,
                        KEY_COMPLETED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return new Cache(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)),
                    Boolean.parseBoolean(cursor.getString(4)));
        }
        return null;
    }

    public Cache getClosestCache(float lat, float lon) {
        ArrayList<Cache> caches = getCaches(lat, lon);
        return caches.get(0);
    }

    // Getting all caches
    public ArrayList<Cache> getCaches(float lat1, float lon1) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CACHES, new String[]{KEY_ID, KEY_TITLE, KEY_LATITUDE, KEY_LONGITUDE, KEY_COMPLETED}, null,
                null, null, null, KEY_TITLE + " DESC", null);
        ArrayList<Cache> caches = new ArrayList<Cache>();
        if (cursor.moveToFirst()) {
            do {
                Cache cache = new Cache(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)),
                        Boolean.parseBoolean(cursor.getString(4)));
                caches.add(cache);
            } while (cursor.moveToNext());
        }

        return sortByDistance(caches, lat1, lon1);
    }

    //Get all uncompleted caches
    public ArrayList<Cache> getCompletedCaches(float lat1, float lon1) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CACHES, new String[]{KEY_ID, KEY_TITLE, KEY_LATITUDE, KEY_LONGITUDE, KEY_COMPLETED},
                KEY_COMPLETED + "=?", new String[]{String.valueOf(true)}, null, null, KEY_TITLE + " DESC", null);
        ArrayList<Cache> caches = new ArrayList<Cache>();
        if (cursor.moveToFirst()) {
            do {
                Cache cache = new Cache(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)),
                        Boolean.parseBoolean(cursor.getString(4)));
                caches.add(cache);
            } while (cursor.moveToNext());
        }
        return sortByDistance(caches, lat1, lon1);
    }

    //Getting all caches within radius (in degrees)
    //Should return a square radius, not a circle radius...it's not perfect :(
    public ArrayList<Cache> getWithin(String[] bounds, float lat1, float lon1) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CACHES, new String[]{KEY_ID, KEY_TITLE, KEY_LATITUDE, KEY_LONGITUDE, KEY_COMPLETED},
                KEY_LATITUDE + "<=? AND " + KEY_LONGITUDE + "<=? AND " + KEY_LATITUDE + ">=? AND " + KEY_LONGITUDE + ">=?",
                bounds, null, null, KEY_TITLE + " DESC", null);
        ArrayList<Cache> caches = new ArrayList<Cache>();
        if (cursor.moveToFirst()) {
            do {
                Cache cache = new Cache(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)),
                        Boolean.parseBoolean(cursor.getString(4)));
                caches.add(cache);
            } while (cursor.moveToNext());
        }
        return sortByDistance(caches, lat1, lon1);
    }

    // Updating single cache
    public void updateCache(Cache cache) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, cache.getId());
        values.put(KEY_TITLE, cache.getTitle());
        values.put(KEY_LATITUDE, String.valueOf(cache.getLat()));
        values.put(KEY_LONGITUDE, String.valueOf(cache.getLon()));
        values.put(KEY_COMPLETED, String.valueOf(cache.getCompleted()));

        db.update(TABLE_CACHES, values, KEY_ID + " = ?", new String[]{String.valueOf(cache.getId())});
    }

    // Deleting single cache
    // Returns true if a cache has been deleted
    public boolean deleteCache(Cache cache) {
        SQLiteDatabase db = getWritableDatabase();
        boolean value = (db.delete(TABLE_CACHES, KEY_ID + " = ?",
                new String[]{String.valueOf(cache.getId())}) > 0);
        db.close();
        return value;
    }

    //Delete all completed caches
    //Returns number of caches deleted
    public int deleteFinishedCaches() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_CACHES, KEY_COMPLETED + "=?", new String[]{String.valueOf(true)});
        return deleted;
    }

    private ArrayList<Cache> sortByDistance(ArrayList<Cache> caches, float lat1, float lon1) {
        //get distances
        double R = 6371; // km
        for (int i = 0; i < caches.size(); i++) {
            float lat2 = caches.get(i).getLat();
            float lon2 = caches.get(i).getLon();
            double dLat = Math.toRadians(lat2 - lat1); // get the distance between lats
            double dLon = Math.toRadians(lon2 - lon1); // get the distance between longs
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // taken from yourhomenow.com
            double d = R * c;
            d = d / 1.609344; // d from km to mi
            caches.get(i).setDistance(d);
        }

        ArrayList<Cache> newCaches = new ArrayList<Cache>(caches.size());
        newCaches.set(0, caches.get(0));
        //insertion sort -- can change it later to more efficient
        for (int i = 1; i < caches.size(); i++) {
            Cache cache = caches.get(i);
            int j;
            for (j = i; j > 0 && newCaches.get(j-1).getDistance() > cache.getDistance(); j--) {
                newCaches.set(j, newCaches.get(j-1));
            }
            newCaches.set(j, cache);
        }
        return newCaches;
    }
}