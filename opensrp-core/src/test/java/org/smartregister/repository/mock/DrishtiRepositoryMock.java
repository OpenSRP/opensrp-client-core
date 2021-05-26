package org.smartregister.repository.mock;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.DrishtiRepository;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class DrishtiRepositoryMock {
    public static DrishtiRepository getDrishtiRepository() {
        return new DrishtiRepository() {
            @Override
            protected void onCreate(SQLiteDatabase database) {
                System.out.println();
            }
        };
    }
}
