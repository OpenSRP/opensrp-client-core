package org.smartregister.view.viewholder;

import android.view.View;

import org.smartregister.view.activity.SecuredActivity;

public class OnClickFormLauncher implements View.OnClickListener {
    private SecuredActivity activity;
    private String formName;
    private String entityId;
    private String metaData;

    public OnClickFormLauncher(SecuredActivity activity, String formName, String entityId) {
        this(activity, formName, entityId, null);
    }

    public OnClickFormLauncher(SecuredActivity activity, String formName, String entityId, String
            metaData) {
        this.activity = activity;
        this.formName = formName;
        this.entityId = entityId;
        this.metaData = metaData;
    }

    @Override
    public void onClick(View view) {
        activity.startFormActivity(formName, entityId, metaData);
    }
}