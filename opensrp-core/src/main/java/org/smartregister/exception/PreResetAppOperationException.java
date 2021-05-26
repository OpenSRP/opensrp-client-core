package org.smartregister.exception;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class PreResetAppOperationException extends Exception {

    public PreResetAppOperationException() {
        super();
    }

    public PreResetAppOperationException(String message) {
        super(message);
    }

    public PreResetAppOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreResetAppOperationException(Throwable cause) {
        super(cause);
    }
}
