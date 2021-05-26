package org.smartregister.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.base.Strings;

import org.json.JSONObject;
import org.smartregister.R;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.Map;

import atv.holder.SelectableItemHolder;
import atv.model.TreeNode;
import atv.view.AndroidTreeView;

import static org.smartregister.util.StringUtil.humanize;

public class LocationSelectorDialogFragment extends DialogFragment {

    private static final String LocationJSONString = "locationJSONString";
    private static final String FormName = "formName";

    public static String savestate;
    AndroidTreeView tView;

    OnLocationSelectedListener mCallback;

    public static LocationSelectorDialogFragment newInstance(SecuredNativeSmartRegisterActivity
                                                                     activity, DialogOptionModel
                                                                     dialogOptionModel, String locationJSONString, String formname) {

        LocationSelectorDialogFragment lsd = new LocationSelectorDialogFragment();
        Bundle bundle = new Bundle(2);
        bundle.putString(LocationJSONString, locationJSONString);
        bundle.putString(FormName, formname);
        lsd.setArguments(bundle);
        return lsd;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnLocationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement " + "OnLocationSelectedListener.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle
            savedInstanceState) {
        ViewGroup dialogView = new LinearLayout(getActivity());
        TreeNode root = TreeNode.root();

        LocationTree locationTree = AssetHandler.jsonStringToJava(getArguments().getString(LocationJSONString), LocationTree.class);

        Map<String, org.smartregister.domain.jsonmapping.util.TreeNode<String, Location>> locationMap = locationTree
                .getLocationsHierarchy();

        // creating the tree
        locationTreeToTreNode(root, locationMap, getArguments().getString(FormName));

        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        tView.setSelectionModeEnabled(false);

        if (savestate != null) {
            tView.restoreState(savestate);
        }

        // tView.getSelected().get(1).
        dialogView.addView(tView.getView());
        return dialogView;
    }

    public TreeNode createNode(String locationlevel, String locationname, String formName) {
        TreeNode node = new TreeNode(locationname, locationlevel)
                .setViewHolder(new SelectableItemHolder(getActivity(), locationlevel + ": "));
        node.setSelectable(false);
        addselectlistener(node, formName);
        return node;
    }

    public void addChildToParentNode(TreeNode parent, TreeNode[] nodes) {
        for (TreeNode node : nodes) {
            parent.addChild(node);
        }
    }

    public void addselectlistener(TreeNode node, final String formName) {
        node.setClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                if (node.isLeaf()) {
                    JSONObject locationjson = new JSONObject();
                    TreeNode traversingnode = node;
                    while (!traversingnode.isRoot()) {
                        try {
                            locationjson.put(traversingnode.getlocationlevel(),
                                    traversingnode.getName());
                        } catch (Exception e) {

                        }
                        traversingnode = traversingnode.getParent();
                    }
                    if (mCallback != null) {
                        mCallback.OnLocationSelected(locationjson.toString());
                        // FieldOverrides fieldOverrides = new FieldOverrides(locationjson
                        // .toString());
                        // ((SecuredNativeSmartRegisterActivity)getActivity()).startFormActivity
                        // (formName, null, fieldOverrides.getJSONString());
                    }
                    savestate = tView.getSaveState();
                    dismiss();
                }
            }
        });
    }

    public void locationTreeToTreNode(TreeNode node, Map<String,
            org.smartregister.domain.jsonmapping.util.TreeNode<String, Location>> location, String formName) {

        for (Map.Entry<String, org.smartregister.domain.jsonmapping.util.TreeNode<String, Location>> entry : location
                .entrySet()) {
            String locationTag = entry.getValue().getNode().getTags().iterator().next();
            TreeNode tree = createNode(
                    Strings.isNullOrEmpty(locationTag) ? "-" : humanize(locationTag),
                    humanize(entry.getValue().getLabel()), formName);
            node.addChild(tree);
            if (entry.getValue().getChildren() != null) {
                locationTreeToTreNode(tree, entry.getValue().getChildren(), formName);
            }
        }
    }

    public interface OnLocationSelectedListener {
        void OnLocationSelected(String locationSelected);
    }

}