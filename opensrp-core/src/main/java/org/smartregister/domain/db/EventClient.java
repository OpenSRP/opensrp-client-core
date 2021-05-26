package org.smartregister.domain.db;

import org.smartregister.domain.Event;
import org.smartregister.domain.Client;

public class EventClient {

    private Event event;
    private Client client;

    public EventClient(Event event) {
        this.event = event;
    }

    public EventClient(Event event, Client client) {
        this.event = event;
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
