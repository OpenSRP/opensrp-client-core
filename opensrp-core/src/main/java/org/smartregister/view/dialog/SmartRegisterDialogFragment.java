package org.smartregister.view.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

public class SmartRegisterDialogFragment extends DialogFragment {
    private final SecuredNativeSmartRegisterActivity parentActivity;
    private final DialogOption[] options;
    private final DialogOptionModel dialogOptionModel;
    private final Object tag;

    private SmartRegisterDialogFragment(SecuredNativeSmartRegisterActivity activity,
                                        DialogOptionModel dialogOptionModel, Object tag) {
        this.parentActivity = activity;
        this.options = dialogOptionModel.getDialogOptions();
        this.dialogOptionModel = dialogOptionModel;
        this.tag = tag;
    }

    public static SmartRegisterDialogFragment newInstance(SecuredNativeSmartRegisterActivity
                                                                  activity, DialogOptionModel
                                                                  dialogOptionModel, Object tag) {
        return new SmartRegisterDialogFragment(activity, dialogOptionModel, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle
            savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater
                .inflate(R.layout.smart_register_dialog_view, container, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.dialog_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final ArrayAdapter<DialogOption> adapter = new ArrayAdapter<DialogOption>(parentActivity,
                R.layout.smart_register_dialog_list_item, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewGroup itemView;
                if (convertView == null) {
                    itemView = (ViewGroup) inflater
                            .inflate(R.layout.smart_register_dialog_list_item, parent, false);
                } else {
                    itemView = (ViewGroup) convertView;
                }

                ((TextView) itemView.findViewById(R.id.dialog_list_option))
                        .setText(getItem(position).name());
                return itemView;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                dialogOptionModel.onDialogOptionSelection(options[i], tag);
            }
        });

        return dialogView;
    }
}
