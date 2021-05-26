package org.smartregister.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;

import java.util.List;

import static org.smartregister.repository.MotherRepository.TYPE_ANC;
import static org.smartregister.repository.MotherRepository.TYPE_PNC;

public class AllBeneficiaries {
    private final AlertRepository alertRepository;
    private final TimelineEventRepository timelineEventRepository;
    private ChildRepository childRepository;
    private MotherRepository motherRepository;

    public AllBeneficiaries(MotherRepository motherRepository, ChildRepository childRepository,
                            AlertRepository alertRepository, TimelineEventRepository
                                    timelineEventRepository) {
        this.childRepository = childRepository;
        this.motherRepository = motherRepository;
        this.alertRepository = alertRepository;
        this.timelineEventRepository = timelineEventRepository;
    }

    //#TODO
    public Mother findMotherWithOpenStatus(String caseId) {
        return motherRepository.findOpenCaseByCaseID(caseId);
    }

    public Mother findMother(String caseId) {
        List<Mother> mothers = motherRepository.findByCaseIds(caseId);
        if (mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public Child findChild(String caseId) {
        return childRepository.find(caseId);
    }

    public long ancCount() {
        return motherRepository.ancCount();
    }

    public long pncCount() {
        return motherRepository.pncCount();
    }

    public long childCount() {
        return childRepository.count();
    }

    public List<Pair<Mother, EligibleCouple>> allANCsWithEC() {
        return motherRepository.allMothersOfATypeWithEC(TYPE_ANC);
    }

    public List<Pair<Mother, EligibleCouple>> allPNCsWithEC() {
        return motherRepository.allMothersOfATypeWithEC(TYPE_PNC);
    }

    public Mother findMotherByECCaseId(String ecCaseId) {
        List<Mother> mothers = motherRepository.findAllCasesForEC(ecCaseId);
        if (mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public List<Child> findAllChildrenByMotherId(String entityId) {
        return childRepository.findByMotherCaseId(entityId);
    }

    public List<Child> findAllChildrenByCaseIDs(List<String> caseIds) {
        return childRepository.findChildrenByCaseIds(caseIds.toArray(new String[caseIds.size()]));
    }

    public List<Mother> findAllMothersByCaseIDs(List<String> caseIds) {
        return motherRepository.findByCaseIds(caseIds.toArray(new String[caseIds.size()]));
    }

    public void switchMotherToPNC(String entityId) {
        motherRepository.switchToPNC(entityId);
    }

    public void closeMother(String entityId) {
        alertRepository.deleteAllAlertsForEntity(entityId);
        timelineEventRepository.deleteAllTimelineEventsForEntity(entityId);
        motherRepository.close(entityId);
    }

    public void closeChild(String entityId) {
        alertRepository.deleteAllAlertsForEntity(entityId);
        timelineEventRepository.deleteAllTimelineEventsForEntity(entityId);
        childRepository.close(entityId);
    }

    public void closeAllMothersForEC(String ecId) {
        List<Mother> mothers = motherRepository.findAllCasesForEC(ecId);
        if (mothers == null || mothers.isEmpty()) {
            return;
        }
        for (Mother mother : mothers) {
            closeMother(mother.caseId());
        }
    }

    public List<Child> allChildrenWithMotherAndEC() {
        return childRepository.allChildrenWithMotherAndEC();
    }

    public List<Child> findAllChildrenByECId(String ecId) {
        return childRepository.findAllChildrenByECId(ecId);
    }

    public Mother findMotherWithOpenStatusByECId(String ecId) {
        return motherRepository.findMotherWithOpenStatusByECId(ecId);
    }

    public boolean isPregnant(String ecId) {
        return motherRepository.isPregnant(ecId);
    }

    public void updateChild(Child child) {
        childRepository.update(child);
    }

    public void updateMother(Mother mother) {
        motherRepository.update(mother);
    }
}
