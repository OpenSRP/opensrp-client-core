package org.smartregister.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.util.Cache;
import org.smartregister.view.contract.Village;
import org.smartregister.view.contract.Villages;

import java.util.Arrays;
import java.util.List;

public class VillageControllerTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    private VillageController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new VillageController(allEligibleCouples, new Cache<String>(), new Cache<Villages>());
    }

    @Test
    public void shouldLoadVillages() throws Exception {
        List<Village> expectedVillages = Arrays.asList(new Village("village1"), new Village("village2"));
        Mockito.when(allEligibleCouples.villages()).thenReturn(Arrays.asList("village1", "village2"));

        String villages = controller.villages();
        List<Village> actualVillages = new Gson().fromJson(villages, new TypeToken<List<Village>>() {
        }.getType());
        Assert.assertEquals(actualVillages, expectedVillages);
    }
}
