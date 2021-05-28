package org.smartregister.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ServiceLocationsAdapterTest extends BaseUnitTest {

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private Context applicationContext;

    private ArrayList<String> locationNames;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private ListView itemsListView;

    @Mock
    private TextView textView;

    @Mock
    private ImageView imageView;

    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        locationNames = new ArrayList<>();
        locationNames.add("test1"); locationNames.add("test2"); locationNames.add("test3");
    }

    @Test
    public void testGetCount() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getCount(), 3);
    }

    @Test
    public void testGetItemId() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getItemId(0), 2321);
    }

    @Test
    public void testGetLocationAt() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getLocationAt(0), "test1");
    }

    @Test
    public void testGetLocationNames() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        List<String> names = adapter.getLocationNames();

        Assert.assertEquals(names.get(0), "test1");
    }

    @Test
    public void testGetView() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();
        itemsListView.setAdapter(adapter);

        when(applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater);
        Mockito.doReturn(linearLayout).when(layoutInflater).inflate(R.layout.location_picker_dropdown_item, null);

        ArrayList<String> ALLOWED_LEVELS;
        String DEFAULT_LOCATION_LEVEL = "Health Facility";
        String SCHOOL = "School";
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(SCHOOL);

        LocationHelper.init(ALLOWED_LEVELS, "Health Facility");
        when(locationHelper.getOpenMrsReadableName(anyString())).thenReturn("three");

        Mockito.doReturn(textView).when(linearLayout).findViewById(android.R.id.text1);
        Mockito.doReturn(imageView).when(linearLayout).findViewById(R.id.checkbox);

        adapter.getView(0, null, null);

        Mockito.verify(layoutInflater).inflate(R.layout.location_picker_dropdown_item, null);
    }

    private ServiceLocationsAdapter getAdapterWithFakeClients() {
        return new ServiceLocationsAdapter(applicationContext, locationNames);
    }
}
