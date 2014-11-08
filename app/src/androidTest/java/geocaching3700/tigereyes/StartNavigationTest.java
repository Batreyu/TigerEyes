package geocaching3700.tigereyes;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Created by Kyle on 11/7/2014.
 */
public class StartNavigationTest extends ActivityInstrumentationTestCase2<StartNavigation> {

    private StartNavigation mStartNavigation;

    public StartNavigationTest() {
        super(StartNavigation.class);
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
        TextView distanceDisplay = (TextView) mStartNavigation.findViewById(R.id.distance);
        assertNotSame("Distance to travel: 0.00", distanceDisplay.getText());
    }
}
