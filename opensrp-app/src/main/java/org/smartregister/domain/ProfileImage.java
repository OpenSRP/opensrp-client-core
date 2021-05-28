package org.smartregister.domain;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;

import static java.text.MessageFormat.format;

public class ProfileImage {
    private String imageid;
    private String anmId;
    private String entityID;
    private String contenttype;
    private String filepath;
    private String syncStatus;
    private String filecategory;

    public ProfileImage(String imageid, String anmId, String entityID, String contenttype, String
            filepath, String syncStatus, String filecategory) {
        this.imageid = imageid;
        this.entityID = entityID;
        this.anmId = anmId;
        this.contenttype = contenttype;
        this.filepath = filepath;
        this.syncStatus = syncStatus;
        this.filecategory = filecategory;
    }

    public ProfileImage() {
    }

    public ProfileImage(String imageId) {
        this(imageId, null, null, null, null, null, null);
    }

    public String getFilecategory() {
        return filecategory;
    }

    public void setFilecategory(String filecategory) {
        this.filecategory = filecategory;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getImageUrl() {
        String url = format("{0}/{1}/{2}",
                CoreLibrary.getInstance().context().allSharedPreferences().fetchBaseURL(""),
                AllConstants.PROFILE_IMAGES_DOWNLOAD_PATH, entityID);
        return url;
    }
}
