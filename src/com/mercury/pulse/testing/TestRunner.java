package com.mercury.pulse.testing;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

public class TestRunner extends InstrumentationTestRunner {
	
    public TestSuite getAllTests(){
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);

        suite.addTestSuite(LoginActivityUnitTest.class);
        //suite.addTestSuite(MainActivityUnitTest.class);
        return suite;
    }

    public ClassLoader getLoader() {
        return TestRunner.class.getClassLoader();
    }

}
