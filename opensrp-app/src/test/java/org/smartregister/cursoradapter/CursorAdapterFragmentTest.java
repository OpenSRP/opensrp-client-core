package org.smartregister.cursoradapter;

import android.content.Context;
import android.os.Build;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import net.sqlcipher.MatrixCursor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowView;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.mockactivities.HouseHoldSmartRegisterActivity;
import org.smartregister.customshadows.AndroidTreeViewShadow;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.service.ZiggyService;
import org.smartregister.shadows.ShadowContext;
import org.smartregister.shadows.ShadowDrawableResourcesImpl;
import org.smartregister.shadows.ShadowViewPager;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.ECClients;
import org.smartregister.view.contract.Village;
import org.smartregister.view.contract.Villages;
import org.smartregister.view.controller.ANMLocationController;
import org.smartregister.view.controller.ECSmartRegisterController;
import org.smartregister.view.controller.VillageController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Config(shadows = {ShadowContext.class, FontTextViewShadow.class, AndroidTreeViewShadow.class, ShadowDrawableResourcesImpl.class, ShadowViewPager.class}, sdk = Build.VERSION_CODES.O_MR1)
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class CursorAdapterFragmentTest extends BaseUnitTest {

    private HouseHoldSmartRegisterActivity ecActivity;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private Context applicationContext;

    @Mock
    private ZiggyService ziggyService;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private ANMLocationController anmLocationController;

    public String locationJson = "{\"locationsHierarchy\":{\"map\":{\"4d2b6b78-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2b6b78-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d2b6b78-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4cff021b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceded7f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Faridpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cff021b-9f95-11e6-a293-000c299c7c5d\"},\"4d490c27-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d490c27-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d490c27-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d0d6a3b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf2aa1d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Dhopadanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0d6a3b-9f95-11e6-a293-000c299c7c5d\"},\"4d3dce1e-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3dce1e-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d3dce1e-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf13ffd-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Bamandanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\"},\"4d3e7c0d-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3e7c0d-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d3e7c0d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf13ffd-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Bamandanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\"},\"4d202352-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d202352-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d202352-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4cfa1085-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cec07fe-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Laxmipur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfa1085-9f95-11e6-a293-000c299c7c5d\"},\"4d37269c-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d37269c-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d37269c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d049150-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cefd3e2-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Naldanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d049150-9f95-11e6-a293-000c299c7c5d\"},\"4d51b81c-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d51b81c-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d51b81c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d11240c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf40a29-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ramjiban\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d11240c-9f95-11e6-a293-000c299c7c5d\"},\"4d4fec87-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d4fec87-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ga\",\"node\":{\"locationId\":\"4d4fec87-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ga\",\"parentLocation\":{\"locationId\":\"4d0fde93-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cf35774-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kanchibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0fde93-9f95-11e6-a293-000c299c7c5d\"},\"4d1abb13-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d1abb13-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d1abb13-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4cf780cf-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceb63c3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kuptala\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cf780cf-9f95-11e6-a293-000c299c7c5d\"},\"4d601aab-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d601aab-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d601aab-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d16e7d5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf61dc1-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sonaroy\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d16e7d5-9f95-11e6-a293-000c299c7c5d\"},\"4d52ec31-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d52ec31-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d52ec31-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d11240c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf40a29-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ramjiban\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d11240c-9f95-11e6-a293-000c299c7c5d\"},\"4d4cc968-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d4cc968-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d4cc968-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d0f41a0-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf35774-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kanchibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0f41a0-9f95-11e6-a293-000c299c7c5d\"},\"4d5bf39a-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d5bf39a-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d5bf39a-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d14f89b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf56d3d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sarbanonda\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d14f89b-9f95-11e6-a293-000c299c7c5d\"},\"4d2c0d10-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2c0d10-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d2c0d10-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4cffa005-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4ceded7f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Faridpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cffa005-9f95-11e6-a293-000c299c7c5d\"},\"4d4371b3-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d4371b3-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ga\",\"node\":{\"locationId\":\"4d4371b3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ga\",\"parentLocation\":{\"locationId\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf1f2d6-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Chaparhati\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\"},\"4d3ad2ec-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3ad2ec-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d3ad2ec-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d06eedc-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf08264-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Rasulpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d06eedc-9f95-11e6-a293-000c299c7c5d\"},\"4d42d4d5-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d42d4d5-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d42d4d5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf1f2d6-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Chaparhati\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\"},\"4d3f10d2-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3f10d2-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ga\",\"node\":{\"locationId\":\"4d3f10d2-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ga\",\"parentLocation\":{\"locationId\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf13ffd-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Bamandanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d08d133-9f95-11e6-a293-000c299c7c5d\"},\"4d5db7fe-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d5db7fe-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ga\",\"node\":{\"locationId\":\"4d5db7fe-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ga\",\"parentLocation\":{\"locationId\":\"4d15a150-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cf56d3d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sarbanonda\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d15a150-9f95-11e6-a293-000c299c7c5d\"},\"4d26eb3d-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d26eb3d-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d26eb3d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4cfd271f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ced4440-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Damodar Pur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfd271f-9f95-11e6-a293-000c299c7c5d\"},\"4d2ac47c-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2ac47c-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d2ac47c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4cff021b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceded7f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Faridpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cff021b-9f95-11e6-a293-000c299c7c5d\"},\"4d1b59f3-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d1b59f3-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d1b59f3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4cf780cf-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceb63c3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kuptala\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cf780cf-9f95-11e6-a293-000c299c7c5d\"},\"4d49acde-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d49acde-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d49acde-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d0d6a3b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf2aa1d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Dhopadanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0d6a3b-9f95-11e6-a293-000c299c7c5d\"},\"4d2facfa-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2facfa-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d2facfa-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4d017353-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cee8ec5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Jamalpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d017353-9f95-11e6-a293-000c299c7c5d\"},\"4d30f754-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d30f754-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ga\",\"node\":{\"locationId\":\"4d30f754-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ga\",\"parentLocation\":{\"locationId\":\"4d017353-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cee8ec5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Jamalpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d017353-9f95-11e6-a293-000c299c7c5d\"},\"4d2f107c-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2f107c-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d2f107c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d00ddff-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cee8ec5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Jamalpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d00ddff-9f95-11e6-a293-000c299c7c5d\"},\"4d5738a0-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d5738a0-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d5738a0-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d13046d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf4be41-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Shantiram\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d13046d-9f95-11e6-a293-000c299c7c5d\"},\"4d37be5d-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d37be5d-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d37be5d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4d053aa8-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cefd3e2-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Naldanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d053aa8-9f95-11e6-a293-000c299c7c5d\"},\"4d2e77a0-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2e77a0-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d2e77a0-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d00ddff-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cee8ec5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Jamalpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d00ddff-9f95-11e6-a293-000c299c7c5d\"},\"4d1eead7-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d1eead7-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d1eead7-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4cf9792f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cec07fe-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Laxmipur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cf9792f-9f95-11e6-a293-000c299c7c5d\"},\"4d5c86f4-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d5c86f4-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d5c86f4-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d14f89b-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf56d3d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sarbanonda\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d14f89b-9f95-11e6-a293-000c299c7c5d\"},\"4d368df9-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d368df9-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d368df9-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d049150-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cefd3e2-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Naldanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d049150-9f95-11e6-a293-000c299c7c5d\"},\"4d278906-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d278906-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d278906-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4cfd271f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ced4440-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Damodar Pur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfd271f-9f95-11e6-a293-000c299c7c5d\"},\"4d246dd5-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d246dd5-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d246dd5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4cfbf623-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4ceca83f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Malibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfbf623-9f95-11e6-a293-000c299c7c5d\"},\"4d1bf291-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d1bf291-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d1bf291-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4cf8350a-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4ceb63c3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kuptala\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cf8350a-9f95-11e6-a293-000c299c7c5d\"},\"4d32d4e3-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d32d4e3-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d32d4e3-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d02a3f7-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cef30df-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kamar Para\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d02a3f7-9f95-11e6-a293-000c299c7c5d\"},\"4d40dc58-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d40dc58-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ga\",\"node\":{\"locationId\":\"4d40dc58-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ga\",\"parentLocation\":{\"locationId\":\"4d096e7d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cf13ffd-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Bamandanga\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d096e7d-9f95-11e6-a293-000c299c7c5d\"},\"4d58ff1d-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d58ff1d-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d58ff1d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d18e293-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf6c72e-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sripur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d18e293-9f95-11e6-a293-000c299c7c5d\"},\"4d3a3748-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3a3748-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d3a3748-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d06eedc-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf08264-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Rasulpur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d06eedc-9f95-11e6-a293-000c299c7c5d\"},\"4d6300a0-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d6300a0-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d6300a0-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d18e293-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf6c72e-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sripur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d18e293-9f95-11e6-a293-000c299c7c5d\"},\"4d4e0aa6-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d4e0aa6-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d4e0aa6-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d0f41a0-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf35774-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kanchibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0f41a0-9f95-11e6-a293-000c299c7c5d\"},\"4d336caf-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d336caf-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d336caf-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4d02a3f7-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cef30df-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kamar Para\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d02a3f7-9f95-11e6-a293-000c299c7c5d\"},\"4d569207-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d569207-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d569207-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d13046d-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf4be41-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Shantiram\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d13046d-9f95-11e6-a293-000c299c7c5d\"},\"4d3404d7-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d3404d7-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d3404d7-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4d034013-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4cef30df-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Kamar Para\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d034013-9f95-11e6-a293-000c299c7c5d\"},\"4d2342cc-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d2342cc-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d2342cc-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4cfb54f1-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceca83f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Malibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfb54f1-9f95-11e6-a293-000c299c7c5d\"},\"4d44321c-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d44321c-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Gha\",\"node\":{\"locationId\":\"4d44321c-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Gha\",\"parentLocation\":{\"locationId\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf1f2d6-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Chaparhati\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\"},\"4d28238f-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d28238f-9f95-11e6-a293-000c299c7c5d\",\"label\":\"2-Ka\",\"node\":{\"locationId\":\"4d28238f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"2-Ka\",\"parentLocation\":{\"locationId\":\"4cfdca18-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-2\",\"parentLocation\":{\"locationId\":\"4ced4440-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Damodar Pur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfdca18-9f95-11e6-a293-000c299c7c5d\"},\"4d22a675-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d22a675-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d22a675-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4cfb54f1-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4ceca83f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Malibari\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cfb54f1-9f95-11e6-a293-000c299c7c5d\"},\"4d5f87eb-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d5f87eb-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Ka\",\"node\":{\"locationId\":\"4d5f87eb-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Ka\",\"parentLocation\":{\"locationId\":\"4d16e7d5-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cf61dc1-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Sonaroy\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4d16e7d5-9f95-11e6-a293-000c299c7c5d\"},\"4d1f7f4a-9f95-11e6-a293-000c299c7c5d\":{\"id\":\"4d1f7f4a-9f95-11e6-a293-000c299c7c5d\",\"label\":\"1-Kha\",\"node\":{\"locationId\":\"4d1f7f4a-9f95-11e6-a293-000c299c7c5d\",\"name\":\"1-Kha\",\"parentLocation\":{\"locationId\":\"4cf9792f-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Ward-1\",\"parentLocation\":{\"locationId\":\"4cec07fe-9f95-11e6-a293-000c299c7c5d\",\"name\":\"Laxmipur\",\"voided\":false},\"voided\":false},\"tags\":[\"Subunit\"],\"voided\":false},\"parent\":\"4cf9792f-9f95-11e6-a293-000c299c7c5d\"}},\"parentChildren\":{\"4cfb54f1-9f95-11e6-a293-000c299c7c5d\":[\"4d2342cc-9f95-11e6-a293-000c299c7c5d\",\"4d22a675-9f95-11e6-a293-000c299c7c5d\"],\"4d0f41a0-9f95-11e6-a293-000c299c7c5d\":[\"4d4e0aa6-9f95-11e6-a293-000c299c7c5d\",\"4d4cc968-9f95-11e6-a293-000c299c7c5d\"],\"4cfa1085-9f95-11e6-a293-000c299c7c5d\":[\"4d202352-9f95-11e6-a293-000c299c7c5d\"],\"4d08d133-9f95-11e6-a293-000c299c7c5d\":[\"4d3f10d2-9f95-11e6-a293-000c299c7c5d\",\"4d3dce1e-9f95-11e6-a293-000c299c7c5d\",\"4d3e7c0d-9f95-11e6-a293-000c299c7c5d\"],\"4d0b82ad-9f95-11e6-a293-000c299c7c5d\":[\"4d42d4d5-9f95-11e6-a293-000c299c7c5d\",\"4d44321c-9f95-11e6-a293-000c299c7c5d\",\"4d4371b3-9f95-11e6-a293-000c299c7c5d\"],\"4cff021b-9f95-11e6-a293-000c299c7c5d\":[\"4d2b6b78-9f95-11e6-a293-000c299c7c5d\",\"4d2ac47c-9f95-11e6-a293-000c299c7c5d\"],\"4d18e293-9f95-11e6-a293-000c299c7c5d\":[\"4d6300a0-9f95-11e6-a293-000c299c7c5d\",\"4d58ff1d-9f95-11e6-a293-000c299c7c5d\"],\"4d16e7d5-9f95-11e6-a293-000c299c7c5d\":[\"4d601aab-9f95-11e6-a293-000c299c7c5d\",\"4d5f87eb-9f95-11e6-a293-000c299c7c5d\"],\"4d00ddff-9f95-11e6-a293-000c299c7c5d\":[\"4d2e77a0-9f95-11e6-a293-000c299c7c5d\",\"4d2f107c-9f95-11e6-a293-000c299c7c5d\"],\"4cf780cf-9f95-11e6-a293-000c299c7c5d\":[\"4d1b59f3-9f95-11e6-a293-000c299c7c5d\",\"4d1abb13-9f95-11e6-a293-000c299c7c5d\"],\"4d15a150-9f95-11e6-a293-000c299c7c5d\":[\"4d5db7fe-9f95-11e6-a293-000c299c7c5d\"],\"4d11240c-9f95-11e6-a293-000c299c7c5d\":[\"4d51b81c-9f95-11e6-a293-000c299c7c5d\",\"4d52ec31-9f95-11e6-a293-000c299c7c5d\"],\"4d02a3f7-9f95-11e6-a293-000c299c7c5d\":[\"4d336caf-9f95-11e6-a293-000c299c7c5d\",\"4d32d4e3-9f95-11e6-a293-000c299c7c5d\"],\"4d0d6a3b-9f95-11e6-a293-000c299c7c5d\":[\"4d490c27-9f95-11e6-a293-000c299c7c5d\",\"4d49acde-9f95-11e6-a293-000c299c7c5d\"],\"4cffa005-9f95-11e6-a293-000c299c7c5d\":[\"4d2c0d10-9f95-11e6-a293-000c299c7c5d\"],\"4d14f89b-9f95-11e6-a293-000c299c7c5d\":[\"4d5c86f4-9f95-11e6-a293-000c299c7c5d\",\"4d5bf39a-9f95-11e6-a293-000c299c7c5d\"],\"4d017353-9f95-11e6-a293-000c299c7c5d\":[\"4d2facfa-9f95-11e6-a293-000c299c7c5d\",\"4d30f754-9f95-11e6-a293-000c299c7c5d\"],\"4cf9792f-9f95-11e6-a293-000c299c7c5d\":[\"4d1eead7-9f95-11e6-a293-000c299c7c5d\",\"4d1f7f4a-9f95-11e6-a293-000c299c7c5d\"],\"4cf8350a-9f95-11e6-a293-000c299c7c5d\":[\"4d1bf291-9f95-11e6-a293-000c299c7c5d\"],\"4d06eedc-9f95-11e6-a293-000c299c7c5d\":[\"4d3a3748-9f95-11e6-a293-000c299c7c5d\",\"4d3ad2ec-9f95-11e6-a293-000c299c7c5d\"],\"4cfdca18-9f95-11e6-a293-000c299c7c5d\":[\"4d28238f-9f95-11e6-a293-000c299c7c5d\"],\"4d0fde93-9f95-11e6-a293-000c299c7c5d\":[\"4d4fec87-9f95-11e6-a293-000c299c7c5d\"],\"4d034013-9f95-11e6-a293-000c299c7c5d\":[\"4d3404d7-9f95-11e6-a293-000c299c7c5d\"],\"4d096e7d-9f95-11e6-a293-000c299c7c5d\":[\"4d40dc58-9f95-11e6-a293-000c299c7c5d\"],\"4cfd271f-9f95-11e6-a293-000c299c7c5d\":[\"4d26eb3d-9f95-11e6-a293-000c299c7c5d\",\"4d278906-9f95-11e6-a293-000c299c7c5d\"],\"4d049150-9f95-11e6-a293-000c299c7c5d\":[\"4d37269c-9f95-11e6-a293-000c299c7c5d\",\"4d368df9-9f95-11e6-a293-000c299c7c5d\"],\"4d053aa8-9f95-11e6-a293-000c299c7c5d\":[\"4d37be5d-9f95-11e6-a293-000c299c7c5d\"],\"4cfbf623-9f95-11e6-a293-000c299c7c5d\":[\"4d246dd5-9f95-11e6-a293-000c299c7c5d\"],\"4d13046d-9f95-11e6-a293-000c299c7c5d\":[\"4d569207-9f95-11e6-a293-000c299c7c5d\",\"4d5738a0-9f95-11e6-a293-000c299c7c5d\"]}}}";

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        HouseHoldSmartRegisterActivity.setContext(context_);
        String[] columns = new String[]{"_id", "relationalid", "FWHOHFNAME", "FWGOBHHID", "FWJIVHHID", "existing_Mauzapara", "ELCO"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"1", "relationalid1", "FWHOHFNAME1", "FWGOBHHID1", "FWJIVHHID1", "existing_Mauzapara1", "ELCO1"});
        matrixCursor.addRow(new Object[]{"2", "relationalid2", "FWHOHFNAME2", "FWGOBHHID2", "FWJIVHHID2", "existing_Mauzapara2", "ELCO2"});
        for (int i = 3; i < 22; i++) {
            matrixCursor.addRow(new Object[]{"" + i, "relationalid" + i, "FWHOHFNAME" + i, "FWGOBHHID" + i, "FWJIVHHID+i", "existing_Mauzapara" + i, "ELCO" + i});
        }
        when(context_.applicationContext()).thenReturn(applicationContext);
        when(context_.anmLocationController()).thenReturn(anmLocationController);
        when(context_.commonrepository(anyString())).thenReturn(commonRepository);
        when(commonRepository.rawCustomQueryForAdapter(anyString())).thenReturn(matrixCursor);
        when(anmLocationController.get()).thenReturn(locationJson);
        when(context_.ziggyService()).thenReturn(ziggyService);

        ecActivity = Robolectric.buildActivity(HouseHoldSmartRegisterActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

    }


    @Test
    public void assertBaseFragmentNotNullandIsSecuredNativeSmartRegisterCursorAdapterFragment() {
        Fragment mBaseFragment = ecActivity.mBaseFragment;

        assertNotNull(mBaseFragment);
        assertTrue(mBaseFragment instanceof SecuredNativeSmartRegisterCursorAdapterFragment);
    }

    @Test
    public void pressingSearchCancelButtonShouclickOnldClearSearchTextAndLoadAllClients() {
        Fragment mBaseFragment = ecActivity.mBaseFragment;

        EditText searchText = (EditText) mBaseFragment.getActivity().findViewById(R.id.edt_search);
        searchText.setText("FWHOHFNAME1");
        assertTrue("FWHOHFNAME1".equalsIgnoreCase(searchText.getText().toString()));

        ImageButton cancel = mBaseFragment.getView()
                .findViewById(R.id.btn_search_cancel);
        assertNotNull(cancel);
        assertTrue(ShadowView.clickOn(cancel));

        assertEquals("", searchText.getText().toString());
//        assertEquals(2, tryGetAdapter(list).getCount());
    }

    @Test
    public void listViewNavigationShouldWorkIfClientsSpanMoreThanOnePage() throws InterruptedException {
        Fragment mBaseFragment = ecActivity.mBaseFragment;

        Button nextButton = (Button) mBaseFragment.getView().findViewById(R.id.btn_next_page);
        Button previousButton = (Button) mBaseFragment.getView().findViewById(R.id.btn_previous_page);
        TextView info = (TextView) mBaseFragment.getView().findViewById(R.id.txt_page_info);
        nextButton.performClick();
        assertEquals("Page 1 of 1", info.getText());
        previousButton.performClick();
    }


    public static ECClients createClients(int clientCount) {
        ECClients clients = new ECClients();
        for (int i = 0; i < clientCount; i++) {
            clients.add(new ECClient("CASE " + i, "Wife 1" + i, "Husband 1" + i, "Village 1" + i, 100 + i));
        }
        return clients;
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerWithZeroClients {

        @Implementation
        public ECClients getClients() {
            return new ECClients();
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor1Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(1);
        }
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor20Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(20);
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor21Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(21);
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor5Clients {

        @Implementation
        public ECClients getClients() {
            ECClients clients = new ECClients();
            clients.add(new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69));
            clients.add(new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500));
            clients.add(new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87));
            clients.add(new ECClient("abcd4", "Bhagya", "Ramesh", "Hosa agrahara", 140));
            clients.add(new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36));
            return clients;
        }
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor25Clients {

        @Implementation
        public ECClients getClients() {
            ECClients clients = new ECClients();
            clients.add(new ECClient("abcd4", "Bhagya", "Ramesh", "Hosa agrahara", 140));
            clients.add(new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36));
            for (int i = 0; i < 20; i++) {
                clients.add(new ECClient("abcd2" + i, "wife" + i, "husband" + i, "Village" + i, 1001 + i));
            }
            clients.add(new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69));
            clients.add(new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500));
            clients.add(new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87));
            return clients;
        }
    }

    @Implements(VillageController.class)
    public static class ShadowVillageController {

        @Implementation
        public Villages getVillages() {
            Villages villages = new Villages();
            villages.add(new Village("Hosa Agrahara"));
            villages.add(new Village("Mysore"));
            villages.add(new Village("Bangalore"));
            villages.add(new Village("Kanakpura"));
            return villages;
        }
    }
}
