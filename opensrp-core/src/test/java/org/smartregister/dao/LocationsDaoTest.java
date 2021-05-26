package org.smartregister.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Location;
import org.smartregister.repository.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationsDaoTest extends BaseUnitTest {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void testJoiningTags() {
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        String joinedTags = String.format("('%s')", StringUtils.join(tags, "', '"));
        Assert.assertEquals("('tag1', 'tag2')", joinedTags);
    }

    @Test
    public void testGetLocationsByTags() {
        LocationsDao.setRepository(repository);
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"uuid", "location_name", "parent_id"});
        matrixCursor.addRow(new Object[]{"fb7ed5db-138d-4e6f-94d8-bc443b58dadb", "Tabata Dampo", "bcf5a36d-fb53-4de9-9813-01f1d480e3fe"});
        matrixCursor.addRow(new Object[]{"69d24450-ec06-450c-bf05-ee7ebb7f47e4", "Ebrahim Haji", "bcf5a36d-fb53-4de9-9813-01f1d480e3fe"});

        Mockito.doReturn(matrixCursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        HashSet<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        List<Location> locations = LocationsDao.getLocationsByTags(tags);

        Mockito.verify(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(locations.size(), 2);

        Assert.assertEquals(locations.get(0).getId(), "fb7ed5db-138d-4e6f-94d8-bc443b58dadb");
        Assert.assertEquals(locations.get(0).getProperties().getParentId(), "bcf5a36d-fb53-4de9-9813-01f1d480e3fe");
        Assert.assertEquals(locations.get(0).getProperties().getName(), "Tabata Dampo");

        Assert.assertEquals(locations.get(1).getId(), "69d24450-ec06-450c-bf05-ee7ebb7f47e4");
        Assert.assertEquals(locations.get(1).getProperties().getParentId(), "bcf5a36d-fb53-4de9-9813-01f1d480e3fe");
        Assert.assertEquals(locations.get(1).getProperties().getName(), "Ebrahim Haji");
    }
}