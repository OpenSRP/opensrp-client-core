package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.domain.TimelineEvent;
import org.smartregister.util.Session;

import java.util.Collections;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.smartregister.domain.TimelineEvent.forChildBirthInChildProfile;

public class TimelineEventRepositoryTest extends AndroidTestCase {
    private TimelineEventRepository repository;

    @Override
    protected void setUp() throws Exception {
        repository = new TimelineEventRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, repository);
    }

    public void testShouldInsertTimelineEvents() throws Exception {
        TimelineEvent event1 = forChildBirthInChildProfile("CASE X", "2012-12-12", "3", "bcg opv_0");
        TimelineEvent event2 = forChildBirthInChildProfile("CASE X", "2012-01-01", "4", "bcg opv_1");
        TimelineEvent event3 = forChildBirthInChildProfile("CASE Y", "2012-12-1", "3.5", "opv_0 hepB_0");
        repository.add(event1);
        repository.add(event2);
        repository.add(event3);

        assertEquals(asList(event1, event2), repository.allFor("CASE X"));
    }

    public void testShouldDeleteTimelineEventsByCaseId() throws Exception {
        TimelineEvent event1 = forChildBirthInChildProfile("CASE X", "2012-12-12", null, null);
        TimelineEvent event2 = forChildBirthInChildProfile("CASE X", "2012-01-01", "3.5", "opv_0 hepB_0");
        TimelineEvent event3 = forChildBirthInChildProfile("CASE Y", "2012-12-1", "3", "bcg opv_0");
        repository.add(event1);
        repository.add(event2);
        repository.add(event3);

        repository.deleteAllTimelineEventsForEntity("CASE X");

        assertEquals(Collections.emptyList(), repository.allFor("CASE X"));
        assertEquals(asList(event3), repository.allFor("CASE Y"));
    }
}
