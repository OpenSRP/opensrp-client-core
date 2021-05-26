package org.smartregister.customshadows;

import android.content.Context;
import android.view.View;

import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;

import atv.model.TreeNode;
import atv.view.AndroidTreeView;

/**
 * Created by onadev on 15/06/2017.
 */
@Implements(AndroidTreeView.class)
public class AndroidTreeViewShadow extends Shadow {

    Context context;

    public void __constructor__(Context context, TreeNode root) {
        this.context = context;
    }

    public View getView(int style) {
        return new View(context);
    }

    public void setSelectionModeEnabled(boolean selectionModeEnabled) {


    }

}
