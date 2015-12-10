package com.pku.lesshst.weathershow;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

//import de.vogella.android.test.target.SimpleActivity;
//import de.vogella.android.test.target.SimpleListActivity;
//
//import static org.junit.Assert.*;

/**
 * Created by lesshst on 2015/11/30.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNavigateToHomeScreen() throws Exception {
        //choose environment
        solo.waitForDialogToOpen();
        solo.clickOnText("qa");
        solo.clickOnButton("OK");

        //assert home screen finished loading.
        assertTrue(solo.waitForText("Diapering"));
    }

}