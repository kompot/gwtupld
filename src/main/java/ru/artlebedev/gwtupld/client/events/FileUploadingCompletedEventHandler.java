package ru.artlebedev.gwtupld.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileUploadingCompletedEventHandler extends EventHandler {
  void onFileUploadingCompleted(FileUploadingCompletedEvent event);
}
