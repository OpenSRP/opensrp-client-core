package org.smartregister.view.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;

import java.util.List;

/**
 * Created by keyamn on 27/06/2018.
 */
public interface BaseRegisterContract {
    interface Presenter {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void onDestroy(boolean isChangingConfiguration);

        void updateInitials();
    }

    interface View {

        Context getContext();

        void displaySyncNotification();

        void displayToast(int resourceId);

        void displayToast(String message);

        void displayShortToast(int resourceId);

        void startFormActivity(JSONObject form);

        void refreshList(final FetchStatus fetchStatus);

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void updateInitialsText(String initials);
    }
}
