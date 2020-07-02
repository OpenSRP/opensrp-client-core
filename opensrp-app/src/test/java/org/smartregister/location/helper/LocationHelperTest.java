package org.smartregister.location.helper;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
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
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LocationHelperTest extends BaseRobolectricUnitTest {

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

    /*@Test
    public void testGetChildLocationIdForJambulaGirlsSchool() throws Exception {
        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
        assertEquals("982eb3f3-b7e3-450f-a38e-d067f2345212", parentAndChildLocationIds.second);
    }

    @Test
    public void testGetChildLocationIdForNsaloGirlsSchool() throws Exception {
        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
        assertEquals("ee08a6e0-3f73-4c28-b186-64d5cd06f4ce", parentAndChildLocationIds.second);
    }


    @Test
    public void testGetChildLocationIdForBukesaUrbanHealthCentre() throws Exception {

        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.second);
    }

    @Test
    public void testGetParentLocationIdForNsaloGirlsSchool() throws Exception {
        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }

    @Test
    public void testGetParentLocationIdForJambulaGirlsSchool() throws Exception {
        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }

    @Test
    public void testGetParentLocationIdForBukesaUrbanHealthCentre() throws Exception {
        Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
        assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
    }*/

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

        Mockito.verify(spiedLocationHelper).locationsFromHierarchy(Mockito.eq(true), Mockito.nullable(String.class));
        assertEquals("718b2864-7d6a-44c8-b5b6-bb375f82654e,2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8", locationIds);
    }


    @Test
    public void testLocationsFromHierarchyWhenAllowedLevelsContainsReveal() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Rural Health Centre");
        allowedLevels.add("Operational Area");
        allowedLevels.add("Canton");
        allowedLevels.add("Village");
        allowedLevels.add("reveal");

        LocationHelper.init(allowedLevels, "Rural Health Centre");
        locationHelper = LocationHelper.getInstance();

        AllSettings allSettings = Mockito.spy(CoreLibrary.getInstance().context().allSettings());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSettings", allSettings);
        SettingsRepository settingsRepository = ReflectionHelpers.getField(allSettings, "settingsRepository");
        settingsRepository.updateMasterRepository(repository);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ReflectionHelpers.setField(locationHelper, "allSharedPreferences", allSharedPreferences);
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn("NL1").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("97809856-5c31-5a4e-abb2-efe152a0b715").when(allSharedPreferences).fetchDefaultTeamId(Mockito.nullable(String.class));
        Mockito.doReturn("{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}")
                .when(anmLocationController).get();

        LocationHelper spiedLocationHelper = Mockito.spy(locationHelper);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", spiedLocationHelper);

        List<String> campaignIds = new ArrayList<>();
        campaignIds.add("campaign-1");
        campaignIds.add("campaign-2");
        campaignIds.add("campaign-3");

        List<String> operationalArea = new ArrayList<>();
        operationalArea.add("operational-area-1");
        operationalArea.add("operational-area-2");
        operationalArea.add("operational-area-3");

        ReflectionHelpers.setField(spiedLocationHelper, "allCampaigns", campaignIds);
        ReflectionHelpers.setField(spiedLocationHelper, "allOperationalArea", operationalArea);

        List<String> locations = spiedLocationHelper.locationsFromHierarchy(true, null);

        Mockito.verify(allSharedPreferences).savePreference(Mockito.eq(AllConstants.CAMPAIGNS), Mockito.eq("campaign-1,campaign-2,campaign-3"));
        Mockito.verify(allSharedPreferences).savePreference(Mockito.eq(AllConstants.OPERATIONAL_AREAS), Mockito.eq("operational-area-1,operational-area-2,operational-area-3"));
        assertEquals(2, locations.size());
        assertEquals("ed7c4a07-6e02-4784-ae9a-9cd41cfef390", locations.get(0));
        assertEquals("1b0ba804-54c3-40ef-820b-a8eaffa5d054", locations.get(1));
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

        Mockito.doReturn("{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}")
                .when(anmLocationController).get();

        List<String> locations = locationHelper.generateDefaultLocationHierarchy(allowedLevels);
        assertEquals(2, locations.size());
        assertEquals("ra Nchelenge", locations.get(0));
        assertEquals("ra Kashikishi HAHC", locations.get(1));

    }

    @Test
    public void testGenerateLocationHierarchyTreeShouldReturnEmptyList() {
        String locationData = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);
        assertEquals(0, formLocationsList.size());
    }


    @Test
    public void testGenerateLocationHierarchyTreeWithMapShouldReturnListWithOtherFormLocationOnly() {
        locationHelper = Mockito.spy(locationHelper);
        String locationData = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";

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
        String locationData = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();

        List<FormLocation> formLocationsList = locationHelper.generateLocationHierarchyTree(false, allowedLevels, map);
        assertEquals(0, formLocationsList.size());
    }


    @Test
    public void testGenerateLocationHierarchyTreeWithMapShouldReturnListWithZambiaFormLocation() {
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
        String locationData = "{\"locationsHierarchy\":{\"map\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":{\"children\":{\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":{\"children\":{\"620332e0-6108-4611-bac5-8b48d20051c9\":{\"children\":{\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":{\"children\":{\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\":{\"id\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"label\":\"ra_ksh_5\",\"node\":{\"locationId\":\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\",\"name\":\"ra_ksh_5\",\"parentLocation\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"}},\"id\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"label\":\"ra Kashikishi HAHC\",\"node\":{\"locationId\":\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\",\"name\":\"ra Kashikishi HAHC\",\"parentLocation\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"620332e0-6108-4611-bac5-8b48d20051c9\"}},\"id\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"label\":\"ra Nchelenge\",\"node\":{\"locationId\":\"620332e0-6108-4611-bac5-8b48d20051c9\",\"name\":\"ra Nchelenge\",\"parentLocation\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"}},\"id\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"label\":\"ra Luapula\",\"node\":{\"locationId\":\"2e823ceb-4de6-41ac-8025-e2ae3512a331\",\"name\":\"ra Luapula\",\"parentLocation\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\"}},\"id\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"label\":\"ra Zambia\",\"node\":{\"locationId\":\"9c3e8715-1c59-44db-9709-2b49f440ef00\",\"name\":\"ra Zambia\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"9c3e8715-1c59-44db-9709-2b49f440ef00\":[\"2e823ceb-4de6-41ac-8025-e2ae3512a331\"],\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\":[\"1b0ba804-54c3-40ef-820b-a8eaffa5d054\"],\"620332e0-6108-4611-bac5-8b48d20051c9\":[\"ed7c4a07-6e02-4784-ae9a-9cd41cfef390\"],\"2e823ceb-4de6-41ac-8025-e2ae3512a331\":[\"620332e0-6108-4611-bac5-8b48d20051c9\"]}}}";

        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add("Canton");

        LinkedHashMap<String, TreeNode<String, Location>> map = AssetHandler.jsonStringToJava(locationData, LocationTree.class).getLocationsHierarchy();
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
}
