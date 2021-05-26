package org.smartregister.view.contract;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

import java.util.Calendar;

public class ECClientTest extends BaseUnitTest {

    @Test
    public void shouldReturnTrueForIsBPLWhenEconomicStatusIsBPL() throws Exception {
        ECClient client = getClient().withEconomicStatus("BPL");

        Assert.assertTrue(client.isBPL());
    }

    @Test
    public void shouldReturnFalseForIsBPLWhenEconomicStatusIsAPLOrNothing() throws Exception {
        ECClient client = getClient().withEconomicStatus("APL");
        Assert.assertFalse(client.isBPL());

        client = getClient();
        Assert.assertFalse(client.isBPL());
    }

    @Test
    public void shouldReturnTrueWhenClientContainsCasteAsSC() throws Exception {
        ECClient SCClient = getClient().withCaste("SC");

        Assert.assertTrue(SCClient.isSC());
        Assert.assertFalse(SCClient.isST());
    }

    @Test
    public void shouldReturnTrueWhenClientContainsCasteAsST() throws Exception {
        ECClient STClient = getClient().withCaste("ST");

        Assert.assertFalse(STClient.isSC());
        Assert.assertTrue(STClient.isST());
    }

    @Test
    public void shouldReturn34YearsWhenDOBIs4_4_1980() {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 4, 4);
        DateTimeUtils.setCurrentMillisFixed(cal.getTimeInMillis());

        final int age = getClient().withDateOfBirth(new LocalDate(1980, 4, 4).toString()).age();

        Assert.assertEquals(34, age);
    }

    @Test
    public void shouldReturn0YearsWhenDOBIs4_4_2014() {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 4, 4);
        DateTimeUtils.setCurrentMillisFixed(cal.getTimeInMillis());

        int age = getClient().withDateOfBirth(new LocalDate(2014, 4, 4).toString()).age();

        Assert.assertEquals(0, age);
    }

    @Test
    public void ShouldReturn1YearsWhenDOBIs18_4_2013() {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 4, 18);
        DateTimeUtils.setCurrentMillisFixed(cal.getTimeInMillis());

        final int age = getClient().withDateOfBirth(new LocalDate(2013, 4, 18).toString()).age();

        Assert.assertEquals(1, age);
    }

    @Test
    public void ShouldReturnUpperCaseIUDPerson() {
        String iudPerson = getClient().withIUDPerson("iudperson").iudPerson();

        Assert.assertEquals(iudPerson, "IUDPERSON");
    }

    @Test
    public void ShouldReturnUpperCaseIUDPlace() {
        String iudPerson = getClient().withIUDPerson("iudplace").iudPerson();

        Assert.assertEquals(iudPerson, "IUDPLACE");
    }

    @Test
    public void shouldSatisfyFilterForNameStartingWithSameCharacters() {
        boolean filterMatches = getClient().satisfiesFilter("Dr");

        Assert.assertFalse(filterMatches);
    }

    @Test
    public void shouldSatisfyFilterForECNumberStartingWithSameCharacters() {
        boolean filterMatches = getClient().satisfiesFilter("12");

        Assert.assertTrue(filterMatches);
    }

    @Test
    public void shouldNotSatisfyFilterForNameNotStartingWithSameCharacters() {
        boolean filterMatches = getClient().satisfiesFilter("shti");

        Assert.assertFalse(filterMatches);
    }

    @Test
    public void shouldSatisfyFilterForBlankName() {
        boolean filterMatches = getClient().satisfiesFilter("");

        Assert.assertTrue(filterMatches);
    }

    @Test
    public void shouldNotSatisfyFilterForECNumberNotStartingWithSameCharacters() {
        boolean filterMatches = getClient().satisfiesFilter("23");

        Assert.assertFalse(filterMatches);
    }

    @Test
    public void shouldSatisfyFilterForBlankECNumber() {
        boolean filterMatches = getClient().satisfiesFilter("");

        Assert.assertTrue(filterMatches);
    }

    private ECClient getClient() {
        return new ECClient("abcd", "opensrp", "husband1", "village1", 123);
    }
}
