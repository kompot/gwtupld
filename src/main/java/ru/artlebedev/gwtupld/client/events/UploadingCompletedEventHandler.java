package ru.artlebedev.gwtupld.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface UploadingCompletedEventHandler extends EventHandler {
  void onUploadingCompleted(UploadingCompletedEvent event);
}
