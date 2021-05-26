package org.smartregister.view.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.R;

/**
 * Created by ndegwamartin on 2020-04-07.
 */
public class LibraryFragmentTest {

    private LibraryFragment libraryFragment;

    @Mock
    private LayoutInflater inflater;

    @Mock
    private ViewGroup container;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        libraryFragment = Mockito.mock(LibraryFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void assertLibraryFragmentInitsCorrectly() {
        Assert.assertNotNull(libraryFragment);
    }

    @Test
    public void assertOnCreateViewInflatesCorrectLayout() {

        libraryFragment.onCreateView(inflater, container, null);
        Assert.assertNotNull(libraryFragment);

        Mockito.verify(inflater).inflate(R.layout.fragment_library, container, false);
    }

}