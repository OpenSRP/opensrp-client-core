package org.smartregister.domain;

import org.joda.time.DateTime;
import org.smartregister.domain.Task.TaskStatus;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class Campaign {
    private String identifier;

    private String title;

    private String description;

    private TaskStatus status;

    private Period executionPeriod;

    private DateTime authoredOn;

    private DateTime lastModified;

    private String owner;

    private long serverVersion;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Period getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(Period executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    public void setAuthoredOn(DateTime authoredOn) {
        this.authoredOn = authoredOn;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(long serverVersion) {
        this.serverVersion = serverVersion;
    }

}
