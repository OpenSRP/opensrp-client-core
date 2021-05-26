package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.AddressMock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class AddressTest extends BaseUnitTest {

    private AddressMock address;
    private final String regex = "field";
    private String field = regex;
    private String value = "value";
    private String mockString = "xd";
    private String latitude = "0";
    private String longitude = "1";

    @Before
    public void setUp() {
        address = new AddressMock("", new Date(), new Date(), new HashMap<String, String>(), "", "", "", "", "");
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(address);
        Assert.assertNotNull(new AddressMock());
    }

    public String getAddressType() {
        return address.getAddressType();
    }

    @Test
    public void assertsetAddressType() {
        String addressType = "";
        address.setAddressType(addressType);
        Assert.assertEquals(getAddressType(), addressType);
    }

    public Date getStartDate() {
        return address.getStartDate();
    }

    @Test
    public void assertsetStartDate() {
        Date startDate = new Date(0l);
        address.setStartDate(startDate);
        Assert.assertEquals(getStartDate(), startDate);
    }

    public Date getEndDate() {
        return address.getEndDate();
    }

    @Test
    public void assertsetEndDate() {
        Date endDate = new Date(0l);
        address.setEndDate(endDate);
        Assert.assertEquals(getEndDate(), endDate);
    }

    public Map<String, String> getAddressFields() {
        return address.getAddressFields();
    }

    public String getAddressField(String addressField) {
        return address.getAddressField(addressField);
    }

    @Test
    public void assertgetAddressFieldMatchingRegex() {
        Assert.assertNull(address.getAddressFieldMatchingRegex(regex));
        Map<String, String> addressFields = new HashMap<>();
        addressFields.put(field, value);
        address.setAddressFields(addressFields);
        Assert.assertNotNull(address.getAddressFieldMatchingRegex(regex));
    }

    @Test
    public void assertsetAddressFields() {
        Map<String, String> addressFields = new HashMap<>();
        address.setAddressFields(addressFields);
        Assert.assertEquals(getAddressFields(), addressFields);
    }

    @Test
    public void assertthataddAddressField() {
        address.setAddressFields(null);
        address.addAddressField(field, value);
        Assert.assertEquals(getAddressField(field), value);
    }

    @Test
    public void assertthatremoveAddressField() {
        address.addAddressField(field, value);
        address.removeAddressField(field);
        Assert.assertEquals(getAddressField(field), null);
    }

    public String getLatitude() {
        return address.getLatitude();
    }

    @Test
    public void assertsetLatitude() {

        address.setLatitude(latitude);
        Assert.assertEquals(getLatitude(), latitude);
    }

    public String getLongitude() {
        return address.getLongitude();
    }

    @Test
    public void assertsetLongitude() {

        address.setLongitude(longitude);
        Assert.assertEquals(getLongitude(), longitude);
    }

    public String getGeopoint() {
        return address.getGeopoint();
    }

    @Test
    public void assertsetGeopoint() {
        String geopoint = mockString;
        address.setGeopoint(geopoint);
        Assert.assertEquals(getGeopoint(), geopoint);
    }

    public String getPostalCode() {
        return address.getPostalCode();
    }

    @Test
    public void assertsetPostalCode() {
        String postalCode = mockString;
        address.setPostalCode(postalCode);
        Assert.assertEquals(getPostalCode(), postalCode);
    }

    public String getSubTown() {
        return address.getSubTown();
    }

    @Test
    public void assertsetSubTown() {
        String subTown = mockString;
        address.setSubTown(subTown);
        Assert.assertEquals(getSubTown(), subTown);
    }

    public String getTown() {
        return address.getTown();
    }

    @Test
    public void assertsetTown() {
        String town = mockString;
        address.setTown(town);
        Assert.assertEquals(getTown(), town);
    }

    public String getSubDistrict() {
        return address.getSubDistrict();
    }

    @Test
    public void assertsetSubDistrict() {
        String subDistrict = mockString;
        address.setSubDistrict(subDistrict);
        Assert.assertEquals(getSubDistrict(), subDistrict);
    }

    public String getCountyDistrict() {
        return address.getCountyDistrict();
    }

    @Test
    public void assertsetCountyDistrict() {
        String countyDistrict = mockString;
        address.setCountyDistrict(countyDistrict);
        Assert.assertEquals(getCountyDistrict(), countyDistrict);
    }

    public String getCityVillage() {
        return address.getCityVillage();
    }

    @Test
    public void assertsetCityVillage() {
        String cityVillage = mockString;
        address.setCityVillage(cityVillage);
        Assert.assertEquals(getCityVillage(), cityVillage);
    }

    public String getStateProvince() {
        return address.getStateProvince();
    }

    @Test
    public void assertsetStateProvince() {
        String stateProvince = mockString;
        address.setStateProvince(stateProvince);
        Assert.assertEquals(getStateProvince(), stateProvince);
    }

    public String getCountry() {
        return address.getCountry();
    }

    @Test
    public void assertsetCountry() {
        String country = mockString;
        address.setCountry(country);
        Assert.assertEquals(getCountry(), country);
    }

    @Test
    public void assertisActive() {
        Assert.assertEquals(address.isActive(), false);
        address.setEndDate(null);
        Assert.assertEquals(address.isActive(), true);
    }

    @Test
    public void assertdurationInDays() {
        Assert.assertEquals(address.durationInDays(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInDays(), -1);
        address.setStartDate(new Date(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInDays());
    }

    @Test
    public void assertdurationInWeeks() {
        Assert.assertEquals(address.durationInWeeks(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInWeeks(), -1);
        address.setStartDate(new Date(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInWeeks());
    }

    @Test
    public void assertdurationInMonths() {
        Assert.assertEquals(address.durationInMonths(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInMonths(), -1);
        address.setStartDate(new Date(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInMonths());
    }

    @Test
    public void assertdurationInYears() {
        Assert.assertEquals(address.durationInYears(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInYears(), -1);
        address.setStartDate(new Date(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInYears());
    }

    @Test
    public void assertwithAddressType() {
        String addressType = "";
        Assert.assertNotNull(address.withAddressType(addressType));
    }

    @Test
    public void assertwithStartDate() {
        Date startDate = new Date(0l);
        Assert.assertNotNull(address.withStartDate(startDate));
    }

    @Test
    public void assertwithEndDate() {
        Date endDate = new Date(0l);
        Assert.assertNotNull(address.withEndDate(endDate));
    }

    @Test
    public void assertwithAddressFields() {
        Map<String, String> addressFields = new HashMap<>();
        Assert.assertNotNull(address.withAddressFields(addressFields));
    }

    @Test
    public void assertthatwithAddressField() {

        Assert.assertNotNull(address.withAddressField(field, value));
        address.setAddressFields(null);
        Assert.assertNotNull(address.withAddressField(field, value));

    }

    @Test
    public void assertwithLatitude() {

        Assert.assertNotNull(address.withLatitude(latitude));
    }

    @Test
    public void assertwithLongitude() {

        Assert.assertNotNull(address.withLongitude(longitude));
    }

    @Test
    public void assertwithGeopoint() {
        String geopoint = mockString;
        Assert.assertNotNull(address.withGeopoint(geopoint));
    }

    @Test
    public void assertwithPostalCode() {
        String postalCode = mockString;
        Assert.assertNotNull(address.withPostalCode(postalCode));
    }

    @Test
    public void assertwithTown() {
        String town = mockString;
        Assert.assertNotNull(address.withTown(town));
    }

    @Test
    public void assertwithSubDistrict() {
        String subDistrict = mockString;
        Assert.assertNotNull(address.withSubDistrict(subDistrict));
    }

    @Test
    public void assertwithCountyDistrict() {
        String countyDistrict = mockString;
        Assert.assertNotNull(address.withCountyDistrict(countyDistrict));
    }

    @Test
    public void assertwithCityVillage() {
        String cityVillage = mockString;
        Assert.assertNotNull(address.withCityVillage(cityVillage));
    }

    @Test
    public void assertwithStateProvince() {
        String stateProvince = mockString;
        Assert.assertNotNull(address.withStateProvince(stateProvince));
    }

    @Test
    public void assertwithCountry() {
        String country = mockString;
        Assert.assertNotNull(address.withCountry(country));
    }

    @Test
    public void assertgetAddressField() {
        Assert.assertEquals(address.getAddressField(AddressField.CITY), null);
    }

    @Test
    public void assertassertaddAddressField() {
        address.setAddressFields(null);
        address.addAddressField(AddressField.CITY, mockString);
        Assert.assertEquals(address.getAddressField(AddressField.CITY), mockString);
    }

    @Test
    public void assertassertremoveAddressField() {
        address.setAddressFields(null);
        address.addAddressField(AddressField.CITY, mockString);
        Assert.assertEquals(address.getAddressField(AddressField.CITY), mockString);
        address.removeAddressField(AddressField.CITY);
        Assert.assertEquals(address.getAddressField(AddressField.CITY), null);
    }

    @Test
    public void assertwithAddressField() {
        address.setAddressFields(null);
        Assert.assertNotNull(address.withAddressField(AddressField.CITY, value));
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(address.toString());
    }

}
