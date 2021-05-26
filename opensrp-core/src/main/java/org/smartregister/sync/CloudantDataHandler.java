package org.smartregister.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.query.IndexManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.R;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.cloudant.models.Client;
import org.smartregister.cloudant.models.Event;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Handles Cloudant data access methods
 * Created by onamacuser on 11/03/2016.
 */

public class CloudantDataHandler {
    private static final String baseEntityIdJSONKey = "baseEntityId";
    private static final String DATASTORE_MANGER_DIR = "data";
    private static final String DATASTORE_NAME = "opensrp_clients_events";
    private static CloudantDataHandler instance;
    private final Context mContext;
    private final Datastore mDatastore;
    private final IndexManager mIndexManager;
    private final SQLiteDatabase mDatabase;

    public CloudantDataHandler(Context context) throws Exception {
        this.mContext = context;

        // Set up our datastore within its own folder in the applications data directory.
        File path = this.mContext.getApplicationContext()
                .getDir(DATASTORE_MANGER_DIR, Context.MODE_PRIVATE);
        DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
        this.mDatastore = manager.openDatastore(DATASTORE_NAME);

        this.mIndexManager = new IndexManager(mDatastore);
        List<Object> indexFields = new ArrayList<>();
        // indexFields.add("version");
        indexFields.add("type");
        indexFields.add(baseEntityIdJSONKey);
        this.mIndexManager.ensureIndexed(indexFields, "eventdocindex");
        this.mDatabase = loadDatabase();

        Timber.d("Set up database at %s", path.getAbsolutePath());

        instance = this;

    }

    public static CloudantDataHandler getInstance(Context context) throws Exception {
        if (instance == null) {
            instance = new CloudantDataHandler(context);
        }
        return instance;
    }

    public JSONObject getClientByBaseEntityId(String baseEntityId) throws Exception {
        try {
            String documentId = getClientDocumentIdByBaseEntityId(baseEntityId);

            if (StringUtils.isNotBlank(documentId)) {
                SQLiteDatabase db = loadDatabase();
                String query = "select json from revs r inner join docs d on r.doc_id=d.doc_id "
                        + "where  d.docid=? and length(json)>2 order by "
                        + "updated_at desc";
                Timber.i(query);
                Cursor cursor = db.rawQuery(query, new String[]{documentId});
                if (cursor != null && cursor.moveToFirst()) {
                    byte[] json = (cursor.getBlob(0));
                    String jsonEventStr = new String(json, "UTF-8");
                    if (StringUtils.isNotBlank(jsonEventStr) && !jsonEventStr.equals("{}")) { //
                        // Check blank/empty json string
                        JSONObject jsonObectClient = new JSONObject(jsonEventStr);
                        return jsonObectClient;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public Client getClientDocumentByBaseEntityId(String baseEntityId) throws Exception {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        Map<String, Object> query = new HashMap<String, Object>();
        query.put("type", "Client");
        query.put(baseEntityIdJSONKey, baseEntityId);

        Iterator<DocumentRevision> iterator = this.mIndexManager.find(query).iterator();

        if (iterator != null && iterator.hasNext()) {
            DocumentRevision rev = iterator.next();
            Client c = Client.fromRevision(rev);
            return c;
        }

        return null;
    }

    public String getClientDocumentIdByBaseEntityId(String baseEntityId) throws Exception {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        Map<String, Object> query = new HashMap<String, Object>();
        query.put("type", "Client");
        query.put(baseEntityIdJSONKey, baseEntityId);

        List<String> fields = Arrays.asList(baseEntityIdJSONKey);

        Iterator<DocumentRevision> iterator = this.mIndexManager.find(query, 0, 0, fields, null)
                .iterator();

        if (iterator != null && iterator.hasNext()) {
            DocumentRevision rev = iterator.next();
            return rev.getId();
        }

        return null;
    }

    public List<JSONObject> getUpdatedEventsAndAlerts(Date lastSyncDate) throws Exception {

        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);

        List<JSONObject> eventAndAlerts = new ArrayList<JSONObject>();
        SQLiteDatabase db = loadDatabase();
        String query = "select json, updated_at from revs where updated_at > ?" +
                "  and length(json)>2 order by updated_at asc ";
        Timber.i(query);
        Cursor cursor = db.rawQuery(query, new String[]{lastSyncString});

        try {
            while (cursor.moveToNext()) {
                byte[] json = (cursor.getBlob(0));
                String jsonEventStr = new String(json, "UTF-8");
                if (StringUtils.isBlank(jsonEventStr) || jsonEventStr.equals("{}")) { // Skip
                    // blank/empty json string
                    continue;
                }

                JSONObject jsonObectEventOrAlert = new JSONObject(jsonEventStr);
                String type =
                        jsonObectEventOrAlert.has("type") ? jsonObectEventOrAlert.getString("type")
                                : null;
                if (StringUtils.isBlank(type)) { // Skip blank types
                    continue;
                }

                if (!type.equals("Event") && !type.equals("Action")) { // Skip type that isn't
                    // Event or Action
                    continue;
                }

                eventAndAlerts.add(jsonObectEventOrAlert);
                try {
                    lastSyncDate
                            .setTime(DateUtil.yyyyMMddHHmmss.parse(cursor.getString(1)).getTime());
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }
        } finally {
            cursor.close();
        }

        if (eventAndAlerts.isEmpty()) {
            return eventAndAlerts;
        }

        Collections.sort(eventAndAlerts, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    String lhvar = "version";
                    String rhvar = "version";
                    if (lhs.getString("type") == "Action") {
                        lhvar = "timeStamp";
                    }

                    if (rhs.getString("type") == "Action") {
                        rhvar = "timeStamp";
                    }

                    if (!lhs.has(lhvar)) {
                        return 1;
                    }
                    if (!rhs.has(rhvar)) {
                        return -1;
                    }
                    if (lhs.getLong(lhvar) > rhs.getLong(rhvar)) {
                        return 1;
                    }
                    if (lhs.getLong(lhvar) < rhs.getLong(rhvar)) {
                        return -1;
                    }
                    return 0;
                } catch (JSONException e) {
                    return -1;
                }
            }
        });

        return eventAndAlerts;
    }

    //load cloudant db
    public SQLiteDatabase loadDatabase() {
        if (this.mDatabase != null && this.mDatabase.isOpen()) {
            return mDatabase;
        }

        SQLiteDatabase db = null;
        try {
            String dataStoreName = mContext.getString(R.string.datastore_name);
            // data directory.
            File path = mContext.getDir(AllConstants.DATASTORE_MANAGER_DIR,
                    android.content.Context.MODE_PRIVATE);
            String dbpath = path.getAbsolutePath().concat(File.separator).concat(dataStoreName)
                    .concat(File.separator).concat("db.sync");
            db = SQLiteDatabase.openDatabase(dbpath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            // ((HelloCloudantApplication) this.getApplication()).setCloudantDB(db);

        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
        return db;
    }

    /**
     * Updates a Client document within the datastore.
     *
     * @param client client to update
     * @return the updated revision of the Client
     * @throws ConflictException if the client passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public Client updateDocument(Client client) throws ConflictException {
        DocumentRevision rev = client.getDocumentRevision();
        rev.setBody(DocumentBodyFactory.create(client.asMap()));
        try {
            DocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return Client.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    //
    // DOCUMENT CRUD
    //

    /**
     * Creates a Client, assigning an ID.
     *
     * @param client Client to create
     * @return new revision of the document
     */
    public Client createClientDocument(Client client) {
        DocumentRevision rev = new DocumentRevision();
        rev.setBody(DocumentBodyFactory.create(client.asMap()));
        try {
            //save the model only once if it already exist ignore, or merge the document
            Client c = getClientDocumentByBaseEntityId(client.getBaseEntityId());

            if (c == null) {
                DocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
                return Client.fromRevision(created);
            } else {
                //TODO: merge/update the client document
                DocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
                return Client.fromRevision(created);
//                DocumentRevision revupdate = c.getDocumentRevision();
//                revupdate.setBody(DocumentBodyFactory.create(client.asMap()));
//                DocumentRevision updated = this.mDatastore.updateDocumentFromRevision(revupdate);
//                return Client.fromRevision(updated);
//                return c;
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public Client addClient(Client client) {
        DocumentRevision rev = new DocumentRevision();
        rev.setBody(DocumentBodyFactory.create(client.asMap()));
        try {

            DocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            return Client.fromRevision(created);

        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    /**
     * Creates a Event, assigning an ID.
     *
     * @param event Client to create
     * @return new revision of the document
     */
    public Event createEventDocument(Event event) {
        DocumentRevision rev = new DocumentRevision();
        rev.setBody(DocumentBodyFactory.create(event.asMap()));
        try {
            DocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            return Event.fromRevision(created);
        } catch (DocumentException de) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Deletes a Client document within the datastore.
     *
     * @param client client to delete
     * @throws ConflictException if the client passed in has a rev which doesn't
     *                           match the current rev in the datastore.
     */
    public void deleteDocument(Client client) throws ConflictException {
        this.mDatastore.deleteDocumentFromRevision(client.getDocumentRevision());
    }

    /**
     * <p>Returns all {@code Client} documents in the datastore.</p>
     */
    public List<Client> allClients() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<DocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<Client> clients = new ArrayList<Client>();
        // Filter all documents down to those of type client.
        for (DocumentRevision rev : all) {
            Client client = null;
            try {
                client = Client.fromRevision(rev);
            } catch (ParseException e) {
                Timber.e(e);
            }
            if (client != null) {
                clients.add(client);
            }
        }

        return clients;
    }

    public Datastore getDatastore() {
        return mDatastore;
    }
}
