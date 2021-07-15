package org.smartregister.location.helper;

import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class LocationHelperTest extends BaseRobolectricUnitTest {

    private static final String anmLocation1 = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";
    private static final String anmLocation2 = "{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Health Facility\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Sub-county\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"County\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"tags\":[\"District\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}},\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false,\"serverVersion\":0}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private Repository repository;

    private LocationHelper locationHelper;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        ArrayList<String> ALLOWED_LEVELS;
        String DEFAULT_LOCATION_LEVEL = "Health Facility";
        String SCHOOL = "School";

        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(SCHOOL);
        ALLOWED_LEVELS.add("MOH Jhpiego Facility Name");
        ALLOWED_LEVELS.add("Village");

        LocationHelper.init(ALLOWED_LEVELS, "Health Facility");
        locationHelper = LocationHelper.getInstance();

        repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
    }

    @Test
    public void testGetChildLocationIdForJambulaGirlsSchool() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
        assertEquals("982eb3f3-b7e3-450f-a38e-d067f2345212", parentAndChildLocationIds.second);
    }

    @Test
    public void testGetChildLocationIdForNsaloGirlsSchool() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
        assertEquals("ee08a6e0-3f73-4c28-b186-64d5cd06f4ce", parentAndChildLocationIds.second);
    }


    @Test
    public void testGetChildLocationIdForBukesaUrbanHealthCentre() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.second);
    }

    @Test
    public void testGetParentLocationIdForNsaloGirlsSchool() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }

    @Test
    public void testGetParentLocationIdForJambulaGirlsSchool() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }

    @Test
    public void testGetParentLocationIdForBukesaUrbanHealthCentre() throws Exception {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }

    @Test
    public void testParentLocationIdSetterAndGetter() {
        locationHelper.setParentLocationId("parentId");
        assertEquals("parentId", locationHelper.getParentLocationId());
    }

    @Test
    public void testChildLocationIdSetterAndGetter() {
        locationHelper.setChildLocationId("childId");
        assertEquals("childId", locationHelper.getChildLocationId());
    }

    @Test
    public void testLocationIdsFromHierarchy() {
        AllSettings allSettings = Mockito.spy(CoreLibrary.getInstance().context().allSettings());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSettings", allSettings);
        SettingsRepository settingsRepository = ReflectionHelpers.getField(allSettings, "settingsRepository");
        settingsRepository.updateMasterRepository(repository);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn("d60e1ee9-19e9-4e7d-a949-39f790a0ceda").when(allSharedPreferences).fetchDefaultTeamId(Mockito.nullable(String.class));
        Mockito.doReturn("{\"locationsHierarchy\":{\"map\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":{\"children\":{\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":{\"children\":{\"fc747bcd-f812-4229-b40e-9de496e016ac\":{\"children\":{\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":{\"children\":{\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":{\"children\":{\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\":{\"id\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"label\":\"Kabila Village\",\"node\":{\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"name\":\"Kabila Village\",\"parentLocation\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"}},\"id\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"label\":\"Huruma\",\"node\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"MOH Jhpiego Facility Name\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"}},\"id\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"label\":\"Kabila\",\"node\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Ward\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"fc747bcd-f812-4229-b40e-9de496e016ac\"}},\"id\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"label\":\"Magu DC\",\"node\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"}},\"id\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"label\":\"Mwanza\",\"node\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\"}},\"id\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"label\":\"Tanzania\",\"node\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":[\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"],\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":[\"fc747bcd-f812-4229-b40e-9de496e016ac\"],\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":[\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"],\"fc747bcd-f812-4229-b40e-9de496e016ac\":[\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"],\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":[\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\"]}}}")
                .when(anmLocationController).get();

        LocationHelper spiedLocationHelper = Mockito.spy(locationHelper);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", spiedLocationHelper);

        String locationIds = spiedLocationHelper.locationIdsFromHierarchy();

        assertNotNull(spiedLocationHelper.getLocationIds());
        Mockito.verify(spiedLocationHelper).locationsFromHierarchy(Mockito.eq(true), Mockito.nullable(String.class));
        assertEquals("718b2864-7d6a-44c8-b5b6-bb375f82654e,2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8", locationIds);

        List<String> locationNames = spiedLocationHelper.locationNamesFromHierarchy("MOH Jhpiego Facility Name");
        assertNotNull(spiedLocationHelper.getLocationNames());
        assertTrue(locationNames.contains("Huruma"));
        assertTrue(locationNames.contains("Kabila Village"));
    }

    @Test
    public void testGenerateDefaultLocationHierarchy() {
        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("District");
        allowedLevels.add("Rural Health Centre");
        allowedLevels.add("Village");
        allowedLevels.add("Canton");
        allowedLevels.add("Sub-district");

        AllSharedPreferences spiedAllSharedPreferences = Mockito.spy((AllSharedPreferences) ReflectionHelpers.getField(locationHelper, "allSharedPreferences"));
        ReflectionHelpers.setField(locationHelper, "allSharedPreferences", spiedAllSharedPreferences);

        Mockito.doReturn("NL1").when(spiedAllSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("1b0ba804-54c3-40ef-820b-a8eaffa5d054").when(spiedAllSharedPreferences).fetchDefaultLocalityId("NL1");

        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation1)
                .when(anmLocationController).get();

        List<String> locations = locationHelper.generateDefaultLocationHierarchy(allowedLevels);
        assertEquals(2, locations.size());
        assertEquals("ra Nchelenge", locations.get(0));
        assertEquals("ra Kashikishi HAHC", locations.get(1));

    }

    @Test
    public void testGenerateDefaultLocationHierarchyReturnsEmptyForEmptyAllowedLevels(){
        List<String> levelsEmpty = new ArrayList<>();
        List<String> result = locationHelper.generateDefaultLocationHierarchy(levelsEmpty);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateDefaultLocationHierarchyWithRegisteredANMNotSetReturnsNull(){
        LocationHelper spyLocationHelper = Mockito.spy(locationHelper);
        AllSharedPreferences spiedAllSharedPreferences = Mockito.spy((AllSharedPreferences) ReflectionHelpers.getField(spyLocationHelper, "allSharedPreferences"));
        ReflectionHelpers.setField(spyLocationHelper, "allSharedPreferences", spiedAllSharedPreferences);

        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation1)
                .when(anmLocationController).get();

        List<String> allowedLevels = Arrays.asList("District", "Village");
        List<String> result = spyLocationHelper.generateDefaultLocationHierarchy(allowedLevels);

        Mockito.verify(spiedAllSharedPreferences).fetchDefaultLocalityId(Mockito.eq(""));
        Mockito.verify(spyLocationHelper).getDefaultLocationHierarchy(Mockito.isNull(), Mockito.any(), Mockito.anyList(), Mockito.eq(allowedLevels), Mockito.eq(false));
        assertNull(result);
    }

    @Test
    public void testGenerateDefaultLocationHierarchyReturnsNUllWhenNoDefaultLocationIdSet(){
        LocationHelper spyLocationHelper = Mockito.spy(locationHelper);
        AllSharedPreferences spiedAllSharedPreferences = Mockito.spy((AllSharedPreferences) ReflectionHelpers.getField(spyLocationHelper, "allSharedPreferences"));
        ReflectionHelpers.setField(spyLocationHelper, "allSharedPreferences", spiedAllSharedPreferences);

        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation1).when(anmLocationController).get();
        final String anmIdentifier = "NL1";
        Mockito.doReturn(anmIdentifier).when(spiedAllSharedPreferences).fetchRegisteredANM();

        List<String> allowedLevels = Arrays.asList("District", "Village");
        List<String> result = spyLocationHelper.generateDefaultLocationHierarchy(allowedLevels);

        final String defaultLocationId = Mockito.verify(spiedAllSharedPreferences).fetchDefaultLocalityId(Mockito.eq(anmIdentifier));
        assertNull(defaultLocationId);
        Mockito.verify(spyLocationHelper).getDefaultLocationHierarchy(Mockito.isNull(), Mockito.any(), Mockito.anyList(), Mockito.eq(allowedLevels), Mockito.eq(false));
        assertNull(result);
    }


    @Test
    public void testGenerateLocationHierarchyTreeShouldReturnEmptyList() {
        String locationData = anmLocation1;

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);
        assertEquals(0, formLocationsList.size());
    }

    @Test
    public void testGenerateLocationHierarchyTreeWithMapShouldReturnListWithOtherFormLocationOnly() {
        locationHelper = Mockito.spy(locationHelper);
        String locationData = anmLocation1;

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(true, allowedLevels, map);

        assertEquals(1, formLocationsList.size());

        FormLocation formLocation = formLocationsList.get(0);
        assertEquals("Other", formLocation.name);
        assertEquals("Other", formLocation.key);
        assertEquals("", formLocation.level);
    }

    @Test
    public void testGenerateLocationHierarchyTreeWithMapAndOtherOptionFalseShouldReturnEmptyList() {
        locationHelper = Mockito.spy(locationHelper);
        String locationData = anmLocation1;

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);
        assertEquals(0, formLocationsList.size());
    }

    @Test
    public void testGenerateLocationHierarchyTreeWithMapShouldReturnListWithZambiaFormLocation() {
        locationHelper = Mockito.spy(locationHelper);
        String locationData = anmLocation1;

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Country");
        allowedLevels.add("Province");
        allowedLevels.add("Region");
        allowedLevels.add("District");
        allowedLevels.add("Sub-district");
        allowedLevels.add("Operational Area");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);

        assertEquals(1, formLocationsList.size());

        FormLocation formLocation = formLocationsList.get(0);
        assertEquals("Zambia", formLocation.name);
        assertEquals("ra Zambia", formLocation.key);
        assertEquals("", formLocation.level);
        assertEquals(1, formLocation.nodes.size());
    }

    @Test
    public void testGenerateLocationHierarchyTreeWithMapShouldReturnListWithZambiaFormLocationAndLocationTags() {
        locationHelper = Mockito.spy(locationHelper);
        String locationData = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Country");
        allowedLevels.add("Province");
        allowedLevels.add("Region");
        allowedLevels.add("District");
        allowedLevels.add("Sub-district");
        allowedLevels.add("Operational Area");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        Mockito.doReturn(true).when(locationHelper).isLocationTagsShownEnabled();
        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);

        assertEquals(1, formLocationsList.size());

        FormLocation formLocation = formLocationsList.get(0);
        assertEquals("Zambia", formLocation.name);
        assertEquals("ra Zambia", formLocation.key);
        assertEquals("Country", formLocation.level);
        assertEquals(1, formLocation.nodes.size());
    }

    @Test
    public void testGenerateLocationHierarchyTreeShouldReturnListWithOtherFormLocationOnly() {
        locationHelper = Mockito.spy(locationHelper);

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(anmLocation1, LocationTree.class).getLocationsHierarchy();
        Mockito.doReturn(map).when(locationHelper).map();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(true, allowedLevels);
        assertEquals(1, formLocationsList.size());
        FormLocation formLocation = formLocationsList.get(0);

        assertEquals("Other", formLocation.name);
        assertEquals("Other", formLocation.key);
        assertEquals("", formLocation.level);
    }


    @Test
    public void testGetOpenMrsReadableName() {
        assertEquals("Zambia", locationHelper.getOpenMrsReadableName("ra Zambia"));
    }

    @Test
    public void testPrivateSortTreeViewQuestionOptions() {
        List<FormLocation> formLocations = new ArrayList<>();
        FormLocation firstOne = new FormLocation();
        firstOne.name = "First One";
        firstOne.level = "";
        firstOne.key = "First One";


        FormLocation lastOne = new FormLocation();
        lastOne.name = "Last One";
        lastOne.level = "";
        lastOne.key = "Last One";
        formLocations.add(lastOne);
        formLocations.add(firstOne);

        List<FormLocation> formLocationList = ReflectionHelpers.callInstanceMethod(locationHelper, "sortTreeViewQuestionOptions", ReflectionHelpers.ClassParameter.from(List.class, formLocations));

        assertEquals(firstOne.name, formLocationList.get(0).name);
        assertEquals(lastOne.name, formLocationList.get(1).name);
    }

    @Test
    public void testGetOpenMrsLocationName() {
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn(anmLocation2)
                .when(anmLocationController).get();
        assertEquals("Jambula Girls School", locationHelper.getOpenMrsLocationName("982eb3f3-b7e3-450f-a38e-d067f2345212"));
    }

    public void testGetOpenMrsLocationHierarchyWithEmptyLocationIdShouldReturnEmptyList() {
        locationHelper = Mockito.spy(locationHelper);
        List<String> hierarchy = locationHelper.getOpenMrsLocationHierarchy("", false);
        assertEquals(0, hierarchy.size());
    }

    @Test
    public void testGetOpenMrsLocationHierarchyWithLocationIdAndAllowedLevelsFlagShouldReturnListOfLocationNames() {
        locationHelper = Mockito.spy(locationHelper);

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("District");
        allowedLevels.add("Facility");
        allowedLevels.add("Commune");

        AllSharedPreferences spiedAllSharedPreferences = Mockito.spy((AllSharedPreferences) ReflectionHelpers.getField(locationHelper, "allSharedPreferences"));
        ReflectionHelpers.setField(locationHelper, "allSharedPreferences", spiedAllSharedPreferences);

        Mockito.doReturn("Mabebe").when(spiedAllSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("11").when(spiedAllSharedPreferences).fetchDefaultLocalityId("Mabebe");

        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        String locationTree = "{\"locationsHierarchy\":{\"map\":{\"1\":{\"id\":\"1\",\"label\":\"Kiamb\",\"node\":{\"locationId\":\"1\",\"name\":\"Kiamb\",\"tags\":[\"District\"],\"voided\":false},\"children\":{\"11\":{\"id\":\"11\",\"label\":\"Mabebe\",\"node\":{\"locationId\":\"11\",\"name\":\"Mabebe\",\"parentLocation\":{\"locationId\":\"1\",\"voided\":false},\"tags\":[\"Commune\"],\"voided\":false},\"children\":{\"111\":{\"id\":\"111\",\"label\":\"Omshindi\",\"node\":{\"locationId\":\"111\",\"name\":\"Omshindi\",\"parentLocation\":{\"locationId\":\"11\",\"voided\":false},\"tags\":[\"District\"],\"voided\":false},\"parent\":\"11\"}},\"parent\":\"1\"}}}},\"parentChildren\":{\"1\":[\"11\"],\"11\":[\"111\"]}}}";
        Mockito.doReturn(locationTree).when(anmLocationController).get();

        List<String> hierarchy = locationHelper.getOpenMrsLocationHierarchy("1", false);

        assertEquals(1, hierarchy.size());
        assertTrue(hierarchy.contains("Kiamb"));
    }

    @Test
    public void testGetAllowedLevelsReturnsListOfLevelNames() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("District");
        allowedLevels.add("Commune");
        allowedLevels.add("Facility");

        LocationHelper.init(allowedLevels, "Facility");
        locationHelper = LocationHelper.getInstance();

        List<String> actualAllowedLevels = locationHelper.getAllowedLevels();

        assertNotNull(actualAllowedLevels);
        assertEquals(3, actualAllowedLevels.size());
        assertTrue(actualAllowedLevels.contains("Facility"));
    }

    @Test
    public void testGetDefaultLocationLevelReturnsLocationLevelName() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("District");
        allowedLevels.add("Commune");
        allowedLevels.add("Facility");

        LocationHelper.init(allowedLevels, "Facility");
        locationHelper = LocationHelper.getInstance();

        String defaultLocationLevel = locationHelper.getDefaultLocationLevel();

        assertEquals("Facility", defaultLocationLevel);
    }

    @Test
    public void testGetAdvancedDataCaptureStrategies() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        String advancedDataStrategyType = "Mobile Clinic";
        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("County");
        allowedLevels.add("Ward");
        allowedLevels.add("Facility");

        ArrayList<String> advancedDataStrategy = new ArrayList<>();
        advancedDataStrategy.add(advancedDataStrategyType);

        LocationHelper.init(allowedLevels, "Facility", advancedDataStrategy);
        locationHelper = LocationHelper.getInstance();

        List<String> defaultLocationLevel = locationHelper.getAdvancedDataCaptureStrategies();

        assertEquals(1, defaultLocationLevel.size());
        assertEquals(advancedDataStrategyType, defaultLocationLevel.get(0));
    }

}
