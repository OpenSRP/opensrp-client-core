package org.smartregister;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Environment;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 27-04-2021.
 */
public class EnvironmentManagerTest extends BaseRobolectricUnitTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private EnvironmentManager environmentManager;

    @Test
    public void getEnvironments() {
        HashMap<String, Environment> environmentHashMap = new HashMap<>();
        Environment env1 = new Environment();
        Environment env2 = new Environment();

        environmentHashMap.put("goldsmith-stage", env1);
        environmentHashMap.put("reveal-stage", env2);

        ReflectionHelpers.setField(environmentManager, "environments", environmentHashMap);

        // Call the method under test
        Collection<Environment> environments = environmentManager.getEnvironments();

        Assert.assertEquals(2, environments.size());
        Assert.assertTrue(environments.contains(env1));
        Assert.assertTrue(environments.contains(env2));

    }

    @Test
    public void getEnvironment() {
        HashMap<String, Environment> environmentHashMap = new HashMap<>();
        Environment env1 = new Environment();
        Environment env2 = new Environment();

        environmentHashMap.put("https:goldsmith-stage.com", env1);
        environmentHashMap.put("https:reveal-stage.com", env2);

        ReflectionHelpers.setField(environmentManager, "environments", environmentHashMap);

        Assert.assertEquals(env1, environmentManager.getEnvironment("https://goldsmith-stage.com"));
    }


    @Test
    public void constructorShouldLoadEnvironments() {
        String gsStageURL = "https://goldsmith-stage.com";

        Environment env1 = new Environment();
        ReflectionHelpers.setField(env1, "url", gsStageURL);
        ReflectionHelpers.setField(env1, "id", "gs-client-id");
        Environment env2 = new Environment();
        ReflectionHelpers.setField(env2, "url", "https://reveal-stage.com");
        ReflectionHelpers.setField(env2, "id", "reveal-stage-client-id");

        ArrayList<Environment> environmentsList = new ArrayList<>();
        environmentsList.add(env1);
        environmentsList.add(env2);

        // Call the method under test
        environmentManager = new EnvironmentManager(JsonFormUtils.gson.toJson(environmentsList));

        Assert.assertEquals(gsStageURL, environmentManager.getEnvironment("https://goldsmith-stage.com").getUrl());
        Assert.assertEquals("gs-client-id", environmentManager.getEnvironment("https://goldsmith-stage.com").getId());
        Assert.assertEquals("https://reveal-stage.com", environmentManager.getEnvironment("https://reveal-stage.com").getUrl());
        Assert.assertEquals("reveal-stage-client-id", environmentManager.getEnvironment("https://reveal-stage.com").getId());

    }
}