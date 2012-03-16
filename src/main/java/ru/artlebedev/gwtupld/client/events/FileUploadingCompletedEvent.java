package ru.artlebedev.gwtupld.client.events;

import ru.artlebedev.gwtupld.client.FileInfo;

import com.google.gwt.event.shared.GwtEvent;

public class FileUploadingCompletedEvent
    extends GwtEvent<FileUploadingCompletedEventHandler> {
  public static Type<FileUploadingCompletedEventHandler> TYPE
      = new Type<FileUploadingCompletedEventHandler>();
  private FileInfo fileInfo;

  public FileUploadingCompletedEvent(FileInfo fi) {
    this.fileInfo = fi;
  }

  @Override
  public Type<FileUploadingCompletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(FileUploadingCompletedEventHandler handler) {
    handler.onFileUploadingCompleted(this);
  }

  public FileInfo getFileInfo() {
    return fileInfo;
  }
}
