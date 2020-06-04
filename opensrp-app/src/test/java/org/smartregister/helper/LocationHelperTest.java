package org.smartregister.helper;

import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class LocationHelperTest extends BaseUnitTest {

    private final String anmLocation = "{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Health Facility\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Sub-county\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"County\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"tags\":[\"District\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}},\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false,\"serverVersion\":0}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}";

    @Mock
    private Context context;

    @Mock
    private ANMLocationController anmLocationController;

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

        Mockito.when(context.anmLocationController()).thenReturn(anmLocationController);
        Mockito.when(anmLocationController.get()).thenReturn(anmLocation);

        repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void testGetChildLocationIdForJambulaGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
            assertEquals("982eb3f3-b7e3-450f-a38e-d067f2345212", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetChildLocationIdForNsaloGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
            assertEquals("ee08a6e0-3f73-4c28-b186-64d5cd06f4ce", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetChildLocationIdForBukesaUrbanHealthCentre() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForNsaloGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForJambulaGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForBukesaUrbanHealthCentre() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void locationIdsFromHierarchy() {
        AllSettings allSettings = Mockito.spy(CoreLibrary.getInstance().context().allSettings());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSettings", allSettings);
        SettingsRepository settingsRepository = ReflectionHelpers.getField(allSettings, "settingsRepository");
        settingsRepository.updateMasterRepository(repository);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ReflectionHelpers.setField(locationHelper, "allSharedPreferences", allSharedPreferences);

        Mockito.doReturn("d60e1ee9-19e9-4e7d-a949-39f790a0ceda").when(allSharedPreferences).fetchDefaultTeamId(Mockito.nullable(String.class));
        Mockito.doReturn("{\"locationsHierarchy\":{\"map\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":{\"children\":{\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":{\"children\":{\"fc747bcd-f812-4229-b40e-9de496e016ac\":{\"children\":{\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":{\"children\":{\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":{\"children\":{\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\":{\"id\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"label\":\"Kabila Village\",\"node\":{\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"name\":\"Kabila Village\",\"parentLocation\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"}},\"id\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"label\":\"Huruma\",\"node\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"MOH Jhpiego Facility Name\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"}},\"id\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"label\":\"Kabila\",\"node\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Ward\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"fc747bcd-f812-4229-b40e-9de496e016ac\"}},\"id\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"label\":\"Magu DC\",\"node\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"}},\"id\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"label\":\"Mwanza\",\"node\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\"}},\"id\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"label\":\"Tanzania\",\"node\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":[\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"],\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":[\"fc747bcd-f812-4229-b40e-9de496e016ac\"],\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":[\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"],\"fc747bcd-f812-4229-b40e-9de496e016ac\":[\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"],\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":[\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\"]}}}")
                .when(allSettings).fetchANMLocation();
        //CoreLibrary.getInstance().context().allSettings().saveANMLocation("{\"locationsHierarchy\":{\"map\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":{\"children\":{\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":{\"children\":{\"fc747bcd-f812-4229-b40e-9de496e016ac\":{\"children\":{\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":{\"children\":{\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":{\"children\":{\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\":{\"id\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"label\":\"Kabila Village\",\"node\":{\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"name\":\"Kabila Village\",\"parentLocation\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"}},\"id\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"label\":\"Huruma\",\"node\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"MOH Jhpiego Facility Name\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"}},\"id\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"label\":\"Kabila\",\"node\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Ward\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"fc747bcd-f812-4229-b40e-9de496e016ac\"}},\"id\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"label\":\"Magu DC\",\"node\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"}},\"id\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"label\":\"Mwanza\",\"node\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\"}},\"id\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"label\":\"Tanzania\",\"node\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":[\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"],\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":[\"fc747bcd-f812-4229-b40e-9de496e016ac\"],\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":[\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"],\"fc747bcd-f812-4229-b40e-9de496e016ac\":[\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"],\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":[\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\"]}}}");

        LocationHelper spiedLocationHelper = Mockito.spy(locationHelper);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", spiedLocationHelper);

        String locationIds = spiedLocationHelper.locationIdsFromHierarchy();

        Mockito.verify(spiedLocationHelper).locationsFromHierarchy(Mockito.eq(true), Mockito.nullable(String.class));
        Assert.assertEquals("718b2864-7d6a-44c8-b5b6-bb375f82654e,2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8", locationIds);
    }
}
