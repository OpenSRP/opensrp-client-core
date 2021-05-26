package org.smartregister.exception;

import java.io.IOException;

/**
 * Created by samuelgithengi on 5/20/19.
 */
public class NoHttpResponseException extends IOException {

    public NoHttpResponseException(String message) {
        super(message);
    }
}
