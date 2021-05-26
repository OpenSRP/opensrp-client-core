package org.smartregister.util;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;

/**
 * Created by Dimas Ciputra on 3/23/15.
 */
@Config(manifest = Config.NONE)
public class LocationTreeTest extends BaseUnitTest {
    private LocationTree locationTree;
    private String locationJSONString;

    @Before
    public void setUp() throws Exception {
        locationJSONString = "{\"locationsHierarchy\":{\"map\" : {\"765cb701-9e61-4ead-afb9-a63c943f4f14\":{\"id\":\"765cb701-9e61-4ead-afb9-a63c943f4f14\", \"label\":\"testloc4\", \"node\":{\"creator\":null, \"dateCreated\":null, \"editor\":null, \"dateEdited\":null, \"voided\":false, \"dateVoided\":null, \"voider\":null, \"voidReason\":null, \"locationId\":\"765cb701-9e61-4ead-afb9-a63c943f4f14\", \"name\":\"testloc4\", \"address\":null, \"identifiers\":null, \"parentLocation\":null, \"tags\":null, \"attributes\":null}, \"children\":null, \"parent\":null}}}}";
    }

    @Test
    public void shouldShowLocationTree() throws Exception {
        locationTree = AssetHandler.jsonStringToJava(locationJSONString, LocationTree.class);
        Assert.assertNotNull(locationTree);
        System.out.println("shouldShowLocationTree " + new Gson().toJson(locationTree));
    }

    @Test
    public void shouldFoundALocation() throws Exception {
        locationTree = AssetHandler.jsonStringToJava(locationJSONString, LocationTree.class);
        Location l = locationTree.findLocation("765cb701-9e61-4ead-afb9-a63c943f4f14");
        Assert.assertNotNull(l);
        Assert.assertEquals(l.getName(), "testloc4");
        System.out.println("shouldFoundALocation " + new Gson().toJson(l));
    }

    @After
    public void validate() {
        Mockito.validateMockitoUsage();
    }

}
