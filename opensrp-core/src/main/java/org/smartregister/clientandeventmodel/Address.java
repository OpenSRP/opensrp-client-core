package org.smartregister.clientandeventmodel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Address {

    @JsonProperty
    private Boolean preferred;
    @JsonProperty
    private String addressType;
    @JsonProperty
    private Date startDate;
    @JsonProperty
    private Date endDate;
    @JsonProperty
    private Map<String, String> addressFields;
    @JsonProperty
    private String latitude;
    @JsonProperty
    private String longitude;
    @JsonProperty
    private String geopoint;
    @JsonProperty
    private String postalCode;
    @JsonProperty
    private String subTown;
    @JsonProperty
    private String town;
    @JsonProperty
    private String subDistrict;
    @JsonProperty
    private String countyDistrict;
    @JsonProperty
    private String cityVillage;
    @JsonProperty
    private String stateProvince;
    @JsonProperty
    private String country;

    public Address() {
    }

    public Address(String addressType, Date startDate, Date endDate, Map<String, String>
            addressFields, String latitude, String longitude, String postalCode, String
                           stateProvince, String country) {
        this.addressType = addressType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.addressFields = addressFields;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.stateProvince = stateProvince;
        this.country = country;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<String, String> getAddressFields() {
        return addressFields;
    }

    /**
     * WARNING: Overrides all existing fields
     *
     * @param addressFields
     * @return
     */
    public void setAddressFields(Map<String, String> addressFields) {
        this.addressFields = addressFields;
    }

    public String getAddressField(String addressField) {
        return addressFields.get(addressField);
    }

    public String getAddressField(AddressField addressField) {
        return addressFields.get(addressField.name());
    }

    /**
     * Returns field matching the regex. Note that incase of multiple fields matching criteria
     * function would return first match. The must be well formed to find out a single value
     *
     * @param regex
     * @return
     */
    public String getAddressFieldMatchingRegex(String regex) {
        for (Entry<String, String> a : addressFields.entrySet()) {
            if (a.getKey().matches(regex)) {
                return a.getValue();
            }
        }
        return null;
    }

    public void addAddressField(String field, String value) {
        if (addressFields == null) {
            addressFields = new HashMap<>();
        }
        addressFields.put(field, value);
    }

    /**
     * Add field name from a list of predefined options from enum {@link AddressField}
     *
     * @param field
     * @param value
     */
    public void addAddressField(AddressField field, String value) {
        if (addressFields == null) {
            addressFields = new HashMap<>();
        }
        addressFields.put(field.name(), value);
    }

    public void removeAddressField(AddressField field) {
        addressFields.remove(field.name());
    }

    public void removeAddressField(String field) {
        addressFields.remove(field);
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(String geopoint) {
        this.geopoint = geopoint;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSubTown() {
        return subTown;
    }

    public void setSubTown(String subTown) {
        this.subTown = subTown;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * True if endDate is null or endDate is in future
     *
     * @return
     */
    @JsonIgnore
    public boolean isActive() {
        return endDate == null || endDate.after(new Date());
    }

    /**
     * If startDate is not specified returns -1. If endDate is not specified duration is from
     * startDate to current date
     *
     * @return
     */
    private long durationInMillis() {
        if (startDate == null) {
            return -1;
        }
        if (endDate == null) {
            return new Date().getTime() - startDate.getTime();
        }

        return endDate.getTime() - startDate.getTime();
    }

    /**
     * If startDate is not specified returns -1. If endDate is not specified duration is from
     * startDate to current date
     *
     * @return
     */
    public int durationInDays() {
        return (int) (durationInMillis() == -1 ? durationInMillis()
                : (durationInMillis() / (1000 * 60 * 60 * 24)));
    }

    /**
     * If startDate is not specified returns -1. If endDate is not specified duration is from
     * startDate to current date
     *
     * @return
     */
    public int durationInWeeks() {
        return durationInDays() == -1 ? durationInDays() : (durationInDays() / 7);
    }

    /**
     * If startDate is not specified returns -1. If endDate is not specified duration is from
     * startDate to current date
     *
     * @return
     */
    public int durationInMonths() {
        return durationInDays() == -1 ? durationInDays() : durationInDays() / 30;
    }

    /**
     * If startDate is not specified returns -1. If endDate is not specified duration is from
     * startDate to current date
     *
     * @return
     */
    public int durationInYears() {
        return durationInDays() == -1 ? durationInDays() : (durationInDays() / 365);
    }

    /**
     * The type address represents
     *
     * @param addressType
     * @return
     */
    public Address withAddressType(String addressType) {
        this.addressType = addressType;
        return this;
    }

    /**
     * The date when address was started or owned
     *
     * @param endDate
     * @return
     */
    public Address withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * The date when address was outdated or abandoned
     *
     * @param endDate
     * @return
     */
    public Address withEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * WARNING: Overrides all existing fields
     *
     * @param addressFields
     * @return
     */
    public Address withAddressFields(Map<String, String> addressFields) {
        this.addressFields = addressFields;
        return this;
    }

    public Address withAddressField(String field, String value) {
        if (addressFields == null) {
            addressFields = new HashMap<>();
        }
        addressFields.put(field, value);
        return this;
    }

    public Address withAddressField(AddressField field, String value) {
        if (addressFields == null) {
            addressFields = new HashMap<>();
        }
        addressFields.put(field.name(), value);
        return this;
    }

    public Address withLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public Address withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public Address withGeopoint(String geopoint) {
        this.geopoint = geopoint;
        return this;
    }

    public Address withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public Address withTown(String town) {
        this.town = town;
        return this;
    }

    public Address withSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
        return this;
    }

    public Address withCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
        return this;
    }

    public Address withCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
        return this;
    }

    public Address withStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
        return this;
    }

    public Address withCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

