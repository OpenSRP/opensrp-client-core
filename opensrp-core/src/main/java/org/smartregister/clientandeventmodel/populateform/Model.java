package org.smartregister.clientandeventmodel.populateform;

/**
 * Created by samuelgithengi on 1/23/18.
 */

public class Model {

    private String tag;

    private String openMRSEntity;

    private String openMRSEntityId;

    private String openMRSEntityParent;

    public String getTag() {
        return tag;
    }

    public Model(String tag, String openMRSEntity, String openMRSEntityId, String openMRSEntityParent) {
        this.tag = tag;
        this.openMRSEntity = openMRSEntity;
        this.openMRSEntityId = openMRSEntityId;
        this.openMRSEntityParent = openMRSEntityParent;
    }

    public String getOpenMRSEntity() {
        return openMRSEntity;
    }

    public String getOpenMRSEntityId() {
        return openMRSEntityId;
    }

    public String getOpenMRSEntityParent() {
        return openMRSEntityParent;
    }
}
