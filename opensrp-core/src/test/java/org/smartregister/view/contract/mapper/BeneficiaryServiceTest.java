package org.smartregister.view.contract.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.BeneficiaryService;
import org.smartregister.view.contract.Beneficiary;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.EasyMap.mapOf;

public class BeneficiaryServiceTest {
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    private BeneficiaryService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new BeneficiaryService(allEligibleCouples, allBeneficiaries);
    }

    @Test
    public void shouldFetchEcsFromEcCaseIds() throws Exception {
        EligibleCouple ec1 = new EligibleCouple("CASE X", "Wife 1", "Husband 1", "EC 1", "Village 1", "SC 1", mapOf("isHighPriority", "yes"));
        EligibleCouple ec2 = new EligibleCouple("CASE Y", "Wife 2", "Husband 2", "EC 2", "Village 2", "SC 2", mapOf("some-key", "some-value"));
        when(allEligibleCouples.findByCaseIDs(asList("CASE X", "CASE Y"))).thenReturn(asList(ec1, ec2));
        when(allEligibleCouples.findByCaseID("EC CASE X")).thenReturn(ec1);
        when(allEligibleCouples.findByCaseID("EC CASE Y")).thenReturn(ec2);

        List<Beneficiary> beneficiaries = service.fetchFromEcCaseIds(asList("CASE X", "CASE Y"));

        Beneficiary expectedBeneficiary1 = new Beneficiary("CASE X", "Wife 1", "Husband 1", "", "EC 1", "Village 1", true);
        Beneficiary expectedBeneficiary2 = new Beneficiary("CASE Y", "Wife 2", "Husband 2", "", "EC 2", "Village 2", false);
        assertEquals(asList(expectedBeneficiary1, expectedBeneficiary2), beneficiaries);
    }

    @Test
    public void shouldFetchBeneficiariesFromChildCaseIds() throws Exception {
        Child child1 = new Child("CASE X", "MOTHER CASE X", "TC 1", "01/01/2008", "male", mapOf("isChildHighRisk", "yes"));
        Mother child1sMother = new Mother("MOTHER CASE X", "EC CASE X", "TC 1", "01-01-2006");
        EligibleCouple child1sEC = new EligibleCouple("EC CASE X", "Wife 1", "Husband 1", "EC 1", "Village 1", "SC 1", mapOf("some-key", "some-value"));
        Child child2 = new Child("CASE Y", "MOTHER CASE Y", "TC 2", "01/02/2008", "female", mapOf("some-key", "some-value"));
        Mother child2sMother = new Mother("MOTHER CASE Y", "EC CASE Y", "TC 2", "01-01-2007");
        EligibleCouple child2sEC = new EligibleCouple("EC CASE Y", "Wife 2", "Husband 2", "EC 2", "Village 2", "SC 2", mapOf("some-key", "some-value"));
        when(allBeneficiaries.findAllChildrenByCaseIDs(asList("CASE X", "CASE Y"))).thenReturn(asList(child1, child2));
        when(allBeneficiaries.findMother("MOTHER CASE X")).thenReturn(child1sMother);
        when(allEligibleCouples.findByCaseID("EC CASE X")).thenReturn(child1sEC);
        when(allBeneficiaries.findMother("MOTHER CASE Y")).thenReturn(child2sMother);
        when(allEligibleCouples.findByCaseID("EC CASE Y")).thenReturn(child2sEC);

        List<Beneficiary> beneficiaries = service.fetchFromChildCaseIds(asList("CASE X", "CASE Y"));

        Beneficiary expectedBeneficiary1 = new Beneficiary("CASE X", "Wife 1", "Husband 1", "TC 1", "EC 1", "Village 1", true);
        Beneficiary expectedBeneficiary2 = new Beneficiary("CASE Y", "Wife 2", "Husband 2", "TC 2", "EC 2", "Village 2", false);
        assertEquals(asList(expectedBeneficiary1, expectedBeneficiary2), beneficiaries);
    }

    @Test
    public void shouldFetchBeneficiariesFromMotherCaseIds() throws Exception {
        Mother mother1 = new Mother("CASE X", "EC CASE X", "TC 1", "01-01-2006").withDetails(mapOf("isHighRisk", "yes"));
        EligibleCouple mother1sEC = new EligibleCouple("EC CASE X", "Wife 1", "Husband 1", "EC 1", "Village 1", "SC 1", mapOf("some-key", "some-value"));
        Mother mother2 = new Mother("CASE Y", "EC CASE Y", "TC 2", "01-01-2007");
        EligibleCouple mother2sEC = new EligibleCouple("EC CASE Y", "Wife 2", "Husband 2", "EC 2", "Village 2", "SC 2", mapOf("some-key", "some-value"));
        when(allBeneficiaries.findAllMothersByCaseIDs(asList("CASE X", "CASE Y"))).thenReturn(asList(mother1, mother2));
        when(allEligibleCouples.findByCaseID("EC CASE X")).thenReturn(mother1sEC);
        when(allEligibleCouples.findByCaseID("EC CASE Y")).thenReturn(mother2sEC);

        List<Beneficiary> beneficiaries = service.fetchFromMotherCaseIds(asList("CASE X", "CASE Y"));

        Beneficiary expectedBeneficiary1 = new Beneficiary("CASE X", "Wife 1", "Husband 1", "TC 1", "EC 1", "Village 1", true);
        Beneficiary expectedBeneficiary2 = new Beneficiary("CASE Y", "Wife 2", "Husband 2", "TC 2", "EC 2", "Village 2", false);
        assertEquals(asList(expectedBeneficiary1, expectedBeneficiary2), beneficiaries);
    }
}
