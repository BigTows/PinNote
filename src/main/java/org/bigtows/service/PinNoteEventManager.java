package org.bigtows.service;

import org.bigtows.service.event.SourceUpdateEvent;

public interface PinNoteEventManager {


     void registerSourceUpdateEvent(SourceUpdateEvent updateEvent);

     void callSourceUpdate();
}
