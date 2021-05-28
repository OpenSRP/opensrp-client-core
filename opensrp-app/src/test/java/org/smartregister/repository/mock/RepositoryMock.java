package org.smartregister.repository.mock;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Session;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class RepositoryMock extends Repository {


    public RepositoryMock(Context context, Session session, DrishtiRepository... repositories) {
        super(context, session, repositories);
    }

    public RepositoryMock(Context context, Session session, CommonFtsObject commonFtsObject, DrishtiRepository... repositories) {
        super(context, session, commonFtsObject, repositories);
    }

    public RepositoryMock(Context context, String dbName, int version, Session session, CommonFtsObject commonFtsObject, DrishtiRepository... repositories) {
        super(context, dbName, version, session, commonFtsObject, repositories);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        super.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public boolean canUseThisPassword(byte[] password) {
        return super.canUseThisPassword(password);
    }

    public Repository getInstance1() {
        try {
            //Libraryname without .so!!
            System.loadLibrary("sqlcipher");
        } catch (UnsatisfiedLinkError ex) {
            //Not found
        }
        Session session = new Session();
        session.setRepositoryName(AllConstants.DATABASE_NAME);
        DrishtiRepository drishtiRepositories[] = {DrishtiRepositoryMock.getDrishtiRepository()};
        Repository repository = new Repository(ContextMock.getContext(), session, drishtiRepositories);
        return repository;
    }
}
