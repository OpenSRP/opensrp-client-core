package org.smartregister.dao;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;

import java.util.List;
import java.util.Set;

import static org.smartregister.AllConstants.LocationConstants.LOCATION_NAME;
import static org.smartregister.AllConstants.LocationConstants.PARENT_ID;
import static org.smartregister.AllConstants.LocationConstants.UUID;

public class LocationsDao extends AbstractDao {

    /**
     * Return a list of locations matching the specified tags
     *
     * @param tags location tags
     * @return list of locations
     */
    public static List<Location> getLocationsByTags(Set<String> tags) {
        String sql = String.format("SELECT uuid, location.name as location_name, parent_id\n" +
                "FROM location\n" +
                "         INNER JOIN location_tag on location.uuid = location_tag.location_id\n" +
                "WHERE location_tag.name IN ('%s');", StringUtils.join(tags, "', '"));

        DataMap<Location> dataMap = cursor -> {
            Location location = new Location();
            location.setId(getCursorValue(cursor, UUID));
            LocationProperty property = new LocationProperty();
            property.setUid(getCursorValue(cursor, UUID));
            property.setParentId(getCursorValue(cursor, PARENT_ID));
            property.setName(getCursorValue(cursor, LOCATION_NAME));
            location.setProperties(property);
            return location;
        };

        return AbstractDao.readData(sql, dataMap);
    }
}
