package org.smartregister.event;

public class CapturedPhotoInformation {
    private final String entityId;
    private final String photoPath;

    public CapturedPhotoInformation(String entityId, String photoPath) {
        this.entityId = entityId;
        this.photoPath = photoPath;
    }

    public String entityId() {
        return entityId;
    }

    public String photoPath() {
        return photoPath;
    }
}
