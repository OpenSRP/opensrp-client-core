package org.smartregister.exception;

/**
 * Created by ndegwamartin on 2019-11-27.
 * <p>
 * This class is Deprecated and was only reinstalled to prevent build breaks for anyone upgrading to the latter versions of the core lib
 */
public class JsonFormMissingStepCountException extends Exception {

    public JsonFormMissingStepCountException(String errorMessage) {
        super(errorMessage);
    }
}
