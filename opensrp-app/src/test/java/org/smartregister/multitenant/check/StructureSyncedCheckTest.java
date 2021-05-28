package org.smartregister.multitenant.check;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.FetchStatus;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.StructureRepository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-11-2020.
 */
public class StructureSyncedCheckTest extends BaseRobolectricUnitTest {

    private StructureSyncedCheck structureSyncedCheck;
    private Timber.Tree mockitoTree;

    @Before
    public void setUp() throws Exception {
        structureSyncedCheck = new StructureSyncedCheck();
        mockitoTree = Mockito.mock(Timber.Tree.class);
        Timber.plant(mockitoTree);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(mockitoTree);
    }

    @Test
    public void isCheckOkShouldReturnTrueWhenStructuresAreSynced() {
        structureSyncedCheck = Mockito.spy(structureSyncedCheck);
        Mockito.doReturn(true).when(structureSyncedCheck).isStructuresSynced(Mockito.eq(DrishtiApplication.getInstance()));

        Assert.assertTrue(structureSyncedCheck.isCheckOk(DrishtiApplication.getInstance()));
    }

    @Test
    public void isCheckOkShouldReturnFalseWhenStructuresAreNotSynced() {
        structureSyncedCheck = Mockito.spy(structureSyncedCheck);
        Mockito.doReturn(false).when(structureSyncedCheck).isStructuresSynced(Mockito.eq(DrishtiApplication.getInstance()));

        Assert.assertFalse(structureSyncedCheck.isCheckOk(DrishtiApplication.getInstance()));
    }

    @Test
    public void performPreResetAppOperationsShouldCallLocationServiceHelperEvidenceThroughCallingStructureRepository() throws PreResetAppOperationException {
        // Spy on structure-repository that is called when location repository wants to sync
        StructureRepository structureRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getStructureRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "structureRepository", structureRepository);

        structureSyncedCheck.performPreResetAppOperations(DrishtiApplication.getInstance());

        Mockito.verify(structureRepository).getAllUnsynchedCreatedStructures();
    }

    @Test
    public void isStructuresSyncedShouldReturnFalseWhenUnsyncedStructuresCountIsMoreThan0() {
        StructureRepository structureRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getStructureRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "structureRepository", structureRepository);

        Mockito.doReturn(2).when(structureRepository).getUnsyncedStructuresCount();

        Assert.assertFalse(structureSyncedCheck.isStructuresSynced(DrishtiApplication.getInstance()));
    }

    @Test
    public void isStructuresSyncedShouldReturnTrueWhenUnsyncedStructuresCountIsMoreThan0() {
        StructureRepository structureRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getStructureRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "structureRepository", structureRepository);

        Mockito.doReturn(0).when(structureRepository).getUnsyncedStructuresCount();

        Assert.assertTrue(structureSyncedCheck.isStructuresSynced(DrishtiApplication.getInstance()));
    }

    @Test
    public void onSyncStartShouldLogTextAsError() {
        structureSyncedCheck.onSyncStart();

        Mockito.verify(mockitoTree).e("Sync is starting");
    }

    @Test
    public void onSyncInProgressShouldLogWhenFetchStatusIsProgress() {
        structureSyncedCheck.onSyncInProgress(FetchStatus.fetchProgress);

        Mockito.verify(mockitoTree).e("Sync progress is %s", FetchStatus.fetchProgress.displayValue());
    }

    @Test
    public void onSyncInProgressShouldNotLogWhenFetchStatusIsNotProgress() {
        structureSyncedCheck.onSyncInProgress(FetchStatus.fetchStarted);

        Mockito.verify(mockitoTree, Mockito.times(0)).e("Sync progress is %s", FetchStatus.fetchProgress.displayValue());
    }

    @Test
    public void onSyncCompleteShouldLogTextAsError() {
        structureSyncedCheck.onSyncComplete(FetchStatus.fetched);

        Mockito.verify(mockitoTree).e("The sync is complete");
    }

    @Test
    public void getUniqueName() {
        Assert.assertEquals("StructureSyncedCheck", structureSyncedCheck.getUniqueName());
    }
}