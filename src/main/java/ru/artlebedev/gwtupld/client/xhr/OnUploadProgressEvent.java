package ru.artlebedev.gwtupld.client.xhr;

import com.google.gwt.core.client.JavaScriptObject;

public class OnUploadProgressEvent extends JavaScriptObject {
  protected OnUploadProgressEvent() {
  }

  public final native int getLoaded() /*-{
    return this.loaded;
  }-*/;

  public final native int getTotal() /*-{
    return this.total;
  }-*/;

  public final native boolean isLengthComputable() /*-{
    return this.lengthComputable;
  }-*/;
}
