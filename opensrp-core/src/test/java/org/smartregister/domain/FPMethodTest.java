package org.smartregister.domain;

import org.junit.Test;
import org.smartregister.BaseUnitTest;

import static junit.framework.Assert.assertEquals;

public class FPMethodTest extends BaseUnitTest {

    @Test
    public void shouldParseStringToFPMethod() throws Exception {
        assertEquals(FPMethod.CONDOM, FPMethod.tryParse("condom", FPMethod.NONE));
        assertEquals(FPMethod.DMPA_INJECTABLE, FPMethod.tryParse("dmpa_injectable", FPMethod.NONE));
        assertEquals(FPMethod.ECP, FPMethod.tryParse("ecp", FPMethod.NONE));
        assertEquals(FPMethod.FEMALE_STERILIZATION, FPMethod.tryParse("female_sterilization", FPMethod.NONE));
        assertEquals(FPMethod.IUD, FPMethod.tryParse("iud", FPMethod.NONE));
        assertEquals(FPMethod.LAM, FPMethod.tryParse("lam", FPMethod.NONE));
        assertEquals(FPMethod.MALE_STERILIZATION, FPMethod.tryParse("male_sterilization", FPMethod.NONE));
        assertEquals(FPMethod.NONE, FPMethod.tryParse("none", FPMethod.NONE));
        assertEquals(FPMethod.OCP, FPMethod.tryParse("ocp", FPMethod.NONE));
        assertEquals(FPMethod.TRADITIONAL_METHODS, FPMethod.tryParse("traditional_methods", FPMethod.NONE));
    }

    @Test
    public void shouldParseBlankStringAsDefaultFPMethod() throws Exception {
        FPMethod defaultMethod = FPMethod.NONE;
        assertEquals(defaultMethod, FPMethod.tryParse("", defaultMethod));
    }

    @Test
    public void shouldParseInvalidStringAsDefaultFPMethod() throws Exception {
        FPMethod defaultMethod = FPMethod.CONDOM;
        assertEquals(defaultMethod, FPMethod.tryParse("---", defaultMethod));
    }
}
