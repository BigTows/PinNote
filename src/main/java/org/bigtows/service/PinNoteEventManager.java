package org.bigtows.service;

import org.bigtows.service.event.SourceUpdateEvent;

/**
 * Event manager of workflow PinNote plugin
 */
public interface PinNoteEventManager {

    /**
     * Subscribe for update source.
     *
     * @param updateEvent instance of event
     */
    void registerSourceUpdateEvent(SourceUpdateEvent updateEvent);

    /**
     * Call when source updated
     */
    void callSourceUpdate();
}
