package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.view.activity.DrishtiApplication;

public abstract class DrishtiRepository {
    private Repository masterRepository;

    public void updateMasterRepository(Repository repository) {
        this.masterRepository = repository;
    }

    protected Repository masterRepository(){
        if(masterRepository == null){
            masterRepository = DrishtiApplication.getInstance().getRepository();
        }

        return masterRepository;
    }


    abstract protected void onCreate(SQLiteDatabase database);

}
