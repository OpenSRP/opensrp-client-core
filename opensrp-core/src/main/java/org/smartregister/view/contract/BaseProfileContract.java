package org.smartregister.view.contract;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public interface BaseProfileContract {

    interface Presenter {

        void onDestroy(boolean isChangingConfiguration);

    }

    interface View {

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void displayToast(int resourceId);

        String getIntentString(String intentKey);

    }

}
