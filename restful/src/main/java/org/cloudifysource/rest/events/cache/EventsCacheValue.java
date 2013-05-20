package org.cloudifysource.rest.events.cache;

import org.cloudifysource.dsl.rest.response.ServiceDeploymentEvents;

/**
 * Created with IntelliJ IDEA.
 * User: elip
 * Date: 5/20/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventsCacheValue {

    private ServiceDeploymentEvents events;
    private long lastRefreshedTimestamp;
    private int lastEventIndex;
    private volatile Object mutex = new Object();

    public ServiceDeploymentEvents getEvents() {
        return events;
    }

    public void setEvents(final ServiceDeploymentEvents events) {
        this.events = events;
    }

    public long getLastRefreshedTimestamp() {
        return lastRefreshedTimestamp;
    }

    public void setLastRefreshedTimestamp(final long lastRefreshedTimestamp) {
        this.lastRefreshedTimestamp = lastRefreshedTimestamp;
    }

    public int getLastEventIndex() {
        return lastEventIndex;
    }

    public void setLastEventIndex(final int lastEventIndex) {
        this.lastEventIndex = lastEventIndex;
    }

    public Object getMutex() {
        return mutex;
    }


    @Override
    public String toString() {
        return "EventsCacheValue{" + "events=" + events
                + ", lastRefreshedTimestamp=" + lastRefreshedTimestamp
                + ", lastEventIndex=" + lastEventIndex +
                ", mutex=" + mutex + '}';
    }
}
