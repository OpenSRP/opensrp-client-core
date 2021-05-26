package org.smartregister.clientandeventmodel;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Mappings in OpenSRP for OpenMRS entities and properties
 */
public class FormEntityConstants {

    public static final SimpleDateFormat FORM_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public enum FieldType {
        concept, person, person_address, person_attribute, person_identifier,
    }

    public enum FieldDataType {

    }

    public enum Person implements FormEntity {
        first_name, middle_name, last_name, gender, birthdate, birthdate_estimated, dead,
        deathdate, deathdate_estimated, client_type;

        public String entity() {
            return "person";
        }

        public String entityId() {
            return this.name();
        }
    }

    public enum PersonAddress implements FormEntity {
        ;

        public String entity() {
            return "person_address";
        }

        public String entityId() {
            return this.name();
        }
    }

    public enum Encounter implements FormEntity {
        encounter_date, location_id, encounter_start, encounter_end, team, teamId;

        public String entity() {
            return "encounter";
        }

        public String entityId() {
            return this.name();
        }
    }

    public interface FormEntity {
        String entity();

        String entityId();
    }

    public enum Address {
        start_date, startDate, end_date, endDate, latitude, longitute, geopoint, postal_code,
        postalCode, sub_town, subTown, town, sub_district, subDistrict, district, county,
        county_district, countyDistrict, city, village, cityVillage, city_village, state,
        state_province, stateProvince, country
    }
}

