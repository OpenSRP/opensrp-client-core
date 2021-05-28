package org.smartregister.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.sync.helper.ValidateAssignmentHelper;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 9/21/20.
 */
public class ValidateAssignmentReceiver extends BroadcastReceiver {

    private static ValidateAssignmentReceiver instance;

    private Set<UserAssignmentListener> userAssignmentListeners;

    public static void init(Context context) {
        if (instance != null) {
            destroy(context);
        }
        instance = new ValidateAssignmentReceiver();
        context.registerReceiver(instance,
                new IntentFilter(ValidateAssignmentHelper.ACTION_ASSIGNMENT_REMOVED));
    }

    public static void destroy(Context context) {
        try {
            if (instance != null) {
                context.unregisterReceiver(instance);
                instance = null;
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    public static ValidateAssignmentReceiver getInstance() {
        return instance;
    }

    private ValidateAssignmentReceiver() {
        userAssignmentListeners = new LinkedHashSet<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            Serializable removedAssignmentsSerializable = data.getSerializable(ValidateAssignmentHelper.ASSIGNMENTS_REMOVED);
            if (removedAssignmentsSerializable instanceof UserAssignmentDTO) {
                UserAssignmentDTO userAssignment = (UserAssignmentDTO) removedAssignmentsSerializable;
                for (UserAssignmentListener listener : userAssignmentListeners) {
                    listener.onUserAssignmentRevoked(userAssignment);
                }
            }
        }

    }

    public void addListener(UserAssignmentListener userAssignmentListener) {
        userAssignmentListeners.add(userAssignmentListener);
    }

    public void removeLister(UserAssignmentListener userAssignmentListener) {
        userAssignmentListeners.remove(userAssignmentListener);
    }

    public interface UserAssignmentListener {

        void onUserAssignmentRevoked(UserAssignmentDTO userAssignment);

    }
}
