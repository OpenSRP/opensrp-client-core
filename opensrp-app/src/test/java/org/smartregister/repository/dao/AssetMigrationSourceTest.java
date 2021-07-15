package org.smartregister.repository.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.repository.contract.MigrationSource;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 02-02-2021.
 */
public class AssetMigrationSourceTest extends BaseRobolectricUnitTest {

    private AssetMigrationSource assetMigrationSource;

    @Before
    public void setUp() throws Exception {
        assetMigrationSource = new AssetMigrationSource(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getMigrationsShouldReturnAllMigrationsInAssetsFolder() {
        HashMap<Integer, ArrayList<MigrationSource.Migration>> migrations =
                assetMigrationSource.getMigrations();

        assertEquals(3, migrations.size());
        assertEquals(MigrationSource.Migration.MigrationType.UP, migrations.get(1).get(0).getMigrationType());
        assertEquals(MigrationSource.Migration.MigrationType.UP, migrations.get(2).get(0).getMigrationType());
        assertEquals("CREATE IF NOT EXISTS TABLE clients(id INTEGER, full_name VARCHAR, age INTEGER, dob INTEGER);"
                , migrations.get(2).get(0).getUpMigrationQueries()[0]);
        assertEquals(MigrationSource.Migration.MigrationType.UP, migrations.get(3).get(0).getMigrationType());
    }

    @Test
    public void getMigrationsShouldReturnMigrationsFromV2() {

        HashMap<Integer, ArrayList<MigrationSource.Migration>> migrations =
                assetMigrationSource.getMigrations(2);

        assertEquals(2, migrations.size());
        assertEquals(MigrationSource.Migration.MigrationType.UP, migrations.get(2).get(0).getMigrationType());
        assertEquals("CREATE IF NOT EXISTS TABLE clients(id INTEGER, full_name VARCHAR, age INTEGER, dob INTEGER);"
                , migrations.get(2).get(0).getUpMigrationQueries()[0]);
        assertEquals(MigrationSource.Migration.MigrationType.UP, migrations.get(3).get(0).getMigrationType());

    }
}