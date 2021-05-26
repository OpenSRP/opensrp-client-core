package org.smartregister.view.dialog;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.view.contract.Village;

import java.util.List;

import static com.google.common.collect.Iterables.toArray;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;

public class DialogOptionMapperTest {

    private DialogOptionMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new DialogOptionMapper();
    }

    @Test
    public void shouldMapVillagesToVillageFilterOptions() throws Exception {
        List<Village> villages = asList(new Village("village1"), new Village("village2"));

        Iterable<? extends DialogOption> villageFilterOptions =
                mapper.mapToVillageFilterOptions(villages);

        VillageFilter[] expectedVillageFilters =
                ArrayUtils.toArray(new VillageFilter("Village1"), new VillageFilter("Village2"));
        assertArrayEquals(
                expectedVillageFilters, toArray(villageFilterOptions, DialogOption.class));
    }
}
