package org.bigtows.service;

import org.bigtows.service.event.SourceUpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of PinNote event manager.
 */
final public class PinNoteEventManagerImpl implements PinNoteEventManager {

    /**
     * Storage of events
     */
    private final List<SourceUpdateEvent> sourceUpdateEventList = new ArrayList<>();

    @Override
    public void registerSourceUpdateEvent(SourceUpdateEvent updateEvent) {
        sourceUpdateEventList.add(updateEvent);
    }

    @Override
    public void callSourceUpdate() {
        sourceUpdateEventList.forEach(SourceUpdateEvent::sourceUpdated);
    }
}
