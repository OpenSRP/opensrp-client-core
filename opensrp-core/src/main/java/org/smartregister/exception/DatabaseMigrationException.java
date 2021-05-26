package org.smartregister.exception;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 14/05/2019
 */

public class DatabaseMigrationException extends RuntimeException {

    public DatabaseMigrationException(String message) {
        super(message);
    }
}
