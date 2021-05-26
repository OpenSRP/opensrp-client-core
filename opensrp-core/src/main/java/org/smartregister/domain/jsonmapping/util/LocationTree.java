package org.smartregister.domain.jsonmapping.util;

import org.smartregister.domain.jsonmapping.Location;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * {@link LocationTree} is an specification of {@link Tree} which provides helper methods for creating a Tree for
 * {@link Location}. The key is the locationId and label is name of {@link Location} and the data for tree node
 * is teamLocation object itself. Each node has locationId if its parent teamLocation and has a {@link TreeNode} map of
 * children of the teamLocation. This way it allows to create a complete tree of teamLocation starting from root nodes to
 * the end or lowest level in the form of tree. A root node is one with parent teamLocation null.
 */
public class LocationTree {

    private Tree<String, Location> locationsHierarchy;

    public LocationTree() {
        this.locationsHierarchy = new Tree<String, Location>();
    }

    public void addLocation(Location l) {
        if (!locationsHierarchy.hasNode(l.getLocationId())) {
            if (l.getParentLocation() == null) {
                locationsHierarchy.addNode(l.getLocationId(), l.getName(), l, null);
            } else {
                locationsHierarchy.addNode(l.getLocationId(), l.getName(), l, l.getParentLocation().getLocationId());
            }
        }
    }

    /**
     * WARNING: Overrides existing locations
     *
     * @param locations
     */
    public void buildTreeFromList(List<Location> locations) {
        for (Location location : locations) {
            addLocation(location);
        }
    }

    public Location findLocation(String locationId) {
        return locationsHierarchy.getNode(locationId).getNode();
    }

    public boolean hasLocation(String locationId) {
        return locationsHierarchy.hasNode(locationId);
    }

    public boolean hasChildLocation(String locationId, String childLocationId) {
        return locationsHierarchy.getNode(locationId).findChild(childLocationId) != null;
    }

    public LinkedHashMap<String, TreeNode<String, Location>> getLocationsHierarchy() {
        return locationsHierarchy.getTree();
    }

    public LinkedHashMap<String, LinkedHashSet<String>> getChildParent() {
        return locationsHierarchy.getChildParent();
    }

    public void deleteLocation(String locationId) {
        locationsHierarchy.deleteNode(locationId);
    }
}