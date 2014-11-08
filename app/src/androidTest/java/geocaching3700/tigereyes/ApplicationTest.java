package geocaching3700.tigereyes;

import android.app.Application;
import android.location.Location;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        storeCurrentLocationTest();
    }

    public void storeCurrentLocationTest() {
        DatabaseHandler db = new DatabaseHandler(getApplication());
        Cache currentLoc = new Cache("Current", 10.10f, 10.10f);
        db.addCache(currentLoc);

        Cache theCache = db.getCache(currentLoc.getId());
        assertEquals(currentLoc.getTitle(), theCache.getTitle());
        assertEquals(currentLoc.getCompleted(), theCache.getCompleted());
        assertEquals(currentLoc.getId(), theCache.getId());
        assertEquals(currentLoc.getLat(), theCache.getLat());
        assertEquals(currentLoc.getLon(), theCache.getLon());
    }
}