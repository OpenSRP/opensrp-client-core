package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.domain.ServiceProvided;
import org.smartregister.util.Session;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.smartregister.util.EasyMap.mapOf;

public class ServiceProvidedRepositoryTest extends AndroidTestCase {
    private ServiceProvidedRepository repository;

    @Override
    protected void setUp() throws Exception {
        repository = new ServiceProvidedRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, repository);
    }

//    Fail Test
//    public void testShouldInsertServiceProvidedIntoRepository() throws Exception {
//        ServiceProvided serviceProvided = new ServiceProvided("entity id 1", "name", "2013-01-02", mapOf("key 1", "value 1"));
//
//        repository.add(serviceProvided);
//
//        assertEquals(asList(serviceProvided), repository.all());
//    }

    public void testShouldFindServicesProvidedByEntityIdAndServiceNames() throws Exception {
        ServiceProvided name1Entity1 = new ServiceProvided("entity id 1", "name 1", "2013-01-02", mapOf("key 1", "value 1"));
        ServiceProvided name2Entity1 = new ServiceProvided("entity id 1", "name 2", "2013-01-02", mapOf("key 1", "value 1"));
        ServiceProvided name3Entity1 = new ServiceProvided("entity id 1", "name 3", "2013-01-02", mapOf("key 1", "value 1"));
        ServiceProvided name1Entity2 = new ServiceProvided("entity id 2", "name 1", "2013-01-02", mapOf("key 1", "value 1"));
        repository.add(name1Entity1);
        repository.add(name2Entity1);
        repository.add(name3Entity1);
        repository.add(name1Entity2);

        List<ServiceProvided> actualServicesProvided = repository.findByEntityIdAndServiceNames("entity id 1", "name 1", "name 2");

        assertEquals(asList(name1Entity1, name2Entity1), actualServicesProvided);
    }

    public void testShouldReturnEmptyServicesProvidedWhenNoneExistsWithGivenEntityIdAndServiceNames() throws Exception {
        assertEquals(Collections.<ServiceProvided>emptyList(), repository.findByEntityIdAndServiceNames("entity id 1", "name 1", "name 2"));
    }
}
