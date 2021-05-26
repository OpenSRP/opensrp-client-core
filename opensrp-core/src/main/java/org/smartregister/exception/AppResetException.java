package org.smartregister.exception;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class AppResetException extends Exception {

    public AppResetException() {
    }

    public AppResetException(String message) {
        super(message);
    }

    public AppResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
