package ru.artlebedev.gwtupld.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class UploadingCompletedEvent
    extends GwtEvent<UploadingCompletedEventHandler> {
  public static Type<UploadingCompletedEventHandler> TYPE
      = new Type<UploadingCompletedEventHandler>();

  public UploadingCompletedEvent() {
  }

  @Override
  public Type<UploadingCompletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(UploadingCompletedEventHandler handler) {
    handler.onUploadingCompleted(this);
  }
}
