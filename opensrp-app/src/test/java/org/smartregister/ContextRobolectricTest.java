package org.smartregister;

import android.content.res.Configuration;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.domain.ColumnDetails;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 19-01-2021.
 */
public class ContextRobolectricTest extends BaseRobolectricUnitTest {

    @Test
    public void getInstanceShouldCallConstructorWhenContextIsNull() {
        ReflectionHelpers.setStaticField(Context.class, "context", null);
        Assert.assertNull(ReflectionHelpers.getStaticField(Context.class, "context"));

        Context.getInstance();

        Assert.assertNotNull(ReflectionHelpers.getStaticField(Context.class, "context"));
    }

    @Test
    public void setInstanceShouldReturnNullWhenContextPassedIsNull() {
        Assert.assertNull(Context.setInstance(null));
    }

    @Test
    public void applicationContextShouldReturnNullWhenApplicationContextIsNull() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        Context.getInstance().setApplicationContext(null);

        // Call the actual method
        Assert.assertNull(Context.getInstance().applicationContext());

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

    @Test
    public void applicationContextShouldReturnThrowExceptionAndReturnApplicationContext() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        android.content.Context spiedContext = Mockito.spy(context);
        Context.getInstance().setApplicationContext(spiedContext);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalStateException("An exception");
            }
        }).when(spiedContext).getResources();

        // Call the actual method
        Assert.assertEquals(spiedContext, Context.getInstance().applicationContext());

        Mockito.verify(spiedContext, Mockito.times(0)).createConfigurationContext(Mockito.any(Configuration.class));

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

    @Test
    public void sharedRepositoriesShouldAddBindTypesAsRepositories() {
        ArrayList<CommonRepositoryInformationHolder> bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        ColumnDetails[] columnNames = new ColumnDetails[]{
                ColumnDetails.builder().name("base_entity_id").build(),
                ColumnDetails.builder().name("fname").build(),
                ColumnDetails.builder().name("lname").build(),
        };
        String tableName = "ec_client_test";
        bindtypes.add(new CommonRepositoryInformationHolder(tableName, columnNames));
        Context context = Context.getInstance();
        Context spiedContext = Mockito.spy(context);
        Context.setInstance(spiedContext);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ReflectionHelpers.setField(spiedContext, "bindtypes", bindtypes);
                return null;
            }
        }).when(spiedContext).assignbindtypes();

        // Call the actual method
        ArrayList<DrishtiRepository> repositories = spiedContext.sharedRepositories();


        // Verifications & assertions
        boolean repositoryFoundWithColumns = false;
        for (DrishtiRepository drishtiRepository : repositories) {
            if (drishtiRepository instanceof CommonRepository && tableName.equals(((CommonRepository) drishtiRepository).TABLE_NAME)) {
                CommonRepository commonRepository = ((CommonRepository) drishtiRepository);
                Assert.assertEquals(7, commonRepository.common_TABLE_COLUMNS.length);
                Assert.assertEquals("base_entity_id", commonRepository.common_TABLE_COLUMNS[4].getName());
                Assert.assertEquals("fname", commonRepository.common_TABLE_COLUMNS[5].getName());
                Assert.assertEquals("lname", commonRepository.common_TABLE_COLUMNS[6].getName());
                repositoryFoundWithColumns = true;
            }
        }


        Assert.assertTrue(repositoryFoundWithColumns);
        Mockito.verify(spiedContext).assignbindtypes();

        // Return context instance
        Context.setInstance(context);
    }

    @Test
    public void alertServiceShouldGenerateAlertServiceWhenAlertServiceIsNull() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "alertService"));
        CommonFtsObject commonFtsObject = new CommonFtsObject(new String[]{"ec_client_test"});
        Context.getInstance().updateCommonFtsObject(commonFtsObject);
        Context.getInstance().assignbindtypes();

        // Call the method under test
        Assert.assertNotNull(Context.getInstance().alertService());

        Context.getInstance().updateCommonFtsObject(null);
    }

    @Test
    public void smartRegisterClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "smartRegisterClientsCache"));

        Assert.assertNotNull(Context.getInstance().smartRegisterClientsCache());
    }

    @Test
    public void ecClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "ecClientsCache"));

        Assert.assertNotNull(Context.getInstance().ecClientsCache());
    }

    @Test
    public void fpClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "fpClientsCache"));

        Assert.assertNotNull(Context.getInstance().fpClientsCache());
    }

    @Test
    public void ancClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "ancClientsCache"));

        Assert.assertNotNull(Context.getInstance().ancClientsCache());
    }

    @Test
    public void pncClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "pncClientsCache"));

        Assert.assertNotNull(Context.getInstance().pncClientsCache());
    }

    @Test
    public void villagesCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "villagesCache"));

        Assert.assertNotNull(Context.getInstance().villagesCache());
    }

    @Test
    public void typefaceCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "typefaceCache"));

        Assert.assertNotNull(Context.getInstance().typefaceCache());
    }

    @Test
    public void personObjectClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "personObjectClientsCache"));

        Assert.assertNotNull(Context.getInstance().personObjectClientsCache());
    }

    @Test
    public void getClientRelationshipRepositoryShouldGenerateInstance() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "clientRelationshipRepository"));

        Assert.assertNotNull(Context.getInstance().getClientRelationshipRepository());
    }

    @Test
    public void setDetailsRepository() {
        Context oldContext = Context.getInstance();
        ReflectionHelpers.setStaticField(Context.class, "context", null);

        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "detailsRepository"));

        // Execute the method under test
        Context.getInstance().setDetailsRepository(detailsRepository);

        Assert.assertEquals(detailsRepository, Context.getInstance().detailsRepository());
        Context.setInstance(oldContext);
    }

    @Test
    public void customHumanReadableConceptResponse() {
        HashMap<String, String> humanReadableConceptResponse = new HashMap<>();
        Context.getInstance().updateCustomHumanReadableConceptResponse(humanReadableConceptResponse);

        // Call method under test
        Assert.assertEquals(humanReadableConceptResponse, Context.getInstance().customHumanReadableConceptResponse());
    }

    @Test
    public void getEcBindtypesShouldUpdateBindtypesVariable() {
        Context context = Mockito.spy(Context.getInstance());
        ReflectionHelpers.setField(context, "bindtypes", null);
        Assert.assertNull(ReflectionHelpers.getField(context, "bindtypes"));

        // Mock ec_client_fields.json file
        String ecClientFields = "{\"bindobjects\":[{\"name\":\"ec_family\",\"columns\":[{\"column_name\":\"base_entity_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"baseEntityId\"}},{\"column_name\":\"unique_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"identifiers.opensrp_id\"}},{\"column_name\":\"first_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"firstName\"}},{\"column_name\":\"last_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"lastName\"}},{\"column_name\":\"village_town\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.cityVillage\"}},{\"column_name\":\"quarter_clan\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.commune\"}},{\"column_name\":\"street\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.street\"}},{\"column_name\":\"landmark\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.landmark\"}},{\"column_name\":\"gps\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"obs.fieldCode\",\"concept\":\"163277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"column_name\":\"fam_source_income\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.fam_source_income\"}},{\"column_name\":\"family_head\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.family_head\"}},{\"column_name\":\"primary_caregiver\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.primary_caregiver\"}},{\"column_name\":\"last_interacted_with\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"version\"}},{\"column_name\":\"date_removed\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.dateRemoved\"}},{\"column_name\":\"entity_type\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"entityType\"}}]}]}";
        Mockito.doReturn(ecClientFields).when(context).ReadFromfile(Mockito.eq("ec_client_fields.json"), Mockito.any(android.content.Context.class));

        // Execute the method being tested
        ReflectionHelpers.setField(context, "bindtypes", new ArrayList<CommonRepositoryInformationHolder>());
        context.getEcBindtypes();


        ArrayList<CommonRepositoryInformationHolder> bindtypes = ReflectionHelpers.getField(context, "bindtypes");
        Assert.assertEquals(1, bindtypes.size());
        Assert.assertEquals(15, bindtypes.get(0).getColumnNames().length);
        Assert.assertEquals("base_entity_id", bindtypes.get(0).getColumnNames()[0].getName());
        Assert.assertEquals("last_interacted_with", bindtypes.get(0).getColumnNames()[12].getName());
        Assert.assertEquals("street", bindtypes.get(0).getColumnNames()[6].getName());
    }

    @Test
    public void getEcBindtypesShouldNotUpdateBindtypesWhenEcClientFieldsCouldNotBeRead() {
        Context context = Mockito.spy(Context.getInstance());
        ReflectionHelpers.setField(context, "bindtypes", null);
        Assert.assertNull(ReflectionHelpers.getField(context, "bindtypes"));

        // Execute the method being tested
        Mockito.doReturn(null).when(context).ReadFromfile(Mockito.eq("ec_client_fields.json"), Mockito.any(android.content.Context.class));
        context.getEcBindtypes();

        Assert.assertNull(ReflectionHelpers.getField(context, "bindtypes"));
    }

    @Test
    public void getEcBindtypesShouldNotUpdateBindtypesWhenApplicationContextIsNull() {
        Context context = Mockito.spy(Context.getInstance());
        ReflectionHelpers.setField(context, "bindtypes", null);
        Assert.assertNull(ReflectionHelpers.getField(context, "bindtypes"));

        // Execute the method being tested
        Mockito.doReturn(null).when(context).applicationContext();
        context.getEcBindtypes();

        Assert.assertNull(ReflectionHelpers.getField(context, "bindtypes"));
    }

    @Test
    public void commonrepositoryShouldReturnCommonRepositoryWithoutFtsSupport() {
        Context context = Mockito.spy(Context.getInstance());

        // Mock ec_client_fields.json file
        String ecClientFields = "{\"bindobjects\":[{\"name\":\"ec_family\",\"columns\":[{\"column_name\":\"base_entity_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"baseEntityId\"}},{\"column_name\":\"unique_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"identifiers.opensrp_id\"}},{\"column_name\":\"first_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"firstName\"}},{\"column_name\":\"last_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"lastName\"}},{\"column_name\":\"village_town\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.cityVillage\"}},{\"column_name\":\"quarter_clan\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.commune\"}},{\"column_name\":\"street\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.street\"}},{\"column_name\":\"landmark\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.landmark\"}},{\"column_name\":\"gps\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"obs.fieldCode\",\"concept\":\"163277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"column_name\":\"fam_source_income\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.fam_source_income\"}},{\"column_name\":\"family_head\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.family_head\"}},{\"column_name\":\"primary_caregiver\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.primary_caregiver\"}},{\"column_name\":\"last_interacted_with\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"version\"}},{\"column_name\":\"date_removed\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.dateRemoved\"}},{\"column_name\":\"entity_type\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"entityType\"}}]}]}";
        Mockito.doReturn(ecClientFields).when(context).ReadFromfile(Mockito.eq("ec_client_fields.json"), Mockito.any(android.content.Context.class));

        ReflectionHelpers.setField(context, "bindtypes", new ArrayList<CommonRepositoryInformationHolder>());
        context.getEcBindtypes();

        // Execute the method being tested
        CommonRepository commonRepository = context.commonrepository("ec_family");

        Assert.assertEquals("ec_family", commonRepository.TABLE_NAME);
        Assert.assertEquals(19, commonRepository.common_TABLE_COLUMNS.length);
        Assert.assertEquals("id", commonRepository.common_TABLE_COLUMNS[0].getName());
        Assert.assertEquals("details", commonRepository.common_TABLE_COLUMNS[2].getName());
        Assert.assertEquals("last_interacted_with", commonRepository.common_TABLE_COLUMNS[16].getName());
        Assert.assertEquals("entity_type", commonRepository.common_TABLE_COLUMNS[18].getName());
        Assert.assertFalse(commonRepository.isFts());
    }

    @Test
    public void commonrepositoryShouldReturnCommonRepositoryWithFtsSupport() {
        Context context = Mockito.spy(Context.getInstance());

        // Mock ec_client_fields.json file
        String ecClientFields = "{\"bindobjects\":[{\"name\":\"ec_family\",\"columns\":[{\"column_name\":\"base_entity_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"baseEntityId\"}},{\"column_name\":\"unique_id\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"identifiers.opensrp_id\"}},{\"column_name\":\"first_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"firstName\"}},{\"column_name\":\"last_name\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"lastName\"}},{\"column_name\":\"village_town\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.cityVillage\"}},{\"column_name\":\"quarter_clan\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.commune\"}},{\"column_name\":\"street\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.street\"}},{\"column_name\":\"landmark\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"addresses.landmark\"}},{\"column_name\":\"gps\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"obs.fieldCode\",\"concept\":\"163277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"column_name\":\"fam_source_income\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.fam_source_income\"}},{\"column_name\":\"family_head\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.family_head\"}},{\"column_name\":\"primary_caregiver\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"relationships.primary_caregiver\"}},{\"column_name\":\"last_interacted_with\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"version\"}},{\"column_name\":\"date_removed\",\"type\":\"Client\",\"json_mapping\":{\"field\":\"attributes.dateRemoved\"}},{\"column_name\":\"entity_type\",\"type\":\"Event\",\"json_mapping\":{\"field\":\"entityType\"}}]}]}";
        Mockito.doReturn(ecClientFields).when(context).ReadFromfile(Mockito.eq("ec_client_fields.json"), Mockito.any(android.content.Context.class));

        ReflectionHelpers.setField(context, "bindtypes", new ArrayList<CommonRepositoryInformationHolder>());
        context.getEcBindtypes();

        CommonFtsObject commonFtsObject = new CommonFtsObject(new String[]{"ec_family"});
        commonFtsObject.updateSearchFields("ec_family", new String[]{""});
        context.updateCommonFtsObject(commonFtsObject);

        // Execute the method being tested
        CommonRepository commonRepository = context.commonrepository("ec_family");

        Assert.assertEquals("ec_family", commonRepository.TABLE_NAME);
        Assert.assertEquals(19, commonRepository.common_TABLE_COLUMNS.length);
        Assert.assertEquals("id", commonRepository.common_TABLE_COLUMNS[0].getName());
        Assert.assertEquals("details", commonRepository.common_TABLE_COLUMNS[2].getName());
        Assert.assertEquals("last_interacted_with", commonRepository.common_TABLE_COLUMNS[16].getName());
        Assert.assertEquals("entity_type", commonRepository.common_TABLE_COLUMNS[18].getName());
        Assert.assertTrue(commonRepository.isFts());
    }

    @Test
    public void getColorResource() {
        Assert.assertEquals(RuntimeEnvironment.application.getColor(R.color.alert_complete_green), Context.getInstance().getColorResource(R.color.alert_complete_green));
    }
}
