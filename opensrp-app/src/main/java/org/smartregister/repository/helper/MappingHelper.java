package org.smartregister.repository.helper;

import android.location.Location;
import androidx.annotation.NonNull;

import org.smartregister.domain.Geometry;

/**
 * Created by samuelgithengi on 3/19/19.
 */
public interface MappingHelper {

    /**
     * Generates the center from the {@link Geometry} of  MultiPolygon, Polygon and @MultiPoint
     *
     * @param featureGeometry the geometry of structure
     * @return Location of the center
     */
    Location getCenter(@NonNull String featureGeometry);
}
