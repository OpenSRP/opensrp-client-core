package org.smartregister.view.dialog.mock;

import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class DialogOptionModalMock {

    public static DialogOptionModel getDialogOptionModal() {
        return new DialogOptionModel() {
            @Override
            public DialogOption[] getDialogOptions() {
                return new DialogOption[0];
            }

            @Override
            public void onDialogOptionSelection(DialogOption option, Object tag) {

            }
        };
    }

}
