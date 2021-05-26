package org.smartregister.domain.db;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.mock.AddressMock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class AddressTest extends BaseUnitTest {

    AddressMock address;

    @Before
    public void setUp() {
        address = new AddressMock("", new DateTime(), new DateTime(), new HashMap<String, String>(), "", "", "", "", "");
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(address);
    }


    public String getAddressType() {
        return address.getAddressType();
    }

    @Test
    public void setAddressType() {
        String addressType = "";
        address.setAddressType(addressType);
        Assert.assertEquals(getAddressType(), addressType);
    }


    public Date getStartDate() {
        return address.getStartDate().toDate();
    }

    @Test
    public void setStartDate() {
        Date startDate = new Date(0l);
        address.setStartDate(new DateTime(startDate.getTime()));
        Assert.assertEquals(getStartDate(), startDate);
    }


    public Date getEndDate() {
        return address.getEndDate().toDate();
    }

    @Test
    public void setEndDate() {
        Date endDate = new Date(0l);
        address.setEndDate(new DateTime(endDate.getTime()));
        Assert.assertEquals(getEndDate(), endDate);
    }


    public Map<String, String> getAddressFields() {
        return address.getAddressFields();
    }


    public String getAddressField(String addressField) {
        return address.getAddressField(addressField);
    }

    @Test
    public void getAddressFieldMatchingRegex() {
        String regex = "field";
        Assert.assertNull(address.getAddressFieldMatchingRegex(regex));
        Map<String, String> addressFields = new HashMap<>();
        addressFields.put("field", "value");
        address.setAddressFields(addressFields);
        Assert.assertNotNull(address.getAddressFieldMatchingRegex(regex));
    }

    @Test
    public void setAddressFields() {
        Map<String, String> addressFields = new HashMap<>();
        address.setAddressFields(addressFields);
        Assert.assertEquals(getAddressFields(), addressFields);
    }

    @Test
    public void addAddressField() {
        String field = "field";
        String value = "value";
        address.setAddressFields(null);
        address.addAddressField(field, value);
        Assert.assertEquals(getAddressField(field), value);
    }

    @Test
    public void removeAddressField() {
        String field = "field";
        String value = "value";
        address.addAddressField(field, value);
        address.removeAddressField(field);
        Assert.assertEquals(getAddressField(field), null);
    }


    public String getLatitude() {
        return address.getLatitude();
    }

    @Test
    public void setLatitude() {
        String latitude = "0";
        address.setLatitude(latitude);
        Assert.assertEquals(getLatitude(), latitude);
    }


    public String getLongitude() {
        return address.getLongitude();
    }

    @Test
    public void setLongitude() {
        String longitude = "0";
        address.setLongitude(longitude);
        Assert.assertEquals(getLongitude(), longitude);
    }


    public String getGeopoint() {
        return address.getGeopoint();
    }

    @Test
    public void setGeopoint() {
        String geopoint = "xd";
        address.setGeopoint(geopoint);
        Assert.assertEquals(getGeopoint(), geopoint);
    }


    public String getPostalCode() {
        return address.getPostalCode();
    }

    @Test
    public void setPostalCode() {
        String postalCode = "xxxx";
        address.setPostalCode(postalCode);
        Assert.assertEquals(getPostalCode(), postalCode);
    }


    public String getSubTown() {
        return address.getSubTown();
    }

    @Test
    public void setSubTown() {
        String subTown = "xd";
        address.setSubTown(subTown);
        Assert.assertEquals(getSubTown(), subTown);
    }


    public String getTown() {
        return address.getTown();
    }

    @Test
    public void setTown() {
        String town = "xd";
        address.setTown(town);
        Assert.assertEquals(getTown(), town);
    }


    public String getSubDistrict() {
        return address.getSubDistrict();
    }

    @Test
    public void setSubDistrict() {
        String subDistrict = "xd";
        address.setSubDistrict(subDistrict);
        Assert.assertEquals(getSubDistrict(), subDistrict);
    }


    public String getCountyDistrict() {
        return address.getCountyDistrict();
    }

    @Test
    public void setCountyDistrict() {
        String countyDistrict = "xd";
        address.setCountyDistrict(countyDistrict);
        Assert.assertEquals(getCountyDistrict(), countyDistrict);
    }


    public String getCityVillage() {
        return address.getCityVillage();
    }

    @Test
    public void setCityVillage() {
        String cityVillage = "xd";
        address.setCityVillage(cityVillage);
        Assert.assertEquals(getCityVillage(), cityVillage);
    }


    public String getStateProvince() {
        return address.getStateProvince();
    }

    @Test
    public void setStateProvince() {
        String stateProvince = "xd";
        address.setStateProvince(stateProvince);
        Assert.assertEquals(getStateProvince(), stateProvince);
    }


    public String getCountry() {
        return address.getCountry();
    }

    @Test
    public void setCountry() {
        String country = "xd";
        address.setCountry(country);
        Assert.assertEquals(getCountry(), country);
    }

    @Test
    public void isActive() {
        Assert.assertEquals(address.isActive(), false);
        address.setEndDate(null);
        Assert.assertEquals(address.isActive(), true);
    }

    @Test
    public void durationInDays() {
        Assert.assertEquals(address.durationInDays(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInDays(), -1);
        address.setStartDate(new DateTime(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInDays());
    }

    @Test
    public void durationInWeeks() {
        Assert.assertEquals(address.durationInWeeks(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInWeeks(), -1);
        address.setStartDate(new DateTime(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInWeeks());
    }

    @Test
    public void durationInMonths() {
        Assert.assertEquals(address.durationInMonths(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInMonths(), -1);
        address.setStartDate(new DateTime(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInMonths());
    }

    @Test
    public void durationInYears() {
        Assert.assertEquals(address.durationInYears(), 0);
        address.setStartDate(null);
        Assert.assertEquals(address.durationInYears(), -1);
        address.setStartDate(new DateTime(0l));
        address.setEndDate(null);
        Assert.assertNotNull(address.durationInYears());
    }

    @Test
    public void withAddressType() {
        String addressType = "";
        Assert.assertNotNull(address.withAddressType(addressType));
    }

    @Test
    public void withStartDate() {
        DateTime startDate = new DateTime(0l);
        Assert.assertNotNull(address.withStartDate(startDate));
    }

    @Test
    public void withEndDate() {
        DateTime endDate = new DateTime(0l);
        Assert.assertNotNull(address.withEndDate(endDate));
    }

    @Test
    public void withAddressFields() {
        Map<String, String> addressFields = new HashMap<>();
        Assert.assertNotNull(address.withAddressFields(addressFields));
    }

    @Test
    public void withAddressField() {
        String field = "field";
        String value = "value";
        Assert.assertNotNull(address.withAddressField(field, value));
        address.setAddressFields(null);
        Assert.assertNotNull(address.withAddressField(field, value));

    }

    @Test
    public void withLatitude() {
        String latitude = "0";
        Assert.assertNotNull(address.withLatitude(latitude));
    }

    @Test
    public void withLongitude() {
        String longitude = "0";
        Assert.assertNotNull(address.withLongitude(longitude));
    }

    @Test
    public void withGeopoint() {
        String geopoint = "xd";
        Assert.assertNotNull(address.withGeopoint(geopoint));
    }

    @Test
    public void withPostalCode() {
        String postalCode = "xxxx";
        Assert.assertNotNull(address.withPostalCode(postalCode));
    }

    @Test
    public void withTown() {
        String town = "xd";
        Assert.assertNotNull(address.withTown(town));
    }

    @Test
    public void withSubDistrict() {
        String subDistrict = "xd";
        Assert.assertNotNull(address.withSubDistrict(subDistrict));
    }

    @Test
    public void withCountyDistrict() {
        String countyDistrict = "xd";
        Assert.assertNotNull(address.withCountyDistrict(countyDistrict));
    }

    @Test
    public void withCityVillage() {
        String cityVillage = "xd";
        Assert.assertNotNull(address.withCityVillage(cityVillage));
    }

    @Test
    public void withStateProvince() {
        String stateProvince = "xd";
        Assert.assertNotNull(address.withStateProvince(stateProvince));
    }

    @Test
    public void withCountry() {
        String country = "xd";
        Assert.assertNotNull(address.withCountry(country));
    }
}
