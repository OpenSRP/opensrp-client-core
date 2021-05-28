package org.smartregister.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllServicesProvided;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by Richard Kareko on 11/17/20.
 */

public class ServiceProvidedServiceTest extends BaseUnitTest {

    @Mock
    private AllServicesProvided allServiceProvided;

    @Captor
    private ArgumentCaptor<ServiceProvided> serviceProvidedArgumentCaptor;

    private ServiceProvidedService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new ServiceProvidedService(allServiceProvided);
    }

    @Test
    public void testConstructor() {
        assertEquals(allServiceProvided, Whitebox.getInternalState(service, "allServiceProvided"));
    }

    @Test
    public void testFindByEntityIdAndServiceNames() {
        String entityId = "Task";
        String name = "Blood Screening";
        service.findByEntityIdAndServiceNames(entityId,name);
        verify(allServiceProvided).findByEntityIdAndServiceNames(entityId, name);
    }

    @Test
    public void testAdd(){
        String entityId = "Child";
        String immunization = "BCG";
        String date = "12-12-2020";
        ServiceProvided serviceProvided = ServiceProvided.forChildImmunization(entityId, immunization,date);

        service.add(serviceProvided);
        verify(allServiceProvided).add(serviceProvidedArgumentCaptor.capture());
        assertEquals(entityId, serviceProvidedArgumentCaptor.getValue().entityId());
        assertEquals(immunization, serviceProvidedArgumentCaptor.getValue().name());
        assertEquals(date, serviceProvidedArgumentCaptor.getValue().date());
    }
}
