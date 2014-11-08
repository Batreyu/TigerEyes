package geocaching3700.tigereyes;

import android.content.SharedPreferences;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Kyle on 11/7/2014.
 */
public class NavigationTest extends ActivityInstrumentationTestCase2<StartNavigation> {

    private StartNavigation mStartNavigation;

    public NavigationTest() {
        super(StartNavigation.class);
        mStartNavigation = new StartNavigation();
        testPreconditions();
        navigateToCacheTest();
        onLocationChangedTest();
        reachDestinationTest();
        //TODO Thinking it will already be stopped when we get here because we reach our
        //TODO Destination right above this.
        stopNavigationTest();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStartNavigation = getActivity();
    }

    public void navigateToCacheTest() {
        mStartNavigation.settings = mStartNavigation.getSharedPreferences("settings", mStartNavigation.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = mStartNavigation.settings.edit();
        e.putFloat("destLatitude", 10.10f);
        e.putFloat("destLongitude", 10.10f);
        e.commit();
        final Location location = new Location("testLocation");
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        mStartNavigation.changeLocation(location);
        TextView distanceDisplay = (TextView) mStartNavigation.findViewById(R.id.distance);
        assertNotSame("Distance to travel: 0.00", distanceDisplay.getText());
    }

    /**
     * Created by Michael Jones on 11/7/2014.
     * - Use Case Test:: Start Navigation
     * - Use Case Test:: Reach Destination
     * - Use Case Test:: Stop Navigation
     */

        private void testPreconditions() {
            assertNotNull("mStartNavigation is null", mStartNavigation);
        }

        private void onLocationChangedTest() {
            final Location location = new Location("testLocation");
            location.setLatitude(5.5);
            location.setLongitude(5.5);
            mStartNavigation.changeLocation(location);

            TextView currentCoords = (TextView) mStartNavigation.findViewById(R.id.currentCoordText);
            assertEquals("Latitude:5.5, Longitude:5.5", currentCoords.getText());
        }

        private void reachDestinationTest()
        {
            Cache ca = new Cache("cache", 10.10f, 10.10f);
            DatabaseHandler db = new DatabaseHandler(mStartNavigation);
            db.addCache(ca);

            final Location location = new Location("testLocation");
            location.setLatitude(10.10);
            location.setLongitude(10.10);
            mStartNavigation.changeLocation(location);

            TextView distanceDisplay = (TextView) mStartNavigation.findViewById(R.id.distance);
            assertTrue("Distance does not equal zero", distanceDisplay.getText().toString().contains(" 0 "));

            Cache ca2 = db.getCache(ca.getId());
            assertTrue("Cache not completed", ca2.getCompleted());
        }

        private void stopNavigationTest(){
            assertTrue("Not navigating", mStartNavigation.getNavigating()); //precondition
            final ImageButton StopNav = (ImageButton) mStartNavigation.findViewById(R.id.stop);
            StopNav.callOnClick();
            assertTrue("Still navigating after exiting", mStartNavigation.getNavigating());
        }
    }
}
