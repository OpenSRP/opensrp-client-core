package org.smartregister.clientandeventmodel.mock;

import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.AddressField;

import java.util.Date;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */
public class AddressMock extends Address {
    public AddressMock() {
        super();
    }

    public AddressMock(String addressType, Date startDate, Date endDate, Map<String, String> addressFields, String latitude, String longitude, String postalCode, String stateProvince, String country) {
        super(addressType, startDate, endDate, addressFields, latitude, longitude, postalCode, stateProvince, country);
    }

    @Override
    public String getAddressType() {
        return super.getAddressType();
    }

    @Override
    public void setAddressType(String addressType) {
        super.setAddressType(addressType);
    }

    @Override
    public Date getStartDate() {
        return super.getStartDate();
    }

    @Override
    public void setStartDate(Date startDate) {
        super.setStartDate(startDate);
    }

    @Override
    public Date getEndDate() {
        return super.getEndDate();
    }

    @Override
    public void setEndDate(Date endDate) {
        super.setEndDate(endDate);
    }

    @Override
    public Map<String, String> getAddressFields() {
        return super.getAddressFields();
    }

    @Override
    public String getAddressField(String addressField) {
        return super.getAddressField(addressField);
    }

    @Override
    public String getAddressFieldMatchingRegex(String regex) {
        return super.getAddressFieldMatchingRegex(regex);
    }

    @Override
    public void setAddressFields(Map<String, String> addressFields) {
        super.setAddressFields(addressFields);
    }

    @Override
    public void addAddressField(String field, String value) {
        super.addAddressField(field, value);
    }

    @Override
    public void removeAddressField(String field) {
        super.removeAddressField(field);
    }

    @Override
    public String getLatitude() {
        return super.getLatitude();
    }

    @Override
    public void setLatitude(String latitude) {
        super.setLatitude(latitude);
    }

    @Override
    public String getLongitude() {
        return super.getLongitude();
    }

    @Override
    public void setLongitude(String longitude) {
        super.setLongitude(longitude);
    }

    @Override
    public String getGeopoint() {
        return super.getGeopoint();
    }

    @Override
    public void setGeopoint(String geopoint) {
        super.setGeopoint(geopoint);
    }

    @Override
    public String getPostalCode() {
        return super.getPostalCode();
    }

    @Override
    public void setPostalCode(String postalCode) {
        super.setPostalCode(postalCode);
    }

    @Override
    public String getSubTown() {
        return super.getSubTown();
    }

    @Override
    public void setSubTown(String subTown) {
        super.setSubTown(subTown);
    }

    @Override
    public String getTown() {
        return super.getTown();
    }

    @Override
    public void setTown(String town) {
        super.setTown(town);
    }

    @Override
    public String getSubDistrict() {
        return super.getSubDistrict();
    }

    @Override
    public void setSubDistrict(String subDistrict) {
        super.setSubDistrict(subDistrict);
    }

    @Override
    public String getCountyDistrict() {
        return super.getCountyDistrict();
    }

    @Override
    public void setCountyDistrict(String countyDistrict) {
        super.setCountyDistrict(countyDistrict);
    }

    @Override
    public String getCityVillage() {
        return super.getCityVillage();
    }

    @Override
    public void setCityVillage(String cityVillage) {
        super.setCityVillage(cityVillage);
    }

    @Override
    public String getStateProvince() {
        return super.getStateProvince();
    }

    @Override
    public void setStateProvince(String stateProvince) {
        super.setStateProvince(stateProvince);
    }

    @Override
    public String getCountry() {
        return super.getCountry();
    }

    @Override
    public void setCountry(String country) {
        super.setCountry(country);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public int durationInDays() {
        return super.durationInDays();
    }

    @Override
    public int durationInWeeks() {
        return super.durationInWeeks();
    }

    @Override
    public int durationInMonths() {
        return super.durationInMonths();
    }

    @Override
    public int durationInYears() {
        return super.durationInYears();
    }

    @Override
    public Address withAddressType(String addressType) {
        return super.withAddressType(addressType);
    }

    @Override
    public Address withStartDate(Date startDate) {
        return super.withStartDate(startDate);
    }

    @Override
    public Address withEndDate(Date endDate) {
        return super.withEndDate(endDate);
    }

    @Override
    public Address withAddressFields(Map<String, String> addressFields) {
        return super.withAddressFields(addressFields);
    }

    @Override
    public Address withAddressField(String field, String value) {
        return super.withAddressField(field, value);
    }

    @Override
    public Address withLatitude(String latitude) {
        return super.withLatitude(latitude);
    }

    @Override
    public Address withLongitude(String longitude) {
        return super.withLongitude(longitude);
    }

    @Override
    public Address withGeopoint(String geopoint) {
        return super.withGeopoint(geopoint);
    }

    @Override
    public Address withPostalCode(String postalCode) {
        return super.withPostalCode(postalCode);
    }

    @Override
    public Address withTown(String town) {
        return super.withTown(town);
    }

    @Override
    public Address withSubDistrict(String subDistrict) {
        return super.withSubDistrict(subDistrict);
    }

    @Override
    public Address withCountyDistrict(String countyDistrict) {
        return super.withCountyDistrict(countyDistrict);
    }

    @Override
    public Address withCityVillage(String cityVillage) {
        return super.withCityVillage(cityVillage);
    }

    @Override
    public Address withStateProvince(String stateProvince) {
        return super.withStateProvince(stateProvince);
    }

    @Override
    public Address withCountry(String country) {
        return super.withCountry(country);
    }

    @Override
    public String getAddressField(AddressField addressField) {
        return super.getAddressField(addressField);
    }

    @Override
    public void addAddressField(AddressField field, String value) {
        super.addAddressField(field, value);
    }

    @Override
    public void removeAddressField(AddressField field) {
        super.removeAddressField(field);
    }

    @Override
    public Address withAddressField(AddressField field, String value) {
        return super.withAddressField(field, value);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
