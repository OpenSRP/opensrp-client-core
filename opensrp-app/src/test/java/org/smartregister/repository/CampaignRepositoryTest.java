package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Campaign;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.Task.TaskStatus.IN_PROGRESS;
import static org.smartregister.repository.CampaignRepository.CAMPAIGN_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

public class CampaignRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CampaignRepository campaignRepository;

    @Mock
    private static Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<String[]> argsCaptor;

    private String campaignJson = "{\"identifier\":\"IRS_2018_S1\",\"title\":\"2019 IRS Season 1\",\"description\":\"IRS_2018_S1 Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.\",\"status\":\"In Progress\",\"executionPeriod\":{\"start\":\"2019-01-01T0000\",\"end\":\"2019-03-31T0000\"},\"authoredOn\":\"2018-10-01T0900\",\"lastModified\":\"2018-10-01T0900\",\"owner\":\"jdoe\",\"serverVersion\":0}";

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .serializeNulls().create();

    @Before
    public void setUp() {
        campaignRepository = new CampaignRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        Campaign campaign = gson.fromJson(campaignJson, Campaign.class);
        campaignRepository.addOrUpdate(campaign);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(CAMPAIGN_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(10, contentValues.size());

        assertEquals("IRS_2018_S1", contentValues.getAsString("_id"));
        assertEquals("2019 IRS Season 1", contentValues.getAsString("title"));
        assertEquals(IN_PROGRESS.name(), contentValues.getAsString("status"));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {

        Campaign campaign = new Campaign();
        campaignRepository.addOrUpdate(campaign);

    }

    @Test
    public void tesGetCampaignsAllCampaigns() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + CAMPAIGN_TABLE, null)).thenReturn(getCursor());
        List<Campaign> allCampaigns = campaignRepository.getAllCampaigns();
        verify(sqLiteDatabase).rawQuery("SELECT * FROM " + CAMPAIGN_TABLE, null);
        assertEquals(1, allCampaigns.size());
        assertEquals(campaignJson, gson.toJson(allCampaigns.get(0)));


    }

    @Test
    public void testGetCampaignByIdentifier() {

        when(sqLiteDatabase.rawQuery("SELECT * FROM campaign WHERE _id =?", new String[]{"IRS_2018_S1"})).thenReturn(getCursor());
        Campaign campaign = campaignRepository.getCampaignByIdentifier("IRS_2018_S1");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("IRS_2018_S1", argsCaptor.getValue()[0]);
        assertEquals("SELECT * FROM campaign WHERE _id =?", stringArgumentCaptor.getValue());
        assertEquals(campaignJson, gson.toJson(campaign));


    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(CampaignRepository.COLUMNS);
        Campaign campaign = gson.fromJson(campaignJson, Campaign.class);

        cursor.addRow(new Object[]{campaign.getIdentifier(), campaign.getTitle(),
                campaign.getDescription(), campaign.getStatus().name(),
                campaign.getExecutionPeriod().getStart().toDate().getTime(),
                campaign.getExecutionPeriod().getEnd().toDate().getTime(),
                campaign.getAuthoredOn().getMillis(), campaign.getLastModified().getMillis(),
                campaign.getOwner(), campaign.getServerVersion()});
        return cursor;
    }


}
