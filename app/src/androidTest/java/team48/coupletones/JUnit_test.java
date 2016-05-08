package team48.coupletones;

import android.test.ActivityInstrumentationTestCase2;
import team48.coupletones.MainActivity;
/**
 * Created by Zhuyupei on 5/7/16.
 */
public class JUnit_test extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;
    public JUnit_test(){
        super(MainActivity.class);
    }


    public void test_first(){
        mainActivity = getActivity();
        assertEquals(20+233, 20+233);
    }
}
