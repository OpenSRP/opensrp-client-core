package org.smartregister.view.fragment;

import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;

import android.view.MotionEvent;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.view.activity.BaseProfileActivity;

/**
 * Created by ndegwamartin on 2020-04-21.
 */
public class BaseProfileFragmentTest extends BaseUnitTest {

    private BaseProfileFragment baseProfileFragment;

    @Mock
    private GestureDetectorCompat gestureDetector;

    @Mock
    private View view;

    @Mock
    private MotionEvent motionEvent;

    @Mock
    private Bundle bundle;

    @Mock
    private Context context;

    @Mock
    private MotionEvent motionEvent1;

    @Mock
    private MotionEvent motionEvent2;

    @Mock
    private BaseProfileActivity baseProfileActivity;

    @Mock
    private AppBarLayout appBarLayout;

    @Captor
    private ArgumentCaptor<BaseProfileFragment> baseProfileFragmentArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> appBarLayoutExpandedArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> appBarLayoutAnimateArgumentCaptor;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        baseProfileFragment = Mockito.mock(BaseProfileFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void assertFragmentInstantiatesCorrectly() {

        Assert.assertNotNull(baseProfileFragment);
    }

    @Test
    public void testOnViewCreatedAddsOnTouchListenerHandlerToView() {

        Mockito.when(context.IsUserLoggedOut()).thenReturn(false);
        Mockito.doReturn(context).when(baseProfileFragment).context();

        FragmentManager mChildFragmentManager = Mockito.mock(FragmentManager.class);
        Whitebox.setInternalState(baseProfileFragment, "mChildFragmentManager", mChildFragmentManager);

        baseProfileFragment.onViewCreated(view, bundle);

        Mockito.verify(view).setOnTouchListener(baseProfileFragmentArgumentCaptor.capture());
        Fragment baseProfileFragment = baseProfileFragmentArgumentCaptor.getValue();
        Assert.assertNotNull(baseProfileFragment);
        Assert.assertTrue(baseProfileFragment instanceof BaseProfileFragment);
    }


    @Test
    public void testOnViewCreatedInstantiatesValidGestureDetectorField() {

        Mockito.when(context.IsUserLoggedOut()).thenReturn(false);
        Mockito.doReturn(context).when(baseProfileFragment).context();

        FragmentManager mChildFragmentManager = Mockito.mock(FragmentManager.class);
        Whitebox.setInternalState(baseProfileFragment, "mChildFragmentManager", mChildFragmentManager);

        GestureDetectorCompat gestureDetector = ReflectionHelpers.getField(baseProfileFragment, "gestureDetector");
        Assert.assertNull(gestureDetector);

        baseProfileFragment.onViewCreated(view, bundle);

        gestureDetector = ReflectionHelpers.getField(baseProfileFragment, "gestureDetector");
        Assert.assertNotNull(gestureDetector);
    }

    @Test
    public void testOnTouchAssignsMotionEventHandler() {

        ReflectionHelpers.setField(baseProfileFragment, "gestureDetector", gestureDetector);
        baseProfileFragment.onTouch(view, motionEvent);

        Mockito.verify(gestureDetector).onTouchEvent(motionEvent);


    }

    @Test
    public void testOnFlingProcessorReturnsTrueIfXTranlationGreatherThanSwipeOffMaxPath() {

        Mockito.doReturn(500f).when(motionEvent1).getX();
        Mockito.doReturn(100f).when(motionEvent2).getX();

        Boolean result = baseProfileFragment.onFlingProcessor(motionEvent1, motionEvent2, 1.0f);

        Assert.assertNotNull(result);
        Assert.assertTrue(result);

    }

    @Test
    public void testOnFlingProcessorReturnsFalseIfXTranlationLessThanSwipeOffMaxPath() {

        Mockito.doReturn(400f).when(motionEvent1).getX();
        Mockito.doReturn(300f).when(motionEvent2).getX();

        Boolean result = baseProfileFragment.onFlingProcessor(motionEvent1, motionEvent2, 1.0f);

        Assert.assertNotNull(result);
        Assert.assertFalse(result);

    }

    @Test
    public void testOnFlingProcessorSetsAppBarLayoutExpandedToTrueWhenYTranlationGreaterThanSwipeOffMinPath() {

        Mockito.doReturn(200f).when(motionEvent1).getY();
        Mockito.doReturn(50f).when(motionEvent2).getY();

        Mockito.doReturn(baseProfileActivity).when(baseProfileFragment).getActivity();
        Mockito.doReturn(appBarLayout).when(baseProfileActivity).getProfileAppBarLayout();

        Boolean result = baseProfileFragment.onFlingProcessor(motionEvent1, motionEvent2, 250.0f);

        Assert.assertNotNull(result);
        Assert.assertFalse(result);

        Mockito.verify(appBarLayout).setExpanded(appBarLayoutExpandedArgumentCaptor.capture(), appBarLayoutAnimateArgumentCaptor.capture());
        Boolean isExpanded = appBarLayoutExpandedArgumentCaptor.getValue();
        Assert.assertNotNull(isExpanded);
        Assert.assertFalse(isExpanded);

    }

    @Test
    public void testOnFlingProcessorSetsAppBarLayoutExpandedToFalseWhenYTranlationLessThanSwipeOffMinPath() {

        Mockito.doReturn(100f).when(motionEvent1).getY();
        Mockito.doReturn(550f).when(motionEvent2).getY();

        Mockito.doReturn(baseProfileActivity).when(baseProfileFragment).getActivity();
        Mockito.doReturn(appBarLayout).when(baseProfileActivity).getProfileAppBarLayout();

        Boolean result = baseProfileFragment.onFlingProcessor(motionEvent1, motionEvent2, 350.0f);

        Assert.assertNotNull(result);

        Assert.assertFalse(result);

        Mockito.verify(appBarLayout).setExpanded(appBarLayoutExpandedArgumentCaptor.capture(), appBarLayoutAnimateArgumentCaptor.capture());
        Boolean isExpanded = appBarLayoutExpandedArgumentCaptor.getValue();

        Assert.assertNotNull(isExpanded);
        Assert.assertTrue(isExpanded);

    }
}