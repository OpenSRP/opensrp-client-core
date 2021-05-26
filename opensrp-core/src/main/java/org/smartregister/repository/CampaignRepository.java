package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.Period;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.domain.Task.TaskStatus;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class CampaignRepository extends BaseRepository {

    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";
    private static final String START = "start";
    private static final String END = "end";
    private static final String AUTHORED_ON = "authored_on";
    private static final String LAST_MODIFIED = "last_modified";
    private static final String OWNER = "owner";
    private static final String SERVER_VERSION = "server_version";

    protected static final String[] COLUMNS = {ID, TITLE, DESCRIPTION, STATUS, START, END, AUTHORED_ON, LAST_MODIFIED, OWNER, SERVER_VERSION};

    protected static final String CAMPAIGN_TABLE = "campaign";

    private static final String CREATE_CAMPAIGN_TABLE =
            "CREATE TABLE " + CAMPAIGN_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    TITLE + " VARCHAR NOT NULL, " +
                    DESCRIPTION + " VARCHAR , " +
                    STATUS + " VARCHAR NOT NULL, " +
                    START + " INTEGER , " +
                    END + " INTEGER , " +
                    AUTHORED_ON + " INTEGER , " +
                    LAST_MODIFIED + " INTEGER , " +
                    OWNER + " VARCHAR , " +
                    SERVER_VERSION + " INTEGER ) ";


    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_CAMPAIGN_TABLE);
    }

    public void addOrUpdate(Campaign campaign) {
        if (StringUtils.isBlank(campaign.getIdentifier()))
            throw new IllegalArgumentException("Identifier must be specified");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, campaign.getIdentifier());
        contentValues.put(TITLE, campaign.getTitle());
        contentValues.put(DESCRIPTION, campaign.getDescription());
        if (campaign.getStatus() != null) {
            contentValues.put(STATUS, campaign.getStatus().name());
        }
        if (campaign.getExecutionPeriod() != null) {
            contentValues.put(START, DateUtil.getMillis(campaign.getExecutionPeriod().getStart()));
            contentValues.put(END, DateUtil.getMillis(campaign.getExecutionPeriod().getEnd()));

        }

        contentValues.put(AUTHORED_ON, DateUtil.getMillis(campaign.getAuthoredOn()));
        contentValues.put(LAST_MODIFIED, DateUtil.getMillis(campaign.getLastModified()));
        contentValues.put(OWNER, campaign.getOwner());
        contentValues.put(SERVER_VERSION, campaign.getServerVersion());

        getWritableDatabase().replace(CAMPAIGN_TABLE, null, contentValues);

    }

    public List<Campaign> getAllCampaigns() {
        Cursor cursor = null;
        List<Campaign> campaigns = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + CAMPAIGN_TABLE, null);
            while (cursor.moveToNext()) {
                campaigns.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return campaigns;
    }


    public Campaign getCampaignByIdentifier(String identifier) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + CAMPAIGN_TABLE +
                    " WHERE " + ID + " =?", new String[]{identifier});
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private Campaign readCursor(Cursor cursor) {
        Campaign campaign = new Campaign();
        campaign.setIdentifier(cursor.getString(cursor.getColumnIndex(ID)));
        campaign.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        campaign.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        if (cursor.getString(cursor.getColumnIndex(STATUS)) != null) {
            campaign.setStatus(TaskStatus.valueOf(cursor.getString(cursor.getColumnIndex(STATUS))));
        }
        Period executionPeriod = new Period();
        executionPeriod.setStart(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(START))));
        executionPeriod.setEnd(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(END))));
        campaign.setExecutionPeriod(executionPeriod);

        campaign.setAuthoredOn(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(AUTHORED_ON))));
        campaign.setLastModified(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(LAST_MODIFIED))));
        campaign.setOwner(cursor.getString(cursor.getColumnIndex(OWNER)));
        campaign.setServerVersion(cursor.getLong(cursor.getColumnIndex(SERVER_VERSION)));

        return campaign;
    }
}
