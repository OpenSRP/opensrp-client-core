package org.smartregister.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.receiver.ValidateAssignmentReceiver.UserAssignmentListener;
import org.smartregister.sync.helper.ValidateAssignmentHelper;

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smartregister.sync.helper.ValidateAssignmentHelper.ACTION_ASSIGNMENT_REMOVED;

/**
 * Created by samuelgithengi on 9/24/20.
 */
public class ValidateAssignmentReceiverTest extends BaseRobolectricUnitTest {

    private ValidateAssignmentReceiver validateAssignmentReceiver;

    @Mock
    private UserAssignmentListener listener;

    @Mock
    private UserAssignmentDTO assignment;

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<IntentFilter> intentFilterArgumentCaptor;

    @Before
    public void setup() {
        ValidateAssignmentReceiver.init(RuntimeEnvironment.application);
        validateAssignmentReceiver = ValidateAssignmentReceiver.getInstance();
    }

    @Test
    public void testGetInstanceShouldReturnInstance() {
        assertNotNull(ValidateAssignmentReceiver.getInstance());
    }

    @Test
    public void testDestroyShouldCleanupAndUnRegisterReceiver() {
        ValidateAssignmentReceiver.destroy(context);
        assertNull(ValidateAssignmentReceiver.getInstance());
        verify(context).unregisterReceiver(validateAssignmentReceiver);
    }


    @Test
    public void testInitShouldInitAndRegisterReceiver() {
        ValidateAssignmentReceiver.init(context);
        verify(context).unregisterReceiver(any());
        verify(context).registerReceiver(eq(ValidateAssignmentReceiver.getInstance()), intentFilterArgumentCaptor.capture());
        assertNotNull(ValidateAssignmentReceiver.getInstance());
        assertEquals(ACTION_ASSIGNMENT_REMOVED, intentFilterArgumentCaptor.getValue().getAction(0));
    }

    @Test
    public void testAddListenerShouldAddListenerOnceAndInOrderOfAddition() {
        validateAssignmentReceiver.addListener(listener);
        validateAssignmentReceiver.addListener(listener);
        UserAssignmentListener listener2 = mock(UserAssignmentListener.class);
        validateAssignmentReceiver.addListener(listener2);
        Set<UserAssignmentListener> listeners = Whitebox.getInternalState(validateAssignmentReceiver, "userAssignmentListeners");
        assertEquals(2, listeners.size());
        Iterator<UserAssignmentListener> iterator = listeners.iterator();
        assertEquals(listener, iterator.next());
        assertEquals(listener2, iterator.next());
    }

    @Test
    public void testRemoveListenerShouldRemoveListener() {
        validateAssignmentReceiver.addListener(listener);
        Set<UserAssignmentListener> listeners = Whitebox.getInternalState(validateAssignmentReceiver, "userAssignmentListeners");
        assertEquals(1, listeners.size());
        assertEquals(listener, listeners.iterator().next());

        validateAssignmentReceiver.removeLister(mock(UserAssignmentListener.class));
        listeners = Whitebox.getInternalState(validateAssignmentReceiver, "userAssignmentListeners");
        assertEquals(1, listeners.size());

        validateAssignmentReceiver.removeLister(listener);
        listeners = Whitebox.getInternalState(validateAssignmentReceiver, "userAssignmentListeners");
        assertEquals(0, listeners.size());
    }

    @Test
    public void testOnReceiveShouldNotifyAllListeners() {
        validateAssignmentReceiver.addListener(listener);
        UserAssignmentListener listener2 = mock(UserAssignmentListener.class);
        validateAssignmentReceiver.addListener(listener2);
        Intent intent = new Intent();
        intent.putExtra(ValidateAssignmentHelper.ASSIGNMENTS_REMOVED, assignment);
        validateAssignmentReceiver.onReceive(RuntimeEnvironment.application, intent);
        verify(listener).onUserAssignmentRevoked(assignment);
        verify(listener2).onUserAssignmentRevoked(assignment);
    }


}
