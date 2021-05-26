package org.smartregister.domain;

import static org.smartregister.domain.ResponseErrorStatus.malformed_url;
import static org.smartregister.domain.ResponseErrorStatus.not_found;
import static org.smartregister.domain.ResponseErrorStatus.timeout;
import static org.smartregister.domain.ResponseStatus.failure;

public class Response<T> {
    private ResponseStatus status;
    private T payload;
    private Long totalRecords;

    public Response(ResponseStatus status, T payload) {
        this.status = status;
        this.payload = payload;
    }

    public T payload() {
        return payload;
    }

    public ResponseStatus status() {
        return status;
    }

    public boolean isFailure() {
        return status.equals(failure);
    }

    public boolean isUrlError(){
        return status.displayValue().equals(malformed_url.name()) || status.displayValue().equals(not_found.name());
    }

    public boolean isTimeoutError(){
        return status.displayValue().equals(timeout.name());
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public Response withTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }
}
