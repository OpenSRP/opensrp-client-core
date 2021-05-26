package org.smartregister.setup;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
//import org.robolectric.bytecode.Setup;

public class DrishtiTestRunner extends RobolectricTestRunner {

    public DrishtiTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

//    @Override
//    public Setup createSetup() {
//        return new DrishtiTestSetup();
//    }
}
