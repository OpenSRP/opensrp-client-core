package org.smartregister.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.FetchStatus;
import org.smartregister.event.Event;

public class CacheTest extends BaseUnitTest {
    @Mock
    private CacheableData<String> cacheableData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetAndCacheValueOnlyWhenItDoesNotExist() throws Exception {
        Cache<String> cache = new Cache<String>();
        Mockito.when(cacheableData.fetch()).thenReturn("value");

        Assert.assertEquals("value", cache.get("key", cacheableData));

        Mockito.verify(cacheableData).fetch();

        Assert.assertEquals("value", cache.get("key", cacheableData));

        Mockito.verify(cacheableData, Mockito.times(1)).fetch();
    }

    @Test
    public void shouldClearCacheWhenActionsAreUpdated() throws Exception {
        Cache<String> cache = new Cache<String>();
        Mockito.when(cacheableData.fetch()).thenReturn("value");

        cache.get("key", cacheableData);
        Event.ON_DATA_FETCHED.notifyListeners(FetchStatus.fetched);

        Assert.assertEquals("value", cache.get("key", cacheableData));
        Mockito.verify(cacheableData, Mockito.times(2)).fetch();

        Event.ON_DATA_FETCHED.notifyListeners(FetchStatus.fetchedFailed);
        Assert.assertEquals("value", cache.get("key", cacheableData));
        Mockito.verify(cacheableData, Mockito.times(2)).fetch();
    }

    @Test
    public void shouldClearCacheWhenFormIsSubmitted() throws Exception {
        Cache<String> cache = new Cache<String>();
        Mockito.when(cacheableData.fetch()).thenReturn("value");

        cache.get("key", cacheableData);
        Event.FORM_SUBMITTED.notifyListeners("ec_registration");

        Assert.assertEquals("value", cache.get("key", cacheableData));
        Mockito.verify(cacheableData, Mockito.times(2)).fetch();
    }
}
